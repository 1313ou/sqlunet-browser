#!/bin/bash

whereto=$1
whereto=`readlink -m "${whereto}"`

mkdir -p "${whereto}/images"
mkdir -p "${whereto}/relations/images"

for d in xnet wordnet verbnet propbank framenet bnc syntagnet; do
	./make-artwork.sh $d "${whereto}/images"
done

pushd wordnet/relations > /dev/null
	./make-artwork.sh "${whereto}/relations/images"
popd > /dev/null
