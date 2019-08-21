#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"

RED='\u001b[31m'
GREEN='\u001b[32m'
YELLOW='\u001b[33m'
BLUE='\u001b[34m'
MAGENTA='\u001b[35m'
CYAN='\u001b[36m'
RESET='\u001b[0m'

res=`find . -type d -path './*/src/main/resources'`

for r in ${res}; do

	scripts=`find ${r} -name '*.xsl'`
	for s in ${scripts}; do
		d=`dirname $s`
		echo -e "${MAGENTA}${d}${RESET}"
		echo -e "${YELLOW}${s}${RESET}"
		grep -o '[^\"]*png' ${s} | sort -u
	done

done

