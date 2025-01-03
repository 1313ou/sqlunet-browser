#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

set -e

source ./define_colors.sh

echo -e "${Y}OEWN${Z}"
./make-html.sh
./make-artwork.sh

echo -e "${Y}WN31${Z}"
./make-html.sh -31
./make-artwork.sh -31
