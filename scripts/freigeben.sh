#!/usr/bin/env bash
# Rückweg vom PC: hebt die Sperre auf und blendet alle Apps wieder ein.
set -euo pipefail

ADB="$(cygpath -u "${ANDROID_HOME:-$LOCALAPPDATA/Android/Sdk}")/platform-tools/adb.exe"
PKG=de.kungfubrot.tierfon

# Tierfon blendet die Apps selbst wieder ein und gibt den Gerätebesitz ab.
"$ADB" shell am broadcast -a "$PKG.RELEASE" -n "$PKG/.ReleaseReceiver"
sleep 2
if "$ADB" shell dumpsys device_policy | grep -q "Device Owner"; then
  echo "Tierfon ist noch Gerätebesitzer, entferne ihn direkt ..."
  "$ADB" shell dpm remove-active-admin "$PKG/.AdminReceiver"
fi
"$ADB" shell am start -a android.settings.SETTINGS
echo "Sperre aufgehoben. Die Android-Einstellungen sind geöffnet."
