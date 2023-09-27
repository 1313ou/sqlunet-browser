#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

wherefrom="artwork"
wherefrom=`readlink -m "${wherefrom}"`

whereto="$1"
if [ -z "${whereto}" ]; then
	whereto="assets/images"
fi
whereto=`readlink -m "${whereto}"`
mkdir -p ${whereto}

pos="pos pos.n pos.v pos.a pos.s pos.r"
utils="focus category sense synonym synset members links item other ${pos}"
utils2="reflexive semantic lexical"

l2="${l1} ${utils2}"
res=30
for img in ${l2}; do
	echo "make ${img}.png -> ${whereto}/${img}.png"
	inkscape ${wherefrom}/${img}.svg --export-type="png" --export-filename=${whereto}/${img}.png -h ${res} #> /dev/null 2> /dev/null
done
cp menu.png ${whereto}
