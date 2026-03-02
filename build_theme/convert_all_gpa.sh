#!/usr/bin/env bash

for f in *.gpa; do
        d=$(dirname "$filepath")
        b="${f%.*}"
        #echo $f $d $b "$d/$b.gpl"
        python3 convert_gpa.py "$f" "$d/$b.txt"
done


