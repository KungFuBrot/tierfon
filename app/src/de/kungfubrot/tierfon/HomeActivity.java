package de.kungfubrot.tierfon;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

/** Der Startbildschirm des Telefons: Tiere, Musik, Kamera und Galerie. */
public class HomeActivity extends BaseActivity {
    private static final long PARENT_HOLD_MS = 3000;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        FracLayout root = new FracLayout(this);
        root.setBackgroundColor(0xFFFFF4D6);

        TileView animals = new TileView(this, 0xFFBDEAA8, "frog");
        animals.setOnClickListener(v -> startActivity(new Intent(this, AnimalsActivity.class)));
        root.add(animals, 0.06f, 0.09f, 0.48f, 0.48f);

        TileView music = new TileView(this, 0xFFFF9F43, "note");
        music.setOnClickListener(v -> startActivity(new Intent(this, MusicActivity.class)));
        root.add(music, 0.52f, 0.09f, 0.94f, 0.48f);

        TileView camera = new TileView(this, 0xFF5B9BF0, "camera");
        camera.setOnClickListener(v -> launch("org.cyanogenmod.snap"));
        root.add(camera, 0.06f, 0.52f, 0.48f, 0.91f);

        TileView gallery = new TileView(this, 0xFFB57BEA, "gallery");
        gallery.setOnClickListener(v -> launch("com.android.gallery3d"));
        root.add(gallery, 0.52f, 0.52f, 0.94f, 0.91f);

        // Versteckter Zugang zum Elternbereich: rechte obere Ecke drei Sekunden gedrückt halten.
        View corner = new View(this);
        Runnable open = () -> startActivity(new Intent(this, ParentActivity.class));
        corner.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                v.postDelayed(open, PARENT_HOLD_MS);
            } else if (e.getAction() == MotionEvent.ACTION_UP
                    || e.getAction() == MotionEvent.ACTION_CANCEL) {
                v.removeCallbacks(open);
            }
            return true;
        });
        root.add(corner, 0.8f, 0f, 1f, 0.08f);

        setContentView(root);
    }

    private void launch(String pkg) {
        Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
        if (i == null) return;
        try {
            startActivity(i);
        } catch (RuntimeException ignored) {
        }
    }

    @Override
    public void onBackPressed() {
        // Vom Startbildschirm geht es nirgendwohin zurück.
    }
}
