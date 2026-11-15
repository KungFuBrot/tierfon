package de.kungfubrot.tierfon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/** Ordnet Kinder nach Bruchteilen der Bildschirmgröße an (0 bis 1), passt also auf jede Auflösung. */
class FracLayout extends ViewGroup {
    FracLayout(Context c) {
        super(c);
    }

    void add(View v, float x0, float y0, float x1, float y1) {
        v.setTag(new float[] {x0, y0, x1, y1});
        addView(v);
    }

    @Override
    protected void onMeasure(int wSpec, int hSpec) {
        int w = MeasureSpec.getSize(wSpec);
        int h = MeasureSpec.getSize(hSpec);
        setMeasuredDimension(w, h);
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            float[] f = (float[]) v.getTag();
            v.measure(
                MeasureSpec.makeMeasureSpec(Math.round((f[2] - f[0]) * w), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(Math.round((f[3] - f[1]) * h), MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int w = r - l;
        int h = b - t;
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            float[] f = (float[]) v.getTag();
            v.layout(Math.round(f[0] * w), Math.round(f[1] * h),
                Math.round(f[2] * w), Math.round(f[3] * h));
        }
    }
}
