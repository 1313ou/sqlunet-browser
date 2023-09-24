#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

source "./lib-artwork.sh"

if [ -z "$1" ]; then
	echo "Specify dir"
	exit 1
fi
wherefrom="$1/artwork"
wherefrom=`readlink -m "${wherefrom}"`

whereto="$2"
if [ -z "${whereto}" ]; then
	whereto="${wherefrom}/../images"
fi
whereto=`readlink -m "${whereto}"`

pushd "${wherefrom}" > /dev/null
mkdir -p "${whereto}"
for f in *.svg; do
	make_icon "${f}" 32 "${whereto}" h
done
popd > /dev/null

