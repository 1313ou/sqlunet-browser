#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

source define_colors.sh

dirs="$@"
if [ -z "${dirs}" ]; then
    dirs=$(./find-artwork.sh)
fi

function tidysvg(){
	local f="$1"
	echo -e "${M}${f}${Z}"
	svgo "${f}"
}

for d in ${dirs}; do
	pushd ${d} > /dev/null
	echo -e "${Y}${d}${Z}"
	#find . -name "*.svg" -exec tidysvg {} \;
	#find . -name "*.svg" | xargs tidysvg
	while read f; do tidysvg "${f}"; done < <(find . -name "*.svg")
	popd > /dev/null
done

