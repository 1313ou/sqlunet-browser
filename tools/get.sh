#!/bin/bash

# target
t=$1
if [ -z "$t" ]; then
	read -p 'item >' t
fi

# destination
dp=$t.xml

# source
s=$2
if [ -z "$s" ]; then
	s=sqlunet.log
fi
sp=/storage/emulated/legacy/$s

echo "$sp -> $dp"

adb pull $sp
rm $dp
mv $s $dp
#tidy -i -m -xml $dp 2> /dev/null
