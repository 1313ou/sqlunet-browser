#!/usr/bin/bash

m="$1"
if [ -z "$m" ]; then
        echo "Module needed"
        exit 1
fi

if [ ! -z "$2" ]; then
  seedsDay=$(readlink -f "$2")
  seedsDay="-f $2"
else
        echo "Day seeds needed"
        exit 2
fi

if [ ! -z "$3" ]; then
  seedsNight=$(readlink -f "$3")
  seedsNight="-f $3"
else
        echo "Night seeds needed"
        exit 3
fi

source define_colors.sh

echo -e "${M}seeds day          ${seedsDay}$Z"
echo -e "${M}seeds night        ${seedsNight}$Z"

# run

./run.sh -o theme_html    $seedsDay   -x > "html/theme-${m}-day.html"
./run.sh -o theme_html -d $seedsNight -x > "html/theme-${m}-night.html"
