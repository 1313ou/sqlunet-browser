#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

#$1==:browser: :browserfn: :browservn: :browserwn: :browserewn: :browsersn: or null

function rungradle()
{
        export JAVA_HOME=/opt/java/java17
        ./gradlew $@
}

# start
case "$1" in
-z)
	shift
	echo "stop daemon"
	rungradle --stop
	echo "clean task"
	rungradle clean
	;;
*)
	echo "using current daemon"
	;;
esac

# determine which project
which=
if [[ "$1" == "--*" ]]; then
        which=$1
        shift
fi

echo "project=${which} args=$*"
if rungradle ${which}bundleRelease $*; then
	echo "success"
else
	echo "failed"
fi

