#!/bin/bash

if [ "$1" == "-z" ]; then
	adb uninstall org.sqlunet.browser
	adb uninstall org.sqlunet.browser.fn
fi

adb install -r browser-release.apk
adb install -r browserfn-release.apk
