package de.kungfubrot.tierfon;

import android.graphics.Canvas;

import java.util.Random;

/** Der Fuchs. */
final class Fox extends Animal {
    private static final int ORANGE = 0xFFF08A3C;
    private static final int BROWN = 0xFF5A3A28;
    private static final int CREAM = 0xFFFFF3E6;

    @Override
    short[] makeClip(Random r) {
        return Synth.yip(r);
    }

    @Override
    void draw(Canvas c) {
        float y = -hop - attention * 16;
        float wag = (float) Math.sin(time * 3.5) * (10 + attention * 12 + mouth * 12);

        oval(c, 0, 262, 215 - hop * 0.2f, 32, 0x26000000);
        // Buschiger Schwanz mit weißer Spitze
        oval(c, -225 + wag * 0.5f, 150 + y * 0.3f, 72, 112, ORANGE);
        oval(c, -238 + wag, 66 + y * 0.3f, 48, 54, CREAM);
        // Körper
        oval(c, 0, 160 + y * 0.5f, 160, 124, ORANGE);
        oval(c, 0, 186 + y * 0.5f, 96, 88, CREAM);
        oval(c, -86, 262, 52, 22, BROWN);
        oval(c, 86, 262, 52, 22, BROWN);
        // Spitze Ohren: stellen sich beim Zuhören auf
        float up = attention * 14;
        for (int sx = -1; sx <= 1; sx += 2) {
            tri(c, ORANGE, sx * 218, -112 + y, sx * 152, -292 + y - up, sx * 62, -172 + y);
            tri(c, BROWN, sx * 190, -140 + y, sx * 150, -248 + y - up, sx * 98, -176 + y);
        }
        // Kopf mit weißen Wangen
        oval(c, 0, -60 + y, 216, 160, ORANGE);
        oval(c, -118, 4 + y, 104, 84, CREAM);
        oval(c, 118, 4 + y, 104, 84, CREAM);
        oval(c, 0, 22 + y, 92, 70, CREAM);
        eye(c, -96, -84 + y, 34);
        eye(c, 96, -84 + y, 34);
        oval(c, 0, -14 + y, 25, 18, 0xFF222222);
        mouthShape(c, 0, 58 + y, 54, BROWN);
    }
}
