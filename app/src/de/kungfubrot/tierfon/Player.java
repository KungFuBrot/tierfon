package de.kungfubrot.tierfon;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.PowerManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Spielt die Musikdateien aus dem Ordner „Music“ des Telefons. */
final class Player {
    private static Player instance;

    final List<File> tracks = new ArrayList<>();
    int index = -1;

    private final Context app;
    private MediaPlayer mp;

    private Player(Context app) {
        this.app = app;
    }

    static Player get(Context c) {
        if (instance == null) instance = new Player(c.getApplicationContext());
        return instance;
    }

    void scan() {
        File current = index >= 0 && index < tracks.size() ? tracks.get(index) : null;
        tracks.clear();
        collect(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), 0);
        Collections.sort(tracks);
        index = current == null ? -1 : tracks.indexOf(current);
    }

    private void collect(File dir, int depth) {
        File[] files = dir.listFiles();
        if (files == null || depth > 4) return;
        for (File f : files) {
            String n = f.getName().toLowerCase(Locale.ROOT);
            if (f.isDirectory()) {
                collect(f, depth + 1);
            } else if (n.endsWith(".mp3") || n.endsWith(".m4a") || n.endsWith(".ogg")) {
                tracks.add(f);
            }
        }
    }

    static String title(File f) {
        String n = f.getName();
        int dot = n.lastIndexOf('.');
        return dot > 0 ? n.substring(0, dot) : n;
    }

    boolean playing() {
        try {
            return mp != null && mp.isPlaying();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    void play(int i) {
        if (tracks.isEmpty()) return;
        index = ((i % tracks.size()) + tracks.size()) % tracks.size();
        stop();
        try {
            mp = new MediaPlayer();
            mp.setDataSource(tracks.get(index).getPath());
            // Die Musik läuft auch bei ausgeschaltetem Bildschirm weiter.
            mp.setWakeMode(app, PowerManager.PARTIAL_WAKE_LOCK);
            mp.setOnCompletionListener(m -> play(index + 1));
            mp.prepare();
            mp.start();
            app.startService(new Intent(app, MusicService.class));
        } catch (Exception e) {
            stop();
        }
    }

    void toggle() {
        if (mp == null) {
            play(Math.max(index, 0));
        } else if (playing()) {
            mp.pause();
            app.stopService(new Intent(app, MusicService.class));
        } else {
            mp.start();
            app.startService(new Intent(app, MusicService.class));
        }
    }

    void stop() {
        if (mp != null) {
            mp.release();
            mp = null;
        }
        app.stopService(new Intent(app, MusicService.class));
    }
}
