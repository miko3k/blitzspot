#!/bin/bash

# Point       Hermite       Cubic
# Box         Gaussian      Catrom
# Triangle    Quadratic     Mitchell
# CubicSpline

function doit() {
	size1=$1
	size2=$2
	subdir=$3
	filter=Box

	echo $size1 $size2 $dir $filter
	dir=mipmap-$subdir
	rm -rfv $dir
	mkdir -p $dir

	composite blitz-fore.png blitz-back.png $dir/ic_launcher.png
	mogrify -crop 644x644+110+110 +repage -resize $size1  $dir/ic_launcher.png

	dir=mipmap-$subdir-v26
	rm -rfv $dir
	mkdir -p $dir
	convert blitz-fore.png -filter $filter -resize $size2 $dir/ic_launcher_foreground.png
	convert blitz-back.png -filter $filter -resize $size2 $dir/ic_launcher_background.png
	

#	mkdir -p $dir
#	convert $file -filter $filter -resize $size $dir/$file
}

doit 36x36 81x81  ldpi
doit 48x48 108x108 mdpi
doit 72x72 162x162 hdpi
doit 96x96 216x216 xhdpi
doit 144x144 324x324 xxhdpi 
doit 192x192 432x432 xxxhdpi

rm -rf store

out=store.png
composite blitz-fore.png blitz-back.png $out
mogrify -crop 644x644+110+110 +repage -resize 512x512 $out

