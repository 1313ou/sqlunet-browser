#!/bin/bash


#
# Copyright (c) 2023. Bernard Bou
#

source define_colors.sh

packages="
org.sqlunet.browser
org.sqlunet.browser.fn
org.sqlunet.browser.vn
org.sqlunet.browser.wn
org.sqlunet.browser.ewn
org.sqlunet.browser.sn
"

echo -en "${M}"
./find-version.sh
echo -en "${Z}"

for p in ${packages}; do
	echo -e "${YELLOW}${p}${RESET}"
	python2 googleplay_list.py ${p}
	echo
done
