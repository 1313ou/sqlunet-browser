#!/bin/bash

source "./lib-artwork.sh"

icon_list="framenet.svg role1.svg rolex.svg annoset.svg coreset.svg governor.svg grouprealization.svg layer.svg metadefinition.svg realization.svg semtype.svg"
asset_list="closed.svg open.svg pointer.svg"
asset_list_framenet="framenet.svg"
asset_list_xnet="member.svg roleclass.svg role.svg rolex.svg definition.svg sentence.svg sample.svg"
search_list="ic_search_fnsentence.svg"

make_res "${icon_list}" 16
make_res "${search_list}" 32

make_clear_asset "${asset_list}" 16 "${dirassets}/images"
make_clear_asset "${asset_list_framenet}" 16 "${dirassets}/images/framenet"
make_clear_asset "${asset_list_xnet}" 16 "${dirassets}/images/xnet"

check

