#!/bin/bash

source "./lib-artwork.sh"

launch_list="ic_launcher.svg ic_launcher_round.svg"
logo_list="ic_logo.svg"
splash_list="ic_splash.svg"
supersplash_list="home.svg"
icon_list="ic_xselector.svg"
asset_list="open.svg closed.svg error.svg pointer.svg"
help_list="wordnet.svg verbnet.svg propbank.svg framenet.svg bnc.svg predicatematrix.svg"

make_mipmap "${launch_list}" 48
make_res "${logo_list}" 64
make_res "${splash_list}" 144
make_res "${supersplash_list}" 400
make_res "${icon_list}" 32

make_app "ic_launcher.svg" 512

make_asset "${asset_list}" 16 ${dirassets}/images
make_asset "logo.svg" 64 "${dirassets}/images"

make_help "logo.svg" 128
make_help "sqlunet.svg" 384
make_help "${help_list}" 24

touch_help "selector-wn selector-vn selector-pb"
touch_asset_dir "images/wordnet"
touch_asset_dir "images/verbnet"
touch_asset_dir "images/propbank"
touch_asset_dir "images/xnet"
check
