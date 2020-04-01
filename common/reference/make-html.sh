#!/bin/bash

R='\u001b[31m'
G='\u001b[32m'
B='\u001b[34m'
Y='\u001b[33m'
M='\u001b[35m'
C='\u001b[36m'
Z='\u001b[0m'

if [ -z "$1" ]; then
	exit 1
fi
wherefrom="$1/xml"
wherefrom=`readlink -m "${wherefrom}"`

whereto="$2"
if [ -z "${whereto}" ]; then
	whereto="${wherefrom}/.."
fi
whereto=`readlink -m "${whereto}"`

xsl="concept2html.xsl"
xsl=`readlink -m "${xsl}"`

for xml in ${wherefrom}/*.xml; do
	echo -e "${M}XST ${xml}${Z}"
	outfile=`basename ${xml}`
	outfile=${outfile%.*}.html
	if ! ./xsl-transform.sh "${xml}" "${whereto}/${outfile}" "$xsl" html; then
		echo -e "${R}XST ${xml}${Z}"
	fi
done

