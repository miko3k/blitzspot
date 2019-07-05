/*
 * blitzspot
 * Copyright (C) 2018-2019 Peter Hanula
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.deletethis.blitzspot.lib;

import android.content.SharedPreferences;

import java.util.Set;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

public class SharedPreferenceLiveData<T> extends LiveData<T> {
    private interface Retriever<T> {
        T retrieve(SharedPreferences sharedPreferences, String key);
    }

    private final SharedPreferences sharedPreferences;
    private final String key;
    private final Retriever<T> retriever;
    private final SharedPreferences.OnSharedPreferenceChangeListener listener;

    private SharedPreferenceLiveData(SharedPreferences sharedPreferences, String key, Retriever<T> retriever) {
        this.sharedPreferences = sharedPreferences;
        this.key = key;
        this.retriever = retriever;
        this.listener = (p, k) -> {
            if (key.equals(k)) {
                setValue(retriever.retrieve(p, k));
            }
        };
    }

    @Nullable
    @Override
    public T getValue() {
        return super.getValue();
    }

    @Override
    protected void onActive() {
        super.onActive();
        setValue(retriever.retrieve(sharedPreferences, key));
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static SharedPreferenceLiveData<Long> ofLong(
            SharedPreferences preferences, String key, long dflt) {

        return new SharedPreferenceLiveData<>(preferences, key, (p, k) -> p.getLong(k, dflt));
    }
    public static SharedPreferenceLiveData<Boolean> ofBoolean(
            SharedPreferences preferences, String key, boolean dflt) {

        return new SharedPreferenceLiveData<>(preferences, key, (p, k) -> p.getBoolean(k, dflt));
    }
    public static SharedPreferenceLiveData<Float> ofFloat(
            SharedPreferences preferences, String key, float dflt) {

        return new SharedPreferenceLiveData<>(preferences, key, (p, k) -> p.getFloat(k, dflt));
    }
    public static SharedPreferenceLiveData<Integer> ofInteger(
            SharedPreferences preferences, String key, int dflt) {

        return new SharedPreferenceLiveData<>(preferences, key, (p, k) -> p.getInt(k, dflt));
    }
    public static SharedPreferenceLiveData<String> ofString(
            SharedPreferences preferences, String key, String dflt) {

        return new SharedPreferenceLiveData<>(preferences, key, (p, k) -> p.getString(k, dflt));
    }
    public static SharedPreferenceLiveData<Set<String>> ofStringSet(
            SharedPreferences preferences, String key, Set<String> dflt) {

        return new SharedPreferenceLiveData<>(preferences, key, (p, k) -> p.getStringSet(k, dflt));
    }
}
