#!/bin/bash

source "./lib-artwork.sh"

icon_list="predicatematrix.svg verbnet.svg propbank.svg framenet.svg synset.svg"
asset_list="closed.svg open.svg pointer.svg"
asset_list_predicatematrix="predicatematrix.svg"
asset_list_xnet="roles.svg role.svg"
search_list="ic_search_pm.svg"

make_res "${icon_list}" 16
make_res "${search_list}" 32


make_clear_asset "${asset_list}" 16 "${dirassets}/images"
make_clear_asset "${asset_list_predicatematrix}" 16 "${dirassets}/images/predicatematrix"
make_clear_asset "${asset_list_xnet}" 16 "${dirassets}/images/xnet"

check

