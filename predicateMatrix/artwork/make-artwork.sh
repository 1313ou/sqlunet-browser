#!/bin/bash

source "./lib-artwork.sh"

icon_list="predicatematrix.svg verbnet.svg propbank.svg framenet.svg synset.svg"
asset_list=""
search_list="ic_search_pm.svg"

make_res "${icon_list}" 16
make_res "${search_list}" 32

make_clear_asset "${asset_list}" 16 "${dirassets}/images/predicatematrix"

check

