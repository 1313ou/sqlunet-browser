#!/bin/bash

RELEASE_NAME="Anew"
if [ ! -z "$1" ]; then
	RELEASE_NAME="$1"
	echo "Version name ${RELEASE_NAME}"
fi
RECENT_CHANGES="Fixes"

./googleplay_upload_apk_semantikos.sh		"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_apk_semantikos_wn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_apk_semantikos_ewn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_apk_semantikos_fn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_apk_semantikos_vn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_apk_semantikos_sn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"

