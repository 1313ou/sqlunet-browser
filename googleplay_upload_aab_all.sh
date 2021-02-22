#!/bin/bash

RELEASE_NAME="$1"
if [ -z "${RELEASE_NAME}" ]; then
	V=`./find-version.sh`
	RELEASE_NAME="I${V}"
	echo "Version name ${RELEASE_NAME}"
fi
RECENT_CHANGES="Fixes"

./googleplay_upload_aab_semantikos.sh		"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_aab_semantikos_wn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_aab_semantikos_ewn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_aab_semantikos_fn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_aab_semantikos_vn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
./googleplay_upload_aab_semantikos_sn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"

