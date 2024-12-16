#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

whereto="generated"
whereto=`readlink -m "${whereto}"`
mkdir -p "${whereto}"

./make-all-artwork.sh "${whereto}"
./make-all-html.sh "${whereto}"

echo 'copy relations'
cp -R wordnet/relations/html/* "${whereto}/relations"
