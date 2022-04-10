#!/bin/bash

R='\u001b[31m'
G='\u001b[32m'
Y='\u001b[33m'
B='\u001b[34m'
M='\u001b[35m'
C='\u001b[36m'
Z='\u001b[0m'

from=/opt/devel/sqlbuilder2/semantikos

declare -A data
data=(
[ewn]=ewn
[vn]=vn
[fn]=fn
[sn]=sn
)
declare -A data2
data2=(
[ewn]=oewn
[vn]=vn
[fn]=fn
[sn]=sn
)

for k in ${!data[@]}; do
  m=${data[$k]}
  m2=${data2[$k]}
  src=${from}/db_${m2}
  dst=db${m}_ewn_asset/src/main/assets/XX
  if [ ! -e "${src}" ]; then
    echo -e "${R}${src}${Z}"
    continue
    fi
  if [ ! -e "${dst}" ]; then
    echo -e "${R}${dst}${Z}"
    continue
    fi

  echo -e "${G}${src}${Z} -> ${G}${dst}${Z}"
	[ -e ${src}/distrib-${m}.hsize ] || echo -e "${R}${m} hsize${Z}"
  [ -e ${src}/distrib-${m}.size ] || echo -e "${R}${m} size${Z}"
  [ -e ${src}/distrib-${m}.md5 ] || echo -e "${R}${m} md5${Z}"
  [ -e ${src}/sqlunet-${m}.db.zip.md5 ] || echo -e "${R}${m} md5zip${Z}"
  [ -e ${src}/sqlunet-${m}.db.zip ] || echo -e "${R}${m} zip${Z}"

	mkdir -p ${dst}

	cp ${src}/distrib-${m}.hsize ${dst}
  cp ${src}/distrib-${m}.size ${dst}
  cp ${src}/distrib-${m}.md5 ${dst}
  cp ${src}/sqlunet-${m}.db.zip ${dst}
  cp ${src}/sqlunet-${m}.db.zip.md5 ${dst}

done


