package de.kungfubrot.tierfon;

import android.graphics.Canvas;

import java.util.Random;

/** Der Brachiosaurus: langer Hals, der sich beim Zuhören reckt. */
final class Brachio extends Animal {
    private static final int TEAL = 0xFF5BB8A6;
    private static final int DARK = 0xFF3E8F80;
    private static final int BELLY = 0xFFCFEFE6;

    @Override
    short[] makeClip(Random r) {
        return Synth.rumble(r);
    }

    @Override
    String id() {
        return "brachio";
    }

    @Override
    float mouthY() {
        return -75;
    }

    @Override
    void drawBackground(Canvas c, float w, float h) {
        meadow(c, w, h, 0xFFFFE3B8, 0xFFB8D98A);
        // Vulkan mit aufsteigendem Rauch
        tri(c, 0xFF9C6B4A, w * 0.5f, h * 0.6f, w * 1.1f, h * 0.6f, w * 0.8f, h * 0.27f);
        tri(c, 0xFFE8590C, w * 0.755f, h * 0.32f, w * 0.845f, h * 0.32f, w * 0.8f, h * 0.27f);
        for (int i = 0; i < 3; i++) {
            float rise = (time * 28 + i * 55) % 165;
            oval(c, w * 0.8f + (float) Math.sin(time + i) * 14, h * 0.25f - rise,
                18 + rise * 0.2f, 14 + rise * 0.16f, ((int) (150 * (1 - rise / 165)) << 24) | 0x888888);
        }
        // Farne
        for (int i = 0; i < 4; i++) {
            float x = w * (0.08f + i * 0.085f);
            tri(c, 0xFF5E9E4A, x - 26, h * 0.62f, x + 26, h * 0.62f, x - 12, h * 0.53f);
            tri(c, 0xFF79B95E, x - 20, h * 0.62f, x + 30, h * 0.62f, x + 16, h * 0.55f);
        }
    }

    @Override
    void draw(Canvas c) {
        float by = -hop;
        float sway = (float) Math.sin(time * 1.2) * 14;
        float hx = sway;
        float hy = -150 - attention * 30 + by;

        oval(c, 70, 296, 250 - hop * 0.2f, 28, 0x26000000);
        tri(c, TEAL, 230, 205 + by, 352, 276 + by, 236, 286 + by);
        oval(c, -34, 282 + by * 0.3f, 48, 62, DARK);
        oval(c, 172, 282 + by * 0.3f, 48, 62, DARK);
        oval(c, 80, 215 + by, 215, 105, TEAL);
        oval(c, 70, 250 + by, 150, 62, BELLY);
        oval(c, 150, 158 + by, 30, 20, DARK);
        oval(c, 218, 200 + by, 24, 16, DARK);
        oval(c, 86, 142 + by, 22, 15, DARK);
        // Hals und Kopf
        curve(c, TEAL, 118, -10, 190 + by, -78, 30 + by, hx, hy + 56);
        oval(c, hx, hy - 76, 58, 46, TEAL);
        oval(c, hx, hy, 125, 92, TEAL);
        oval(c, hx, hy + 44, 138, 70, TEAL);
        oval(c, hx - 30, hy + 8, 7, 5, DARK);
        oval(c, hx + 30, hy + 8, 7, 5, DARK);
        eye(c, hx - 64, hy - 28, 30);
        eye(c, hx + 64, hy - 28, 30);
        oval(c, hx - 104, hy + 40, 26, 16, 0x8CFF808C);
        oval(c, hx + 104, hy + 40, 26, 16, 0x8CFF808C);
        mouthShape(c, hx, hy + 96, 60, DARK);
    }
}
