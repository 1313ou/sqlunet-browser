#!/bin/bash

source "./lib-artwork.sh"

icon_list="propbank.svg alias.svg vnrole.svg fnfe.svg trace.svg"
asset_list="closed.svg open.svg pointer.svg"
asset_list_propbank="propbank.svg"
asset_list_xnet="roleclass.svg role.svg relation.svg definition.svg sample.svg"
search_list="ic_search_pbexample.svg"

make_res "${icon_list}" 16
make_res "${search_list}" 32

make_clear_asset "${asset_list}" 16 "${dirassets}/images"
make_clear_asset "${asset_list_propbank}" 16 "${dirassets}/images/propbank"
make_clear_asset "${asset_list_xnet}" 16 "${dirassets}/images/xnet"

check

