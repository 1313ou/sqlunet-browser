#!/bin/bash

if [ $# -ne 0 ]; then
	all=$*
else
	all=dist/releases/*.apk
fi

R='\u001b[31m'
G='\u001b[32m'
Y='\u001b[33m'
B='\u001b[34m'
M='\u001b[35m'
C='\u001b[36m'
Z='\u001b[0m'

for apk in ${all}; do
	echo -e "${M}${apk}${Z}"
	aapt l -a ${apk} | egrep '(package=|android:versionCode|android:versionName)'
done

for apk in ${all}; do
	echo -en "${M}`basename ${apk}`${Z} "
	#echo -e "${G}" 
	#aapt l -a ${apk} | egrep 'android:versionCode' | sed 's/^.*(type 0x10)/ /g'
	#echo -e "${Z}" 
	n=`aapt l -a ${apk} | egrep 'android:versionCode' | sed 's/^.*(type 0x10)/ /g'`
	echo -e "${B}$(($n))${Z}" 
done
