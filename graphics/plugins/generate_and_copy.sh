#!/bin/bash

DEST=../../app/src/main/res/ 

./generate.sh
for d in drawable-hdpi  drawable-mdpi   drawable-xxhdpi drawable-ldpi  drawable-xhdpi  drawable-xxxhdpi; do
	rm -rvf $DEST/$d
	mv -v $d $DEST/$d
done

