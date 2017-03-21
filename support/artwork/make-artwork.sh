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
declare -A res_logo_icon
res_logo_icon=([mdpi]=48 [hdpi]=72 [xhdpi]=96 [xxhdpi]=144 [xxxhdpi]=192)
list_logo_icon="logo_sqlunet.svg logo_treebolicwordnet.svg"

# icons
for svg in ${list_logo_icon}; do
	echo $svg
	for r in ${!res_logo_icon[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_logo_icon[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_logo_icon[$r]} > /dev/null 2> /dev/null
	done
done

# res
declare -A res_donate_icon
res_donate_icon=([mdpi]=32 [hdpi]=48 [xhdpi]=64 [xxhdpi]=96 [xxxhdpi]=128)
list_donate_icon="ic_donate1.svg ic_donate2.svg ic_donate3.svg ic_donate4.svg"

# icons
for svg in ${list_donate_icon}; do
	for r in ${!res_donate_icon[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_donate_icon[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${res_donate_icon[$r]} > /dev/null 2> /dev/null
	done
done

# res
declare -A res_googleplay_icon
res_googleplay_icon=([mdpi]=128 [hdpi]=192 [xhdpi]=256 [xxhdpi]=384 [xxxhdpi]=512)
list_googleplay_icon="ic_googleplay.svg"

# icons
for svg in ${list_googleplay_icon}; do
	for r in ${!res_googleplay_icon[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg} -> ${d}/${png} @ resolution ${res_googleplay_icon[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -w${res_googleplay_icon[$r]} > /dev/null 2> /dev/null
	done
done

