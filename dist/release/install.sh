#!/bin/bash

#list devices
#adb devices -l

device=ce12160c903ac05e0c

for e in "$*"; do

	d=$e
	if [ ! -z "$e" ]; then
		d=".$e"
	fi
	echo "browser$e <$e><$d>"

	if [ "$1" == "-z" ]; then
		adb -s ${device} uninstall org.sqlunet.browser$d
	fi

	adb -s ${device} install -r browser$e-release.apk

done

