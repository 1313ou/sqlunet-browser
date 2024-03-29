#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

#list devices
#adb devices -l
#device=ce12160c903ac05e0c	#samsung
#device=e7f518b7				#xiaomi

if [ "-device" == "$1" ]; then
	shift
	device=$1
	deviceswitch="-s $1"
	shift 
fi

R='\u001b[31m'
G='\u001b[32m'
Y='\u001b[33m'
B='\u001b[34m'
M='\u001b[35m'
C='\u001b[36m'
Z='\u001b[0m'

APKDIR=dist/releases

for e in '' 'fn' 'wn' 'ewn' 'vn' 'sn'; do

	d=$e
	if [ ! -z "$e" ]; then
		d=".$e"
	fi
	echo -e "${Y}browser$e <$e><$d>${Z}"

	if [ "$1" == "-z" ]; then
		adb ${deviceswitch} uninstall org.sqlunet.browser$d
	fi

	adb ${deviceswitch} install -r ${APKDIR}/browser$e-release.apk

done

