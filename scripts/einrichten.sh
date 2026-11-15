#!/usr/bin/env bash
# Richtet das Kindertelefon ein: App installieren, fremde Apps entfernen, Sperre aktivieren.
# Voraussetzungen: USB-Debugging ist an, und auf dem Telefon ist KEIN Konto mehr eingerichtet
# (Einstellungen -> Konten), sonst verweigert Android den Gerätebesitz.
set -euo pipefail
cd "$(dirname "$0")/.."

ADB="$(cygpath -u "${ANDROID_HOME:-$LOCALAPPDATA/Android/Sdk}")/platform-tools/adb.exe"
PKG=de.kungfubrot.tierfon

[ -f build/tierfon.apk ] || bash build.sh
"$ADB" install -t -r build/tierfon.apk
# Erlaubt das Batteriesymbol über Kamera und Galerie.
"$ADB" shell appops set "$PKG" SYSTEM_ALERT_WINDOW allow

echo "Entferne vom Nutzer installierte Apps ..."
for p in $("$ADB" shell pm list packages -3 | tr -d '\r' | sed 's/^package://'); do
  [ "$p" = "$PKG" ] && continue
  echo "  $p"
  "$ADB" uninstall "$p" || true
done

echo "Trage Tierfon als Gerätebesitzer ein ..."
"$ADB" shell dpm set-device-owner "$PKG/.AdminReceiver"

# Beim ersten Start blendet Tierfon die übrigen Apps aus, löscht die WLANs und sperrt.
"$ADB" shell am start -n "$PKG/.HomeActivity"
echo "Fertig. Rückweg: Ecke oben rechts 3 Sekunden halten und PIN eingeben, oder scripts/freigeben.sh."
