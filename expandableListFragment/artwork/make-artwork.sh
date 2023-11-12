#!/bin/bash

source "./lib-artwork.sh"

smallicon_list="*.svg"

make_res "${smallicon_list}" 32

touch_res "collapsed_triangle.9 collapsed_arrow.9 expanded_triangle.9 expanded_arrow.9" true
check
