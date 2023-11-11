#!/bin/bash

source "./lib-artwork.sh"

icon_list="open.svg closed.svg error.svg pointer.svg"

make_asset "${icon_list}" 16 ${dirassets}/images
make_asset "logo.svg" 64 "${dirassets}/images"

make_help "logo.svg" 128
make_help "sqlunet.svg" 384

check
