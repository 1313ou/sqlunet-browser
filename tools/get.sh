#!/bin/bash

#source
s=sqlunet.log

#target
t=$1
if [ -z "$t" ]; then
	read -p 'item >' t
fi
b=$2
tp=$b$t.xml

sp=/storage/emulated/legacy/$b$s

echo "$sp -> $tp"

adb pull $sp
rm $tp
mv $b$s $tp
#tidy -i -m -xml $tp 2> /dev/null
