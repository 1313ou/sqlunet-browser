#!/bin/bash

source "./lib-artwork.sh"

icon_list="vnframe.svg syntax.svg semantics.svg restr.svg grouping.svg"
asset_list="vnframe.svg syntax.svg semantics.svg restr.svg synsetspecific.svg"
search_list="ic_search_vnexample.svg"

make_res "${icon_list}" 16
make_res "${search_list}" 32

rm ${dirassets}/images/verbnet/*
make_icon "${asset_list}" 16 "${dirassets}/images/verbnet"

check
