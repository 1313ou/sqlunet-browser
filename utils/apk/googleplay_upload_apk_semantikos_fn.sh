#!/bin/bash

RELEASE_NAME="$1"
if [ -z "${RELEASE_NAME}" ]; then
	V=`./find-version.sh`
	RELEASE_NAME="A${V}"
	echo "Version name ${RELEASE_NAME}"
fi
RECENT_CHANGES="$2"
if [ -z "${RECENT_CHANGES}" ]; then
	RECENT_CHANGES="Fixes"
fi
DIR=dist/releases
PACKAGE=org.sqlunet.browser.fn
APK=browserfn

python2 googleplay_upload_apk.py \
	${PACKAGE} \
	"${RELEASE_NAME}" \
	"${RECENT_CHANGES}" \
	${DIR}/${APK}-release.apk \

