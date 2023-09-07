#!/bin/bash

source define_colors.sh

fromwhere="data2/dist/"

projects="$*"
if [ -z "${projects}" ];then
  projects="top ewn wn vn fn sn"
fi
for project in ${projects}; do
  >&2 echo -e "${M}${project}${Z}"
  if [ "${project}" == "top" ]; then
    tag=
    suffix=
  else
    tag="-${project}"
    suffix="${project}"
  fi
  ./_sizes_tables.py "${fromwhere}" sqlunet${tag}.sqlite
done
