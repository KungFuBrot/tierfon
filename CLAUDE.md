# Entwicklungsnotizen

Notizen für die Weiterarbeit an Tierfon. Was die App kann, steht im [README](README.md); wie Eltern sie bedienen, in der [Anleitung](ANLEITUNG.md).

## Stand (15.11.2026)

- Die App ist auf dem Moto G installiert, Tierfon ist dort Gerätebesitzer, die Sperre ist aktiv.
- Auf dem Gerät getestet: vier Tiere, Füttern, Musikplayer, Zeitlimit, Lautstärkegrenze, Sperre, beide Rückwege.
- Offen: Bluetooth, eigene Lieder, Klang der Stimmen (kann nur ein Mensch beurteilen), Tageswechsel um Mitternacht.

## Werkzeuge auf dem Entwicklungsrechner (Windows)

| Was | Wo |
|---|---|
| JDK 17 (Temurin, ZIP) | `%LOCALAPPDATA%\Programs\Temurin17\jdk-17.0.21+9` (`JAVA_HOME`) |
| Android SDK | `%LOCALAPPDATA%\Android\Sdk` (`ANDROID_HOME`), mit platform-tools, build-tools 35.0.0, platforms/android-35 |
| Signaturschlüssel | `%USERPROFILE%\.android\debug.keystore`, wird von `build.sh` bei Bedarf erzeugt |

`adb` und `fastboot` liegen in `platform-tools` und stehen nicht im `PATH`. Gradle, Python und ffmpeg sind nicht installiert.

## Bauen und aufspielen

```bash
bash build.sh
```

```bash
adb install -t -r build/tierfon.apk
```

`build.sh` baut ohne Gradle: `aapt2` → `javac` → `d8` → `aapt add` → `zipalign` → `apksigner`. Das `-t` beim Installieren ist nötig, weil die App `testOnly` ist. `testOnly` erlaubt es, den Gerätebesitz notfalls mit `dpm remove-active-admin` zu entfernen.

## Aufbau des Codes

| Datei | Aufgabe |
|---|---|
| `Kiosk.java` | Sperre, Elternmodus, PIN, Freigaben, Lautstärkegrenze |
| `Usage.java`, `UsageService.java` | Spielzeit zählen, Batterie-Fenster über fremden Apps, schwarzen Bildschirm auslösen |
| `BaseActivity.java` | Vollbild, Sperre, Batteriesymbol und Lauter-Taste für alle Bildschirme |
| `Animal.java` | Verhalten aller Tiere und Zeichenhilfen |
| `Frog`, `Hedgehog`, `Brachio`, `Ptero` | Je ein Tier: `draw`, `drawBackground`, `makeClip`, `id`, `mouthY` |
| `CallActivity.java` | Anruf: Klingeln, Mikrofon, Füttern, Herzen und Noten |
| `Synth.java` | Alle Töne, rechnerisch erzeugt |
| `Player.java`, `MusicService.java`, `MusicActivity.java` | Musik |
| `ReleaseReceiver.java` | Befehle vom PC (`SET_USED`, `SET_PIN`, `LOCK`, `RELEASE`) |

Ein neues Tier braucht: eine Klasse, die von `Animal` ableitet, einen Eintrag in `CallActivity.create`, in `AnimalsActivity`, ein Symbol in `TileView.drawIcon` und einen Leckerbissen in `Treats`.

## Stolperfallen

- **Godot 4 läuft auf dem Moto G nicht.** Der Treiber des Adreno 306 stürzt beim Laden der Shader ab (`libsc-a3xx.so`, `__link_shaders`). Deshalb ist die App nativ.
- **Gerätebesitzer geht nur ohne Konto.** `dpm set-device-owner` schlägt fehl, solange ein Konto auf dem Telefon eingerichtet ist.
- **Elternmodus hält die Sperre 15 Minuten auf.** Wenn die Sperre beim Testen nicht greift, hilft die Aktion `LOCK`.
- **Kein `DISALLOW_INSTALL_APPS` setzen.** Diese Einschränkung blockiert auch `adb install`, also jedes Update.
- **Ausgeblendeter Play Store.** Er erzeugt Fehlermeldungen, wenn die Google-Dienste noch laufen. Deshalb blendet `Kiosk.ALSO_HIDE` sie mit aus. `com.google.android.gms` lässt sich nicht ausblenden.
- **Spielzeit beim Testen.** Jede Sekunde mit eingeschaltetem Bildschirm zählt. Vor Tests `SET_USED --ei minutes 0` schicken, sonst erscheint mitten im Test der schwarze Bildschirm.
- **adb zeigt „offline“.** Am Telefon unter Entwickleroptionen die USB-Debugging-Autorisierungen widerrufen, Debugging aus- und einschalten, Kabel neu stecken, Abfrage bestätigen.
- **`uiautomator dump` ist wackelig.** Er liefert manchmal „null root node“; einfach wiederholen. Eigene Views (Kacheln) haben keinen Text, dort helfen nur Koordinaten und Bildschirmfotos.
- **Auf dem Telefon gibt es kein `awk`.** Ausgaben von `adb shell` auf dem PC auswerten.
- **Git Bash und Sonderzeichen.** Mehrzeilige Änderungen mit Backticks oder Anführungszeichen als Node-Skript in eine Datei schreiben, nicht als Heredoc übergeben.

## Testen auf dem Gerät

Bildschirmfoto:

```bash
adb exec-out screencap -p > foto.png
```

Welcher Bildschirm ist vorn, und ist die Sperre aktiv:

```bash
adb shell "dumpsys window | grep mCurrentFocus; dumpsys activity activities | grep mLockTaskModeState"
```

Läuft gerade Ton (für Musiktests):

```bash
adb shell dumpsys media.audio_flinger | grep "are active"
```

## Ideen für später

- Bluetooth-Box koppeln und prüfen, ob die Lautstärkegrenze dort wirkt
- Musik bei ausgeschaltetem Bildschirm auf die Spielzeit anrechnen (bisher nicht)
- Eigenes Kamerabild in der Ecke des Anrufs („wie FaceTime“)
- Weitere Tiere
