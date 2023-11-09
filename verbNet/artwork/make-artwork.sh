#!/bin/bash

source "./lib-artwork.sh"

icon_list="vnframe.svg syntax.svg semantics.svg grouping.svg"
asset_list="vnframe.svg syntax.svg semantics.svg restr.svg synsetspecific.svg"
search_list="ic_search_vnexample.svg"

make_res "${icon_list}" 16
make_res "${search_list}" 32

make_clear_asset "${asset_list}" 16 "${dirassets}/images/verbnet"

check
