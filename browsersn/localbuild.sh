#!/bin/bash

BT=/opt/androidsdk/bundle-tool/bundletool-all-1.4.0.jar
TARGET=debug
AAB=build/outputs/bundle/${TARGET}/browsersn-${TARGET}.aab
APKS=build/outputs/apks/${TARGET}.apks
APK=${APKS}/browsersn-${TARGET}.apk

../gradlew :browsersn:bundleDebug

java -jar ${BT} build-apks \
	--bundle=${AAB} \
	--output=${APKS} \
	--overwrite \
	--local-testing
