package de.kungfubrot.tierfon;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.Random;

/**
 * Gemeinsames Verhalten aller Tiere: zuhören, wenn das Kind spricht, und danach zurückblabbern.
 * Ein Tier leitet davon ab und liefert nur sein Aussehen (draw) und seine Stimme (makeClip).
 */
abstract class Animal {
    static final int IDLE = 0;
    static final int LISTEN = 1;
    static final int TALK = 2;

    int state = IDLE;
    /** Werte für die Zeichnung, jeweils 0 bis 1. */
    float mouth;
    float attention;
    float blink;
    /** Sprunghöhe in Pixeln. */
    float hop;
    float time;

    protected final Random rng = new Random();
    protected final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final Synth.Clip[] clips = new Synth.Clip[6];
    private float heard;
    private float listenTime;
    private float silence;
    private float talkLeft;
    private float syllableLeft;
    private float gap;
    private float cooldown;
    private float idleTimer = 7;
    private float blinkTimer = 2;
    private float hopSpeed;

    abstract short[] makeClip(Random r);

    /** Zeichnet das Tier für eine Breite von 720, Mittelpunkt bei (0, 0). */
    abstract void draw(Canvas c);

    void start() {
        for (int i = 0; i < clips.length; i++) clips[i] = new Synth.Clip(makeClip(rng));
        talk(1.2f);  // Begrüßung beim Abnehmen
    }

    void release() {
        for (Synth.Clip c : clips) {
            if (c != null) c.release();
        }
    }

    boolean isTalking() {
        return state == TALK;
    }

    void talk(float seconds) {
        state = TALK;
        talkLeft = seconds;
        syllableLeft = 0;
        gap = 0.12f;
        cooldown = 0.45f;
    }

    /** Antippen: kleiner Hüpfer und ein kurzer Laut. */
    void poke() {
        if (hop <= 0) hopSpeed = 900;
        if (state != TALK) talk(0.5f);
    }

    private float range(float a, float b) {
        return a + rng.nextFloat() * (b - a);
    }

    void update(float dt, boolean voice) {
        time += dt;
        switch (state) {
            case IDLE:
                heard = voice ? heard + dt : Math.max(0, heard - dt);
                idleTimer -= dt;
                if (heard > 0.15f) {
                    state = LISTEN;
                    heard = 0;
                    listenTime = 0;
                    silence = 0;
                } else if (idleTimer <= 0) {
                    talk(range(0.5f, 1.1f));
                }
                break;
            case LISTEN:
                listenTime += dt;
                silence = voice ? 0 : silence + dt;
                if (silence > 0.8f || listenTime > 8) {
                    talk(Math.max(1f, Math.min(3.5f, listenTime * 0.8f)));
                }
                break;
            default:
                talkLeft -= dt;
                if (syllableLeft > 0) {
                    syllableLeft -= dt;
                } else if (talkLeft > 0) {
                    gap -= dt;
                    if (gap <= 0) {
                        syllableLeft = clips[rng.nextInt(clips.length)].play(range(0.85f, 1.25f));
                        gap = range(0.04f, 0.18f);
                    }
                } else {
                    cooldown -= dt;
                    if (cooldown <= 0) {
                        state = IDLE;
                        idleTimer = range(6, 11);
                    }
                }
                break;
        }

        boolean speaking = state == TALK && syllableLeft > 0;
        float mouthTarget = speaking ? 0.35f + 0.65f * (float) Math.abs(Math.sin(time * 20)) : 0;
        mouth = toward(mouth, mouthTarget, dt * 10);
        attention = toward(attention, state == LISTEN ? 1 : 0, dt * 5);

        blinkTimer -= dt;
        if (blinkTimer <= 0) blinkTimer = range(2, 5);
        blink = Math.max(0, Math.min(1, 1 - Math.abs(blinkTimer - 0.08f) / 0.08f));

        if (hop > 0 || hopSpeed > 0) {
            hopSpeed -= 2600 * dt;
            hop = Math.max(0, hop + hopSpeed * dt);
            if (hop <= 0) hopSpeed = 0;
        }
    }

    private static float toward(float v, float target, float step) {
        return v < target ? Math.min(target, v + step) : Math.max(target, v - step);
    }

    protected void tri(Canvas c, int color, float x0, float y0, float x1, float y1,
            float x2, float y2) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        path.rewind();
        path.moveTo(x0, y0);
        path.lineTo(x1, y1);
        path.lineTo(x2, y2);
        path.close();
        c.drawPath(path, paint);
    }

    /** Ein Auge, das blinzelt, sich umsieht und beim Zuhören größer wird. */
    protected void eye(Canvas c, float x, float y, float r) {
        float open = 1 - blink;
        float rr = r * (1 + attention * 0.1f);
        oval(c, x, y, rr, Math.max(3, rr * open), 0xFFFFFFFF);
        if (open > 0.3f) {
            float lx = (float) Math.sin(time * 0.7) * r * 0.14f;
            float ly = (float) Math.cos(time * 0.5) * r * 0.07f - attention * r * 0.14f;
            float pr = r * (0.48f + attention * 0.1f);
            oval(c, x + lx, y + ly, pr, pr * open, 0xFF222222);
            oval(c, x + lx - pr * 0.3f, y + ly - pr * 0.4f, pr * 0.3f, pr * 0.3f * open, 0xFFFFFFFF);
        }
    }

    /** Mund mit der Unterkante bei y: Lächeln, oder offen beim Sprechen. */
    protected void mouthShape(Canvas c, float x, float y, float w, int lineColor) {
        if (mouth < 0.05f) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(6, w * 0.12f));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(lineColor);
            c.drawArc(x - w, y - 2 * w, x + w, y, 30, 120, false, paint);
        } else {
            oval(c, x, y - w * 0.3f, w * 0.75f, w * 0.14f + mouth * w * 0.42f, 0xFF7A1F2B);
            oval(c, x, y - w * 0.2f + mouth * w * 0.22f, w * 0.4f, w * 0.06f + mouth * w * 0.16f,
                0xFFFF8FA3);
        }
    }

    protected void oval(Canvas c, float cx, float cy, float rx, float ry, int color) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        c.drawOval(cx - rx, cy - ry, cx + rx, cy + ry, paint);
    }
}
