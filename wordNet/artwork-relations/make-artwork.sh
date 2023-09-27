#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"
dirres=../src/main/res
dirassets=../src/main/assets
dirapp=..
dirsrc=composite

R='\u001b[31m'
G='\u001b[32m'
Y='\u001b[33m'
B='\u001b[34m'
M='\u001b[35m'
C='\u001b[36m'
Z='\u001b[0m'

declare -A relations
relations=(
[gen]="
relations"
[sem]="
also
attribute
causes
caused
entails
entailed
holonym
hypernym
hyponym
instance_hypernym
instance_hyponym
member_holonym
member_meronym
meronym
part_holonym
participle
part_meronym
pertainym
similar
substance_holonym
substance_meronym"
[lex]="
antonym
similar
also
participle
pertainym
derivation
exemplifies
exemplified"
[dom]="
domain
domain_member
domain_topic
domain_member_topic
domain_region
domain_member_region
domain_term
domain_member_term
exemplifies
exemplified"
[morph]="
state
result
event
property
location
destination
agent
undergoer
uses
instrument
bymeansof
material
vehicle
bodypart"
[pos]="
pos
pos_n
pos_v
pos_a
pos_r
pos_s"
)

# res
declare -A res
res=([mdpi]=16 [hdpi]=24 [xhdpi]=32 [xxhdpi]=48 [xxxhdpi]=64)
webres=16

function svg2png(){
	local r="$1"
	local from="$2"
	local to="$3"
	echo -e ". ${B}${from} -> ${to}${Z} ${C}@${r}${Z}"
	inkscape -h ${r} --export-filename "$(readlink -m ${to})" "$(readlink -m ${from})"
}

function list_svg2png(){
	local r="$1"
	local srcdir="$2"
	local destdir="$3"
	shift
	shift
	shift
	local list="$*"
	echo -e "${G}${list}${Z}"
	for e in ${list}; do
		svg2png "${r}" "${srcdir}/${e}.svg" "${destdir}/${e}.png"
	done
}

function lists_svg2png(){
	local r="$1"
	local srcdir="$2"
	local destdir="$3"
	for k in ${!relations[@]}; do
		vs=${relations[$k]}
		echo -e "- ${M}${k} ${C}${vs}${Z}"
		list_svg2png "${r}" "${srcdir}" "${destdir}" ${vs}
		echo
	done
}

# A S S E T S

echo -e "${M}WEBRESOLUTION${Z} ${B}$r ${webres}${Z}"
dirdest="${dirassets}/images/wordnet"
mkdir -p ${dirdest}
lists_svg2png ${webres} "${dirsrc}" "${dirdest}"

# R E S O U R C E S

for r in ${!res[@]}; do
	rv=${res[$r]}
	echo -e "${M}RESOLUTION${Z} ${B}$r ${res[$r]}${Z}"
	dirdest="${dirres}/drawable-${r}"
	mkdir -p ${dirdest}
	lists_svg2png "${rv}" "${dirsrc}" "${dirdest}"
done

