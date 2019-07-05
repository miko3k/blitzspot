#!/bin/bash

./generate.sh

DEST=../../app/src/main/res
   
for d in mipmap-hdpi     mipmap-mdpi     mipmap-xxhdpi     mipmap-ldpi     mipmap-xhdpi     mipmap-xxxhdpi\
            mipmap-hdpi-v26 mipmap-mdpi-v26 mipmap-xxhdpi-v26 mipmap-ldpi-v26 mipmap-xhdpi-v26 mipmap-xxxhdpi-v26; do
	cp -v $d/* $DEST/$d
	rm -rfv $d
done

