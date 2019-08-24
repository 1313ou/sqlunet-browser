#!/bin/bash

RELEASE=build/outputs/apk/release
all="
app_grammarscope_udpipe/${RELEASE}/app_grammarscope_udpipe-x86_64-release.apk
app_grammarscope_udpipe/${RELEASE}/app_grammarscope_udpipe-arm64-v8a-release.apk
app_grammarscope_udpipe/${RELEASE}/app_grammarscope_udpipe-x86-release.apk
app_grammarscope_udpipe/${RELEASE}/app_grammarscope_udpipe-armeabi-v7a-release.apk

app_mysyntaxnet/${RELEASE}/app_mysyntaxnet-armeabi-v7a-release.apk
app_mysyntaxnet/${RELEASE}/app_mysyntaxnet-arm64-v8a-release.apk
app_mysyntaxnet/${RELEASE}/app_mysyntaxnet-x86_64-release.apk
app_mysyntaxnet/${RELEASE}/app_mysyntaxnet-x86-release.apk

app_grammarscope_syntaxnet/${RELEASE}/app_grammarscope_syntaxnet-arm64-v8a-release.apk
app_grammarscope_syntaxnet/${RELEASE}/app_grammarscope_syntaxnet-armeabi-v7a-release.apk
app_grammarscope_syntaxnet/${RELEASE}/app_grammarscope_syntaxnet-x86-release.apk
app_grammarscope_syntaxnet/${RELEASE}/app_grammarscope_syntaxnet-x86_64-release.apk
"

RED='\u001b[31m'
GREEN='\u001b[32m'
YELLOW='\u001b[33m'
BLUE='\u001b[34m'
MAGENTA='\u001b[35m'
CYAN='\u001b[36m'
RESET='\u001b[0m'

function get_apks() {
	for apk in $all; do
		src=`readlink -m ${apk}`
		>&2 echo -n ${src}
		if [ -e "${src}" ]; then
			echo "${src}"
			>&2 echo -e "${GREEN} EXISTS${RESET}"
		else
			>&2 echo -e "${YELLOW} !EXISTS${RESET}"
		fi
	done
}

#get_apks

export APKS=`get_apks`
echo -e "${BLUE}${APKS}${RESET}"
