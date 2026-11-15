package de.kungfubrot.tierfon;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;

/** Gemeinsames Verhalten aller Bildschirme: Vollbild, Sperre, Spielzeit und Batteriesymbol. */
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

    boolean showsBattery() {
        return true;
    }

    /** Legt das Batteriesymbol oben rechts über den Inhalt. */
    @Override
    public void setContentView(View content) {
        if (!showsBattery()) {
            super.setContentView(content);
            return;
        }
        FracLayout root = new FracLayout(this);
        root.add(content, 0, 0, 1, 1);
        root.add(new BatteryView(this), 0.80f, 0.0125f, 0.96f, 0.0585f);
        super.setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Usage.resumed++;
        UsageService.ensureRunning(this);
        if (locks()) {
            Kiosk.lock(this);
            if (Usage.exhausted(this) && !(this instanceof BlockedActivity)) {
                startActivity(new Intent(this, BlockedActivity.class));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Usage.resumed = Math.max(0, Usage.resumed - 1);
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
