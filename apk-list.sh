#!/bin/bash

RELEASE=build/outputs/apk/release
all="
browser/${RELEASE}/browser-release.apk
browserwn/${RELEASE}/browserwn-release.apk
browserewn/${RELEASE}/browserewn-release.apk
browserfn/${RELEASE}/browserfn-release.apk
browservn/${RELEASE}/browservn-release.apk
browsersn/${RELEASE}/browsersn-release.apk
"

RED='\u001b[31m'
GREEN='\u001b[32m'
YELLOW='\u001b[33m'
BLUE='\u001b[34m'
MAGENTA='\u001b[35m'
CYAN='\u001b[36m'
RESET='\u001b[0m'

function get_apks() {
	for apk in $all; do
		src=`readlink -m ${apk}`
		>&2 echo -n ${src}
		if [ -e "${src}" ]; then
			echo "${src}"
			>&2 echo -e "${GREEN} EXISTS${RESET}"
		else
			>&2 echo -e "${YELLOW} !EXISTS${RESET}"
		fi
	done
}

#get_apks

export APKS=`get_apks`
echo -e "${BLUE}${APKS}${RESET}"
