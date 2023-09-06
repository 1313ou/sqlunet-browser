#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

case "$1" in
-clean)
	echo "clean start"
	./gradlew --stop
	./gradlew clean
	;;
*)
	echo "resuming"
	;;
esac

if ./gradlew bundleRelease; then
	echo "success"
else
	echo "failed"
fi
exit

#specific targets
apps="browser browserfn browservn browserwn browserewn browsersn"
for a in $apps; do
	echo $a
	./gradlew :${a}:bundleRelease
done
