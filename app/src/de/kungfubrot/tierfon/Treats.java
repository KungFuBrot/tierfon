package de.kungfubrot.tierfon;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/** Die Leckerbissen, mit denen das Kind die Tiere füttert. Gezeichnet für ein Feld von 200 x 200. */
final class Treats {
    private static final Path PATH = new Path();

    private Treats() {}

    static String forAnimal(String id) {
        switch (String.valueOf(id)) {
            case "hedgehog":
                return "apple";
            case "brachio":
                return "leaf";
            case "ptero":
                return "fish";
            default:
                return "fly";
        }
    }

    private static void tri(Canvas c, Paint p, float x0, float y0, float x1, float y1,
            float x2, float y2) {
        PATH.rewind();
        PATH.moveTo(x0, y0);
        PATH.lineTo(x1, y1);
        PATH.lineTo(x2, y2);
        PATH.close();
        c.drawPath(PATH, p);
    }

    static void draw(Canvas c, Paint p, String kind) {
        p.setStyle(Paint.Style.FILL);
        switch (kind) {
            case "apple":
                p.setColor(0xFFE5484D);
                c.drawCircle(-18, 12, 44, p);
                c.drawCircle(18, 12, 44, p);
                p.setColor(0xFF6B4A2B);
                c.drawRect(-5, -58, 5, -24, p);
                p.setColor(0xFF4CAF50);
                c.drawOval(4, -62, 46, -38, p);
                break;
            case "leaf":
                p.setColor(0xFF4CAF50);
                c.drawOval(-34, -62, 34, 40, p);
                tri(c, p, -30, -30, 30, -30, 0, -78);
                p.setColor(0xFF2E7D32);
                c.drawRect(-4, -56, 4, 66, p);
                break;
            case "fish":
                p.setColor(0xFF5B9BF0);
                c.drawOval(-58, -30, 34, 30, p);
                tri(c, p, 24, 0, 66, -34, 66, 34);
                p.setColor(0xFFFFFFFF);
                c.drawCircle(-34, -8, 9, p);
                p.setColor(0xFF222222);
                c.drawCircle(-36, -8, 4, p);
                break;
            default:  // Fliege
                p.setColor(0xCCDDEEFF);
                c.drawOval(-52, -46, -4, -6, p);
                c.drawOval(4, -46, 52, -6, p);
                p.setColor(0xFF333333);
                c.drawOval(-22, -24, 22, 36, p);
                c.drawCircle(0, -26, 16, p);
                break;
        }
    }
}
