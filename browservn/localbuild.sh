#!/bin/bash

TAG=vn
BT=/opt/androidsdk/bundle-tool/bundletool-all-1.4.0.jar
TARGET=debug
AAB=build/outputs/bundle/${TARGET}/browser${TAG}-${TARGET}.aab
APKS=build/outputs/apks/${TARGET}.apks
APK=${APKS}/browser${TAG}-${TARGET}.apk

if ! ../gradlew :browser${TAG}:bundleDebug ; then
    echo "gradle failed"
    exit 1
fi

if ! java -jar ${BT} build-apks \
	--bundle=${AAB} \
	--output=${APKS} \
	--overwrite \
	--local-testing ; then
  echo "bundletool failed"
  exit 2
fi

exit 0
