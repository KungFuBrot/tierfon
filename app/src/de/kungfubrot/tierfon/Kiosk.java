package de.kungfubrot.tierfon;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.UserManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Die Sperre: Das Kind kann nur Tierfon, Kamera und Galerie benutzen.
 * Voraussetzung ist, dass Tierfon per adb als "Gerätebesitzer" eingetragen wurde.
 */
final class Kiosk {
    /** Apps, die das Kind benutzen darf. */
    static final String[] ALLOWED = {
        "de.kungfubrot.tierfon", "org.cyanogenmod.snap", "com.android.gallery3d"};
    /** Bleiben vorhanden, sind in der Sperre aber nicht erreichbar. */
    private static final String[] KEEP = {
        "com.android.settings", "org.cyanogenmod.audiofx", "com.android.documentsui"};
    private static final String[] RESTRICTIONS = {
        UserManager.DISALLOW_CONFIG_WIFI, UserManager.DISALLOW_INSTALL_APPS,
        UserManager.DISALLOW_SAFE_BOOT, UserManager.DISALLOW_ADD_USER};
    private static final long PARENT_MINUTES = 15;

    private static boolean setupDone;

    private Kiosk() {}

    static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences("kiosk", Context.MODE_PRIVATE);
    }

    private static DevicePolicyManager dpm(Context c) {
        return (DevicePolicyManager) c.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    private static ComponentName admin(Context c) {
        return new ComponentName(c, AdminReceiver.class);
    }

    static boolean isOwner(Context c) {
        return dpm(c).isDeviceOwnerApp(c.getPackageName());
    }

    /** Elternmodus: Die Sperre ist für einige Minuten ausgesetzt. */
    static boolean parentMode(Context c) {
        return System.currentTimeMillis() < prefs(c).getLong("parentUntil", 0);
    }

    static void setParentMode(Context c, boolean on) {
        long until = on ? System.currentTimeMillis() + PARENT_MINUTES * 60_000 : 0;
        prefs(c).edit().putLong("parentUntil", until).apply();
    }

    static String pin(Context c) {
        return prefs(c).getString("pin", "2580");
    }

    static void lock(Activity a) {
        if (parentMode(a) || !isOwner(a)) return;
        if (!setupDone) {
            setup(a.getApplicationContext());
            setupDone = true;
        }
        ActivityManager am = (ActivityManager) a.getSystemService(Context.ACTIVITY_SERVICE);
        if (am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE) {
            try {
                a.startLockTask();
            } catch (RuntimeException ignored) {
            }
        }
    }

    static void unlock(Activity a) {
        try {
            a.stopLockTask();
        } catch (RuntimeException ignored) {
        }
    }

    private static void setup(Context c) {
        DevicePolicyManager d = dpm(c);
        ComponentName ad = admin(c);
        String me = c.getPackageName();
        d.setLockTaskPackages(ad, ALLOWED);
        for (String r : RESTRICTIONS) d.addUserRestriction(ad, r);
        for (String perm : new String[] {
                Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE}) {
            d.setPermissionGrantState(ad, me, perm,
                DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);
        }
        IntentFilter home = new IntentFilter(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        home.addCategory(Intent.CATEGORY_DEFAULT);
        d.addPersistentPreferredActivity(ad, home, new ComponentName(c, HomeActivity.class));
        hideOtherApps(c, d, ad);
        wifiOff(c);
    }

    /** Blendet alle Apps mit Start-Symbol aus, außer den erlaubten. Rückgängig mit release(). */
    private static void hideOtherApps(Context c, DevicePolicyManager d, ComponentName ad) {
        Set<String> keep = new HashSet<>(Arrays.asList(ALLOWED));
        keep.addAll(Arrays.asList(KEEP));
        Set<String> hidden = new HashSet<>(prefs(c).getStringSet("hidden", new HashSet<String>()));
        PackageManager pm = c.getPackageManager();
        for (String category : new String[] {Intent.CATEGORY_LAUNCHER, Intent.CATEGORY_HOME}) {
            Intent i = new Intent(Intent.ACTION_MAIN).addCategory(category);
            for (ResolveInfo r : pm.queryIntentActivities(i, 0)) {
                String p = r.activityInfo.packageName;
                if (keep.contains(p)) continue;
                try {
                    if (d.setApplicationHidden(ad, p, true)) hidden.add(p);
                } catch (RuntimeException ignored) {
                }
            }
        }
        prefs(c).edit().putStringSet("hidden", hidden).apply();
    }

    /** Löscht gespeicherte WLANs und schaltet das WLAN aus. */
    private static void wifiOff(Context c) {
        try {
            WifiManager w = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
            List<WifiConfiguration> nets = w.getConfiguredNetworks();
            if (nets != null) {
                for (WifiConfiguration n : nets) w.removeNetwork(n.networkId);
                w.saveConfiguration();
            }
            w.setWifiEnabled(false);
        } catch (RuntimeException ignored) {
        }
    }

    /** Hebt die Sperre vollständig auf: Apps wieder einblenden, Gerätebesitz abgeben. */
    static void release(Context c) {
        if (!isOwner(c)) return;
        DevicePolicyManager d = dpm(c);
        ComponentName ad = admin(c);
        String me = c.getPackageName();
        for (String p : prefs(c).getStringSet("hidden", new HashSet<String>())) {
            try {
                d.setApplicationHidden(ad, p, false);
            } catch (RuntimeException ignored) {
            }
        }
        for (String r : RESTRICTIONS) d.clearUserRestriction(ad, r);
        d.clearPackagePersistentPreferredActivities(ad, me);
        d.setLockTaskPackages(ad, new String[0]);
        d.clearDeviceOwnerApp(me);
        prefs(c).edit().remove("hidden").apply();
        setupDone = false;
    }
}
