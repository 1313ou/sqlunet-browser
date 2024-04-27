#!/bin/bash


#
# Copyright (c) 2023. Bernard Bou
#

packages="
org.sqlunet.browser
org.sqlunet.browser.fn
org.sqlunet.browser.vn
org.sqlunet.browser.wn
org.sqlunet.browser.ewn
org.sqlunet.browser.sn
"

./find-version.sh

source define_colors.sh

for p in ${packages}; do
	echo -e "${YELLOW}${p}${RESET}"
	python2 googleplay_list.py ${p}
	echo
done
