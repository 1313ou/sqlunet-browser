#!/bin/bash

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

if ./gradlew assemble; then
	dir=dist/releases
	pushd $dir > /dev/null
	./apk-version.sh
	popd > /dev/null
else
	echo "failed"
fi
exit

#specific targets
apps="browser browserfn browservn browserwn browserewn"
for a in $apps; do
	echo $a
	./gradlew :${a}:assembleRelease
done
