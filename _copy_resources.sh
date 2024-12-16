#!/bin/bash

source define_colors.sh

SRCDIR=../../semantikos-builder/semantikos-android
SRCDIR=$(readlink -m ${SRCDIR})

declare -A modules_short=(['verbNet']='vn'      ['propbank']='pb'       ['frameNet']='fn'       ['syntagNet']='sn'        ['bNC']='bnc' )
declare -A modules_lower=(['verbNet']='verbnet' ['propbank']='propbank' ['frameNet']='framenet' ['syntagNet']='syntagnet' ['bNC']='bnc' )

for m in  "${!modules_short[@]}"; do
        echo -e "${Y}${m}${Z} ${modules_lower[$m]} ${modules_short[$m]}"
        
        src="${SRCDIR}/${modules_short[$m]}/src/main/resources/${modules_short[$m]}/Names.properties"
        col=$G; [ -e "${src}" ] || col=$R
        echo -e "${col}${src}${Z}"
        
        dst="${m}/src/proto/${modules_lower[$m]}/Names.properties"
 
        rm ${dst}       
        cp "${src}" "${dst}"

        col=$M; [ -e "${dst}" ] || col=$R
        echo -e "${col}${dst}${Z}"
done
