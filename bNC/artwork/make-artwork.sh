#!/bin/bash

source "./lib-artwork.sh"

icon_list="bnc.svg convtask.svg imaginf.svg spwr.svg"
asset_list="convtask.svg imaginf.svg spwr.svg"

make_res "${icon_list}" 16

make_clear_asset "${asset_list}" 16 "${dirassets}/images/bnc"

check
