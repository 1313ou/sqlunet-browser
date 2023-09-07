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

  towhere=browser${suffix}/src/main/res/values/integer_sizes.xml
  >&2 echo -e "${G}${towhere}${Z}"

  echo '<?xml version="1.0" encoding="utf-8"?>' > "${towhere}"
  echo '<resources xmlns:tools="http://schemas.android.com/tools">' >> "${towhere}"

  ./_sizes.py "${fromwhere}" sqlunet${tag}.sqlite db${tag}/sqlunet${tag}.db db${tag}/sqlunet${tag}.db.zip 2> /dev/null >> "${towhere}"
  #./_sizes.py "${where}" sqlunet${tag}.sqlite db${tag}/sqlunet${tag}.db db${tag}/sqlunet${tag}.db.zip words samples definitions 2> /dev/null >> "${towhere}"

  echo '</resources>' >> "${towhere}"
done
