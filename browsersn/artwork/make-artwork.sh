#!/bin/bash

source "./lib-artwork.sh"

launch_list="ic_launcher.svg ic_launcher_round.svg"
logo_list="ic_logo.svg"
splash_list="ic_splash.svg"
supersplash_list="home.svg"
icon_list="ic_xselector.svg ic_snselector.svg"
smallicon_list="ic_collocation.svg"
asset_list="open.svg closed.svg error.svg pointer.svg"

make_mipmap "${launch_list}" 48
make_res "${logo_list}" 64
make_res "${splash_list}" 144
make_res "${supersplash_list}" 400
make_res "${icon_list}" 32
make_res "${smallicon_list}" 16

make_app "ic_launcher.svg" 512

make_asset "${asset_list}" 16 ${dirassets}/images
make_asset "logo.svg" 64 "${dirassets}/images"

make_help "logo.svg" 128
make_help "sqlunet.svg" 384

check
