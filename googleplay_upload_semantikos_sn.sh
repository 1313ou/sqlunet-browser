#!/bin/bash

RELEASE_NAME="$1"
if [ -z "${RELEASE_NAME}" ]; then
	RELEASE_NAME="Anew"
	echo "Version name ${RELEASE_NAME}"
fi
RECENT_CHANGES="$2"
if [ -z "${RECENT_CHANGES}" ]; then
	RECENT_CHANGES="Fixes"
fi
DIR=dist/releases
PACKAGE=org.sqlunet.browser.sn
APK=browsersn

python googleplay_upload_apks.py \
	${PACKAGE} \
	"${RELEASE_NAME}" \
	"${RECENT_CHANGES}" \
	${DIR}/${APK}-release.apk \

