#!/bin/bash


packages="
org.sqlunet.browser
org.sqlunet.browser.wn
org.sqlunet.browser.ewn
org.sqlunet.browser.fn
org.sqlunet.browser.vn
"

for p in ${packages}; do
	echo ${p}
	python my_list.py ${p}
	echo
done
