#!/bin/bash

set -e

dirtest=test
dirsrc=composite

source lib-artwork.sh

# T E S T

testres=128
echo -e "${Y}Temp${Z} ${B}$r ${testres}${Z}"
dirdest="${dirtest}"
mkdir -p ${dirdest}
lists_svg2png ${testres} "${dirsrc}" "${dirdest}" ""

