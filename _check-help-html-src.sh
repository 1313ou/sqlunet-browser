#!/bin/bash

source define_colors.sh

# extract source references from html files
findsrc(){
  local d="$1"
  if [ -z "${d}" ]; then
  	d=.
  fi
  find ${d} -name '*.html' -exec grep 'src=' {} \; | sed "s/['\"]/\"/g" | grep -Po 'src="\K[^"]*' | LC_COLLATE=C sort -u
}

modules="browser browserewn browserwn browservn browsersn browserfn"

for m in $modules; do
  dir="${m}/src/main/assets/help"
  echo -e "${M}${dir}${Z}"
  pushd "${dir}" > /dev/null
  while read f; do [ -e "${f}" ] || echo -e "${R}${f} !EXISTS{Z}"; done < <(findsrc)
  popd > /dev/null
done