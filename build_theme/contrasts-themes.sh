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
  ./run.sh -o colors1_day -f "$seedsDay"
  ./run.sh -o map_day -f "$seedsDay"
  echo -e "${M}custom / primary${Z}"
  ./run.sh -o name_gpick -f "$seedsDay" -i 0,1 || echo -e "${R}FAIL${Z}"
  ./run.sh -o contrasts -f "$seedsDay" -i 1,0 || echo -e "${R}FAIL${Z}"
  echo -e "${M}custom / secondary${Z}"
  ./run.sh -o name_gpick -f "$seedsDay" -i 0,2 || echo -e "${R}FAIL${Z}"
  ./run.sh -o contrasts -f "$seedsDay" -i 2,0 || echo -e "${R}FAIL${Z}"
  echo -e "${M}custom / tertiary${Z}"
  ./run.sh -o name_gpick -f "$seedsDay" -i 0,3 || echo -e "${R}FAIL${Z}"
  ./run.sh -o contrasts -f "$seedsDay" -i 3,0 || echo -e "${R}FAIL${Z}"

  echo -e "${B}night ${K} $seedsNight${Z}"
  ./run.sh -o colors1_night -f "$seedsNight"
  ./run.sh -o map_night -f "$seedsNight"
   echo -e "${M}custom / primary${Z}"
  ./run.sh -o name_gpick -f "$seedsNight" -i 0,1 || echo -e "${R}FAIL${Z}"
  ./run.sh -o contrasts -f "$seedsNight" -i 1,0 || echo -e "${R}FAIL${Z}"
  echo -e "${M}custom / secondary${Z}"
  ./run.sh -o name_gpick -f "$seedsNight" -i 0,2 || echo -e "${R}FAIL${Z}"
  ./run.sh -o contrasts -f "$seedsNight" -i 2,0 || echo -e "${R}FAIL${Z}"
  echo -e "${M}custom / tertiary${Z}"
  ./run.sh -o name_gpick -f "$seedsNight" -i 0,3 || echo -e "${R}FAIL${Z}"
  ./run.sh -o contrasts -f "$seedsNight" -i 3,0 || echo -e "${R}FAIL${Z}"
  echo
done
