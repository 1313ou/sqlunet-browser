#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

whereto="reference"
whereto=`readlink -m "${whereto}"`
mkdir -p "${whereto}"

./make-all-artwork.sh "${whereto}"
./make-all-html.sh "${whereto}"

