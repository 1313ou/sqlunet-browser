#!/bin/bash

RELEASE_NAME="Anew"
if [ ! -z "$1" ]; then
	RELEASE_NAME="$1"
	echo "Version name ${RELEASE_NAME}"
fi
RECENT_CHANGES="Fixes"

./googleplay_upload_semantikos.sh		"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_semantikos_wn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_semantikos_ewn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_semantikos_fn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_semantikos_vn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"

