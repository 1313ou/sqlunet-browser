#!/bin/bash

source "./lib-artwork.sh"

top_list="up.svg down.svg"
icon_list="morph.svg adjposition.svg verbtemplate.svg verbframe.svg"
link_list="ic_link_relation.svg"
settings_list="ic_settings_wordnet.svg"
asset_list=""
search_list="ic_search_wnword.svg ic_search_wndefinition.svg ic_search_wnsample.svg"

make_res "${top_list}" 20
make_res "${icon_list}" 16
make_res "${link_list}" 16
make_res "${settings_list}" 24
make_res "${search_list}" 32

#rm ${dirassets}/images/wordnet/*
make_icon "${asset_list}" 16 "${dirassets}/images/wordnet"

check
