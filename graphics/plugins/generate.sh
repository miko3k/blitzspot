#!/bin/bash

# Point       Hermite       Cubic
# Box         Gaussian      Catrom
# Triangle    Quadratic     Mitchell
# CubicSpline

function doit() {
	file=$1
	size=$2
	dir=drawable-$3
	filter=Box

	echo $file $size $dir $filter
	mkdir -p $dir
	convert $file -filter $filter -resize $size $dir/$file
}

rm -rf drawable*

for f in *.png; do
	doit $f 24x24 ldpi $o
	doit $f 32x32 mdpi $o
	doit $f 48x48 hdpi $o
	doit $f 64x64 xhdpi $o
	doit $f 96x96 xxhdpi $o
	doit $f 128x128 xxxhdpi $o
done
