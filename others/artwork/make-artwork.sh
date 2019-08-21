#!/bin/bash

source "./lib-artwork.sh"

logo_list="logo_semantikos.svg logo_semantikos_wn.svg logo_semantikos_ewn.svg logo_semantikos_fn.svg logo_semantikos_vn.svg logo_treebolicwordnet.svg logo_grammarscope.svg logo_grammarscope_udpipe.svg"
googleplay_list="ic_googleplay.svg"

make_res "${logo_list}" 48
make_res "${googleplay_list}" 128 w

check
