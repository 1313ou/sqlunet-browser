#!/bin/bash

RELEASE_NAME="$1"
if [ -z "${RELEASE_NAME}" ]; then
	V=`./find-version.sh`
	RELEASE_NAME="I${V}"
	echo "Version name ${RELEASE_NAME}"
fi
RECENT_CHANGES="Fixes"

./googleplay_upload_semantikos_aab.sh		"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_semantikos_wn_aab.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_semantikos_ewn_aab.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_semantikos_fn_aab.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_semantikos_vn_aab.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_semantikos_sn_aab.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"

