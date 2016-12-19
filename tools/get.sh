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

adb pull $sp
rm $tp
mv $s $tp
tidy -i -m -xml $tp 2> /dev/null
