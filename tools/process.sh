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

#./get.sh $t
#./tohtml-wordnet2html.sh $t

xin=$t
for d in wn vn pb fn; do
	case $d in
		wn) xsl=wordnet2html;;
		vn) xsl=verbnet2html;;
		pb) xsl=propbank2html;;
		fn) xsl=framenet2html;;
	esac
	xout="$d-$t"
	echo $xsl $xin $xout
	./xsl-transform.sh $xin.xml $xout-fragment.html $xsl-ns.xsl html
	./embed.sh $xout
	#read -p 'done '
done
