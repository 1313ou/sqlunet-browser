#!/bin/bash

whereto=$1
whereto=`readlink -m "${whereto}"`

mkdir -p "${whereto}"
mkdir -p "${whereto}/relations"

for d in xnet wordnet verbnet propbank framenet bnc; do
	./make-html.sh $d "${whereto}"
done
cp style.css ${whereto}

pushd wordnet/relations > /dev/null
	./make-html.sh "${whereto}/relations"
popd > /dev/null
