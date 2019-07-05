#!/bin/bash

# Point       Hermite       Cubic
# Box         Gaussian      Catrom
# Triangle    Quadratic     Mitchell
# CubicSpline

function doit() {
	size=$1
	dir=drawable-$2
	filter=Box

	echo $size $dir $filter
	mkdir -p $dir
	convert status.png -filter $filter -resize $size $dir/status.png
}

rm -rfv drawable*

doit 18x18 ldpi 
doit 24x24 mdpi 
doit 36x36 hdpi 
doit 48x48 xhdpi 
doit 72x72 xxhdpi 
doit 96x96 xxxhdpi 
