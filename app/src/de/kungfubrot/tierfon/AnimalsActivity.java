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

        String[] ids = {"frog", "hedgehog", "brachio", "ptero"};
        int[] colors = {0xFFBDEAA8, 0xFFF3D9B5, 0xFFCDEFE6, 0xFFE0D6F7};
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
            TileView t = new TileView(this, colors[i], id);
            t.setOnClickListener(v ->
                startActivity(new Intent(this, CallActivity.class).putExtra("animal", id)));
            float x = i % 2 == 0 ? 0.06f : 0.52f;
            float y = i < 2 ? 0.18f : 0.56f;
            root.add(t, x, y, x + 0.42f, y + 0.34f);
        }
        setContentView(root);
    }
}
