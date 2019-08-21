#!/bin/bash

source "./lib-artwork.sh"

icon_list="morph.svg adjposition.svg verbframesentence.svg verbframe.svg"
asset_list=""

make_res "${icon_list}" 16

#rm ${dirassets}/images/wordnet/*
make_icon "${asset_list}" 16 "${dirassets}/images/wordnet"

check
