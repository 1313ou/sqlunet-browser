#!/bin/bash

source "./lib-artwork.sh"

icon_list="ic_error.svg ic_download.svg ic_download_source.svg ic_download_zip_source.svg ic_download_dest.svg ic_download.svg"
notificon_list="ic_notif_cancel.svg"
middleicon_list="bn_download.svg bn_download_ok.svg"

make_res "${icon_list}" 32
make_res "${notificon_list}" 32
make_res "${middleicon_list}" 24

check
