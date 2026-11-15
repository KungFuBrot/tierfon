package de.kungfubrot.tierfon;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/** Hält die App am Leben, solange Musik läuft, auch wenn der Bildschirm aus ist. */
public class MusicService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification n = new Notification.Builder(this)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("Tierfon")
            .setContentText("Musik läuft")
            .build();
        startForeground(1, n);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
