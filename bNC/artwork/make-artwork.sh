#!/bin/bash

source "./lib-artwork.sh"

icon_list="bnc.svg convtask.svg imaginf.svg spwr.svg"
asset_list="closed.svg open.svg"
asset_list_bnc="bnc.svg convtask.svg imaginf.svg spwr.svg"
asset_list_xnet="pos.svg"

make_res "${icon_list}" 16

make_clear_asset "${asset_list}" 16 "${dirassets}/images"
make_clear_asset "${asset_list_bnc}" 16 "${dirassets}/images/bnc"
make_clear_asset "${asset_list_xnet}" 16 "${dirassets}/images/xnet"

check

