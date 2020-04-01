#!/bin/bash

whereto="reference"
whereto=`readlink -m "${whereto}"`
mkdir -p "${whereto}"

./make-all-artwork.sh "${whereto}"
./make-all-html.sh "${whereto}"

