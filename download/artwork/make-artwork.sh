#!/bin/bash

source "./lib-artwork.sh"

icon_list="ic_ok.svg ic_download.svg ic_run.svg ic_download_source.svg ic_download_dest.svg ic_download.svg"
smallicon_list="ic_error.svg"
middleicon_list="bn_download.svg bn_download_ok.svg"

make_res "${icon_list}" 32
make_res "${smallicon_list}" 16
make_res "${middleicon_list}" 24

check
