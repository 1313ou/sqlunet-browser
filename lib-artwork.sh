#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

DEBUG=true
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

resnames="mdpi hdpi xhdpi xxhdpi xxxhdpi"

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
			echo -e "${svg} ${R}!EXISTS${Z}"
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
      touch "${png}"
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
	echo rm ${d}/*
}

if [ ! -z "${DEBUG}" ]; then
  unset -f emptydir
  function emptydir(){
  	local d="$1"
    [ -e "${d}" ] || echo -e "${R} ${d} DEST !EXISTS ${Z}"
  }
fi

function touch_res(){
 	local list="$1"
 	local warnnotexists=$2
  for i in ${list}; do
    if [ -e "${i}.svg" ]; then
      touch "${i}.svg"
    fi
    for k in ${resnames}; do
      d="${dirres}/drawable-${k}"
      f="${d}/${i}.png"
      if [ -e "${f}" ]; then
        touch "${f}"
      else
         [ ! -z "${warnnotexists}" ] || echo -e "${R}${f} !EXIST${Z}"
      fi
    done
  done
}

function touch_asset(){
 	local list="$1"
  for i in ${list}; do
    if [ -e "${i}.svg" ]; then
      touch "${i}.svg"
    fi
    d="${dirassets}"
    f="${d}/${i}.png"
    if [ -e "${f}" ]; then
      touch "${f}"
    else
      echo -e "${R}${f} does not exist${Z}"
    fi
  done
}

function touch_help(){
 	local list="$1"
  for i in ${list}; do
    if [ -e "${i}.svg" ]; then
      touch "${i}.svg"
    fi
    d="${dirhelp}"
    f="${d}/${i}.png"
    if [ -e "${f}" ]; then
      touch "${f}"
    else
      echo -e "${R}${f} does not exist${Z}"
    fi
  done
}

function touch_asset_dir(){
 	local dir="$1"
  for f in ${dirassets}/${dir}/*; do
    touch "${f}"
  done
}

function check(){
	local d=../src/main
	check_dir ${d}
}

# find stale png files older than 15s
DELAY=15
function check_dir(){
	local d=$1
	d="$(readlink -m ${d})"
	echo -e "checking ${M}${d}${Z}"
	pushd "${d}" > /dev/null

	# resources
	echo -en "${R}"
	find -L ./res -name '*.png' -mmin +${DELAY}
	echo -en "${Z}"

	# assets
  if [ -e "./assets" ]; then
		echo -en "${R}"
		find -L ./assets -name '*.png' -not -path '*/reference/*' -mmin +${DELAY}
		echo -en "${Z}"
	fi

	popd > /dev/null
}

