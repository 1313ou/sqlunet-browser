#!/bin/bash

source "./lib-artwork.sh"

smallicon_list="*.svg"

make_res "${smallicon_list}" 32

check
