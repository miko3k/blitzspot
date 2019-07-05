package org.deletethis.blitzspot.app.activities.mycroft;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;

class UriSelectionDialog extends DialogFragment {
    public interface UriSelectionCallback {
        void onUriSelected(Uri uri);
    }

    private UriSelectionCallback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (UriSelectionCallback) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if(activity == null)
            throw new IllegalStateException();

        MycroftUri[] my = MycroftUri.values();
        String [] strings = new String[my.length];
        for(int i=0;i<my.length;++i) {
            strings[i] = activity.getString(my[i].getName());
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setItems(strings, (dialog, which) -> callback.onUriSelected(my[which].getUri()));
        return builder.create();
    }
}