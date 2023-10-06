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

if [ "-31" == "$1" ]; then
	wn31="31"
	shift
fi

whereto="$1"
if [ -z "${whereto}" ]; then
	whereto="html${wn31}"
fi
whereto=`readlink -m "${whereto}"`
mkdir -p ${whereto}

in_sem="hypernym.xml hyponym.xml holonym.xml meronym.xml causes.xml caused.xml entails.xml entailed.xml attribute.xml similar.xml verbgroup.xml"
in_lex="antonym.xml participle.xml pertainym.xml derivation.xml"
in_both="also.xml"
in_domain="domain.xml member.xml"
in_morph="state.xml result.xml event.xml property.xml location.xml destination.xml agent.xml undergoer.xml uses.xml instrument.xml bymeansof.xml material.xml vehicle.xml bodypart.xml"
in="${in_sem} ${in_lex} ${in_both} ${in_domain}"
if [ -z "${wn31}" ]; then
	in="${in} ${in_morph}"
fi

xsl="relation2html.xsl"
xsl_index="relations2html.xsl"
xsl_selector="relations2selector.xsl"
xsl_toc="relations2toc.xsl"

# merge all *.xml files into all.xml
echo '<?xml version="1.0" encoding="UTF-8"?>' > ${wherefrom}/all.xml 
echo '<relations>' >> ${wherefrom}/all.xml
for xml in $in; do
	grep -v '<\?xml.*'  ${wherefrom}/${xml} 
done >> ${wherefrom}/all.xml 
echo '</relations>' >> ${wherefrom}/all.xml

# transform all *.xml files into *.html
for xml in $in all.xml; do
	outfile=${xml%.*}.html
	echo -e "${M}XST ${xml} to ${outfile}${Z}"
	if ! ./xsl-transform.sh ${wherefrom}/${xml} ${whereto}/${outfile} ${xsl} html; then
		echo -e "${R}XST ${xml}${Z}"
	fi
done

# index0.html
echo -e "${M}XST all.xml to index0.html${Z}"
if ! ./xsl-transform.sh ${wherefrom}/all.xml ${whereto}/index0.html ${xsl_index} html; then
	echo -e "${R}XST ${xml}${Z}"
fi

# selector.html
echo -e "${M}XST all.xml to selector.html${Z}"
if ! ./xsl-transform.sh ${wherefrom}/all.xml ${whereto}/selector.html ${xsl_selector} html; then
	echo -e "${R}XST ${xml}${Z}"
fi
echo -e "${M}XST all.xml to toc.html${Z}"
if ! ./xsl-transform.sh ${wherefrom}/all.xml ${whereto}/toc.html ${xsl_toc} html; then
	echo -e "${R}XST ${xml}${Z}"
fi

# (android) index.html
cat ${whereto}/index0.html |
sed "s/e.style.visibility='collapse'/e.style.display='none'/g" |
sed "s/e.style.visibility='visible'/e.style.display='block'/g" > ${whereto}/index.html
rm ${whereto}/index0.html

#cp index*.html ${whereto}
cp style.css ${whereto}
