#!/bin/bash

if [ $# -ne 0 ]; then
	all=$*
else
	all=dist/releases/*.apk
fi

RED='\u001b[31m'
GREEN='\u001b[32m'
YELLOW='\u001b[33m'
BLUE='\u001b[34m'
MAGENTA='\u001b[35m'
CYAN='\u001b[36m'
RESET='\u001b[0m'

for apk in ${all}; do
	echo -e "${MAGENTA}${apk}${RESET}"
	aapt l -a ${apk} | egrep '(package=|android:versionCode|android:versionName)'
done

for apk in ${all}; do
	echo -en "${MAGENTA}`basename ${apk}`${RESET} "
	n=`aapt l -a ${apk} | egrep versionCode | sed 's/^.*(type 0x10)/ /g'`
	echo -e "${BLUE}$(($n))${RESET}" 
done
