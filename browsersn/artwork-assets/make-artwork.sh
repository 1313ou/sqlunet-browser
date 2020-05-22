#!/bin/bash

source "./lib-artwork.sh"

icon_list="open.svg closed.svg error.svg pointer.svg"
logo_list="logo.svg"

make_icon "${icon_list}" 16 ${dirassets}/images
make_icon "${logo_list}" 64 ${dirassets}/images

check
