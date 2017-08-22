#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"
dirres=../src/main/res
dirassets=../src/main/assets
dirapp=..

#declare -A res_icon16
#res_icon16=([mdpi]=16 [hdpi]=24 [xhdpi]=32 [xxhdpi]=48 [xxxhdpi]=64)
#declare -A res_icon24
#res_icon24=([mdpi]=24 [hdpi]=36 [xhdpi]=48 [xxhdpi]=72 [xxxhdpi]=96)
#declare -A res_icon32
#res_icon32=([mdpi]=32 [hdpi]=48 [xhdpi]=64 [xxhdpi]=96 [xxxhdpi]=128)
#declare -A res_icon48
#res_icon48=([mdpi]=48 [hdpi]=72 [xhdpi]=96 [xxhdpi]=144 [xxxhdpi]=192)
#declare -A res_icon144
#res_icon144=([mdpi]=144 [hdpi]=192 [xhdpi]=288 [xxhdpi]=384 [xxxhdpi]=576)

# res
declare -A res_icon
res_icon=([mdpi]=16 [hdpi]=24 [xhdpi]=32 [xxhdpi]=48 [xxxhdpi]=64)
list_icon="*.svg"

list_assets="*.svg"

# small icons
for svg in ${list_icon}; do
	for r in ${!res_icon[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_icon[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_icon[$r]} > /dev/null 2> /dev/null
	done
done

# A S S E T S

# assets
r=16
d="${dirassets}/images"
mkdir -p ${d}
for svg in ${list_assets}; do
	png="${svg%.svg}.png"
	echo "${svg}.svg -> ${d}/${png}.png @ resolution ${r}"
	inkscape ${svg} --export-png=${d}/${png} -h${r} > /dev/null 2> /dev/null
done

