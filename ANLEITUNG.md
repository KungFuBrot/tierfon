# Tierfon: Anleitung für Eltern

Tierfon ist ein Spieltelefon. Euer Kind kann damit Tiere „anrufen“ und Musik hören. Das Telefon hat kein Internet und kann nicht telefonieren.

## Was euer Kind sieht

Der Startbildschirm zeigt zwei große Kacheln:

- **Frosch:** Tiere anrufen. Zur Auswahl stehen Frosch, Igel, Brachiosaurus und Pterodactylus.
- **Note:** Musik hören.

Beim Anruf klingelt es kurz, dann erscheint das Tier. Euer Kind kann:

- **mit dem Tier sprechen:** Das Tier hört zu. Wird es still, antwortet es in seiner Tiersprache.
- **das Tier antippen:** Es hüpft, und Herzen steigen auf.
- **das Tier füttern:** Der runde Knopf unten links wirft einen Leckerbissen. Das Tier schmatzt.
- **laut rufen:** Das Tier erschrickt und springt hoch.

Der rote Knopf legt auf.

Das Mikrofon misst dabei nur die Lautstärke. Es wird nichts aufgenommen oder gespeichert.

## Spielzeit: 30 Minuten am Tag

- Oben rechts zeigt eine Batterie, wie viel Zeit heute noch übrig ist. Sie wird von grün über gelb zu rot.
- Gezählt wird nur, solange der Bildschirm an ist.
- Ist die Zeit um, stoppt die Musik und der Bildschirm wird schwarz. Das Telefon reagiert dann nicht mehr.
- Am nächsten Tag sind wieder 30 Minuten da.

## Lautstärke

Das Telefon wird höchstens 60 Prozent laut, auch mit Kopfhörern oder Bluetooth-Box. Die Lauter-Taste wirkt nur bis zu dieser Grenze. Im Elternbereich könnt ihr die Grenze auf 40, 60, 80 oder 100 Prozent stellen.

## Der Elternbereich

So kommt ihr hinein:

1. Auf dem Startbildschirm die **Batterie oben rechts drei Sekunden gedrückt halten**. Das geht auch auf dem schwarzen Bildschirm, an derselben Stelle.
2. Eure **PIN** eingeben.

Im Elternbereich sind Sperre und Zeitlimit für 15 Minuten ausgesetzt. Dort könnt ihr:

| Knopf | Wirkung |
|---|---|
| Spielzeit zurücksetzen | Gibt heute noch einmal 30 Minuten |
| Höchste Lautstärke | Wechselt bei jedem Tippen zwischen 40, 60, 80 und 100 Prozent |
| Galerie für das Kind freigeben | Zeigt eine Kachel für die Fotos auf dem Telefon |
| Kamera für das Kind freigeben | Zeigt eine Kachel für die Kamera |
| Bluetooth-Einstellungen | Box oder Kopfhörer koppeln |
| Android-Einstellungen | Lautstärke, Helligkeit, Uhrzeit und alles Übrige |
| PIN ändern | Neue PIN mit mindestens vier Ziffern |
| Sperre vollständig aufheben | Macht aus dem Tierfon wieder ein normales Telefon |
| Wieder sperren | Beendet den Elternbereich sofort |

Wenn ihr fertig seid, tippt auf **Wieder sperren**. Der Knopf steht ganz unten, ihr müsst dafür nach unten wischen. Sonst endet der Elternbereich nach 15 Minuten von selbst.

## Musik aufs Telefon bringen

Das geht nur mit einem PC, weil das Telefon kein Internet hat:

1. Telefon per USB an den PC anschließen.
2. Die MP3-Dateien in den Ordner `Music` auf dem Telefon kopieren.

Im Musikplayer tippt euer Kind ein Lied an oder benutzt die drei runden Tasten: zurück, Start/Pause, weiter. Die Musik läuft weiter, wenn der Bildschirm ausgeht.

## Bluetooth-Box oder Kopfhörer

1. Elternbereich öffnen und auf **Bluetooth-Einstellungen** tippen.
2. Bluetooth einschalten und das Gerät koppeln.
3. Zurück und **Wieder sperren**.

Danach verbindet sich das Gerät von selbst, sobald es eingeschaltet ist.

## Wenn etwas nicht klappt

| Problem | Lösung |
|---|---|
| Der Bildschirm ist schwarz und reagiert nicht | Die Spielzeit ist um. Morgen geht es weiter, oder ihr setzt sie im Elternbereich zurück. |
| Das Tier antwortet nicht | Näher ans Telefon sprechen und danach kurz still sein. |
| Das Tier antwortet ständig von selbst | Es ist zu laut im Raum. In einer ruhigeren Umgebung probieren. |
| Keine Musik zu sehen | Es liegen noch keine MP3-Dateien im Ordner `Music`. |
| Die Zeit beginnt nicht um Mitternacht neu | Die Uhr des Telefons geht falsch. Im Elternbereich unter Android-Einstellungen stellen. |
| PIN vergessen | Am PC `bash scripts/freigeben.sh` ausführen. Das hebt die Sperre über das USB-Kabel auf. |

## Gut zu wissen

- Das Telefon lässt sich wie gewohnt mit der Einschalttaste aus- und einschalten und am Ladegerät laden.
- Solange die Sperre aktiv ist, gibt es keine Statusleiste, keine Home-Taste und keine anderen Apps.
- Das USB-Debugging bleibt eingeschaltet. Es ist der Rückweg vom PC, falls die PIN verloren geht.
