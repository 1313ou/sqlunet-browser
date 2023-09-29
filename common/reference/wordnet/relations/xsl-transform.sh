#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

in=$1
out=$2
xsl=$3
html=$4
dtd=$5
echo "$in -> $out ($xsl)" 
java -classpath "XsltTransformer.jar" bsys.xml.transformer.Main $in $out $xsl $html $dtd

