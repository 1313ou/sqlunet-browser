#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

R='\u001b[31m'
G='\u001b[32m'
B='\u001b[34m'
Y='\u001b[33m'
M='\u001b[35m'
C='\u001b[36m'
Z='\u001b[0m'

wherefrom="xml"
wherefrom=`readlink -m "${wherefrom}"`

whereto="$1"
if [ -z "${whereto}" ]; then
	whereto="assets"
fi
whereto=`readlink -m "${whereto}"`
mkdir -p ${whereto}

in="hypernym.xml hyponym.xml holonym.xml meronym.xml antonym.xml similar.xml causes.xml caused.xml entails.xml entailed.xml attribute.xml pertainym.xml derivation.xml adjderived.xml participle.xml alsosee.xml verbgroup.xml domain.xml member.xml"

xsl="link2html.xsl"
xsl_index="links2html.xsl"
xsl_selector="links2selector.xsl"
xsl_toc="links2toc.xsl"

# merge all *.xml files into all.xml
echo '<?xml version="1.0" encoding="UTF-8"?>' > ${wherefrom}/all.xml 
echo '<links>' >> ${wherefrom}/all.xml 
for xml in $in; do
	grep -v '<\?xml.*'  ${wherefrom}/${xml} 
done >> ${wherefrom}/all.xml 
echo '</links>' >> ${wherefrom}/all.xml

# transform all *.xml files into *.html
for xml in $in all.xml; do
	outfile=${xml%.*}.html
	echo -e "${M}XST ${xml} to ${outfile}${Z}"
	if ! ./xsl-transform.sh ${wherefrom}/${xml} ${whereto}/${outfile} ${xsl} html; then
		echo -e "${R}XST ${xml}${Z}"
	fi
done

# specific index.html
echo -e "${M}XST all.xml to index.html${Z}"
if ! ./xsl-transform.sh ${wherefrom}/all.xml ${whereto}/index.html ${xsl_index} html; then
	echo -e "${R}XST ${xml}${Z}"
fi

# specific index.html
echo -e "${M}XST all.xml to selector.html${Z}"
if ! ./xsl-transform.sh ${wherefrom}/all.xml ${whereto}/selector.html ${xsl_selector} html; then
	echo -e "${R}XST ${xml}${Z}"
fi
echo -e "${M}XST all.xml to toc.html${Z}"
if ! ./xsl-transform.sh ${wherefrom}/all.xml ${whereto}/toc.html ${xsl_toc} html; then
	echo -e "${R}XST ${xml}${Z}"
fi

cat ${whereto}/index.html |
sed "s/e.style.visibility='collapse'/e.style.display='none'/g" |
sed "s/e.style.visibility='visible'/e.style.display='block'/g" > ${whereto}/android-index.html

cp index*.html ${whereto}
cp style.css ${whereto}
