#!/bin/bash

source "./lib-artwork.sh"

launch_list="ic_launcher.svg ic_launcher_round.svg"
logo_list="ic_logo.svg"
logo_app_list="logo_app.svg"
splash_list="ic_splash.svg"
supersplash_list="home.svg"
icon_list="ic_selector.svg ic_xselector.svg ic_snselector.svg ic_search_wnword.svg ic_search_wndefinition.svg ic_unknown.svg ic_ok.svg ic_fail.svg ic_setup.svg ic_run.svg ic_download_source.svg ic_download_dest.svg"
smallicon_list="ic_item.svg ic_collocation.svg"
middleicon_list="bn_setup.svg bn_download.svg bn_info.svg"
drawericon_list="ic_home.svg ic_search_browse.svg ic_search_text.svg ic_setup.svg ic_settings.svg ic_storage.svg ic_status.svg ic_sql.svg ic_help.svg"
arrow_list="ic_spinner_arrow.svg"

make_mipmap "${launch_list}" 48
make_res "${logo_list}" 64
make_res "${logo_app_list}" 48
make_res "${splash_list}" 144
make_res "${supersplash_list}" 400
make_res "${icon_list}" 32
make_res "${smallicon_list}" 16
make_res "${middleicon_list}" 24
make_res "${drawericon_list}" 32
make_res "${arrow_list}" 10

make_help "logo.svg" 128
make_help "sqlunet.svg" 384
make_app "ic_launcher.svg" 512

make_icon "logo.svg" 64 "${dirassets}/images"

check
