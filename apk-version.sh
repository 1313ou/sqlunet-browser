#!/bin/bash

if [ $# -ne 0 ]; then
	all=$*
else
	all=dist/releases/*.apk
fi

for apk in ${all}; do
	echo ${apk}
	aapt l -a ${apk} | egrep '(package=|android:versionCode|android:versionName)'
done

for apk in ${all}; do
	echo -n "`basename ${apk}` "
	n=`aapt l -a ${apk} | egrep versionCode | sed 's/^.*(type 0x10)/ /g'`
	echo $(($n)) 
done
