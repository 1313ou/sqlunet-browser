#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

export scripts=`find . -mindepth 2 -not -path '*/artwork-relations/*' -not -path '*/reference/*' -name 'make-artwork.sh'`
#echo ${scripts}
