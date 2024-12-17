#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

source ./define_colors.sh

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
	#echo -e "${C}$r0 $r1 $r2 $r3 $r4${Z}"
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
	local d="$4"
	local suffix="$5"
	mkdir -p ${d}
	if [ "${aspect}" == "" ]; then
		aspect=h
	fi
	for svg in ${list}; do
		if [ -e "${svg}" ]; then
			local png="${svg%.svg}${suffix}.png"
			echo -e -n " ${B}${aspect}${r}${Z} ${M}${svg}${Z} ${K}-> ${png}${Z}"
			$INKSCAPE --export-type="png" --export-filename=${d}/${png} -${aspect} ${r} ${svg} > /dev/null # 2> /dev/null
			if [ -e "${d}/${png}" ]; then
				echo -e " ${GREEN}OK${Z}"
			else
				echo -e " ${RED}FAIL${Z}"
			fi
		else
			echo -e "${svg} ${YELLOW}!EXISTS${Z}"
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
	echo -en "${C}drawables${Z}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_pngs "${list}" ${r0} "${aspect}" "drawable"
}

function make_mipmap(){
	echo -e "${C}mipmaps${Z}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_pngs "${list}" ${r0} "${aspect}" "mipmap"
}

function make_asset(){
	echo -en "${C}asset${Z}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_png "${list}" ${r0} "${aspect}" "${dirassets}"
}

function make_icon(){
	echo -en "${C}icon${Z}"
	local list="$1"
	local r0=$2
	local dir=$3
	local aspect=$4
	make_png "${list}" ${r0} "${aspect}" "${dir}"
}

function make_help(){
	echo -en "${C}help${Z}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_png "${list}" ${r0} "${aspect}" "${dirhelp}"
}

function make_helpen(){
	echo -en "${C}help en${Z}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_png "${list}" ${r0} "${aspect}" "${dirhelpen}"
}

function make_app(){
	echo -en "${C}app${Z}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_png "${list}" ${r0} "${aspect}" "${dirapp}" -app
}

function check(){
	local d=../src/main
	#check_dir ${d}
}

function check_dir(){
	local d=$1
	d="$(readlink -m ${d})"
	echo -e "${M}${d}${Z}"
	pushd "${d}" > /dev/null
	echo -en "${R}"
	find -L . -name '*.png' -mmin +15
	echo -en "${Z}"
	popd > /dev/null
}

