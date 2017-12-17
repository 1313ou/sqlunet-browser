#!/bin/bash

for e in '' 'fn' 'wn' 'vn'; do
	d=$e
	if [ ! -z "$e" ]; then
		d=".$e"
	fi
	echo "browser$e <$e><$d>"

	if [ "$1" == "-z" ]; then
		adb uninstall org.sqlunet.browser$d
	fi

	adb install -r browser$e-release.apk

done

