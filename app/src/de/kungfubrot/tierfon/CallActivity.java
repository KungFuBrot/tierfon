package de.kungfubrot.tierfon;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Der „Videoanruf“: erst klingelt es, dann nimmt das Tier ab. Das Kind kann mit dem Tier
 * sprechen, es antippen, es füttern und es mit einem lauten Ruf erschrecken.
 */
public class CallActivity extends BaseActivity {
    private CallView view;
    private MicMeter mic;
    private boolean askedPermission;
    private String animalId;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        animalId = String.valueOf(getIntent().getStringExtra("animal"));
        FracLayout root = new FracLayout(this);
        view = new CallView(this);
        root.add(view, 0, 0, 1, 1);

        TileView hangup = new TileView(this, 0xFFE5484D, "hangup");
        hangup.round = true;
        hangup.setOnClickListener(v -> finish());
        root.add(hangup, 0.38f, 0.845f, 0.62f, 0.98f);

        TileView treat = new TileView(this, 0xFFFFFFFF, Treats.forAnimal(animalId));
        treat.round = true;
        treat.setOnClickListener(v -> view.throwTreat());
        root.add(treat, 0.07f, 0.86f, 0.28f, 0.978f);

        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            mic = new MicMeter();
            mic.start();
        } else if (!askedPermission) {
            askedPermission = true;
            requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mic != null) {
            mic.shutdown();
            mic = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Bildschirm aus oder App verlassen: Der Anruf ist vorbei.
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view.release();
    }

    private static Animal create(String id) {
        switch (id) {
            case "hedgehog":
                return new Hedgehog();
            case "brachio":
                return new Brachio();
            case "ptero":
                return new Ptero();
            default:
                return new Frog();
        }
    }

    private final class CallView extends View {
        private static final int HEART = 0;
        private static final int NOTE = 1;
        private static final float TREAT_SECONDS = 0.7f;

        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Random rng = new Random();
        private final Synth.Clip ring = new Synth.Clip(Synth.ring());
        /** Schwebende Herzen und Noten: x, y, vx, vy, Restzeit, Art. */
        private final List<float[]> particles = new ArrayList<>();
        private final Animal animal = create(animalId);
        private boolean answered;
        private float ringLeft = 1.8f;
        private float t;
        private float treatT = -1;
        private float startleCooldown;
        private long last;
        private boolean released;

        CallView(Context c) {
            super(c);
            ring.play(1f);
        }

        void release() {
            released = true;
            ring.release();
            animal.release();
        }

        void throwTreat() {
            if (answered && treatT < 0) treatT = 0;
        }

        private void spawn(int kind, float x, float y, int count) {
            for (int i = 0; i < count; i++) {
                particles.add(new float[] {
                    x + (rng.nextFloat() - 0.5f) * 120, y + (rng.nextFloat() - 0.5f) * 40,
                    (rng.nextFloat() - 0.5f) * 90, -120 - rng.nextFloat() * 90,
                    1.4f + rng.nextFloat() * 0.6f, kind});
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_DOWN && answered) {
                animal.poke();
                spawn(HEART, e.getX(), e.getY(), 2);
            }
            return true;
        }

        @Override
        protected void onDraw(Canvas c) {
            if (released) return;
            long now = System.nanoTime();
            float dt = last == 0 ? 0 : Math.min(0.1f, (now - last) / 1e9f);
            last = now;
            t += dt;
            float w = getWidth();
            float h = getHeight();
            float scale = w / 720f;
            float cx = w / 2;
            float cy = h * 0.47f;
            float mouthY = cy + animal.mouthY() * scale;

            animal.time = answered ? animal.time : t;
            animal.drawBackground(c, w, h);

            if (!answered) {
                // Klingeln: pulsierende Ringe
                ringLeft -= dt;
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(10);
                for (int i = 0; i < 3; i++) {
                    float ph = (t * 0.9f + i / 3f) % 1f;
                    p.setColor(0xFFFFFF | ((int) (255 * (1 - ph)) << 24));
                    c.drawCircle(cx, h / 2, w * (0.12f + 0.3f * ph), p);
                }
                p.setStyle(Paint.Style.FILL);
                p.setColor(0xFFFFFFFF);
                c.drawCircle(cx, h / 2, w * 0.12f, p);
                if (ringLeft <= 0) {
                    ring.stop();
                    answered = true;
                    animal.start();
                }
                postInvalidateOnAnimation();
                return;
            }

            MicMeter m = mic;
            if (m != null) m.paused = animal.isTalking();
            startleCooldown -= dt;
            if (m != null && !animal.isTalking() && m.isLoud() && startleCooldown <= 0) {
                // Sehr lauter Ruf: das Tier erschrickt.
                animal.startle();
                spawn(NOTE, cx, mouthY - 60 * scale, 4);
                startleCooldown = 3;
            }
            animal.update(dt, m != null && m.isVoice());
            if (animal.syllableStarted) {
                animal.syllableStarted = false;
                spawn(NOTE, cx + (rng.nextBoolean() ? 150 : -150) * scale, mouthY, 1);
            }

            c.save();
            c.translate(cx, cy);
            c.scale(scale, scale);
            animal.draw(c);
            c.restore();

            // Leckerbissen fliegt im Bogen vom Knopf zum Mund
            if (treatT >= 0) {
                treatT += dt / TREAT_SECONDS;
                float u = Math.min(1, treatT);
                float x = w * 0.175f + (cx - w * 0.175f) * u;
                float y = h * 0.92f + (mouthY - h * 0.92f) * u - (float) Math.sin(Math.PI * u) * h * 0.18f;
                c.save();
                c.translate(x, y);
                c.rotate(u * 360);
                c.scale(scale * 0.55f * (1 - u * 0.4f), scale * 0.55f * (1 - u * 0.4f));
                Treats.draw(c, p, Treats.forAnimal(animalId));
                c.restore();
                if (treatT >= 1) {
                    treatT = -1;
                    animal.eat();
                    spawn(HEART, cx, mouthY - 40 * scale, 6);
                }
            }

            drawParticles(c, dt, scale);
            postInvalidateOnAnimation();
        }

        private void drawParticles(Canvas c, float dt, float scale) {
            p.setStyle(Paint.Style.FILL);
            for (Iterator<float[]> it = particles.iterator(); it.hasNext(); ) {
                float[] q = it.next();
                q[4] -= dt;
                if (q[4] <= 0) {
                    it.remove();
                    continue;
                }
                q[0] += q[2] * dt + (float) Math.sin(t * 3 + q[1] * 0.02f) * 20 * dt;
                q[1] += q[3] * dt;
                int alpha = (int) (255 * Math.min(1, q[4])) << 24;
                float s = 16 * scale;
                if (q[5] == HEART) {
                    p.setColor(alpha | 0xFF5C8A);
                    c.drawCircle(q[0] - s * 0.5f, q[1], s * 0.62f, p);
                    c.drawCircle(q[0] + s * 0.5f, q[1], s * 0.62f, p);
                    c.save();
                    c.translate(q[0], q[1] + s * 0.45f);
                    c.rotate(45);
                    c.drawRect(-s * 0.72f, -s * 0.72f, s * 0.72f, s * 0.72f, p);
                    c.restore();
                } else {
                    p.setColor(alpha | 0x4A5BD4);
                    c.drawCircle(q[0], q[1], s * 0.7f, p);
                    c.drawRect(q[0] + s * 0.4f, q[1] - s * 2.2f, q[0] + s * 0.7f, q[1], p);
                    c.drawRect(q[0] + s * 0.4f, q[1] - s * 2.2f, q[0] + s * 1.5f, q[1] - s * 1.7f, p);
                }
            }
        }
    }
}
