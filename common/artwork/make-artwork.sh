#!/bin/bash

source "./lib-artwork.sh"

launch_list="ic_launcher.svg ic_launcher_round.svg"
logo_list="ic_logo.svg"
splash_list="ic_splash.svg"
supersplash_list="sqlunet.svg home.svg"
icon_list="ic_selector.svg ic_xselector.svg ic_role.svg ic_roles.svg ic_rows_bysynset.svg ic_rows_byrole.svg ic_roles_grouped.svg ic_rows_ungrouped.svg ic_search_wnword.svg ic_search_wndefinition.svg ic_search_wnsample.svg ic_search_vnexample.svg ic_search_pbexample.svg ic_search_fnsentence.svg ic_unknown.svg ic_ok.svg ic_fail.svg ic_setup.svg ic_run.svg ic_download_source.svg ic_download_dest.svg"
smallicon_list="ic_item.svg"
middleicon_list="bn_setup.svg bn_download.svg bn_info.svg"
drawericon_list="ic_home.svg ic_search_browse.svg ic_search_text.svg ic_search_pm.svg ic_setup.svg ic_settings.svg ic_storage.svg ic_status.svg ic_sql.svg ic_help.svg ic_about.svg"
arrow_list="ic_spinner_arrow.svg"
settings_list="ic_settings_general.svg ic_settings_filter.svg ic_settings_download.svg ic_settings_database.svg ic_settings_system.svg"

make_mipmap "${launch_list}" 48
make_res "${logo_list}" 64
make_res "${splash_list}" 144
make_res "${supersplash_list}" 400
make_res "${icon_list}" 32
make_res "${smallicon_list}" 16
make_res "${middleicon_list}" 24
make_res "${drawericon_list}" 32
make_res "${arrow_list}" 10
make_res "${settings_list}" 24

make_help "logo.svg" 128
make_help "sqlunet.svg" 384
make_app "ic_launcher.svg" 512

check
