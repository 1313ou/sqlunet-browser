#!/bin/bash

PACKAGE="$1"
APP="$2"
RELEASE_NAME="$3"
if [ -z "${RELEASE_NAME}" ]; then
	RELEASE_NAME="Anew"
	echo "Version name ${RELEASE_NAME}"
fi
RECENT_CHANGES="$4"
if [ -z "${RECENT_CHANGES}" ]; then
	RECENT_CHANGES="Fixes"
fi

DIR=dist/releases
TODIR=dist/releases/mappings

RED='\u001b[31m'
GREEN='\u001b[32m'
YELLOW='\u001b[33m'
BLUE='\u001b[34m'
MAGENTA='\u001b[35m'
CYAN='\u001b[36m'
RESET='\u001b[0m'

function get_version() {
	local apk="$1"
	local n=$(aapt l -a ${apk} | egrep versionCode | sed 's/^.*(type 0x10)/ /g')
	n=$((${n}))
	echo ${n}
}

function save() {
	local from="$1"
	local app="$2"
	local code="$3"
	todir="${TODIR}/app_${app}"
	mkdir -p ${todir}
	to=${todir}/${code}-mapping.txt
	if [ ! -e ${from} ]; then
		echo -e "${from} ${RED}!EXISTS${RESET}"
		continue
	else
		echo "${from} -> ${to}"
	fi
	cp -p ${from} ${to}
	echo -en "${GREEN}"
	stat -c '%n %s %y' ${to}
	echo -en "${RESET}"
}

APK_ARM64=${DIR}/app_${APP}-arm64-v8a-release.apk
APK_ARMEABI=${DIR}/app_${APP}-armeabi-v7a-release.apk
APK_X86_64=${DIR}/app_${APP}-x86_64-release.apk
APK_X86=${DIR}/app_${APP}-x86-release.apk
APK_VERSION_CODE_ARM64=`get_version ${APK_ARM64}`
APK_VERSION_CODE_ARMEABI=`get_version ${APK_ARMEABI}`
APK_VERSION_CODE_X86_64=`get_version ${APK_X86_64}`
APK_VERSION_CODE_X86=`get_version ${APK_X86}`
echo -e "${MAGENTA}arm64  ${BLUE}${APK_VERSION_CODE_ARM64}${RESET} ${APK_ARM64}"
echo -e "${MAGENTA}armABI ${BLUE}${APK_VERSION_CODE_ARMEABI}${RESET} ${APK_ARMEABI}"
echo -e "${MAGENTA}x86_64 ${BLUE}${APK_VERSION_CODE_X86_64}${RESET} ${APK_X86_64}"
echo -e "${MAGENTA}x86    ${BLUE}${APK_VERSION_CODE_X86}${RESET} ${APK_X86}"

echo -e "${CYAN}Upload apks${RESET}"
python googleplay_upload_apks.py \
	${PACKAGE} \
	"${RELEASE_NAME}" \
	"${RECENT_CHANGES}" \
	${APK_ARM64} \
	${APK_ARMEABI} \
	${APK_X86_64} \
	${APK_X86}

exit

echo -e "${CYAN}Upload mappings${RESET}"
DEOBFUSCATION_FILE=app_${APP}/build/outputs/mapping/release/mapping.txt
#echo "deobfuscation file=${DEOBFUSCATION_FILE}"
if [ ! -e "${DEOBFUSCATION_FILE}" ]; then
	echo -e "${DEOBFUSCATION_FILE} ${YELLOW}!EXISTS${RESET}"
	exit
fi
python googleplay_upload_deobfuscation_files.py \
	--apk_version_code ${APK_VERSION_CODE_ARM64} \
	--apk_version_code ${APK_VERSION_CODE_ARMEABI} \
	--apk_version_code ${APK_VERSION_CODE_X86_64} \
	--apk_version_code ${APK_VERSION_CODE_X86} \
	${PACKAGE} \
	"${DEOBFUSCATION_FILE}"

echo -e "${CYAN}Save mappings${RESET}"
CODE=${APK_VERSION_CODE_ARM64:2}
save "${DEOBFUSCATION_FILE}" ${APP} ${CODE}

