#!/bin/bash

if [ -z "$@" ]; then
	what=*.xml
else
	what=$@
fi

for f in $what; do
	tidy -i -m -xml $f
done
