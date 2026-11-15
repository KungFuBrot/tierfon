package de.kungfubrot.tierfon;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * Zählt die Spielzeit, solange der Bildschirm an ist, und sperrt das Telefon, wenn sie
 * aufgebraucht ist. Zeigt das Batteriesymbol auch über Kamera und Galerie.
 */
public class UsageService extends Service {
    private final Handler handler = new Handler();
    private long lastTick;
    private BatteryView overlay;

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            step();
            handler.postDelayed(this, 1000);
        }
    };

    static void ensureRunning(Context c) {
        c.startService(new Intent(c, UsageService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(2, new Notification.Builder(this)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Tierfon")
            .setContentText("Spielzeit läuft")
            .build());
        lastTick = SystemClock.elapsedRealtime();
        handler.post(tick);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(tick);
        showOverlay(false);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void step() {
        long now = SystemClock.elapsedRealtime();
        long dt = Math.min(2000, now - lastTick);
        lastTick = now;
        boolean screenOn = ((PowerManager) getSystemService(POWER_SERVICE)).isInteractive();
        boolean parent = Kiosk.parentMode(this);
        Kiosk.enforceVolume(this);
        if (screenOn && !parent && Usage.remainingMs(this) > 0) Usage.add(this, dt);

        if (!Usage.exhausted(this)) {
            // Neuer Tag oder Zeit von den Eltern zurückgesetzt: schwarzen Bildschirm schließen.
            BlockedActivity blocked = BlockedActivity.current;
            if (blocked != null) blocked.finish();
        } else {
            Player.get(this).stop();
            if (screenOn && !BlockedActivity.showing && !ParentActivity.showing) {
                startActivity(new Intent(this, BlockedActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }
        // In Tierfon zeichnen die Bildschirme die Batterie selbst; das Fenster hier ist für
        // Kamera und Galerie.
        showOverlay(screenOn && !parent && Usage.resumed == 0 && !BlockedActivity.showing);
    }

    private void showOverlay(boolean show) {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (show && overlay == null && Settings.canDrawOverlays(this)) {
            int w = getResources().getDisplayMetrics().widthPixels;
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                Math.round(w * 0.16f), Math.round(w * 0.082f),
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
            lp.gravity = Gravity.TOP | Gravity.END;
            lp.x = Math.round(w * 0.04f);
            lp.y = Math.round(w * 0.022f);
            BatteryView v = new BatteryView(this);
            try {
                wm.addView(v, lp);
                overlay = v;
            } catch (RuntimeException ignored) {
            }
        } else if (!show && overlay != null) {
            View v = overlay;
            overlay = null;
            try {
                wm.removeView(v);
            } catch (RuntimeException ignored) {
            }
        }
    }
}
