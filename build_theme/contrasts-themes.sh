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

for m in ${!tasks[@]}; do
  d=${tasks[$m]}
  res=$H/$d/src/main/res
  seedsDay=${m}-day.txt 
  seedsNight=${m}-night.txt
  echo -e "${Y}${m}${Z}"

  echo -e "${B}day ${K} $seedsDay${Z}"
  ./run.sh -o contrasts  -f "$seedsDay"

   echo -e "${B}night ${K} $seedsNight${Z}"
 ./run.sh -o contrasts  -f "$seedsNight"
done
