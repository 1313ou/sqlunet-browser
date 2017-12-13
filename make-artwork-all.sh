#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"

declare -A dirs
dirs=( 
[browser]="artwork artwork-assets" 
[browserfn]="artwork artwork-assets" 
[browserwn]="artwork artwork-assets" 
[browservn]="artwork artwork-assets" 
[common]="artwork artwork-assets" 
[xNet]="artwork" 
[wordNet]="artwork artwork-links" 
[verbNet]="artwork" 
[propbank]="artwork" 
[frameNet]="artwork" 
[bNC]="artwork" 
[predicateMatrix]="artwork" 
[treeView]="artwork" 
[support]="artwork" 
)

if [ "$1" != "-check" ]; then
for k in ${!dirs[@]}; do 
	#echo $k
	for v in ${dirs[$k]}; do
		#echo "	$v"
		d=$k/$v
		echo "* $d"
		pushd $d > /dev/null
		f=make-artwork.sh
		if [ ! -e $f ]; then
			echo NOT EXISTS $f
		fi
		eval ./$f
		popd > /dev/null
	done
done
fi

dirres=src/main/res
dirassets=src/main/assets
dirapp=..

echo "CHECK"
for v in ${!dirs[@]}; do 
	echo "* $v/$dirres"
	find $v/$dirres -type f -name '*.png' -mmin +15
done

