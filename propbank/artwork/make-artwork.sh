#!/bin/bash

source "./lib-artwork.sh"

icon_list="propbank.svg alias.svg theta.svg trace.svg"
asset_list=""
search_list="ic_search_pbexample.svg"

make_res "${icon_list}" 16
make_res "${search_list}" 32

emptydir "${dirassets}/images/propbank"
make_asset "${asset_list}" 16 "${dirassets}/images/propbank"

check
