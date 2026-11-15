package de.kungfubrot.tierfon;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Die tägliche Spielzeit: gezählt wird jede Sekunde, in der der Bildschirm an ist. */
final class Usage {
    static final long LIMIT_MS = 30 * 60_000L;

    /** Anzahl der Tierfon-Bildschirme, die gerade im Vordergrund sind. */
    static int resumed;

    private Usage() {}

    private static String today() {
        return new SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(new Date());
    }

    static long usedMs(Context c) {
        SharedPreferences p = Kiosk.prefs(c);
        // Am nächsten Tag beginnt die Zeit von vorn.
        return today().equals(p.getString("usageDay", "")) ? p.getLong("usedMs", 0) : 0;
    }

    static void setUsedMs(Context c, long ms) {
        Kiosk.prefs(c).edit().putString("usageDay", today()).putLong("usedMs", ms).apply();
    }

    static void add(Context c, long ms) {
        setUsedMs(c, usedMs(c) + ms);
    }

    static long remainingMs(Context c) {
        return Math.max(0, LIMIT_MS - usedMs(c));
    }

    /** Die Zeit ist aufgebraucht. Im Elternmodus gilt das Limit nicht. */
    static boolean exhausted(Context c) {
        return !Kiosk.parentMode(c) && remainingMs(c) <= 0;
    }
}
