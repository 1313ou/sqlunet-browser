#!/bin/bash

source "./lib-artwork.sh"

icon_list=""
asset_list=""
search_list="ic_search_pm.svg"

make_res "${icon_list}" 16
make_res "${search_list}" 32

rm ${dirassets}/images/framenet/*
make_icon "${asset_list}" 16 "${dirassets}/images/predicatematrix"

check

