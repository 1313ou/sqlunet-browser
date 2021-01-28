#!/bin/bash

source "./lib-artwork.sh"

icon_list="vnframe.svg syntax.svg semantics.svg restr.svg grouping.svg"
asset_list="vnframe.svg syntax.svg semantics.svg restr.svg synsetspecific.svg"

make_res "${icon_list}" 16

rm ${dirassets}/images/verbnet/*
make_icon "${asset_list}" 16 "${dirassets}/images/verbnet"

check
