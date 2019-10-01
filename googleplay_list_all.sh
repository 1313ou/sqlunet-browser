#!/bin/bash


packages="
org.sqlunet.browser
org.sqlunet.browser.fn
org.sqlunet.browser.vn
org.sqlunet.browser.wn
org.sqlunet.browser.ewn
"

for p in ${packages}; do
	echo ${p}
	python googleplay_list.py ${p}
	echo
done
