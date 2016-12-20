#!/bin/bash

#target
t=$1
if [ -z "$t" ]; then
	read -p 'item >' t
fi

xsl="wordnet2html-ns.xsl"
xout=-
xout=$t-fragment.html
xin=$t.xml

#for xin in $@; do
	./xsl-transform.sh $xin $xout $xsl html
	#read -p 'done '
#done
