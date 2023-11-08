#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

ENV="JAVA_HOME=/opt/java/java17 "

case "$1" in
-clean)
	echo "clean start"
	${ENV}./gradlew --stop
	${ENV}./gradlew clean
	;;
*)
	echo "resuming"
	;;
esac

if ${ENV}./gradlew bundleRelease; then
	echo "success"
else
	echo "failed"
fi
exit

#specific targets
apps="browser browserfn browservn browserwn browserewn browsersn"
for a in $apps; do
	echo $a
	${ENV}./gradlew :${a}:bundleRelease
done
