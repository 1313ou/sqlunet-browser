#!/bin/bash

RELEASE_NAME="$1"
if [ -z "${RELEASE_NAME}" ]; then
	V=`./find-version.sh`
	RELEASE_NAME="I${V}"
	echo "Version name ${RELEASE_NAME}"
fi
RECENT_CHANGES="Fixes"

source define_colors.sh

echo -e "${YELLOW}semantikos${RESET}"
./googleplay_upload_aab_semantikos.sh		"${RELEASE_NAME}" "${RECENT_CHANGES}"
echo -e "${YELLOW}semantikos ewn${RESET}"
./googleplay_upload_aab_semantikos_ewn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
echo -e "${YELLOW}semantikos fn${RESET}"
./googleplay_upload_aab_semantikos_fn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
echo -e "${YELLOW}semantikos vn${RESET}"
./googleplay_upload_aab_semantikos_vn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
echo -e "${YELLOW}semantikos sn${RESET}"
./googleplay_upload_aab_semantikos_sn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"

