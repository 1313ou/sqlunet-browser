#!/bin/bash

apps="browser browserfn browservn browserwn"

for a in $apps; do
	echo $a
	./gradlew :${a}:assembleRelease
done
