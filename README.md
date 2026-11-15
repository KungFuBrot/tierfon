# Tierfon

Ein Kindertelefon zum Spielen auf einem alten Moto G (3. Generation). Das Kind kann Tiere „anrufen“ und Musik hören. Internet gibt es auf dem Gerät nicht.

**Stand:** Etappe 1 läuft im Godot-Editor auf dem PC: Startbildschirm, Tierauswahl und der Anruf beim Frosch. Auf dem Moto G ist es noch nicht getestet.

![Der Frosch im Anruf](shots/call.png)

## Zielgerät

| | |
|---|---|
| Gerät | Motorola Moto G 3. Gen. (`osprey`, XT1541), 1 GB RAM, 8 GB Speicher |
| System | LineageOS 14.1 (Android 7.1.2, API-Level 25) |
| Bildschirm | 5 Zoll, 720 × 1280, Hochformat |

## Genau zwei Apps

Auf dem Telefon gibt es für das Kind nur zwei Dinge: **Tiere anrufen** und **Musik**. Der Startbildschirm zeigt genau diese zwei großen Kacheln. Alles andere (Einstellungen, Browser, übrige Android-Apps) wird deaktiviert oder ist nur über den Elternbereich erreichbar.

## Was die Apps können

- **Tiere anrufen:** Frosch, Igel, Waschbär und Brontosaurus. Es klingelt, das Tier nimmt ab und erscheint bildschirmfüllend wie bei einem Videoanruf.
- **Reagieren auf Sprache:** Das Mikrofon misst nur die Lautstärke. Spricht das Kind, hört das Tier zu. Wird es still, blabbert das Tier in seiner eigenen Stimme zurück. Es wird nichts aufgenommen oder gespeichert.
- **Berührung:** Antippen löst kleine Reaktionen aus (kichern, hüpfen).
- **Musikplayer:** Spielt MP3-Dateien aus einem Ordner auf dem Telefon, mit großen Tasten.
- **Bluetooth:** Für Box oder Kopfhörer. Die Eltern koppeln das Gerät einmal in den Android-Einstellungen, danach verbindet es sich automatisch.
- **Kiosk:** Tierfon ersetzt den Android-Startbildschirm, sodass nur die zwei Apps sichtbar sind. Ein versteckter Elternbereich (lange drücken und PIN) führt heraus.

## Kein Internet

- Keine SIM-Karte, gespeicherte WLANs gelöscht, Flugmodus mit eingeschaltetem Bluetooth
- Die App fordert keine Internet-Berechtigung an
- Browser und alle nicht benötigten Apps werden deaktiviert

## Technik

- Alle Bilder werden im Code gezeichnet und alle Töne im Code erzeugt. Das Projekt enthält deshalb keine fremden Bild- oder Tondateien.
- **Godot 4.7**, 2D, Renderer „Compatibility“ (wegen 1 GB RAM und alter Grafikeinheit)
- Export als Android-App (APK), Installation per USB

## Ausprobieren am PC

Das Projekt in Godot 4.7 öffnen und starten. Das Tier reagiert auf das Mikrofon des PCs. Ohne Mikrofon simuliert die Leertaste „das Kind spricht“.

## Etappen

1. Grundgerüst mit einem Tier (Frosch), das auf das Mikrofon reagiert (am PC fertig); Test auf dem Gerät (offen)
2. Die übrigen drei Tiere und der Anruf-Ablauf (klingeln, abnehmen, auflegen)
3. Musikplayer
4. Kiosk-Modus und Internet-Sperre

## Offene Punkte

- Läuft die Musik bei ausgeschaltetem Bildschirm weiter? Muss mit Godot auf dem Gerät getestet werden.
- Das eigene Kamerabild in der Ecke („wie FaceTime“) ist in Godot auf Android unsicher und nur als optionales Extra eingeplant.
- Die APK lässt sich bauen (`build/tierfon.apk`), ist aber noch nicht auf dem Moto G installiert und getestet.
- Wie gut die Erkennung „Kind spricht“ mit dem Mikrofon des Moto G funktioniert, zeigt erst der Test auf dem Gerät.

## Was nicht ins Repository gehört

- Die Eltern-PIN, Seriennummern und IMEI des Geräts
- Eigene MP3-Dateien (Urheberrecht)
- Fotos oder Tonaufnahmen von Kindern
