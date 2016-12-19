#!/bin/bash

out=-
xsl="wordnet2html.xsl"

for f in $@; do
	./xsl-transform.sh $f $out $xsl html
	#read -p 'done '
done
