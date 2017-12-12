#!/bin/bash

source "../../make-artwork-lib.sh"

icon_list="*.svg"
asset_list="*.svg"

make_res "${icon_list}" 16
make_assets "${asset_list}" 16

