#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

source define_colors.sh
source find-script-artwork.sh

MAKE=true
if [ "$1" == "-C" ]; then
	MAKE=
fi

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"

if [ ! -z "${MAKE}" ]; then
  echo "M A K I N G"
	for s in ${scripts}; do
		d=`dirname $s`
		echo -e "${Y}${d}${Z}"
		pushd ${d} > /dev/null
		./make-artwork.sh
		popd > /dev/null
	done
fi

source ./lib-artwork.sh

echo "C H E C K I N G"
modules=`for s in ${scripts}; do echo $(readlink -m $(dirname $s)/..); done | sort -u`
for m in ${modules}; do
	if [[ "${m}" =~ .*common/reference.* ]]; then
		continue
	fi
	echo -e "${B}${m}${Z}"
	d=$m/src/main
	if [ ! -e "${d}" ]; then
		echo -e "${d} ${R}!EXISTS${Z}"
		continue
	fi
	find ${d} -name 'selector*.png' -exec touch {} \;
	find ${d} -name 'drawer*.png' -exec touch {} \;
	check_dir ${d}
done

