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

hierarchy="hypernym hyponym instance.hypernym instance.hyponym holonym meronym member.holonym member.meronym part.holonym part.meronym substance.holonym substance.meronym"
lex="antonym"
verb="causes caused entails entailed verb.group participle"
adj="similar attribute"
adv="adjderived"
deriv="derivation"
misc="alsosee pertainym synonym"
domain="domain domain.member domain.topic domain.member.topic domain.region domain.member.region domain.term domain.member.term exemplifies is_exemplified_by"

pos="pos pos.n pos.v pos.a pos.s pos.r"
utils="focus category sense synonym synset members links item other ${pos}"
utils2="reflexive semantic lexical"

l1="${hierarchy} ${lex} ${verb} ${adj} ${adv} ${deriv} ${misc} ${domain} ${utils}"
l2="${l1} ${utils2}"
#echo $l1
#echo $l2

#declare -A res
#res=([mdpi]=48 [hdpi]=72 [xhdpi]=96 [xxhdpi]=144)

res=30
for img in ${l2}; do
	echo "make ${img}.png -> ${whereto}/${img}.png"
	inkscape ${wherefrom}/${img}.svg --export-png=${whereto}/${img}.png -h${res} > /dev/null 2> /dev/null
done
cp menu.png ${whereto}
