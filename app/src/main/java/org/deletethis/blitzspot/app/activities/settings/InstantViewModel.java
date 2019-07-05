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

class InstantViewModel extends AndroidViewModel {
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