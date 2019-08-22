#!/bin/bash

RELEASE=build/outputs/apk/release
all="
browser/${RELEASE}/browser-release.apk
browserwn/${RELEASE}/browserwn-release.apk
browserewn/${RELEASE}/browserewn-release.apk
browserfn/${RELEASE}/browserfn-release.apk
browservn/${RELEASE}/browservn-release.apk
"

RED='\u001b[31m'
GREEN='\u001b[32m'
YELLOW='\u001b[33m'
BLUE='\u001b[34m'
MAGENTA='\u001b[35m'
CYAN='\u001b[36m'
RESET='\u001b[0m'

WHERE="."

pushd ${WHERE} > /dev/null
for apk in $all; do
	src=`readlink -m ${apk}`
	echo -n ${src}
	if [ -e "${src}" ]; then
		echo -e "${GREEN} EXISTS${RESET}"
	else
		echo -e "${YELLOW} !EXISTS${RESET}"
	fi
	#ln -sf ${src}
done
popd > /dev/null

