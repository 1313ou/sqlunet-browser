#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

wherefrom="reference"
wherefrom=`readlink -m "${wherefrom}"`

# G L O B A L

tag=
app=browser${tag}
appdir="../../${app}/src/main/assets/reference"
appdir=`readlink -m "${appdir}"`
rm -fR "${appdir}"
cp -RT "${wherefrom}" "${appdir}"
rm -f ${appdir}/xnet-ewn.html
rm -f ${appdir}/xnet-vn.html
rm -f ${appdir}/xnet-fn.html
rm -f ${appdir}/relations/index*.html
#mv ${appdir}/relations/android-index.html ${appdir}/relations/index.html

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
#rm -f ${appdir}/xnet-wn.html
#rm -f ${appdir}/xnet-ewn.html
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

rm -f ${appdir}/relations/index*.html
#mv ${appdir}/relations/android-index.html ${appdir}/relations/index.html

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
#rm -f ${appdir}/xnet-wn.html
#rm -f ${appdir}/xnet-ewn.html
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

rm -f ${appdir}/relations/index*.html
#mv ${appdir}/relations/android-index.html ${appdir}/relations/index.html

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
#rm -f ${appdir}/xnet-fn.html
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

# V N

tag=vn
app=browser${tag}
appdir="../../${app}/src/main/assets/reference"
appdir=`readlink -m "${appdir}"`
rm -fR "${appdir}"
cp -RT "${wherefrom}" "${appdir}"
rm -f ${appdir}/framenet.html

rm -f ${appdir}/xnet.html
rm -f ${appdir}/xnet-wn.html
rm -f ${appdir}/xnet-ewn.html
#rm -f ${appdir}/xnet-vn.html
rm -f ${appdir}/xnet-fn.html
rm -f ${appdir}/xnet-sn.html

rm -f ${appdir}/images/fn*.png
rm -f ${appdir}/images/sn*.png

rm -f ${appdir}/images/framenet.png
rm -f ${appdir}/images/syntagnet.png
rm -f ${appdir}/images/predicatematrix.png

rm -f ${appdir}/relations/index*.html
#mv -f ${appdir}/relations/android-index.html ${appdir}/relations/index.html

# S N

tag=sn
app=browser${tag}
appdir="../../${app}/src/main/assets/reference"
appdir=`readlink -m "${appdir}"`
rm -fR "${appdir}"
cp -RT "${wherefrom}"  "${appdir}"
rm -f ${appdir}/verbnet.html
rm -f ${appdir}/propbank.html
rm -f ${appdir}/framenet.html

rm -f ${appdir}/xnet.html
rm -f ${appdir}/xnet-wn.html
rm -f ${appdir}/xnet-ewn.html
rm -f ${appdir}/xnet-vn.html
rm -f ${appdir}/xnet-fn.html
#rm -f ${appdir}/xnet-sn.html

rm -f ${appdir}/images/vn*.png
rm -f ${appdir}/images/pb*.png
rm -f ${appdir}/images/fn*.png

rm -f ${appdir}/images/verbnet.png
rm -f ${appdir}/images/propbank.png
rm -f ${appdir}/images/framenet.png
rm -f ${appdir}/images/predicatematrix.png

rm -f ${appdir}/relations/index*.html
#mv ${appdir}/relations/android-index.html ${appdir}/relations/index.html

