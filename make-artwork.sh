#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"
dirres=src/main/res
dirassets=src/main/assets
dirapp=..

#dirs="bNC browser frameNet predicateMatrix propbank treeView verbNet wordNet xNet"

declare -A dirs
dirs=( [browser]="artwork artwork-assets" [xNet]="artwork" [wordNet]="artwork artwork-links" [verbNet]="artwork" [propbank]="artwork" [frameNet]="artwork" [bNC]="artwork" [predicateMatrix]="artwork" [treeView]="artwork" )

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

for k in ${!dirs[@]}; do 
	echo "* $k"
	find $k/$dirres -type f -path "$k/$dirres/drawable-xxhdpi*" -mmin +10
done

