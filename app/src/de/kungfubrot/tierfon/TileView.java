package de.kungfubrot.tierfon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

/** Große, bunte Schaltfläche mit gezeichnetem Symbol. Kinder müssen nichts lesen können. */
class TileView extends View {
    private static final int WHITE = 0xFFFFFFFF;

    int color;
    String icon;
    boolean round;

    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF r = new RectF();
    private final Path path = new Path();

    TileView(Context c, int color, String icon) {
        super(c);
        this.color = color;
        this.icon = icon;
        setClickable(true);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas c) {
        float inset = isPressed() && isEnabled() ? 8 : 0;
        r.set(inset, inset, getWidth() - inset, getHeight() - inset);
        float s = Math.min(r.width(), r.height());
        p.setStyle(Paint.Style.FILL);
        p.setColor(isEnabled() ? color : 0xFFC9CED6);
        float rad = s * (round ? 0.5f : 0.2f);
        c.drawRoundRect(r, rad, rad, p);

        // Symbole sind für ein Feld von 200 x 200 gezeichnet, Mittelpunkt bei (0, 0).
        c.save();
        c.translate(r.centerX(), r.centerY());
        c.scale(s / 200f, s / 200f);
        drawIcon(c);
        c.restore();
    }

    private void poly(Canvas c, float... xy) {
        path.rewind();
        path.moveTo(xy[0], xy[1]);
        for (int i = 2; i < xy.length; i += 2) path.lineTo(xy[i], xy[i + 1]);
        path.close();
        c.drawPath(path, p);
    }

    private void drawIcon(Canvas c) {
        p.setColor(WHITE);
        switch (icon) {
            case "frog":
                p.setColor(0xFF5CB85C);
                c.drawCircle(0, 12, 58, p);
                for (int sx = -1; sx <= 1; sx += 2) {
                    p.setColor(0xFF5CB85C);
                    c.drawCircle(sx * 34, -36, 26, p);
                    p.setColor(WHITE);
                    c.drawCircle(sx * 34, -36, 18, p);
                    p.setColor(0xFF222222);
                    c.drawCircle(sx * 34, -36, 9, p);
                }
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(5);
                p.setStrokeCap(Paint.Cap.ROUND);
                p.setColor(0xFF2B6B2E);
                c.drawArc(-36, -38, 36, 34, 25, 130, false, p);
                p.setStyle(Paint.Style.FILL);
                break;
            case "raccoon":
                for (int sx = -1; sx <= 1; sx += 2) {
                    p.setColor(0xFF9AA0A6);
                    c.drawCircle(sx * 42, -42, 20, p);
                    p.setColor(0xFF4A4F57);
                    c.drawCircle(sx * 42, -41, 10, p);
                }
                p.setColor(0xFF9AA0A6);
                c.drawOval(-64, -44, 64, 62, p);
                p.setColor(0xFF4A4F57);
                c.drawOval(-56, -18, -4, 18, p);
                c.drawOval(4, -18, 56, 18, p);
                p.setColor(WHITE);
                c.drawOval(-26, 12, 26, 50, p);
                c.drawCircle(-30, 0, 10, p);
                c.drawCircle(30, 0, 10, p);
                p.setColor(0xFF222222);
                c.drawCircle(-30, 0, 5, p);
                c.drawCircle(30, 0, 5, p);
                c.drawCircle(0, 22, 7, p);
                break;
            case "hedgehog":
                p.setColor(0xFF6B4A2B);
                for (int i = 0; i < 9; i++) {
                    double a = Math.toRadians(190 + i * 20);
                    double w = Math.toRadians(11);
                    poly(c, (float) Math.cos(a - w) * 58, (float) Math.sin(a - w) * 58,
                        (float) Math.cos(a + w) * 58, (float) Math.sin(a + w) * 58,
                        (float) Math.cos(a) * 80, (float) Math.sin(a) * 80);
                }
                p.setColor(0xFF8B5E3C);
                c.drawCircle(0, 0, 62, p);
                p.setColor(0xFFF6DDB8);
                c.drawOval(-46, -12, 46, 66, p);
                p.setColor(0xFF222222);
                c.drawCircle(-18, 14, 6, p);
                c.drawCircle(18, 14, 6, p);
                c.drawCircle(0, 32, 8, p);
                break;
            case "fox":
                p.setColor(0xFFF08A3C);
                poly(c, -62, -14, -46, -76, -12, -40);
                poly(c, 62, -14, 46, -76, 12, -40);
                c.drawOval(-64, -44, 64, 60, p);
                p.setColor(0xFFFFF3E6);
                c.drawOval(-62, 6, -4, 58, p);
                c.drawOval(4, 6, 62, 58, p);
                c.drawOval(-26, 12, 26, 60, p);
                p.setColor(0xFF222222);
                c.drawCircle(-26, -6, 7, p);
                c.drawCircle(26, -6, 7, p);
                c.drawCircle(0, 26, 8, p);
                break;
            case "note":
                c.drawCircle(-34, 40, 22, p);
                c.drawCircle(36, 26, 22, p);
                c.drawRect(-22, -52, -12, 40, p);
                c.drawRect(48, -66, 58, 26, p);
                poly(c, -22, -52, 58, -66, 58, -42, -22, -28);
                break;
            case "camera":
                c.drawRoundRect(-66, -38, 66, 50, 14, 14, p);
                c.drawRoundRect(-30, -56, 14, -30, 8, 8, p);
                p.setColor(isEnabled() ? color : 0xFFC9CED6);
                c.drawCircle(0, 6, 30, p);
                p.setColor(WHITE);
                c.drawCircle(0, 6, 16, p);
                break;
            case "gallery":
                c.drawRoundRect(-66, -52, 66, 52, 14, 14, p);
                p.setColor(isEnabled() ? color : 0xFFC9CED6);
                c.drawCircle(-30, -18, 13, p);
                poly(c, -54, 40, -14, -2, 10, 22, 30, 4, 54, 40);
                break;
            case "back":
                poly(c, -46, 0, 14, -52, 14, -20, 50, -20, 50, 20, 14, 20, 14, 52);
                break;
            case "hangup":
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(26);
                p.setStrokeCap(Paint.Cap.ROUND);
                c.drawArc(-70, -10, 70, 130, 215, 110, false, p);
                p.setStyle(Paint.Style.FILL);
                break;
            case "play":
                poly(c, -34, -52, 54, 0, -34, 52);
                break;
            case "pause":
                c.drawRoundRect(-42, -50, -12, 50, 6, 6, p);
                c.drawRoundRect(12, -50, 42, 50, 6, 6, p);
                break;
            case "next":
                poly(c, -46, -46, 22, 0, -46, 46);
                c.drawRoundRect(26, -46, 46, 46, 6, 6, p);
                break;
            case "prev":
                poly(c, 46, -46, -22, 0, 46, 46);
                c.drawRoundRect(-46, -46, -26, 46, 6, 6, p);
                break;
            case "unknown":
                p.setTextSize(120);
                p.setTextAlign(Paint.Align.CENTER);
                p.setFakeBoldText(true);
                c.drawText("?", 0, 42, p);
                break;
            default:
                break;
        }
    }
}
