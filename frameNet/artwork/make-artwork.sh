#!/bin/bash

source "./lib-artwork.sh"

icon_list="annoset.svg coreset.svg governor.svg grouprealization.svg label1.svg label2.svg layer.svg metadefinition.svg realization.svg semtype.svg"
asset_list=""
search_list="ic_search_fnsentence.svg"

make_res "${icon_list}" 16
make_res "${search_list}" 32

rm ${dirassets}/images/framenet/*
make_icon "${asset_list}" 16 "${dirassets}/images/framenet"

check

