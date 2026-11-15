package de.kungfubrot.tierfon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Rückweg vom PC: hebt die Sperre auf, wenn die adb-Shell die Nachricht schickt. */
public class ReleaseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Kiosk.release(context);
        Kiosk.setParentMode(context, true);
    }
}
