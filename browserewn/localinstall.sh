#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

BT=/opt/androidsdk/bundle-tool/bundletool-all-1.4.0.jar
TARGET=debug
AAB=build/outputs/bundle/${TARGET}/browserewn-${TARGET}.aab
APKS=build/outputs/apks/${TARGET}.apks
APK=${APKS}/browserewn-${TARGET}.apk

#adb root

java -jar ${BT} install-apks \
	--apks=${APKS}
