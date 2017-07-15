#!/bin/bash

if [ "$1" == "-z" ]; then
	adb uninstall org.sqlunet.browser
fi

adb install -r browser-release.apk
