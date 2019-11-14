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

package org.deletethis.blitzspot.app.activities.settings;

import android.app.Application;
import android.content.Context;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.InstantState;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

public class InstantViewModel extends AndroidViewModel {
    private final Context context;
    private final InstantState applicationState;
    private final MediatorLiveData<String> hint;
    private final MutableLiveData<Boolean> hintVisible;

    public InstantViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        applicationState = InstantState.get(application);

        hint = new MediatorLiveData<>();
        hint.addSource(applicationState.getRunning(), this::updateHint);
        hint.addSource(applicationState.getDefaultPlugin(), this::updateHint);

        hintVisible = new MutableLiveData<>();
    }

    private void updateHint(Object trash) {
        Boolean running = applicationState.getRunning().getValue();
        byte[] searchPlugin = applicationState.getDefaultPlugin().getValue();

        String hint;
        if(running != null && running && searchPlugin != null) {
            hint = context.getString(R.string.default_plugin_advice);
        } else {
            hint = null;
        }

        if(hint != null) {
            if(hintVisible.getValue() != Boolean.TRUE) {
                hintVisible.setValue(true);
            }
        } else {
            if (hintVisible.getValue() != Boolean.FALSE) {
                hintVisible.setValue(false);
            }
        }
        this.hint.setValue(hint);
    }

    public LiveData<Boolean> getDefaultEnabled() {
        return applicationState.getRunning();
    }

    public LiveData<String> getHint() {
        return hint;
    }

    public LiveData<Boolean> getHintVisible() {
        return hintVisible;
    }
}