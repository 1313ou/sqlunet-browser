#!/bin/bash

source "./lib-artwork.sh"

icon_list="verbnet.svg vnframe.svg syntax.svg semantics.svg grouping.svg"
asset_list="closed.svg open.svg pointer.svg"
asset_list_verbnet="verbnet.svg vnframe.svg syntax.svg semantics.svg restr.svg synsetspecific.svg"
asset_list_xnet="roleclass.svg roles.svg role.svg definition.svg sample.svg"
search_list="ic_search_vnexample.svg"

make_res "${icon_list}" 16
make_res "${search_list}" 32

make_clear_asset "${asset_list}" 16 "${dirassets}/images"
make_clear_asset "${asset_list_verbnet}" 16 "${dirassets}/images/verbnet"
make_clear_asset "${asset_list_xnet}" 16 "${dirassets}/images/xnet"

check

