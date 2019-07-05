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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import org.deletethis.blitzspot.app.InstantState;
import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.activities.defplugin.DefpluginActivity;
import org.deletethis.blitzspot.app.builtin.PluginFactory;
import org.deletethis.blitzspot.app.dao.DbConfig;
import org.deletethis.blitzspot.app.dao.DbOpenHelper;
import org.deletethis.blitzspot.lib.db.QueryRunner;
import org.deletethis.blitzspot.lib.db.operations.RowHandler;
import org.deletethis.blitzspot.lib.db.operations.Selector;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

class SettingsFragment extends PreferenceFragmentCompat {
    private QueryRunner queryRunner;
    private Preference clearHistoryPreference;
    private MySwitchPreference activePreference;
    private Preference defaultPreference;
    private InstantState applicationState;

    private static final int REQ_PERMISSION_CODE = 1;
    private static final int REQ_PLUGIN_CODE = 2;

    private static void findPreferences(PreferenceScreen preferenceScreen, Map<String, Consumer<Preference>> map) {
        int count = preferenceScreen.getPreferenceCount();
        Set<String> found = new HashSet<>();
        for(int i=0; i<count; ++i) {
            Preference pref = preferenceScreen.getPreference(i);
            pref.setIconSpaceReserved(false);

            String key = pref.getKey();
            if(key == null) {
                continue;
            }
            Consumer<Preference> preferenceConsumer = map.get(key);
            if(preferenceConsumer != null) {
                if(!found.add(key)) {
                    throw new IllegalStateException("duplicate key: " + key);
                }
                preferenceConsumer.accept(pref);
            }
        }
        if(!map.keySet().containsAll(found)) {
            Collection<String> all = map.keySet();
            all.removeAll(found);
            throw new IllegalStateException("missing: " + all);
        }
    }

    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        Context context = getContext();
        if(context == null)
            throw new IllegalStateException();

        this.applicationState = InstantState.get(getContext());

        Map<String, Consumer<Preference>> map = new HashMap<>();
        map.put(getString(R.string.pref_clear_history), (p)->clearHistoryPreference = p);
        map.put(getString(R.string.pref_instant_active), (p) -> activePreference = (MySwitchPreference) p);
        map.put(getString(R.string.pref_instant_plugin), (p) -> defaultPreference = p);

        findPreferences(preferenceScreen, map);

        clearHistoryPreference.setOnPreferenceClickListener(preference -> {
            new AlertDialog.Builder(context)
                    .setMessage(R.string.history_clear_confirm)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> clearHistory())
                    .setNegativeButton(android.R.string.no, null).show();

            return true;
        });

        activePreference.setOnClickListener(this::onActiveChanged);
        applicationState.getRunning().observe(this, activePreference::setChecked);
        applicationState.getDefaultPlugin().observe(this, bytes -> {
            if(bytes == null) {
                defaultPreference.setSummary(R.string.always_ask);
            } else {
                try {
                    SearchPlugin plugin = PluginFactory.get(getContext()).load(bytes);
                    defaultPreference.setSummary(plugin.getName());
                } catch (PluginParseException e) {
                    throw new IllegalStateException();
                }

            }
        });

        defaultPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getContext(), DefpluginActivity.class);
            startActivityForResult(intent, REQ_PLUGIN_CODE);
            return true;
        });
        InstantViewModel model = ViewModelProviders.of(this).get(InstantViewModel.class);
        model.getDefaultEnabled().observe(this, defaultPreference::setEnabled);

        super.setPreferenceScreen(preferenceScreen);
    }

    private void clearHistory() {
        queryRunner.run((database, cancel) -> {
            database.delete(DbConfig.CHOICES, "", new String[]{});
            database.delete(DbConfig.HISTORY, "", new String[]{});
            // return if the preference should be enabled.. always false
            return false;
        }, clearHistoryPreference::setEnabled);
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryRunner = new QueryRunner(DbOpenHelper.PROVIDER, getContext(), getViewLifecycleOwner());

        queryRunner.run(
                Selector.stringQuery("select count(*) from history").one(RowHandler.LONG),
                count -> clearHistoryPreference.setEnabled(count > 0));
    }


    private void onActiveChanged(boolean checked) {
        Logging.SETTINGS.i("active changed to: " + checked);
        Context context = Objects.requireNonNull(getContext(), "null context");

        if (!checked) {
            applicationState.stop(context);
        } else {
            if (Settings.canDrawOverlays(context)) {
                applicationState.start(context);
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                startActivityForResult(intent, REQ_PERMISSION_CODE);
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Context context = getContext();
        if (requestCode == REQ_PERMISSION_CODE) {
            if(Settings.canDrawOverlays(getContext())) {
                applicationState.start(context);
            }
        } else if(requestCode == REQ_PLUGIN_CODE && resultCode == Activity.RESULT_OK) {
            byte[] byteArrayExtra = data.getByteArrayExtra(DefpluginActivity.PLUGIN);
            SearchPlugin plugin = null;
            if(byteArrayExtra != null) {
                try {
                    plugin = PluginFactory.get(getContext()).load(byteArrayExtra);
                } catch (PluginParseException e) {
                    throw new IllegalStateException(e);
                }
            }
            applicationState.setDefaultPlugin(plugin);
            Logging.SETTINGS.i("received default plugin: " + data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}