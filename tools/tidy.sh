#!/bin/bash

if [ -z "$@" ]; then
	what=*.xml
else
	what=$@
fi

for f in $what; do
	echo tidy $f
	tidy -quiet -i -m -xml -config tidy.conf $f
done
