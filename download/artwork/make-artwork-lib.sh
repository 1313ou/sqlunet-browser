#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"
dirapp=..
dirres=../src/main/res
dirassets=../src/main/assets/images
dirhelp=../src/main/assets/help/images

function make_seq(){
	r0=$1
	r1=$((r0 + r0 / 2))
	r2=$((r0 * 2))
	r3=$((r0 * 3))
	r4=$((r0 * 4))
	echo $r0 $r1 $r2 $r3 $r4
}

# list
# r resolution
# aspect [h/w]
# directory
# suffix to output
function make_png(){
	list="$1"
	r=$2
	aspect=$3
	d="$4"
	suffix="$5"
	mkdir -p ${d}
	if [ "${aspect}" == "" ]; then
		aspect=h
	fi
	for svg in ${list}; do
		png="${svg%.svg}${suffix}.png"
		echo -n "${svg} -> ${d}/${png} @ ${aspect}${r}"
		inkscape ${svg} --export-png=${d}/${png} -${aspect}${r} > /dev/null 2> /dev/null
		if [ -e "${d}/${png}" ]; then
			echo " OK"
		else
			echo ""
		fi
	done
}

function make_pngs(){
	list="$1"
	r0=$2
	aspect=$3
	subdir=$4
	make_seq $r0
	declare -A res
	res=([mdpi]=$r0 [hdpi]=$r1 [xhdpi]=$r2 [xxhdpi]=$r3 [xxxhdpi]=$r4)
	for k in ${!res[@]}; do 
		d="${dirres}/${subdir}-${k}"
		mkdir -p ${d}
		r=${res[$k]}
		make_png "$1" ${r} "${aspect}" "${d}"
	done
}

function make_res(){
	list="$1"
	r0=$2
	aspect=$3
	make_pngs "$1" $2 "${aspect}" "drawable"
}

function make_mipmap(){
	list="$1"
	r0=$2
	aspect=$3
	make_pngs "$1" $2 "${aspect}" "mipmap"
}

function make_assets(){
	list="$1"
	r0=$2
	aspect=$3
	make_png "$1" $2 "${aspect}" "${dirassets}"
}

function make_help(){
	list="$1"
	r0=$2
	aspect=$3
	make_png "$1" $2 "${aspect}" "${dirhelp}"
}

function make_app(){
	list="$1"
	r0=$2
	aspect=$3
	make_png "$1" $2 "${aspect}" "${dirapp}" -app
}

