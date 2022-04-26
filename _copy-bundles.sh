#!/bin/bash

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
)
declare -A dstdir
dstdir=(
[all]=db_ewn_asset
[ewn]=dbewn_ewn_asset
[vn]=dbvn_ewn_asset
[fn]=dbfn_ewn_asset
[sn]=dbsn_ewn_asset
)
declare -A srcdir
srcdir=(
[all]=db
[ewn]=db_oewn
[vn]=db_vn
[fn]=db_fn
[sn]=db_sn
)

for k in ${!distribtag[@]}; do
  m=${distribtag[$k]}
  src=${from}/${srcdir[$k]}
  dst=${dstdir[$k]}/src/main/assets/XX
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

	cp ${src}/distrib${m}.hsize ${dst}
  cp ${src}/distrib${m}.size ${dst}
  cp ${src}/distrib${m}.md5 ${dst}
  cp ${src}/sqlunet${m}.db.zip ${dst}
  cp ${src}/sqlunet${m}.db.zip.md5 ${dst}

done


