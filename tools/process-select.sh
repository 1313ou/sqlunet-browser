#!/bin/bash

#domains
domains="wn vn pb fn"
if [ ! -z "$2" ];then
	domains=$2
fi

#target
t=$1
if [ -z "$t" ]; then
	read -p 'item >' t
fi
tp=select-$t.xml

xin=select-$t
for d in $domains; do
	case $d in
		wn) xsl=wordnet2html-select;;
		vn) xsl=verbnet2html-select;;
		pb) xsl=propbank2html-select;;
		fn) xsl=framenet2html-select;;
	esac
	xout="$d-$t"

	echo TRANSFORM $xin.xml TO $xout-fragment.html WITH $xsl-ns.xsl
	./xsl-transform.sh $xin.xml $xout-fragment.html $xsl-ns.xsl xml

	echo EMBED $xout-fragment.html TO $xout.html
	./embed.sh $xout

	echo TIDY $xout.html
	#tidy -i -o /dev/null -ashtml $xout.html
	tidy -quiet -i -m -ashtml $xout.html
	#read -p 'done '
done
