#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"

source ./lib-artwork.sh

scripts=`find . -mindepth 2 -name 'make-artwork.sh'`
modules=`for s in ${scripts}; do echo $(readlink -m $(dirname $s)/..); done | sort -u`

for s in ${scripts}; do
	d=`dirname $s`
	echo -e "${MAGENTA}${d}${RESET}"
	pushd ${d} > /dev/null
	./make-artwork.sh
	popd > /dev/null
done

echo "C H E C K I N G"
for m in ${modules}; do
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

