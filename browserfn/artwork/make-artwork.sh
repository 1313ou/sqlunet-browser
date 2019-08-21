#!/bin/bash

source "./lib-artwork.sh"

launch_list="ic_launcher.svg ic_launcher_round.svg"
logo_list="ic_logo.svg"
logo_app_list="logo_app.svg"
splash_list="ic_splash.svg"
supersplash_list="home.svg"
icon_list="ic_search_fnsentence.svg"

make_mipmap "${launch_list}" 48
make_res "${logo_list}" 64
make_res "${logo_app_list}" 48
make_res "${splash_list}" 144
make_res "${supersplash_list}" 400
make_res "${icon_list}" 32

make_help "logo.svg" 128
make_app "ic_launcher.svg" 512

make_icon "logo.svg" 64 "${dirassets}/images"

check
