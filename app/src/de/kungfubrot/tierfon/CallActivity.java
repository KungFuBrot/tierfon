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

/** Der „Videoanruf“: erst klingelt es, dann nimmt das Tier ab. */
public class CallActivity extends BaseActivity {
    private CallView view;
    private MicMeter mic;
    private boolean askedPermission;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FracLayout root = new FracLayout(this);
        view = new CallView(this);
        root.add(view, 0, 0, 1, 1);
        TileView hangup = new TileView(this, 0xFFE5484D, "hangup");
        hangup.round = true;
        hangup.setOnClickListener(v -> finish());
        root.add(hangup, 0.38f, 0.845f, 0.62f, 0.98f);
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

    private final class CallView extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Synth.Clip ring = new Synth.Clip(Synth.ring());
        private Animal animal;
        private float ringLeft = 1.8f;
        private float t;
        private long last;
        private boolean released;

        CallView(Context c) {
            super(c);
            ring.play(1f);
        }

        void release() {
            released = true;
            ring.release();
            if (animal != null) animal.release();
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_DOWN && animal != null) animal.poke();
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

            // Himmel, Sonne, Wiese
            c.drawColor(0xFFBFE6FF);
            p.setStyle(Paint.Style.FILL);
            p.setColor(0xFFFFE066);
            c.drawCircle(w * 0.82f, h * 0.1f, w * 0.09f, p);
            p.setColor(0xFF8FD18A);
            c.drawRect(0, h * 0.6f, w, h, p);

            if (animal == null) {
                // Klingeln: pulsierende Ringe
                ringLeft -= dt;
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(10);
                for (int i = 0; i < 3; i++) {
                    float ph = (t * 0.9f + i / 3f) % 1f;
                    p.setColor(0xFFFFFF | ((int) (255 * (1 - ph)) << 24));
                    c.drawCircle(w / 2, h / 2, w * (0.12f + 0.3f * ph), p);
                }
                p.setStyle(Paint.Style.FILL);
                p.setColor(0xFFFFFFFF);
                c.drawCircle(w / 2, h / 2, w * 0.12f, p);
                if (ringLeft <= 0) {
                    ring.stop();
                    animal = new Frog();
                    animal.start();
                }
            } else {
                MicMeter m = mic;
                if (m != null) m.paused = animal.isTalking();
                animal.update(dt, m != null && m.isVoice());
                c.save();
                c.translate(w / 2, h * 0.47f);
                c.scale(w / 720f, w / 720f);
                animal.draw(c);
                c.restore();
            }
            postInvalidateOnAnimation();
        }
    }
}
