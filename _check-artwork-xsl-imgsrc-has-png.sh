#!/bin/bash

source define_colors.sh

libs="wordNet verbNet propbank frameNet syntagNet bNC"

declare -A uses
uses=(
[browser]="wordNet verbNet propbank bNC predicateMatrix"
[browserewn]="wordNet bNC"
[browserwn]="wordNet bNC"
[browservn]="wordNet verbNet propbank bNC"
[browsersn]="wordNet syntagNet bNC"
[browserfn]="frameNet"
)

# extract source references from html files
findsrc(){
  local d="$1"
  if [ -z "${d}" ]; then
  	d=.
  fi
  find ${d} -name '*.xsl' -path '*/src/main/*' -exec grep 'src=' {} \; | sed "s/['\"]/\"/g" | grep -Po 'src="\K[^"]*' | LC_COLLATE=C sort -u
}

findfileswithsrc(){
  local d="$1"
  if [ -z "${d}" ]; then
  	d=.
  fi
  find ${d} -name '*.xsl' -path '*/src/main/*' -exec grep -l 'src=' {} \;
}

for a in ${!uses[@]}; do
  echo -e "${Y}app ${a}${Z}"
  modules=${uses[${a}]}

  for m in ${modules}; do
    echo -e "${M}module ${m}${Z}"
	
    echo -e "${C}source files for ${m}${Z}"
	while read f; do echo -e "${C}${f}${Z}"; done < <(findfileswithsrc ${m})

    echo -e "${B}use image files for ${m}${Z}"
	while read f; do echo -e "${B}${f}${Z}"; done < <(findsrc ${m})

 	dir="${a}/src/main/assets"
	echo -e "${G}scan ${dir}${Z}"
	while read f; do [ -e "${dir}/${f}" ] || echo -e "${R}${dir}/${f} !EXISTS${Z}"; done < <(findsrc ${m})
  done

  echo
done
echo
for m in ${libs}; do
  echo -e "${Y}module ${m}${Z}"
	
  echo -e "${C}source files for ${m}${Z}"
  while read f; do echo -e "${C}${f}${Z}"; done < <(findfileswithsrc ${m})

  echo -e "${B}use image files for ${m}${Z}"
  while read f; do echo -e "${B}${f}${Z}"; done < <(findsrc ${m})

  dir="${m}/src/main/assets"
  echo -e "${G}scan ${dir}${Z}"
  while read f; do [ -e "${dir}/${f}" ] || echo -e "${R}${dir}/${f} !EXISTS${Z}"; done < <(findsrc ${m})

done



