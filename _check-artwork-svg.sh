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
subdirs="${dirapp} ${dirres} ${dirresources} ${dirassets} ${dirhelp} ${dirhelpen}"
subdirs="${dirres}"

# map scripts to their container dir
dirs=`for s in ${scripts}; do echo $(readlink -m $(dirname $s)); done | sort -u`

for d in ${dirs}; do
	pushd ${d} > /dev/null
	echo -e "${Y}${d}${Z}"

	#try
	for d2 in ${subdirs}; do
		echo -e "${M}${d2}${Z}"
		r=$(readlink -m "${d2}")
		if [ ! -e "${r}" ]; then
			continue
		fi
		pushd "${r}" > /dev/null
		for i in *.png; do
			f=$(basename "${i}")
			b="${f%.*}"
			# nine patch have no svg
			if [[ "${b}" =~ .*\.9 ]]; then
				continue
			fi
			svg="${b}.svg"
			if [ ! -e "${d}/${svg}" ]; then
				echo -e "${svg} ${R}!EXISTS${Z}"
			fi
		done
		popd > /dev/null
	done
	popd > /dev/null
done

