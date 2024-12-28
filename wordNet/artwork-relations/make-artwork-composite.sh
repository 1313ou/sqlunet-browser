#!/bin/bash

set -e 

dircomposite=composite

dirres=../src/main/res
dirassets=../src/main/assets
dirapp=..

source define_colors.sh
source define_relations.sh

declare -A backtofore
backtofore=(
[base]="${relations['gen']}"
[base_sem]="${relations['sem']}"
[base_lex]="${relations['lex']}"
[base_both]="${relations['both']}"
[base_dom]="${relations['dom']}"
[base_role]="${relations['role']}"
[base_pos]="${relations['pos']}"
[base_collocation]="${relations['collocation']}"
)

# composite
d="${dircomposite}"
mkdir -p "${d}"

for b in ${!backtofore[@]}; do
	echo -e "${M}${b}${Z}"
	fs=${backtofore[$b]}
	#echo -e "${M}${b}${Z} ${B}${fs}${Z}"
	for f in ${fs}; do 
		echo -e " ${B}${f}${Z}"
		python3 stack-svgs.py "${f}.svg" "${b}.svg" "fore_${f}.svg"
	done
	echo
done

#optimize
svgo --pretty "${d}"
