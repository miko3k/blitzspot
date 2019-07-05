package org.deletethis.blitzspot.app.builtin.list;

import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;

public interface BuiltinRegistry {
    void plugin(String identifier, @DrawableRes int icon, @RawRes int source);
    void group(@StringRes int title, @DrawableRes int icon, GroupItem ... items);
    void onStart(String identifier);
}
