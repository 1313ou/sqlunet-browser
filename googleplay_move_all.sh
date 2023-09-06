#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

VERSION_CODE="$1"
TRACK="$2"
RELEASE_NAME="$3"
RECENT_CHANGES="$4"

if [ "$#" -ne 4 ]; then
	echo "VERSION(number) TRACK[alpha,beta,production] NAME([A|B|R]number) CHANGES"
	exit 1
fi

packages="
org.sqlunet.browser
org.sqlunet.browser.wn
org.sqlunet.browser.ewn
org.sqlunet.browser.fn
org.sqlunet.browser.vn
org.sqlunet.browser.sn
"

source define_colors.sh

for p in ${packages}; do
	echo -e "${YELLOW}${p}${RESET}"
	./googleplay_move.sh ${p} ${VERSION_CODE} ${TRACK} "${RELEASE_NAME}" "${RECENT_CHANGES}"
done
