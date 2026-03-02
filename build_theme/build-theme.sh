#!/usr/bin/bash

D="./output"
Dcore="./output"
echo $1 $2
if [ ! -z "$1" ]; then
  D="$1"
  Dcore=$(readlink -f "${D}/../../../../core/src/main/res")
  fi
if [ ! -z "$2" ]; then
  seedsDay=$(readlink -f "$2")
  seedsDay="-f $2"
else
        echo "Day seeds needed"
        exit 3
  fi
if [ ! -z "$3" ]; then
  seedsNight=$(readlink -f "$3")
  seedsNight="-f $3"
else
        echo "Night seeds needed"
        exit 4
  fi

source define_colors.sh
if [ ! -e "${D}" ]; then
        echo -e "${R}app   ${D}$Z"
        exit 1
fi

if [ ! -e "${Dcore}" ]; then
        echo -e "${R}core  ${Dcore}$Z"
        exit 2
fi

echo -e "${M}app                ${D}$Z"
echo -e "${M}core               ${Dcore}$Z"
echo -e "${M}seeds day          ${seedsDay}$Z"
echo -e "${M}seeds night        ${seedsNight}$Z"

# run

mkdir -p "$Dcore/values"
mkdir -p "$D/values"
mkdir -p "$D/values-night"

./run.sh -o attrs > "$Dcore/values/attrs.xml"

./run.sh -o themeday > "$D/values/themes.xml"
./run.sh -o overlaysday > "$D/values/themes_overlays.xml"

./run.sh -o themenight > "$D/values-night/themes.xml"
./run.sh -o overlaysnight > "$D/values-night/themes_overlays.xml"

./run.sh -o colorsday $seedsDay  -x > "$D/values/colors.xml"
./run.sh -o colorsnight $seedsNight -x > "$D/values-night/colors.xml"

