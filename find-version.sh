#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"

RED='\u001b[31m'
GREEN='\u001b[32m'
YELLOW='\u001b[33m'
BLUE='\u001b[34m'
MAGENTA='\u001b[35m'
CYAN='\u001b[36m'
RESET='\u001b[0m'

grep 'versionCode = ' build.gradle | \
sed -r  's/^[^0-9]*([0-9]+).*/\1/'

