#!/bin/bash

in=$1
out=$2
xsl=$3
html=$4
dtd=$5
#echo "$in -> $out ($xsl)" 
java -classpath "xmltoolkit.jar" xml.toolkit.Main $in $out $xsl $html $dtd
