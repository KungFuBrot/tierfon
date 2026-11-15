package de.kungfubrot.tierfon;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;

/** Der Startbildschirm des Telefons: Tiere, Musik und Galerie, auf Wunsch der Eltern auch die Kamera. */
public class HomeActivity extends BaseActivity {
    private static final long PARENT_HOLD_MS = 3000;

    @Override
    protected void onResume() {
        super.onResume();
        // Jedes Mal neu aufbauen, weil die Eltern die Kamera freigeben oder sperren können.
        FracLayout root = new FracLayout(this);
        root.setBackgroundColor(0xFFFFF4D6);

        TileView animals = new TileView(this, 0xFFBDEAA8, "frog");
        animals.setOnClickListener(v -> startActivity(new Intent(this, AnimalsActivity.class)));
        root.add(animals, 0.06f, 0.09f, 0.48f, 0.48f);

        TileView music = new TileView(this, 0xFFFF9F43, "note");
        music.setOnClickListener(v -> startActivity(new Intent(this, MusicActivity.class)));
        root.add(music, 0.52f, 0.09f, 0.94f, 0.48f);

        boolean camera = Kiosk.cameraAllowed(this);
        TileView gallery = new TileView(this, 0xFFB57BEA, "gallery");
        gallery.setOnClickListener(v -> launch(Kiosk.GALLERY));
        if (camera) {
            root.add(gallery, 0.52f, 0.52f, 0.94f, 0.91f);
            TileView cam = new TileView(this, 0xFF5B9BF0, "camera");
            cam.setOnClickListener(v -> launch(Kiosk.CAMERA));
            root.add(cam, 0.06f, 0.52f, 0.48f, 0.91f);
        } else {
            root.add(gallery, 0.29f, 0.52f, 0.71f, 0.91f);
        }

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
