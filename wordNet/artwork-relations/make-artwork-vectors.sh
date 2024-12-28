#!/bin/bash

set -e

#Usage: [-c] [-d] [-in <file or directory>] [-out <directory>] [-widthDp <size>] [-heightDp <size>] [-addHeader]
#Options:
#  -in <file or directory>:  If -c is specified, Converts the given .svg file 
#                            to VectorDrawable XML, or if a directory is specified,
#                            all .svg files in the given directory. Otherwise, if -d
#                            is specified, displays the given VectorDrawable XML file
#                            or all VectorDrawables in the given directory.
#  -out <directory>          If specified, write converted files out to the given
#                            directory, which must exist. If not specified the
#                            converted files will be written to the directory
#                            containing the input files.
#  -c                        If present, SVG files are converted to VectorDrawable XML
#                            and written out.
#  -d                        Displays the given VectorDrawable(s), or if -c is
#                            specified the results of the conversion.
#  -widthDp <size>           Force the width to be <size> dp, <size> must be integer
#  -heightDp <size>          Force the height to be <size> dp, <size> must be integer
#  -addHeader                Add AOSP header to the top of the generated XML file

source define_colors.sh
source define_relations.sh

vdtool=$(readlink -m vdutils/vd-tool/bin/vd-tool)
dirsrc=composite
dirdest=../src/main/res/drawable
mkdir -p ${dirdest}

res=128

echo -e "${Y}Vector Drawables${Z} ${B}${res}${Z} ${K}to dirdest${Z}"
for svg in ${rels} ${morphs}; do
        echo -e "${M}${svg}${Z}"
        if ${vdtool} -c -in "${dirsrc}/${svg}.svg" -out "${dirdest}" | grep 'ERROR' ; then
                echo -e "${R}${svg}${Z}"
        else
                echo -e "${G}${svg}${Z}"
        fi       
done

${vdtool} -d -in "${dirdest}"
