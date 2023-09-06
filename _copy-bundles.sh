#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

R='\u001b[31m'
G='\u001b[32m'
Y='\u001b[33m'
B='\u001b[34m'
M='\u001b[35m'
C='\u001b[36m'
Z='\u001b[0m'

from=/opt/devel/sqlbuilder2/semantikos

declare -A distribtag
distribtag=(
[all]=
[ewn]=-ewn
[vn]=-vn
[fn]=-fn
[sn]=-sn
[wn31]=-wn
)
declare -A dstdir
dstdir=(
[all]=db_ewn_asset/src/main/assets/XX
[ewn]=dbewn_ewn_asset/src/main/assets/XX
[vn]=dbvn_ewn_asset/src/main/assets/XX
[fn]=dbfn_ewn_asset/src/main/assets/XX
[sn]=dbsn_ewn_asset/src/main/assets/XX
[wn31]=dbwn_wn31_asset/src/main/assets/31
)
declare -A srcdir
srcdir=(
[all]=db
[ewn]=db-oewn
[vn]=db-vn
[fn]=db-fn
[sn]=db-sn
[wn31]=db-wn31
)

for k in ${!distribtag[@]}; do
  m=${distribtag[$k]}
  src=${from}/${srcdir[$k]}
  dst=${dstdir[$k]}
  if [ ! -e "${src}" ]; then
    echo -e "${R}${src}${Z}"
    continue
    fi
  if [ ! -e "${dst}" ]; then
    echo -e "${R}[DST]${dst}${Z}"
    continue
    fi

echo -e "${G}${src}${Z} -> ${G}${dst}${Z}"
[ -e ${src}/distrib${m}.hsize ] || echo -e "${R}[SRC]${m} hsize${Z}"
[ -e ${src}/distrib${m}.size ] || echo -e "${R}[SRC]${m} size${Z}"
[ -e ${src}/distrib${m}.md5 ] || echo -e "${R}[SRC]${m} md5${Z}"
[ -e ${src}/sqlunet${m}.db.zip.md5 ] || echo -e "${R}[SRC]${m} md5zip${Z}"
[ -e ${src}/sqlunet${m}.db.zip ] || echo -e "${R}[SRC]${m} zip${Z}"

mkdir -p ${dst}

cp -p ${src}/distrib${m}.hsize ${dst}
cp -p ${src}/distrib${m}.size ${dst}
cp -p ${src}/distrib${m}.md5 ${dst}
cp -p ${src}/sqlunet${m}.db.zip ${dst}
cp -p ${src}/sqlunet${m}.db.zip.md5 ${dst}

done

