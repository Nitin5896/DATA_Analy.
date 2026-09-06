#!/usr/bin/env bash
# Generates the upload keystore used to sign release builds for Play Console.
#
# Run this ONCE, on your own machine (not in any shared/CI environment), then:
#   1. Back up the generated .jks file somewhere safe and private (password manager vault,
#      encrypted drive) - if you lose it AND haven't enrolled in Play App Signing, you can
#      never update the app again under the same listing.
#   2. Never commit it. It's already covered by .gitignore (*.jks, *.keystore).
#   3. Fill in keystore.properties (copy from keystore.properties.example) with the same
#      path/passwords/alias you enter below.
#
# Usage: ./scripts/generate_release_keystore.sh

set -euo pipefail
cd "$(dirname "$0")/.."

KEYSTORE_FILE="release-keystore.jks"
ALIAS="beperfectsalon"

if [ -f "$KEYSTORE_FILE" ]; then
  echo "Error: $KEYSTORE_FILE already exists. Refusing to overwrite an existing keystore."
  echo "If you really want a new one, move or delete the old file first (and be aware any"
  echo "app already published with the old key can no longer be updated with a new one,"
  echo "unless you've enrolled in Play App Signing)."
  exit 1
fi

keytool -genkeypair \
  -v \
  -keystore "$KEYSTORE_FILE" \
  -alias "$ALIAS" \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000

echo ""
echo "Created $KEYSTORE_FILE with alias '$ALIAS'."
echo "Now copy keystore.properties.example to keystore.properties and fill in:"
echo "  storeFile=$KEYSTORE_FILE"
echo "  keyAlias=$ALIAS"
echo "  storePassword / keyPassword = whatever you just entered above"
