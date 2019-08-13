#!/bin/bash

source "../../make-artwork-lib.sh"

logo_list="ic_logo.svg"
donate_list="ic_donate1.svg ic_donate2.svg ic_donate3.svg ic_donate4.svg ic_donate5.svg ic_overlay.svg"

make_res "${logo_list}" 48
make_res "${donate_list}" 32

