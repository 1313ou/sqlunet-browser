#!/bin/bash

source "../../make-artwork-lib.sh"

icon_list="*.svg"
logo_list="logo.svg"

make_assets "${icon_list}" 16
make_assets "${logo_list}" 64
