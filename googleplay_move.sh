#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

PACKAGE="$1"
if [ -z "${PACKAGE}" ]; then
	echo 'Missing package'
	exit 1
fi
VERSION_CODE="$2"
if [ -z "${VERSION_CODE}" ]; then
	echo 'Missing version code'
	exit 1
fi
TRACK="$3"
if [ -z "${TRACK}" ]; then
	echo 'Missing track'
	exit 1
fi
RELEASE_NAME="$4"
if [ -z "${RELEASE_NAME}" ]; then
	RELEASE_NAME="B${VERSION_CODE}"
fi
RECENT_CHANGES="$5"
if [ -z "${RECENT_CHANGES}" ]; then
	RECENT_CHANGES="Fixes"
fi

echo "PACKAGE        ${PACKAGE}"
echo "VERSION_CODE   ${VERSION_CODE}"
echo "TRACK          ${TRACK}"
echo "RELEASE_NAME   ${RELEASE_NAME}"
echo "RECENT_CHANGES ${RECENT_CHANGES}"
read -p "Are you sure? " -n 1 -r
echo    # (optional) move to a new line
if [[ $REPLY =~ ^[Yy]$ ]]; then
	python2 googleplay_move.py \
		${PACKAGE} \
		${TRACK} \
		"${RELEASE_NAME}" \
		"${RECENT_CHANGES}" \
		${VERSION_CODE}
fi

