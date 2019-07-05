package org.deletethis.blitzspot.app.builtin;

import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;

public class BuiltinPluginInfo {
    private final String identifier;
    private final @RawRes int source;
    private final int icon;

    public BuiltinPluginInfo(String identifier, @RawRes int source, int icon) {
        this.identifier = identifier;
        this.source = source;
        this.icon = icon;
    }

    public String getIdentifier() {
        return identifier;
    }

    @RawRes
    public int getSource() {
        return source;
    }

    @DrawableRes
    public int getIcon() {
        return icon;
    }
}
