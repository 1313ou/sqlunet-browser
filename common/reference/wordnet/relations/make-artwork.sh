#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

wherefrom="artwork"
wherefrom=`readlink -m "${wherefrom}"`

whereto="$1"
if [ -z "${whereto}" ]; then
	whereto="html/images"
fi
whereto=`readlink -m "${whereto}"`
mkdir -p ${whereto}

utils1="focus category sense synset members relations item"
utils2="reflexive semantic lexical"

utils="${utils1} ${utils2}"
r=30
d=${whereto}
aspect=h
for img in ${utils}; do
	png=${img}.png
	svg=${wherefrom}/${img}.svg
	echo "make ${png} -> ${d}/${png}"
	$INKSCAPE --export-type="png" --export-filename=${d}/${png} -${aspect} ${r} ${svg} > /dev/null # 2> /dev/null
done
cp menu.png ${whereto}
