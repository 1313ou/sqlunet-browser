#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

set -e

source ./define_wordnet.sh
source ./define_wordnet_relations.sh
source ./define_colors.sh

# args
if [ "-31" == "$1" ]; then
        wn31="31"
        selected_relations="${all_relations_wn31}"
	[ "$#" -eq 0 ] || shift
else
        selected_relations="${all_relations}"
fi
whereto="$1"
[ "$#" -eq 0 ] || shift

artworkdir="artwork"
generateddir="generated"
imagesdir="images"

wherefrom=${artworkdir}
if [ -z "${whereto}" ]; then
	whereto="${generateddir}${wn31}/${imagesdir}"
fi
wherefrom=`readlink -m "${wherefrom}"`
echo -e "${C}${whereto}${Z}"
whereto=`readlink -m "${whereto}"`

mkdir -p ${whereto}
rm -f "${whereto}"/*.png

utils="menu"

function svgs2pngs(){
	local r=$1
	[ "$#" -eq 0 ] || shift
	local from="$1"
	[ "$#" -eq 0 ] || shift
	local to="$1"
	[ "$#" -eq 0 ] || shift
	local list="$@"
	local aspect=h
	for e in ${list}; do
		png=${e}.png
		svg=${from}/${e}.svg
		echo -en "${C}icon${Z} ${M}${e}${Z}${K} -> ${png}${Z}"
		$INKSCAPE --export-type="png" --export-filename="${to}/${png}" -${aspect} ${r} ${svg} > /dev/null # 2> /dev/null
                if [ -e "${to}/${png}" ]; then
			echo -e " ${GREEN} OK${Z}"
		else
			echo -e " ${RED} FAIL${Z}"
		fi
	done
}

svgs2pngs 30 "${wherefrom}"           "${whereto}" ${all_wordnet}
svgs2pngs 30 "${wherefrom}/relations" "${whereto}" ${selected_relations}
svgs2pngs 24 "${wherefrom}"           "${whereto}" ${utils}
#cp           splash.png               "${whereto}"

