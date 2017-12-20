#!/bin/bash

if [ $# -ne 0 ]; then
	all=$*
else
	all=*.apk
fi

for apk in $all; do
	echo ${apk}
	aapt l -a ${apk} | egrep '(package=|android:versionCode|android:versionName)'
done
