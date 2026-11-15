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
