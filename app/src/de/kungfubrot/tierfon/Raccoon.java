package de.kungfubrot.tierfon;

import android.graphics.Canvas;

import java.util.Random;

/** Der Waschbär. */
final class Raccoon extends Animal {
    private static final int GREY = 0xFF9AA0A6;
    private static final int DARK = 0xFF4A4F57;
    private static final int LIGHT = 0xFFD5D9DE;
    private static final int WHITE = 0xFFF6F6F6;

    @Override
    short[] makeClip(Random r) {
        return Synth.chitter(r);
    }

    @Override
    void draw(Canvas c) {
        float y = -hop - attention * 16;
        float wag = (float) Math.sin(time * 3) * (8 + attention * 10 + mouth * 10);

        oval(c, 0, 262, 220 - hop * 0.2f, 32, 0x26000000);
        // Geringelter Schwanz
        oval(c, 215 + wag * 0.3f, 170 + y * 0.3f, 62, 62, DARK);
        oval(c, 255 + wag * 0.6f, 105 + y * 0.3f, 56, 56, GREY);
        oval(c, 275 + wag, 40 + y * 0.3f, 48, 48, DARK);
        // Körper
        oval(c, 0, 150 + y * 0.5f, 172, 132, GREY);
        oval(c, 0, 172 + y * 0.5f, 112, 96, LIGHT);
        oval(c, -150, 150 + y * 0.5f, 36, 58, DARK);
        oval(c, 150, 150 + y * 0.5f, 36, 58, DARK);
        oval(c, -88, 262, 56, 22, DARK);
        oval(c, 88, 262, 56, 22, DARK);
        // Ohren
        for (int sx = -1; sx <= 1; sx += 2) {
            oval(c, sx * 152, -188 + y, 56, 56, GREY);
            oval(c, sx * 152, -184 + y, 30, 30, DARK);
        }
        // Kopf mit Maske
        oval(c, 0, -60 + y, 216, 166, GREY);
        oval(c, -104, -78 + y, 96, 62, DARK);
        oval(c, 104, -78 + y, 96, 62, DARK);
        oval(c, 0, 12 + y, 96, 72, WHITE);
        eye(c, -100, -78 + y, 36);
        eye(c, 100, -78 + y, 36);
        oval(c, 0, -22 + y, 26, 18, 0xFF222222);
        mouthShape(c, 0, 52 + y, 56, DARK);
    }
}
