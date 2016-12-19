#!/bin/bash

#target
t=$1
if [ -z "$t" ]; then
	read -p 'item >' t
fi

./xsl-transform.sh ${t}.html ${t}-top.html top.xsl xml
./xsl-transform.sh ${t}.html ${t}-top.html top.xsl xml

