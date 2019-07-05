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