#!/bin/bash


packages="
org.sqlunet.browser
org.sqlunet.browser.fn
org.sqlunet.browser.vn
org.sqlunet.browser.wn
org.sqlunet.browser.ewn
org.sqlunet.browser.sn
"

source define_colors.sh

for p in ${packages}; do
	echo -e "${YELLOW}${p}${RESET}"
	python2 googleplay_list.py ${p}
	echo
done
