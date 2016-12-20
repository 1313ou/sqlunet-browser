#!/bin/bash

#target
t=$1
if [ -z "$t" ]; then
	read -p 'item >' t
fi

./xsl-transform.sh ${t}-fragment.html ${t}.html embed.xsl xml

