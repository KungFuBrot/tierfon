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
            case "brachio":
                p.setColor(0xFF5BB8A6);
                c.drawOval(-30, 8, 74, 62, p);
                c.drawRect(-8, 44, 8, 76, p);
                c.drawRect(44, 44, 60, 76, p);
                poly(c, 66, 22, 94, 52, 66, 50);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(22);
                p.setStrokeCap(Paint.Cap.ROUND);
                path.rewind();
                path.moveTo(-14, 26);
                path.quadTo(-30, -20, -40, -50);
                c.drawPath(path, p);
                p.setStyle(Paint.Style.FILL);
                c.drawOval(-74, -76, -20, -40, p);
                p.setColor(WHITE);
                c.drawCircle(-50, -61, 7, p);
                p.setColor(0xFF222222);
                c.drawCircle(-51, -61, 3.5f, p);
                break;
            case "ptero":
                p.setColor(0xFFB9A6EE);
                poly(c, -10, 0, -86, -30, -16, 46);
                poly(c, 10, 0, 86, -30, 16, 46);
                p.setColor(0xFF9B7EDE);
                c.drawOval(-20, -6, 20, 64, p);
                p.setColor(0xFF6F55B5);
                poly(c, 4, -50, 54, -86, 26, -34);
                p.setColor(0xFF9B7EDE);
                c.drawCircle(0, -34, 28, p);
                p.setColor(0xFFFFB84D);
                poly(c, -14, -28, 14, -28, 0, 24);
                p.setColor(WHITE);
                c.drawCircle(-13, -42, 7, p);
                c.drawCircle(13, -42, 7, p);
                p.setColor(0xFF222222);
                c.drawCircle(-13, -42, 3.5f, p);
                c.drawCircle(13, -42, 3.5f, p);
                break;
            case "fly":
            case "apple":
            case "leaf":
            case "fish":
                Treats.draw(c, p, icon);
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
