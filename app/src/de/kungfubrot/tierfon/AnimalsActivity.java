package de.kungfubrot.tierfon;

import android.content.Intent;
import android.os.Bundle;

/** Auswahl, welches Tier angerufen wird. */
public class AnimalsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        FracLayout root = new FracLayout(this);
        root.setBackgroundColor(0xFFE3F6FF);

        TileView back = new TileView(this, 0xFF7A8794, "back");
        back.round = true;
        back.setOnClickListener(v -> finish());
        root.add(back, 0.04f, 0.02f, 0.24f, 0.1325f);

        TileView frog = new TileView(this, 0xFFBDEAA8, "frog");
        frog.setOnClickListener(v ->
            startActivity(new Intent(this, CallActivity.class).putExtra("animal", "frog")));
        root.add(frog, 0.06f, 0.18f, 0.48f, 0.52f);

        // Igel, Waschbär und Brontosaurus kommen in Etappe 2.
        float[][] cells = {{0.52f, 0.18f}, {0.06f, 0.56f}, {0.52f, 0.56f}};
        for (float[] cell : cells) {
            TileView t = new TileView(this, 0xFFFFFFFF, "unknown");
            t.setEnabled(false);
            root.add(t, cell[0], cell[1], cell[0] + 0.42f, cell[1] + 0.34f);
        }
        setContentView(root);
    }
}
