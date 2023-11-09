#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

DEBUG=

export R='\u001b[31m'
export G='\u001b[32m'
export Y='\u001b[33m'
export B='\u001b[34m'
export M='\u001b[35m'
export C='\u001b[36m'
export Z='\u001b[0m'

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
			echo -e -n "${M}${svg}${Z} -> ${d}/${png} @ ${B}${aspect}${r}${Z}"
			echo
			$INKSCAPE --export-type="png" --export-filename=${d}/${png} -${aspect} ${r} ${svg} #> /dev/null # 2> /dev/null
			if [ -e "${d}/${png}" ]; then
				echo -e " ${G}OK${Z}"
			else
				echo -e " ${R}FAIL${Z}"
			fi
		else
			echo -e "${svg} ${Y}!EXISTS${Z}"
		fi
	done
}

if [ ! -z "${DEBUG}" ]; then
unset -f make_png
function make_png(){
	local list="$1"
	local r=$2
	local aspect=$3
	local d="$(readlink -m $4)"
	local suffix="$5"
	for e in ${list}; do
		svg="${thisdir}/${e}"
		if [ ! -e "${svg}" ]; then
			echo -e "${R} ${svg} SOURCE !EXISTS ${Z}"
		fi
		local png="${d}/${e%.svg}${suffix}.png"
		if [ ! -e "${png}" ]; then
			echo -e "${R} ${png} DEST !EXISTS ${Z}"
		fi
	done	
}
fi

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
	echo -e "${C}drawables${Z}"
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

function make_clear_asset(){
	echo -e "${C}asset${Z}"
	local list="$1"
	local r0=$2
	local dir=$3
	local aspect=$4
	emptydir "${dir}"
	make_png "${list}" ${r0} "${aspect}" "${dir}"
}

function make_asset(){
	echo -e "${C}asset${Z}"
	local list="$1"
	local r0=$2
	local dir=$3
	local aspect=$4
	make_png "${list}" ${r0} "${aspect}" "${dir}"
}

function make_icon(){
	echo -e "${C}icon${Z}"
	local list="$1"
	local r0=$2
	local dir=$3
	local aspect=$4
	make_png "${list}" ${r0} "${aspect}" "${dir}"
}

function make_help(){
	echo -e "${C}help${Z}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_png "${list}" ${r0} "${aspect}" "${dirhelp}"
}

function make_helpen(){
	echo -e "${C}help en${Z}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_png "${list}" ${r0} "${aspect}" "${dirhelpen}"
}

function make_app(){
	echo -e "${C}app${Z}"
	local list="$1"
	local r0=$2
	local aspect=$3
	make_png "${list}" ${r0} "${aspect}" "${dirapp}" -app
}

function emptydir(){
	local d="$1"
	#echo rm ${d}/*
}

function check(){
	local d=../src/main
	#check_dir ${d}
}

DELAY=15
function check_dir(){
	local d=$1
	d="$(readlink -m ${d})"
	echo -e "${M}${d}${Z}"
	pushd "${d}" > /dev/null
	if [ -e "./assets" ]; then
		echo -en "${Y}"
		find -L ./assets -name '*.png' -mmin +${DELAY}
		echo -en "${Z}"
	fi
	echo -en "${R}"
	find -L ./res -name '*.png' -mmin +${DELAY}
	echo -en "${Z}"
	popd > /dev/null
}

