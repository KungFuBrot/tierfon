package de.kungfubrot.tierfon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/** Batteriesymbol: zeigt, wie viel Spielzeit heute noch übrig ist. */
class BatteryView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            invalidate();
            postDelayed(this, 1000);
        }
    };

    BatteryView(Context c) {
        super(c);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(tick);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(tick);
    }

    @Override
    protected void onDraw(Canvas c) {
        float w = getWidth();
        float h = getHeight();
        float frac = Usage.remainingMs(getContext()) / (float) Usage.LIMIT_MS;
        float bodyW = w * 0.9f;
        float rad = h * 0.22f;

        // Gehäuse mit Pluspol
        p.setStyle(Paint.Style.FILL);
        p.setColor(0xFF4A4A4A);
        c.drawRoundRect(0, 0, bodyW, h, rad, rad, p);
        c.drawRoundRect(bodyW - 2, h * 0.3f, w, h * 0.7f, 4, 4, p);
        float m = h * 0.12f;
        p.setColor(0xFFFFFFFF);
        c.drawRoundRect(m, m, bodyW - m, h - m, rad * 0.6f, rad * 0.6f, p);

        // Füllung: grün, gelb, rot
        p.setColor(frac > 0.5f ? 0xFF4CAF50 : frac > 0.2f ? 0xFFF5B301 : 0xFFE5484D);
        float inner = bodyW - 4 * m;
        float fill = Math.max(frac > 0 ? h * 0.12f : 0, inner * frac);
        if (fill > 0) {
            c.drawRoundRect(2 * m, 2 * m, 2 * m + fill, h - 2 * m, rad * 0.4f, rad * 0.4f, p);
        }
    }
}
