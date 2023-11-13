#!/bin/bash

source define_colors.sh

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

modules="wordNet verbNet propbank frameNet syntagNet predicateMatrix"

for m in $modules; do
  echo -e "${Y}${m}${Z}"
  while read f; do echo -e "${M}${f}${Z}"; done < <(findfileswithsrc ${m})
  while read f; do echo -e "${B}${f}${Z}"; done < <(findsrc ${m})

  dir="${m}/src/main/assets"
  echo -e "${M}${dir}${Z}"
  while read f; do [ -e "${dir}/${f}" ] || echo -e "${R}${f} !EXISTS${Z}"; done < <(findsrc ${m})

done