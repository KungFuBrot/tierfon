#!/usr/bin/env bash
# Baut build/tierfon.apk ohne Gradle, nur mit JDK und Android SDK. In Git Bash ausführen.
set -euo pipefail
cd "$(dirname "$0")"

SDK="$(cygpath -m "${ANDROID_HOME:-$LOCALAPPDATA/Android/Sdk}")"
JDK="$(cygpath -m "${JAVA_HOME:-$LOCALAPPDATA/Programs/Temurin17/jdk-17.0.21+9}")"
BT="$SDK/build-tools/35.0.0"
JAR="$SDK/platforms/android-35/android.jar"
KEYSTORE="$(cygpath -m "$USERPROFILE")/.android/debug.keystore"
export JAVA_HOME="$JDK"
export PATH="$(cygpath -u "$JDK")/bin:$PATH"

# Test-Schlüssel zum Signieren; liegt nur auf diesem PC.
if [ ! -f "$KEYSTORE" ]; then
  mkdir -p "$(dirname "$KEYSTORE")"
  keytool -genkeypair -keyalg RSA -alias androiddebugkey -keypass android -storepass android \
    -keystore "$KEYSTORE" -dname "CN=Android Debug,O=Android,C=US" -validity 9999
fi

rm -rf build
mkdir -p build/classes build/dex
touch build/.gdignore

"$BT/aapt2" compile --dir app/res -o build/res.zip
"$BT/aapt2" link -o build/base.apk --manifest app/AndroidManifest.xml -I "$JAR" build/res.zip
javac -source 8 -target 8 -Xlint:-options -encoding UTF-8 -cp "$JAR" -d build/classes \
  $(find app/src -name '*.java')
jar cf build/classes.jar -C build/classes .
"$BT/d8.bat" --min-api 23 --lib "$JAR" --output build/dex build/classes.jar
(cd build/dex && "$BT/aapt" add ../base.apk classes.dex >/dev/null)
"$BT/zipalign" -f 4 build/base.apk build/tierfon.apk
"$BT/apksigner.bat" sign --ks "$KEYSTORE" --ks-pass pass:android \
  --ks-key-alias androiddebugkey build/tierfon.apk
ls -la build/tierfon.apk
