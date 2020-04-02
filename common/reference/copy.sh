#!/bin/bash

wherefrom="reference"
wherefrom=`readlink -m "${wherefrom}"`

tag=
app=browser${tag}
appdir="../../${app}/src/main/assets/reference"
appdir=`readlink -m "${appdir}"`
rm -fR "${appdir}"
cp -RT "${wherefrom}" "${appdir}"
rm ${appdir}/xnet-ewn.html
rm ${appdir}/xnet-vn.html
rm ${appdir}/xnet-fn.html
rm ${appdir}/relations/index*.html
mv ${appdir}/relations/android-index.html ${appdir}/relations/index.html

tag=wn
app=browser${tag}
appdir="../../${app}/src/main/assets/reference"
appdir=`readlink -m "${appdir}"`
rm -fR "${appdir}"
cp -RT "${wherefrom}" "${appdir}"
rm ${appdir}/verbnet.html
rm ${appdir}/propbank.html
rm ${appdir}/framenet.html
rm ${appdir}/xnet.html
#rm ${appdir}/xnet-ewn.html
rm ${appdir}/xnet-vn.html
rm ${appdir}/xnet-fn.html
rm ${appdir}/images/vn*.png
rm ${appdir}/images/pb*.png
rm ${appdir}/images/fn*.png
rm ${appdir}/images/verbnet.png
rm ${appdir}/images/propbank.png
rm ${appdir}/images/framenet.png
rm ${appdir}/images/predicatematrix.png
rm ${appdir}/relations/index*.html
mv ${appdir}/relations/android-index.html ${appdir}/relations/index.html

tag=ewn
app=browser${tag}
appdir="../../${app}/src/main/assets/reference"
appdir=`readlink -m "${appdir}"`
rm -fR "${appdir}"
cp -RT "${wherefrom}"  "${appdir}"
rm ${appdir}/verbnet.html
rm ${appdir}/propbank.html
rm ${appdir}/framenet.html
rm ${appdir}/xnet.html
#rm ${appdir}/xnet-wn.html
rm ${appdir}/xnet-vn.html
rm ${appdir}/xnet-fn.html
rm ${appdir}/images/vn*.png
rm ${appdir}/images/pb*.png
rm ${appdir}/images/fn*.png
rm ${appdir}/images/verbnet.png
rm ${appdir}/images/propbank.png
rm ${appdir}/images/framenet.png
rm ${appdir}/images/predicatematrix.png
rm ${appdir}/relations/index*.html
mv ${appdir}/relations/android-index.html ${appdir}/relations/index.html

tag=fn
app=browser${tag}
appdir="../../${app}/src/main/assets/reference"
appdir=`readlink -m "${appdir}"`
rm -fR "${appdir}"
cp -RT "${wherefrom}"  "${appdir}"
rm -fR ${appdir}/relations
rm ${appdir}/wordnet.html
rm ${appdir}/verbnet.html
rm ${appdir}/propbank.html
rm ${appdir}/bnc.html
rm ${appdir}/xnet.html
rm ${appdir}/xnet-wn.html
rm ${appdir}/xnet-ewn.html
rm ${appdir}/xnet-vn.html
rm ${appdir}/images/wn*.png
rm ${appdir}/images/vn*.png
rm ${appdir}/images/pb*.png
rm ${appdir}/images/bnc*.png
rm ${appdir}/images/wordnet.png
rm ${appdir}/images/verbnet.png
rm ${appdir}/images/propbank.png
rm ${appdir}/images/predicatematrix.png

tag=vn
app=browser${tag}
appdir="../../${app}/src/main/assets/reference"
appdir=`readlink -m "${appdir}"`
rm -fR "${appdir}"
cp -RT "${wherefrom}" "${appdir}"
rm ${appdir}/framenet.html
rm ${appdir}/xnet.html
rm ${appdir}/xnet-wn.html
rm ${appdir}/xnet-ewn.html
rm ${appdir}/xnet-fn.html
rm ${appdir}/images/fn*.png
rm ${appdir}/images/framenet.png
rm ${appdir}/images/predicatematrix.png
rm ${appdir}/relations/index*.html
mv ${appdir}/relations/android-index.html ${appdir}/relations/index.html

