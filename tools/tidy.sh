#!/bin/bash

if [ -z "$@" ]; then
	what=*.xml
else
	what=$@
fi

for f in $what; do
	tidy -i -m -xml -config tidy.conf $f
done
