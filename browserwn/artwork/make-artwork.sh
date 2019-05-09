#!/bin/bash

source "../../make-artwork-lib.sh"

launch_list="ic_launcher.svg ic_launcher_round.svg"
logo_list="ic_logo.svg"
splash_list="ic_splash.svg"
supersplash_list="sqlunet.svg home.svg"

make_mipmap "${launch_list}" 48
make_res "${logo_list}" 64
make_res "${splash_list}" 144
make_res "${supersplash_list}" 400

make_help "logo.svg" 128
make_help "sqlunet.svg" 384
make_app "ic_launcher.svg" 512

