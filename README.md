# Tierfon

Ein Kindertelefon zum Spielen auf einem alten Moto G (3. Generation). Das Kind kann Tiere „anrufen“ und Musik hören. Internet gibt es auf dem Gerät nicht.

**Stand:** Planung. Es gibt noch keinen Programmcode.

## Zielgerät

| | |
|---|---|
| Gerät | Motorola Moto G 3. Gen. (`osprey`, XT1541), 1 GB RAM, 8 GB Speicher |
| System | LineageOS 14.1 (Android 7.1.2, API-Level 25) |
| Bildschirm | 5 Zoll, 720 × 1280, Hochformat |

## Was die App kann

- **Tiere anrufen:** Frosch, Igel, Waschbär und Brontosaurus. Es klingelt, das Tier nimmt ab und erscheint bildschirmfüllend wie bei einem Videoanruf.
- **Reagieren auf Sprache:** Das Mikrofon misst nur die Lautstärke. Spricht das Kind, hört das Tier zu. Wird es still, blabbert das Tier in seiner eigenen Stimme zurück. Es wird nichts aufgenommen oder gespeichert.
- **Berührung:** Antippen löst kleine Reaktionen aus (kichern, hüpfen).
- **Musikplayer:** Spielt MP3-Dateien aus einem Ordner auf dem Telefon, mit großen Tasten.
- **Bluetooth:** Für Box oder Kopfhörer. Die Eltern koppeln das Gerät einmal in den Android-Einstellungen, danach verbindet es sich automatisch.
- **Kiosk:** Die App ersetzt den Startbildschirm. Ein versteckter Elternbereich (lange drücken und PIN) führt heraus.

## Kein Internet

- Keine SIM-Karte, gespeicherte WLANs gelöscht, Flugmodus mit eingeschaltetem Bluetooth
- Die App fordert keine Internet-Berechtigung an
- Browser und alle nicht benötigten Apps werden deaktiviert

## Technik

- **Godot 4.7**, 2D, Renderer „Compatibility“ (wegen 1 GB RAM und alter Grafikeinheit)
- Export als Android-App (APK), Installation per USB

## Etappen

1. Grundgerüst mit einem Tier (Frosch), das auf das Mikrofon reagiert; Test auf dem Gerät
2. Die übrigen drei Tiere und der Anruf-Ablauf (klingeln, abnehmen, auflegen)
3. Musikplayer
4. Kiosk-Modus und Internet-Sperre

## Offene Punkte

- Läuft die Musik bei ausgeschaltetem Bildschirm weiter? Muss mit Godot auf dem Gerät getestet werden.
- Das eigene Kamerabild in der Ecke („wie FaceTime“) ist in Godot auf Android unsicher und nur als optionales Extra eingeplant.
- Für den Android-Export fehlen auf dem Entwicklungsrechner noch Android SDK und JDK.
- Woher kommen Bilder und Tierstimmen? Nur selbst erstellte oder frei lizenzierte Dateien kommen ins Repository.

## Was nicht ins Repository gehört

- Die Eltern-PIN, Seriennummern und IMEI des Geräts
- Eigene MP3-Dateien (Urheberrecht)
- Fotos oder Tonaufnahmen von Kindern
