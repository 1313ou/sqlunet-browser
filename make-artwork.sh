#!/bin/bash

MAKE=true
if [ "$1" == "-checkonly" ]; then
	MAKE=
fi

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"

source ./lib-artwork.sh

scripts=`find . -mindepth 2 -name 'make-artwork.sh'`
modules=`for s in ${scripts}; do echo $(readlink -m $(dirname $s)/..); done | sort -u`
if [ ! -z "${MAKE}" ]; then
	for s in ${scripts}; do
		d=`dirname $s`
		echo -e "${MAGENTA}${d}${RESET}"
		pushd ${d} > /dev/null
		./make-artwork.sh
		popd > /dev/null
	done
fi

echo "C H E C K I N G"
for m in ${modules}; do
	if [[ "${m}" =~ .*common/reference.* ]]; then
		continue
	fi
	echo -e "${BLUE}${m}${RESET}"
	d=$m/src/main
	if [ ! -e "${d}" ]; then
		echo -e "${d} ${YELLOW}!EXISTS${RESET}"
		continue
	fi
	find ${d} -name 'selector*.png' -exec touch {} \;
	find ${d} -name 'drawer*.png' -exec touch {} \;
	check_dir ${d}
done

