#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

source ./define_colors.sh

wheretorel="generated"
whereto=`readlink -m "${wheretorel}"`
mkdir -p "${whereto}"

echo -e "${Y}make modules to ${wheretorel}${Z}"
./make-all-html.sh "${whereto}"
./make-all-artwork.sh "${whereto}"

echo -e "${Y}make modules to ${wheretorel}31${Z}"
./make-all-html.sh "${whereto}31"
./make-all-artwork.sh "${whereto}31"

echo -e "${Y}make relations${Z}"
pushd wordnet/relations > /dev/null
./make.sh
popd > /dev/null

echo  -e "${Y}copy relations to ${wheretorel}${Z}"
cp -RT wordnet/relations/generated   "${whereto}/relations"

echo  -e "${Y}copy relations to ${wheretorel}31${Z}"
cp -RT wordnet/relations/generated31 "${whereto}31/relations"

