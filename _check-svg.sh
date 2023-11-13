#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

source define_colors.sh
source find-script-artwork.sh

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"

dirapp=..
dirres=../src/main/res/drawable-hdpi
dirresources=../src/main/resources
dirassets=../src/main/assets
dirhelp=../src/main/assets/help/images
dirhelpen=../src/main/assets/help/en/images

function tidysvg(){
	local f="$1"
	echo -e "${M}${f}${Z}"
	svgo "${f}"
}

# map scripts to their container dir
dirs=`for s in ${scripts}; do echo $(readlink -m $(dirname $s)); done | sort -u`

for d in ${dirs}; do
	pushd ${d} > /dev/null
	echo -e "${Y}${d}${Z}"
	#find . -name "*.svg" -exec tidysvg {} \;
	#find . -name "*.svg" | xargs tidysvg
	while read f; do tidysvg "${f}"; done < <(find . -name "*.svg")
	popd > /dev/null
done

