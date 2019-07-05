package org.deletethis.blitzspot.app.builtin.list;

import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;

public class GroupItem {
    private final @StringRes int title;
    private final @DrawableRes int titleIcon;
    private final String identifier;
    private final @RawRes int source;

    GroupItem(@StringRes int title, @DrawableRes int titleIcon, String identifier, int source) {
        this.title = title;
        this.titleIcon = titleIcon;
        this.identifier = identifier;
        this.source = source;
    }

    public @StringRes int getTitle() {
        return title;
    }

    public @DrawableRes int getTitleIcon() {
        return titleIcon;
    }

    public String getIdentifier() {
        return identifier;
    }

    public @RawRes int getSource() {
        return source;
    }
}