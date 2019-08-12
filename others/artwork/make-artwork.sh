#!/bin/bash

source "../../make-artwork-lib.sh"

logo_list="logo_semantikos.svg logo_semantikos_wn.svg logo_semantikos_ewn.svg logo_semantikos_fn.svg logo_semantikos_vn.svg logo_treebolicwordnet.svg logo_grammarscope.svg logo_grammarscope_udpipe.svg"
donate_list="ic_donate1.svg ic_donate2.svg ic_donate3.svg ic_donate4.svg ic_donate5.svg"
googleplay_list="ic_googleplay.svg"

make_res "${logo_list}" 48
make_res "${donate_list}" 32
make_res "${googleplay_list}" 128 w

