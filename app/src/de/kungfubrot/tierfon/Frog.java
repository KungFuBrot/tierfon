package de.kungfubrot.tierfon;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

/** Der Frosch. */
final class Frog extends Animal {
    private static final int GREEN = 0xFF5CB85C;
    private static final int DARK = 0xFF3D8B40;
    private static final int BELLY = 0xFFD9F2B4;
    private static final int CHEEK = 0x8CFF808C;

    @Override
    short[] makeClip(Random r) {
        return Synth.croak(r);
    }

    @Override
    void draw(Canvas c) {
        float breathe = (float) Math.sin(time * 2) * 6;
        float y = -hop - attention * 18;

        oval(c, 0, 250, 240 - hop * 0.2f, 36, 0x26000000);
        // Hinterbeine
        oval(c, -195, 215 + y * 0.3f, 95, 40, DARK);
        oval(c, 195, 215 + y * 0.3f, 95, 40, DARK);
        // Körper und Bauch
        oval(c, 0, 60 + y, 235 + breathe * 0.5f, 190 - breathe * 0.5f, GREEN);
        oval(c, 0, 135 + y, 165, 105, BELLY);
        // Schallblase bläht sich beim Quaken auf
        if (mouth > 0.02f) oval(c, 0, 110 + y, 110 + mouth * 50, 50 + mouth * 45, 0xFFF2FBD8);
        // Vorderfüße
        oval(c, -95, 238 + y * 0.3f, 60, 26, DARK);
        oval(c, 95, 238 + y * 0.3f, 60, 26, DARK);

        // Augen: werden beim Zuhören größer
        float open = 1 - blink;
        for (int sx = -1; sx <= 1; sx += 2) {
            float ex = sx * 115;
            float ey = -115 + y;
            oval(c, ex, ey, 82, 82, GREEN);
            float r = 58 + attention * 6;
            oval(c, ex, ey, r, Math.max(4, r * open), 0xFFFFFFFF);
            if (open > 0.3f) {
                float lx = (float) Math.sin(time * 0.7) * 8;
                float ly = (float) Math.cos(time * 0.5) * 4 - attention * 8;
                float pr = 26 + attention * 6;
                oval(c, ex + lx, ey + ly, pr, pr * open, 0xFF222222);
                oval(c, ex + lx - 8, ey + ly - 10, 8, 8 * open, 0xFFFFFFFF);
            }
        }

        // Wangen und Nasenlöcher
        oval(c, -170, 10 + y, 30, 18, CHEEK);
        oval(c, 170, 10 + y, 30, 18, CHEEK);
        oval(c, -22, -50 + y, 6, 4, DARK);
        oval(c, 22, -50 + y, 6, 4, DARK);

        // Mund: Lächeln oder offen beim Quaken
        if (mouth < 0.05f) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(9);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(0xFF2B6230);
            c.drawArc(-140, -260 + y, 140, 20 + y, 25, 130, false, paint);
        } else {
            oval(c, 0, -5 + y, 135, 16 + mouth * 50, 0xFF7A1F2B);
            oval(c, 0, 5 + y + mouth * 30, 70, 8 + mouth * 20, 0xFFFF8FA3);
        }
    }
}
