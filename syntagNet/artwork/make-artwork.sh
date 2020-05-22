#!/bin/bash

source "./lib-artwork.sh"

icon_list="before.svg after.svg"
asset_list=""

make_res "${icon_list}" 16

rm ${dirassets}/images/syntagnet/*
make_icon "${asset_list}" 16 "${dirassets}/images/syntagnet"

check
