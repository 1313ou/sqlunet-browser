#!/bin/bash

source define_colors.sh
source define_relations.sh

# res
declare -A res
res=([mdpi]=16 [hdpi]=24 [xhdpi]=32 [xxhdpi]=48 [xxxhdpi]=64)
webres=16
referenceres=30

function svg2png(){
	local r="$1"
	local from="$2"
	local to="$3"
	echo -e ". ${B}${from} -> ${to}${Z} ${C}@${r}${Z}"
	$INKSCAPE -h ${r} --export-filename "$(readlink -m ${to})" "$(readlink -m ${from})"
}

function list_svg2png(){
	local r="$1"
	local srcdir="$2"
	local destdir="$3"
	local prefix="$4"
	shift
	shift
	shift
	shift
	local list="$*"
	echo -e "${G}${list}${Z}"
	for e in ${list}; do
		svg2png "${r}" "${srcdir}/${e}.svg" "${destdir}/${prefix}${e}.png"
	done
}

function lists_svg2png(){
	local r="$1"
	local srcdir="$2"
	local destdir="$3"
	local prefix="$4"
	for k in ${!relations[@]}; do
		vs=${relations[$k]}
		echo -e "- ${M}${k} ${C}${vs}${Z}"
		list_svg2png "${r}" "${srcdir}" "${destdir}" "${prefix}" ${vs}
		echo
	done
}

function lists_svg2png31(){
	local r="$1"
	local srcdir="$2"
	local destdir="$3"
	local prefix="$4"
	for k in gen pos sem lex both dom; do
		vs=${relations[$k]}
		echo -e "- ${M}${k} ${C}${vs}${Z}"
		list_svg2png "${r}" "${srcdir}" "${destdir}" "${prefix}" ${vs}
		echo
	done
}

