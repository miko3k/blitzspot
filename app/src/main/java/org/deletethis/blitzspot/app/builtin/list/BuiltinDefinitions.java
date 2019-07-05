package org.deletethis.blitzspot.app.builtin.list;

import org.deletethis.blitzspot.app.R;

public class BuiltinDefinitions {
    private static GroupItem g(Variant variant, String identifier, int source) {
        return new GroupItem(variant.getTitle(), variant.getTitleIcon(), identifier, source);
    }

    public static void initialize(BuiltinRegistry reg) {
        final Variant en = new Variant(R.string.english, R.drawable.flag_uk);
        final Variant de = new Variant(R.string.german, R.drawable.flag_de);
        final Variant ru = new Variant(R.string.russian, R.drawable.flag_ru);
        final Variant es = new Variant(R.string.spanish, R.drawable.flag_es);

        final Variant toEn = new Variant(R.string.to_english, R.drawable.flag_uk);
        final Variant toDe = new Variant(R.string.to_german, R.drawable.flag_de);
        final Variant toRu = new Variant(R.string.to_russian, R.drawable.flag_ru);
        final Variant toEs = new Variant(R.string.to_spanish, R.drawable.flag_es);

        final Variant cUk = new Variant(R.string.united_kingdom, R.drawable.flag_uk);
        final Variant cDe = new Variant(R.string.germany, R.drawable.flag_de);
        final Variant cAt = new Variant(R.string.austria, R.drawable.flag_at);
        final Variant cRu = new Variant(R.string.russia, R.drawable.flag_ru);
        final Variant cEs = new Variant(R.string.spain, R.drawable.flag_es);

        reg.group(R.string.wikipedia, R.drawable.plugin_wikipedia,
                g(en, "en.wikipedia.org", R.raw.plugin_wikipedia_en),
                g(de, "de.wikipedia.org", R.raw.plugin_wikipedia_de),
                g(es, "es.wikipedia.org", R.raw.plugin_wikipedia_es),
                g(ru, "ru.wikipedia.org", R.raw.plugin_wikipedia_ru));

        reg.group(R.string.wiktionary, R.drawable.plugin_wiktionary,
                g(en, "en.wiktionary.org", R.raw.plugin_wiktionary_en),
                g(de, "de.wiktionary.org", R.raw.plugin_wiktionary_de),
                g(es, "es.wiktionary.org", R.raw.plugin_wiktionary_es),
                g(ru, "ru.wiktionary.org", R.raw.plugin_wiktionary_ru));

        reg.plugin("google.com", R.drawable.plugin_google, R.raw.plugin_google);

        reg.group(R.string.google_translate, R.drawable.plugin_google_translate,
                g(toEn,"translate.google.com/en",  R.raw.plugin_google_translate_en),
                g(toDe, "translate.google.com/de", R.raw.plugin_google_translate_de),
                g(toEs,"translate.google.com/es",  R.raw.plugin_google_translate_es),
                g(toRu, "translate.google.com/ru", R.raw.plugin_google_translate_ru));

        reg.plugin("maps.google.com", R.drawable.plugin_google_maps, R.raw.plugin_google_maps);

        reg.plugin("youtube.com", R.drawable.plugin_youtube, R.raw.plugin_youtube);
        reg.plugin("duckduckgo.com", R.drawable.plugin_duckduckgo, R.raw.plugin_duckduckgo);
        reg.plugin("bing.com", R.drawable.plugin_bing, R.raw.plugin_bing);
        reg.plugin("imdb.com", R.drawable.plugin_imdb, R.raw.plugin_imdb);
        reg.plugin("duden.de", R.drawable.plugin_duden2, R.raw.plugin_duden);

        reg.onStart("google.com");
        reg.onStart("maps.google.com");
        reg.onStart("translate.google.com/en");
        reg.onStart("en.wikipedia.org");
        reg.onStart("en.wiktionary.org");
        reg.onStart("duckduckgo.com");
        reg.onStart("imdb.com");
    }
}
