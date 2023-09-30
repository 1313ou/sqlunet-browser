#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

RED='\u001b[31m'
GREEN='\u001b[32m'
YELLOW='\u001b[33m'
BLUE='\u001b[34m'
MAGENTA='\u001b[35m'
CYAN='\u001b[36m'
RESET='\u001b[0m'

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"
dirapp=..
dirres=../src/main/res
dirresources=../src/main/resources
dirassets=../src/main/assets
dirhelp=../src/main/assets/help/images
dirhelpen=../src/main/assets/help/en/images

function make_seq(){
	r0=$1
	r1=$((r0 + r0 / 2))
	r2=$((r0 * 2))
	r3=$((r0 * 3))
	r4=$((r0 * 4))
	#echo -e "${CYAN}$r0 $r1 $r2 $r3 $r4${RESET}"
}

# list
# r resolution
# aspect [h/w]
# directory
# suffix to output
function make_png(){
	local list="$1"
	local r=$2
	local aspect=$3
	local d="$(readlink -m $4)"
	local suffix="$5"
	mkdir -p ${d}
	if [ "${aspect}" == "" ]; then
		aspect=h
	fi
	for e in ${list}; do
		svg="${thisdir}/${e}"
		if [ -e "${svg}" ]; then
			local png="${e%.svg}${suffix}.png"
			echo -e -n "${MAGENTA}${svg}${RESET} -> ${d}/${png} @ ${BLUE}${aspect}${r}${RESET}"
			echo
			$INKSCAPE --export-type="png" --export-filename=${d}/${png} -${aspect} ${r} ${svg} #> /dev/null # 2> /dev/null
			if [ -e "${d}/${png}" ]; then
				echo -e " ${GREEN}OK${RESET}"
			else
				echo -e " ${RED}FAIL${RESET}"
			fi
		else
			echo -e "${svg} ${YELLOW}!EXISTS${RESET}"
		fi
	done
}

function make_pngs(){
	local list="$1"
	local r0=$2
	local aspect=$3
	local subdir=$4
	make_seq $r0
	declare -A res
	local res=([mdpi]=$r0 [hdpi]=$r1 [xhdpi]=$r2 [xxhdpi]=$r3 [xxxhdpi]=$r4)
	for k in ${!res[@]}; do 
		local d="${dirres}/${subdir}-${k}"
		mkdir -p ${d}
		local r=${res[$k]}
		make_png "$1" ${r} "${aspect}" "${d}"
	done
}

function make_res(){
	echo -e "${CYAN}drawables${RESET}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_pngs "${list}" ${r0} "${aspect}" "drawable"
}

function make_mipmap(){
	echo -e "${CYAN}mipmaps${RESET}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_pngs "${list}" ${r0} "${aspect}" "mipmap"
}

function make_asset(){
	echo -e "${CYAN}asset${RESET}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_png "${list}" ${r0} "${aspect}" "${dirassets}"
}

function make_icon(){
	echo -e "${CYAN}icon${RESET}"
	local list="$1"
	local r0=$2
	local dir=$3
	local aspect=$4
	make_png "${list}" ${r0} "${aspect}" "${dir}"
}

function make_help(){
	echo -e "${CYAN}help${RESET}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_png "${list}" ${r0} "${aspect}" "${dirhelp}"
}

function make_helpen(){
	echo -e "${CYAN}help en${RESET}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_png "${list}" ${r0} "${aspect}" "${dirhelpen}"
}

function make_app(){
	echo -e "${CYAN}app${RESET}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_png "${list}" ${r0} "${aspect}" "${dirapp}" -app
}

function check(){
	local d=../src/main
	#check_dir ${d}
}

DELAY=15
function check_dir(){
	local d=$1
	d="$(readlink -m ${d})"
	echo -e "${MAGENTA}${d}${RESET}"
	pushd "${d}" > /dev/null
	if [ -e "./assets" ]; then
		echo -en "${YELLOW}"
		find -L ./assets -name '*.png' -mmin +${DELAY}
		echo -en "${RESET}"
	fi
	echo -en "${RED}"
	find -L ./res -name '*.png' -mmin +${DELAY}
	echo -en "${RESET}"
	popd > /dev/null
}

