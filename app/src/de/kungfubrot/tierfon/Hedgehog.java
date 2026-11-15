package de.kungfubrot.tierfon;

import android.graphics.Canvas;

import java.util.Random;

/** Der Igel. */
final class Hedgehog extends Animal {
    private static final int BROWN = 0xFF8B5E3C;
    private static final int SPIKE = 0xFF6B4A2B;
    private static final int CREAM = 0xFFF6DDB8;
    private static final int TAN = 0xFFE8C39A;

    @Override
    short[] makeClip(Random r) {
        return Synth.squeak(r);
    }

    @Override
    String id() {
        return "hedgehog";
    }

    @Override
    float mouthY() {
        return 160;
    }

    @Override
    void drawBackground(Canvas c, float w, float h) {
        meadow(c, w, h, 0xFFCDEBFF, 0xFF7FBF6A);
        // Wald im Hintergrund
        for (int i = 0; i < 6; i++) {
            float x = w * (0.02f + i * 0.2f);
            float top = h * (0.36f + (i % 2) * 0.05f);
            fill(c, x - 9, h * 0.56f, x + 9, h * 0.61f, 0xFF6B4A2B);
            tri(c, 0xFF3E8F4A, x - w * 0.1f, h * 0.58f, x + w * 0.1f, h * 0.58f, x, top);
            tri(c, 0xFF4FA35B, x - w * 0.075f, h * 0.5f, x + w * 0.075f, h * 0.5f, x, top - h * 0.03f);
        }
        // Pilze
        for (int i = 0; i < 2; i++) {
            float x = w * (0.12f + i * 0.78f);
            float y = h * (0.7f + i * 0.04f);
            fill(c, x - 9, y, x + 9, y + 34, 0xFFFFF3E6);
            oval(c, x, y, 34, 22, 0xFFE5484D);
            oval(c, x - 12, y - 6, 6, 5, 0xFFFFFFFF);
            oval(c, x + 12, y - 2, 5, 4, 0xFFFFFFFF);
        }
    }

    @Override
    void draw(Canvas c) {
        float y = -hop - attention * 14;
        float cy = 60 + y;

        oval(c, 0, 258, 235 - hop * 0.2f, 34, 0x26000000);
        // Stacheln: stellen sich beim Zuhören auf und zittern beim Sprechen
        float len = 52 + attention * 22 + mouth * 8;
        int n = 17;
        for (int i = 0; i < n; i++) {
            double a = Math.toRadians(188 + i * (164.0 / (n - 1)));
            double w = Math.toRadians(7);
            float jitter = (float) Math.sin(time * 14 + i * 1.7) * mouth * 5;
            tri(c, SPIKE,
                (float) Math.cos(a - w) * 228, cy + (float) Math.sin(a - w) * 194,
                (float) Math.cos(a + w) * 228, cy + (float) Math.sin(a + w) * 194,
                (float) Math.cos(a) * (236 + len + jitter),
                cy + (float) Math.sin(a) * (202 + len + jitter));
        }
        oval(c, 0, cy, 235, 200, BROWN);
        // Ohren, Gesicht und Füße
        oval(c, -152, -8 + y, 36, 36, TAN);
        oval(c, 152, -8 + y, 36, 36, TAN);
        oval(c, 0, 112 + y, 176, 148, CREAM);
        oval(c, -92, 252, 52, 22, TAN);
        oval(c, 92, 252, 52, 22, TAN);
        eye(c, -72, 62 + y, 30);
        eye(c, 72, 62 + y, 30);
        oval(c, -122, 132 + y, 28, 17, 0x8CFF808C);
        oval(c, 122, 132 + y, 28, 17, 0x8CFF808C);
        oval(c, 0, 116 + y, 24, 18, 0xFF222222);
        mouthShape(c, 0, 176 + y, 50, SPIKE);
    }
}
