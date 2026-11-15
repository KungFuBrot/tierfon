package de.kungfubrot.tierfon;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** Elternbereich: nach der PIN lässt sich die Sperre aussetzen oder ganz aufheben. */
public class ParentActivity extends BaseActivity {
    /** Solange die PIN-Eingabe offen ist, darf der schwarze Bildschirm sie nicht überdecken. */
    static boolean showing;

    private final StringBuilder entered = new StringBuilder();
    private TextView dots;

    @Override
    boolean locks() {
        return false;
    }

    @Override
    boolean showsBattery() {
        return false;
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (Kiosk.parentMode(this)) {
            showMenu();
        } else {
            showPinPad();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showing = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        showing = false;
    }

    private LinearLayout column() {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER_HORIZONTAL);
        col.setPadding(48, 72, 48, 48);
        col.setBackgroundColor(0xFFF3F4F6);
        return col;
    }

    private TextView label(String text, float size) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(size);
        t.setTextColor(0xFF222222);
        t.setGravity(Gravity.CENTER);
        t.setPadding(0, 16, 0, 24);
        return t;
    }

    private Button button(String text, Runnable action) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(20);
        b.setAllCaps(false);
        b.setPadding(24, 36, 24, 36);
        b.setOnClickListener(v -> action.run());
        return b;
    }

    private void showPinPad() {
        LinearLayout col = column();
        col.addView(label("Elternbereich", 28));
        dots = label("PIN eingeben", 24);
        col.addView(dots);
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(3);
        for (String key : new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "✕", "0", "⌫"}) {
            Button b = new Button(this);
            b.setText(key);
            b.setTextSize(30);
            b.setOnClickListener(v -> onKey(key));
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 190;
            lp.height = 170;
            lp.setMargins(8, 8, 8, 8);
            grid.addView(b, lp);
        }
        col.addView(grid);
        setContentView(col);
    }

    private void onKey(String key) {
        if (key.equals("✕")) {
            finish();
            return;
        }
        if (key.equals("⌫")) {
            if (entered.length() > 0) entered.setLength(entered.length() - 1);
        } else {
            entered.append(key);
        }
        String pin = Kiosk.pin(this);
        if (entered.length() >= pin.length()) {
            if (entered.toString().equals(pin)) {
                Kiosk.setParentMode(this, true);
                Kiosk.unlock(this);
                showMenu();
                return;
            }
            entered.setLength(0);
            dots.setText("Falsche PIN");
            return;
        }
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < entered.length(); i++) s.append("● ");
        dots.setText(entered.length() == 0 ? "PIN eingeben" : s.toString().trim());
    }

    private void showMenu() {
        LinearLayout col = column();
        col.addView(label("Elternbereich", 28));
        col.addView(label(Kiosk.isOwner(this)
            ? "Die Sperre ist für 15 Minuten ausgesetzt."
            : "Die Sperre ist nicht eingerichtet (Tierfon ist kein Gerätebesitzer).", 18));
        long left = (Usage.remainingMs(this) + 59_999) / 60_000;
        col.addView(label("Spielzeit heute: noch " + left + " von 30 Minuten.", 18));
        col.addView(button("Spielzeit zurücksetzen", () -> {
            Usage.setUsedMs(this, 0);
            showMenu();
        }));
        int vol = Kiosk.maxVolumePercent(this);
        col.addView(button("Höchste Lautstärke: " + vol + " %", () -> {
            Kiosk.setMaxVolumePercent(this, vol >= 100 ? 40 : vol + 20);
            showMenu();
        }));
        boolean cam = Kiosk.cameraAllowed(this);
        col.addView(button(cam ? "Kamera für das Kind sperren" : "Kamera für das Kind freigeben", () -> {
            Kiosk.setCameraAllowed(this, !cam);
            showMenu();
        }));
        boolean gal = Kiosk.galleryAllowed(this);
        col.addView(button(gal ? "Galerie für das Kind sperren" : "Galerie für das Kind freigeben", () -> {
            Kiosk.setGalleryAllowed(this, !gal);
            showMenu();
        }));
        col.addView(button("Bluetooth-Einstellungen",
            () -> open(Settings.ACTION_BLUETOOTH_SETTINGS)));
        col.addView(button("Android-Einstellungen", () -> open(Settings.ACTION_SETTINGS)));
        col.addView(button("PIN ändern", this::changePin));
        col.addView(button("Sperre vollständig aufheben", this::confirmRelease));
        col.addView(button("Wieder sperren", () -> {
            Kiosk.setParentMode(this, false);
            finish();
        }));
        ScrollView scroll = new ScrollView(this);
        scroll.addView(col);
        setContentView(scroll);
    }

    private void open(String action) {
        try {
            startActivity(new Intent(action));
        } catch (RuntimeException ignored) {
        }
    }

    private void changePin() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Neue PIN, mindestens 4 Ziffern");
        new AlertDialog.Builder(this)
            .setTitle("PIN ändern")
            .setView(input)
            .setPositiveButton("Speichern", (d, w) -> {
                String pin = input.getText().toString().trim();
                if (pin.length() >= 4) Kiosk.prefs(this).edit().putString("pin", pin).apply();
            })
            .setNegativeButton("Abbrechen", null)
            .show();
    }

    private void confirmRelease() {
        new AlertDialog.Builder(this)
            .setTitle("Sperre vollständig aufheben?")
            .setMessage("Alle ausgeblendeten Apps erscheinen wieder, und Tierfon ist nicht mehr "
                + "Gerätebesitzer. Die Sperre lässt sich danach nur vom PC aus neu einrichten.")
            .setPositiveButton("Aufheben", (d, w) -> {
                Kiosk.release(this);
                showMenu();
            })
            .setNegativeButton("Abbrechen", null)
            .show();
    }
}
