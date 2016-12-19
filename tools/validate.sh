#!/bin/bash

#target
t=$1
if [ -z "$t" ]; then
	read -p 'item >' t
fi
tp=$t.xml

java -classpath "xmltoolkit.jar" xml.toolkit.Validate SqlUNet.xsd $tp

