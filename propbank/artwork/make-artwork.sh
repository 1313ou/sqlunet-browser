#!/bin/bash

source "./lib-artwork.sh"

icon_list="alias.svg theta.svg trace.svg"
asset_list=""

make_res "${icon_list}" 16

rm ${dirassets}/images/propbank/*
make_icon "${asset_list}" 16 "${dirassets}/images/propbank"

check
