#!/bin/bash

source "./lib-artwork.sh"

icon_list="collocation.svg collocation1.svg collocation2.svg definition1.svg definition2.svg"
asset_list="collocation.svg"

make_res "${icon_list}" 16

rm ${dirassets}/images/syntagnet/*
make_icon "${asset_list}" 16 "${dirassets}/images/syntagnet"

check
