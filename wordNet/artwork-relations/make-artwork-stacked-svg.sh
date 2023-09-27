#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"
dircomposite=composite

dirres=../src/main/res
dirassets=../src/main/assets
dirapp=..

R='\u001b[31m'
G='\u001b[32m'
Y='\u001b[33m'
B='\u001b[34m'
M='\u001b[35m'
C='\u001b[36m'
Z='\u001b[0m'

gen="
relations"

sem="
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

lex="
antonym
similar
also
participle
pertainym
derivation
exemplifies
exemplified"

dom="
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

morph="
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

pos="
pos_n
pos_v
pos_a
pos_r
pos_s
pos"

declare -A backtofore
backtofore=(
[base]="$gen" 
[base_sem]="$sem" 
[base_lex]="$lex" 
[base_dom]="$dom" 
[base_morph]="$morph" 
[base_pos]="$pos"
)

# composite
d="${dircomposite}"
mkdir -p ${d}

for b in ${!backtofore[@]}; do
	echo -e "${M}${b}${Z}"
	fs=${backtofore[$b]}
	#echo -e "${M}${b}${Z} ${B}${fs}${Z}"
	for f in ${fs}; do 
		echo -e " ${B}${f}${Z}"
		python3 make_stacked-svg.py "${f}.svg" "${b}.svg" "fore_${f}.svg"
	done
	echo
done
