#!/bin/bash

#source
s=sqlunet.log
sp=/storage/emulated/legacy/$s

#target
t=$1
if [ -z "$t" ]; then
	read -p 'item >' t
fi
tp=$t.xml

echo "$sp -> $tp"

./get.sh $t
./tohtml-wordnet2html.sh $t
./embed.sh $t
