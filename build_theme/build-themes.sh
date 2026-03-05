#!/usr/bin/bash


source define_colors.sh

H=..

declare -A tasks
tasks=(
[xn]=browser
[wn]=browserwncommon
[vn]=browservn
[fn]=browserfn
[sn]=browsersn
)

./convert_all_gpa.sh

all="$@"
if [ -z "$all"]; then
  all="${!tasks[@]}"
  fi
for m in ${all}; do
  d=${tasks[$m]}
  res=$H/$d/src/main/res
  seedsDay=${m}-day.txt 
  seedsNight=${m}-night.txt
  echo -e "${Y}${m}${Z}"

  ./build-theme.sh "$res" "$seedsDay" "$seedsNight"
done  
