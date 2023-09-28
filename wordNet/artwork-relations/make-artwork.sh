#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"
dirres=../src/main/res
dirassets=../src/main/assets
dirreference=../../common/reference/wordnet/relations/html
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
hypernym
hyponym
instance_hypernym
instance_hyponym
holonym
meronym
part_holonym
part_meronym
member_holonym
member_meronym
substance_holonym
substance_meronym
causes
caused
entails
entailed
attribute
similar
verb_group"

[lex]="
antonym
participle
pertainym
derivation
adjderived"

[both]="
also
other
"

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
pos_n
pos_v
pos_a
pos_r
pos_s
pos"

)

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
	/snap/bin/inkscape -h ${r} --export-filename "$(readlink -m ${to})" "$(readlink -m ${from})"
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

# R E F E R E N C E

echo -e "${Y}Reference${Z} ${B}$r ${webres}${Z}"
dirdest="${dirreference}/images"
mkdir -p ${dirdest}
lists_svg2png ${referenceres} "${dirsrc}" "${dirdest}" ""

# A S S E T S

echo -e "${Y}Assets${Z} ${B}$r ${webres}${Z}"
dirdest="${dirassets}/images/wordnet"
mkdir -p ${dirdest}
lists_svg2png ${webres} "${dirsrc}" "${dirdest}" ""

# R E S O U R C E S

for r in ${!res[@]}; do
	rv=${res[$r]}
	echo -e "${M}Resources${Z} ${B}$r ${res[$r]}${Z}"
	dirdest="${dirres}/drawable-${r}"
	mkdir -p ${dirdest}
	lists_svg2png "${rv}" "${dirsrc}" "${dirdest}" "ic_"
done

