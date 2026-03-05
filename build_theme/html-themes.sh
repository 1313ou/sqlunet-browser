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
if [ -z "$all" ]; then
  all="${!tasks[@]}"
  fi
for m in ${all}; do
  d=${tasks[$m]}
  res=$H/$d/src/main/res
  seedsDay=${m}-day.txt 
  seedsNight=${m}-night.txt
  echo -e "${Y}${m}${Z}"

  echo -e "${B}day ${K} $seedsDay${Z}"
  values=$(./run.sh -o map_day -f "$seedsDay")
  ./run.sh -o colors1_day -f "$seedsDay"
  ./run.sh -o html $values > html/${m}-day.html

  echo -e "${B}night ${K} $seedsNight${Z}"
  values=$(./run.sh -o map_night -f "$seedsNight")
  ./run.sh -o colors1_night -f "$seedsNight"
  ./run.sh -o html $values > html/${m}-night.html
done
