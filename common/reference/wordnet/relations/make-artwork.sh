#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

R='\u001b[31m'
G='\u001b[32m'
B='\u001b[34m'
Y='\u001b[33m'
M='\u001b[35m'
C='\u001b[36m'
Z='\u001b[0m'

wherefrom="artwork"
wherefrom=`readlink -m "${wherefrom}"`

if [ "-31" == "$1" ]; then
	wn31="31"
	shift
fi

whereto="$1"
if [ -z "${whereto}" ]; then
	whereto="html${wn31}/images"
fi
whereto=`readlink -m "${whereto}"`
mkdir -p ${whereto}

utils1="focus category sense synset members relations item"
utils2="reflexive semantic lexical"
utils3="menu"

function svgs2pngs(){
	local r=$1
	shift
	local list="$@"
	local d=${whereto}
	local aspect=h
	for e in ${list}; do
		png=${e}.png
		svg=${wherefrom}/${e}.svg
		echo -e "${M}${e}${Z} -> ${d}/${png}"
		$INKSCAPE --export-type="png" --export-filename=${d}/${png} -${aspect} ${r} ${svg} > /dev/null # 2> /dev/null
	done
}

svgs2pngs 30 ${utils1} ${utils2}
svgs2pngs 24 ${utils3}

