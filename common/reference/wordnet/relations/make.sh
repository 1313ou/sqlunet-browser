#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

set -e

R='\u001b[31m'
G='\u001b[32m'
B='\u001b[34m'
Y='\u001b[33m'
M='\u001b[35m'
C='\u001b[36m'
Z='\u001b[0m'

echo -e "${Y}OEWN${Z}"
./make-html.sh
./make-artwork.sh

echo -e "${Y}WN31${Z}"
./make-html.sh -31
./make-artwork.sh -31

