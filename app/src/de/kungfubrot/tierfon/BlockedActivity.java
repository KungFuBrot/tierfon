package de.kungfubrot.tierfon;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Die Spielzeit ist aufgebraucht: schwarzer Bildschirm, nichts reagiert, und kurz darauf
 * schaltet sich der Bildschirm aus. Am nächsten Tag verschwindet dieser Bildschirm von selbst.
 */
public class BlockedActivity extends BaseActivity {
    private static final long SCREEN_OFF_MS = 4000;
    private static final long PARENT_HOLD_MS = 3000;

    static boolean showing;
    static BlockedActivity current;

    private View view;
    private final Runnable screenOff = () -> Kiosk.screenOff(this);
    private final Runnable openParent =
        () -> startActivity(new Intent(this, ParentActivity.class));

    @Override
    boolean showsBattery() {
        return false;
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 0.01f;
        getWindow().setAttributes(lp);
        view = new View(this);
        view.setBackgroundColor(0xFF000000);
        // Rückweg für Eltern: rechte obere Ecke drei Sekunden gedrückt halten.
        view.setOnTouchListener((v, e) -> {
            v.removeCallbacks(screenOff);
            boolean corner = e.getX() > v.getWidth() * 0.8f && e.getY() < v.getHeight() * 0.08f;
            if (e.getAction() == MotionEvent.ACTION_DOWN && corner) {
                v.postDelayed(openParent, PARENT_HOLD_MS);
            } else if (e.getAction() == MotionEvent.ACTION_UP
                    || e.getAction() == MotionEvent.ACTION_CANCEL) {
                v.removeCallbacks(openParent);
                v.postDelayed(screenOff, SCREEN_OFF_MS);
            }
            return true;
        });
        setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Usage.exhausted(this)) {
            finish();
            return;
        }
        showing = true;
        current = this;
        view.postDelayed(screenOff, SCREEN_OFF_MS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        showing = false;
        current = null;
        view.removeCallbacks(screenOff);
        view.removeCallbacks(openParent);
    }

    @Override
    public void onBackPressed() {
        // Kein Zurück.
    }
}
