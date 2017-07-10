#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"
dirres=../src/main/res
dirassets=../src/main/assets
dirapp=..

# res
declare -A res_icon
res_icon=([icon]=16)
list_icon="*.svg"

declare -A res_logo
res_logo=([logo]=64)
list_logo="logo.svg"

d="${dirassets}/images"
mkdir -p ${d}

# A S S E T S

# icon
for svg in ${list_icon}; do
	for r in ${!res_icon[@]}; do 
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_icon[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_icon[$r]} > /dev/null 2> /dev/null
	done
done

# logo
for svg in ${list_logo}; do
	for r in ${!res_logo[@]}; do 
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_logo[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_logo[$r]} > /dev/null 2> /dev/null
	done
done

