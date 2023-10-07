#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

RELEASE_NAME="$1"
if [ -z "${RELEASE_NAME}" ]; then
	V=`./find-version.sh`
	RELEASE_NAME="I${V}"
	echo "Version name ${RELEASE_NAME}"
fi
RECENT_CHANGES="Fixes"

source define_colors.sh

echo -e "${YELLOW}semantikos wn${RESET}"
./googleplay_upload_aab_semantikos_wn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"
echo -e "${YELLOW}semantikos ewn${RESET}"
./googleplay_upload_aab_semantikos_ewn.sh	"${RELEASE_NAME}" "${RECENT_CHANGES}"

