#!/bin/bash

source "./lib-artwork.sh"

icon_list="framenet.svg role1.svg rolex.svg annoset.svg coreset.svg governor.svg grouprealization.svg layer.svg metadefinition.svg realization.svg semtype.svg"
asset_list=""
search_list="ic_search_fnsentence.svg"

make_res "${icon_list}" 16
make_res "${search_list}" 32

make_clear_asset "${asset_list}" 16 "${dirassets}/images/framenet"

check
