package org.deletethis.blitzspot.app.builtin.list;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

class Variant {
    private final @StringRes
    int title;
    private final @DrawableRes
    int titleIcon;

    Variant(@StringRes int title, @DrawableRes int titleIcon) {
        this.title = title;
        this.titleIcon = titleIcon;
    }

    @StringRes int getTitle() {
        return title;
    }

    @DrawableRes int getTitleIcon() {
        return titleIcon;
    }
}