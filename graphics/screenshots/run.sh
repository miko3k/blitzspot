function render {
	back="$1"
	file="$2"
	color="$3"
	off="$4"
	text="$5"

	if [[ ! $file == $prefix* ]]; then
		echo "skipping $file"
		return
	fi

	echo "rendering $file"

	shadowcolor="#0008"

	convert -background '#0000' \
		-fill '#0000' -gravity center \
		-font assets/roboto_slab.ttf -pointsize 120 -kerning -2 \
		-strokewidth 10 -stroke $shadowcolor \
		-blur 0x4\
		-size 1240x600 \
		caption:"$text" \
		-fill white -gravity center \
		-stroke "#fff0" \
		caption:"$text" \
		-composite\
		text.png

	convert assets/$back.jpg \
		-crop 1440x2840"$off" +repage\
		-fill "$color" -colorize '60%' \
		"$file" -geometry +188+676 -composite \
		assets/foreground.png -composite \
		text.png -geometry +100+0 -composite \
		"out/$file"

	rm text.png
}
prefix="$1"

# abstract: 5300x3500  ... max 3900,  600
# abstract2: 3200x4900 ... max 1700, 2000
# abstract3: 4000x4700 ... max 2500, 1800
mkdir -p out
render abstract3 app_start.png          "#fe8a71" +0+000 'Hello. I am Blizspot ;)'
render abstract3 app_wiki_search_love.png "#4b86b4" +500+1000 'Suggestions work.'
render abstract3 app_add_menu.png       "#3da4ab" +1500+1200 'Add plugin from the internet, phone,\nor a builtin one.'
render abstract3 app_mycroft_dictionary.png "#d61d41" +2500+600 'The Mycroft Project hosts thousands of plugins!'
render abstract3 app_builtin.png        "#96ceb4" +2100+300 'Batteries included.\nA few plugins are always available.'
render abstract3 app_wiki_menu.png      "#88d8b0" +300+1800 'Order or rename plugins to your liking.'
render abstract3 app_settings.png       "#dfac5c" +1000+1800 'Integrate with clipboard to search faster.'
render abstract3 app_montage.png       "#00b159" +1700+50 'Copy text and tap the button!'


