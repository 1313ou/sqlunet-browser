#!/bin/bash

RELEASE_NAME="Anew"
if [ ! -z "$1" ]; then
	RELEASE_NAME="$1"
	echo "Version name ${RELEASE_NAME}"
fi
RECENT_CHANGES="Fixes"

./googleplay_upload_semantikos_aab.sh		  "${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_semantikos_wn_aab.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_semantikos_ewn_aab.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_semantikos_fn_aab.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_semantikos_vn_aab.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_semantikos_sn_aab.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"

