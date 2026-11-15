package de.kungfubrot.tierfon;

import android.graphics.Canvas;

import java.util.Random;

/** Der Pterodactylus: schwebt in der Luft und schlägt mit den Flügeln. */
final class Ptero extends Animal {
    private static final int PURPLE = 0xFF9B7EDE;
    private static final int DARK = 0xFF6F55B5;
    private static final int LIGHT = 0xFFDDD2F7;
    private static final int BEAK = 0xFFFFB84D;
    private static final int BEAK_DARK = 0xFFE8963A;

    @Override
    short[] makeClip(Random r) {
        return Synth.screech(r);
    }

    @Override
    String id() {
        return "ptero";
    }

    @Override
    float mouthY() {
        return 50;
    }

    @Override
    void drawBackground(Canvas c, float w, float h) {
        // Hoch am Himmel: nur Wolken und Berggipfel
        fill(c, 0, 0, w, h, 0xFF9FD4FF);
        oval(c, w * 0.82f, h * 0.1f, w * 0.09f, w * 0.09f, 0xFFFFE066);
        for (int i = 0; i < 5; i++) {
            float x = ((time * (10 + i * 5) + i * 190) % (w + 300)) - 150;
            cloud(c, x, h * (0.08f + i * 0.14f), 0.7f + (i % 3) * 0.25f);
        }
        for (int i = 0; i < 4; i++) {
            float x = w * (0.1f + i * 0.27f);
            float top = h * (0.74f + (i % 2) * 0.05f);
            tri(c, 0xFF8D99AE, x - w * 0.22f, h, x + w * 0.22f, h, x, top);
            tri(c, 0xFFFFFFFF, x - w * 0.05f, top + (h - top) * 0.23f,
                x + w * 0.05f, top + (h - top) * 0.23f, x, top);
        }
    }

    @Override
    void draw(Canvas c) {
        float y = -hop + (float) Math.sin(time * 2.2) * 14 - attention * 14;
        float flap = (float) Math.sin(time * (6 + mouth * 4)) * 75;

        // Flügel
        for (int sx = -1; sx <= 1; sx += 2) {
            tri(c, LIGHT, sx * 60, 70 + y, sx * 340, 20 + y - flap, sx * 80, 235 + y);
            curve(c, DARK, 16, sx * 60, 76 + y, sx * 200, 30 + y - flap * 0.6f,
                sx * 340, 20 + y - flap);
            oval(c, sx * 40, 276 + y, 18, 30, BEAK);
        }
        oval(c, 0, 160 + y, 95, 120, PURPLE);
        oval(c, 0, 182 + y, 60, 85, LIGHT);
        // Kopf mit Kamm und Schnabel
        tri(c, DARK, 20, -150 + y, 190, -290 + y, 105, -95 + y);
        oval(c, 0, -70 + y, 125, 100, PURPLE);
        float m = mouth * 50;
        tri(c, BEAK_DARK, -42, -5 + y + m * 0.3f, 42, -5 + y + m * 0.3f, 0, 105 + y + m);
        if (mouth > 0.05f) oval(c, 0, 8 + y + m * 0.3f, 32, 4 + m * 0.35f, 0xFF7A1F2B);
        tri(c, BEAK, -58, -30 + y, 58, -30 + y, 0, 120 + y);
        eye(c, -58, -92 + y, 27);
        eye(c, 58, -92 + y, 27);
    }
}
