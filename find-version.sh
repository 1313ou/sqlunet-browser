#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

grep 'set("versionCode",' build.gradle.kts | \
sed -r  's/\s*set\("[^0-9]*", ([0-9]+)\).*/\1/'

