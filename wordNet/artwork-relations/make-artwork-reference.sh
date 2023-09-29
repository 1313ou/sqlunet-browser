#!/bin/bash

if [ "-31" == "$1" ]; then
	wn31="31"
	shift
fi

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"
dirreference=../../common/reference/wordnet/relations/html${wn31}
dirsrc=composite

source lib-artwork.sh

# R E F E R E N C E

echo -e "${Y}Reference${Z} ${B}$r ${webres}${Z}"
dirdest="${dirreference}/images"
mkdir -p ${dirdest}
lists_svg2png${wn31} ${referenceres} "${dirsrc}" "${dirdest}" ""

