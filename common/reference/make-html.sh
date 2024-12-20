#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

source ./define_colors.sh

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
	src=`basename ${xml}`
	stem=${src%.*}
	html=${stem}.html
	dst=`basename ${html}`
	trans=`basename ${xsl}`
	echo -en "${C}xslt${Z} ${M}${src}${Z}${K}-${trans}->${dst}${Z}"
	if ! ./xsl-transform.sh "${xml}" "${whereto}/${html}" "${xsl}" html; then
		echo -e "${R} XST failed ${xml}${Z}"
	else
	        echo -e "${G} OK${Z}"
	fi
done

