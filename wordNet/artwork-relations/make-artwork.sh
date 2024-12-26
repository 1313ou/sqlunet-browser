#!/bin/bash

set -e

dirres=../src/main/res
dirassets=../src/main/assets
dirsrc=composite

source lib-artwork.sh

# C O M P O S I T E S

./make-artwork-composite-svg.sh

# A S S E T S

echo -e "${Y}Assets${Z} ${B}$r ${webres}${Z}"
dirdest="${dirassets}/images/wordnet"
mkdir -p ${dirdest}
lists_svg2png ${webres} "${dirsrc}" "${dirdest}" ""

# R E S O U R C E S

for r in ${!res[@]}; do
	rv=${res[$r]}
	echo -e "${M}Resources${Z} ${B}$r ${res[$r]}${Z}"
	dirdest="${dirres}/drawable-${r}"
	mkdir -p ${dirdest}
	lists_svg2png "${rv}" "${dirsrc}" "${dirdest}" "ic_"
done
