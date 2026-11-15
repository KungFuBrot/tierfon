package de.kungfubrot.tierfon;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;

/** Gemeinsames Verhalten aller Bildschirme: Vollbild und Sperre. */
abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    /** Der Elternbereich sperrt nicht. */
    boolean locks() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locks()) Kiosk.lock(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
