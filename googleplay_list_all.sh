#!/bin/bash


packages="
org.sqlunet.browser
org.sqlunet.browser.fn
org.sqlunet.browser.vn
org.sqlunet.browser.wn
org.sqlunet.browser.ewn
org.sqlunet.browser.sn
"

for p in ${packages}; do
	echo ${p}
	python googleplay_list.py ${p}
	echo
done
