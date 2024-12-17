#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

source ./define_colors.sh

wherefrom="generated"
wherefrom=`readlink -m "${wherefrom}"`

# X N

tag=
app=browser${tag}
appdir="../../${app}/src/main/assets/reference"
appdir=`readlink -m "${appdir}"`
rm -fR "${appdir}"
cp -RT "${wherefrom}" "${appdir}"
rm -f ${appdir}/xnet-ewn.html
rm -f ${appdir}/xnet-vn.html
rm -f ${appdir}/xnet-fn.html
echo -e "copied to ${Y}${app}${Z} assets reference"

#W N

tag=wn
app=browser${tag}
appdir="../../${app}/src/main/assets/reference"
appdir=`readlink -m "${appdir}"`
rm -fR "${appdir}"
cp -RT "${wherefrom}" "${appdir}"
rm -f ${appdir}/verbnet.html
rm -f ${appdir}/propbank.html
rm -f ${appdir}/framenet.html
rm -f ${appdir}/syntagnet.html
rm -f ${appdir}/xnet.html
rm -f ${appdir}/xnet-vn.html
rm -f ${appdir}/xnet-fn.html
rm -f ${appdir}/xnet-sn.html
rm -f ${appdir}/images/vn*.png
rm -f ${appdir}/images/pb*.png
rm -f ${appdir}/images/fn*.png
rm -f ${appdir}/images/sn*.png
rm -f ${appdir}/images/verbnet.png
rm -f ${appdir}/images/propbank.png
rm -f ${appdir}/images/framenet.png
rm -f ${appdir}/images/syntagnet.png
rm -f ${appdir}/images/predicatematrix.png
echo -e "copied to ${Y}${app}${Z} assets reference"

#E W N

tag=ewn
app=browser${tag}
appdir="../../${app}/src/main/assets/reference"
appdir=`readlink -m "${appdir}"`
rm -fR "${appdir}"
cp -RT "${wherefrom}"  "${appdir}"
rm -f ${appdir}/verbnet.html
rm -f ${appdir}/propbank.html
rm -f ${appdir}/framenet.html
rm -f ${appdir}/syntagnet.html
rm -f ${appdir}/xnet.html
rm -f ${appdir}/xnet-vn.html
rm -f ${appdir}/xnet-fn.html
rm -f ${appdir}/xnet-sn.html
rm -f ${appdir}/images/vn*.png
rm -f ${appdir}/images/pb*.png
rm -f ${appdir}/images/fn*.png
rm -f ${appdir}/images/sn*.png
rm -f ${appdir}/images/verbnet.png
rm -f ${appdir}/images/propbank.png
rm -f ${appdir}/images/framenet.png
rm -f ${appdir}/images/syntagnet.png
rm -f ${appdir}/images/predicatematrix.png
echo -e "copied to ${Y}${app}${Z} assets reference"

# F N

tag=fn
app=browser${tag}
appdir="../../${app}/src/main/assets/reference"
appdir=`readlink -m "${appdir}"`
rm -fR "${appdir}"
cp -RT "${wherefrom}"  "${appdir}"
rm -fR ${appdir}/relations
rm -f ${appdir}/wordnet.html
rm -f ${appdir}/verbnet.html
rm -f ${appdir}/propbank.html
rm -f ${appdir}/syntagnet.html
rm -f ${appdir}/bnc.html
rm -f ${appdir}/xnet.html
rm -f ${appdir}/xnet-wn.html
rm -f ${appdir}/xnet-ewn.html
rm -f ${appdir}/xnet-vn.html
rm -f ${appdir}/xnet-sn.html
rm -f ${appdir}/images/wn*.png
rm -f ${appdir}/images/vn*.png
rm -f ${appdir}/images/pb*.png
rm -f ${appdir}/images/sn*.png
rm -f ${appdir}/images/bnc*.png
rm -f ${appdir}/images/wordnet.png
rm -f ${appdir}/images/verbnet.png
rm -f ${appdir}/images/propbank.png
rm -f ${appdir}/images/syntagnet.png
rm -f ${appdir}/images/predicatematrix.png
echo -e "copied to ${Y}${app}${Z} assets reference"

# V N

tag=vn
app=browser${tag}
appdir="../../${app}/src/main/assets/reference"
appdir=`readlink -m "${appdir}"`
rm -fR "${appdir}"
cp -RT "${wherefrom}" "${appdir}"
# cleanup xn,wn,ewn,fn,sn,pm
# keep vn
rm -f ${appdir}/framenet.html
rm -f ${appdir}/xnet.html
rm -f ${appdir}/xnet-wn.html
rm -f ${appdir}/xnet-ewn.html
rm -f ${appdir}/xnet-fn.html
rm -f ${appdir}/xnet-sn.html
rm -f ${appdir}/images/fn*.png
rm -f ${appdir}/images/sn*.png
rm -f ${appdir}/images/framenet.png
rm -f ${appdir}/images/syntagnet.png
rm -f ${appdir}/images/predicatematrix.png
echo -e "copied to ${Y}${app}${Z} assets reference"

# S N

tag=sn
app=browser${tag}
appdir="../../${app}/src/main/assets/reference"
appdir=`readlink -m "${appdir}"`
rm -fR "${appdir}"
cp -RT "${wherefrom}"  "${appdir}"
# clean up xn,wn,ewn,vn,pb,fn,pm
# keep sn
rm -f ${appdir}/verbnet.html
rm -f ${appdir}/propbank.html
rm -f ${appdir}/framenet.html
rm -f ${appdir}/xnet.html
rm -f ${appdir}/xnet-wn.html
rm -f ${appdir}/xnet-ewn.html
rm -f ${appdir}/xnet-vn.html
rm -f ${appdir}/xnet-fn.html
rm -f ${appdir}/images/vn*.png
rm -f ${appdir}/images/pb*.png
rm -f ${appdir}/images/fn*.png
rm -f ${appdir}/images/verbnet.png
rm -f ${appdir}/images/propbank.png
rm -f ${appdir}/images/framenet.png
rm -f ${appdir}/images/predicatematrix.png
echo -e "copied to ${Y}${app}${Z} assets reference"
