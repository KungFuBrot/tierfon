package de.kungfubrot.tierfon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Rückwege vom PC. Nur die adb-Shell darf diese Nachrichten schicken:
 * RELEASE hebt die Sperre auf, SET_USED setzt die heute verbrauchte Spielzeit (Minuten),
 * SET_PIN setzt die Eltern-PIN, LOCK beendet den Elternmodus sofort.
 */
public class ReleaseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = String.valueOf(intent.getAction());
        if (action.endsWith(".SET_USED")) {
            Usage.setUsedMs(context, intent.getIntExtra("minutes", 0) * 60_000L);
        } else if (action.endsWith(".SET_PIN")) {
            String pin = intent.getStringExtra("pin");
            if (pin != null && pin.matches("\\d{4,}")) {
                Kiosk.prefs(context).edit().putString("pin", pin).apply();
            }
        } else if (action.endsWith(".LOCK")) {
            Kiosk.setParentMode(context, false);
        } else if (action.endsWith(".RELEASE")) {
            Kiosk.release(context);
            Kiosk.setParentMode(context, true);
        }
    }
}
