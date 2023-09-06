#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"

dirres=res
dirres=../../src/main/res
dirassets=assets
dirassets=../../src/main/assets
dirapp=../..

RED='\u001b[31m'
GREEN='\u001b[32m'
YELLOW='\u001b[33m'
BLUE='\u001b[34m'
MAGENTA='\u001b[35m'
CYAN='\u001b[36m'
RESET='\u001b[0m'

list_base="
base_morph
"

list_fore="
agent
bodypart
bymeansof
destination
event
instrument
location
material
property
result
state
undergoer
uses
vehicle
"

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
declare -A res
res=([mdpi]=16 [hdpi]=24 [xhdpi]=32 [xxhdpi]=48 [xxxhdpi]=64)
webres=16

to_png=true
#to_png=

# base dir
bdir="./temp"
mkdir -p ${bdir}

# A S S E T S

echo -e "${MAGENTA}WEBRESOLUTION${RESET} ${BLUE}$r ${webres}${RESET}"

# base
if [ ! -z "${to_png}" ]; then
	for svg in *.svg; do
		echo "to png:${svg}"
		png="_${svg%.svg}.png"
		echo -e "${svg} -> ${bdir}/${png} @ resolution ${BLUE}${webres}${RESET}"
		inkscape ${svg} --export-type="png" --export-filename=${bdir}/${png} -w ${webres} -h ${webres} > /dev/null 2> /dev/null
	done
fi

# composite
d="${dirassets}/images/wordnet"
mkdir -p ${d}

b="base_morph"
for f in ${list_fore}; do 
	p="${f}"
	echo "${bdir}/_${b}.png + ${bdir}/_${f}.png -> ${d}/${p}.png"
	composite ${bdir}/_ic_${f}.png ${bdir}/_${b}.png ${d}/${p}.png
done

# R E S O U R C E S

for r in ${!res[@]}; do
	echo -e "${MAGENTA}RESOLUTION${RESET} ${BLUE}$r ${res[$r]}${RESET}"

	# base
	if [ ! -z "${to_png}" ]; then
		for svg in *.svg; do
			echo "to png:${svg}"
			png="_${svg%.svg}.png"
			echo -e "${svg} -> ${bdir}/${png} @ resolution ${BLUE}${res[$r]}${RESET}"
			inkscape ${svg} --export-type="png" --export-filename=${bdir}/${png} -w ${res[$r]} -h ${res[$r]} > /dev/null 2> /dev/null
		done
	fi

	# composite
	d="${dirres}/drawable-${r}"
	mkdir -p ${d}

	b="base_morph"
	for f in ${list_fore}; do 
		p="ic_${f}"
		echo "${bdir}/_${b}.png + ${bdir}/_${f}.png -> ${d}/${p}.png"
		composite ${bdir}/_ic_${f}.png ${bdir}/_${b}.png ${d}/${p}.png
	done
done

rm -fR ./temp
