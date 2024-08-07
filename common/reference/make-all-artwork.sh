#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

whereto=$1
whereto=`readlink -m "${whereto}"`

mkdir -p "${whereto}/images"
mkdir -p "${whereto}/relations/images"

for d in xnet wordnet verbnet propbank framenet bnc syntagnet; do
	./make-artwork.sh $d "${whereto}/images"
done

