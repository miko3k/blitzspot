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

package org.deletethis.blitzspot.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.deletethis.blitzspot.app.activities.search.SearchActivity;
import org.deletethis.blitzspot.app.builtin.PluginFactory;
import org.deletethis.blitzspot.app.dao.DbOpenHelper;
import org.deletethis.blitzspot.app.dao.InsertHistory;
import org.deletethis.blitzspot.lib.db.QueryRunner;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.blitzspot.lib.Browser;
import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPlugin;
import org.deletethis.search.parser.SearchQuery;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LaunchSearchActivity extends AppCompatActivity {
    private static final String PARAM_NS = LaunchSearchActivity.class.getName();
    private static final String API_EXTRA_PLUGIN = PARAM_NS + ".PLUGIN";
    private static final String API_EXTRA_QUERY = PARAM_NS + ".QUERY";
    private static final String API_EXTRA_EDIT = PARAM_NS + ".EDIT";


    public static void launch(Context context, String query, byte[] pluginData, boolean edit, boolean first) {
        Intent intent = new Intent(context, LaunchSearchActivity.class);

        intent.putExtra(API_EXTRA_PLUGIN, Objects.requireNonNull(pluginData));
        intent.putExtra(API_EXTRA_QUERY, Objects.requireNonNull(query));
        intent.putExtra(API_EXTRA_EDIT, edit);
        if(first)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        QueryRunner queryRunner = new QueryRunner(DbOpenHelper.PROVIDER, this, this);

        byte[] searchPluginData = intent.getByteArrayExtra(API_EXTRA_PLUGIN);
        String query = intent.getStringExtra(API_EXTRA_QUERY);
        boolean edit = intent.getBooleanExtra(API_EXTRA_EDIT, false);
        if(searchPluginData == null || query == null)
            throw new IllegalArgumentException();

        Logging.SEARCH_SVC.i("received intent: " + intent + ", query: " + query +
                ", searchPluginData: " + searchPluginData.length + " bytes, edit: " + edit);

        if (edit) {
            Intent act = SearchActivity.createIntent(this, searchPluginData, query);
            startActivity(act);
        } else {
            try {
                SearchPlugin searchPlugin = PluginFactory.get(this).load(searchPluginData);
                String url = searchPlugin.getSearchRequest(SearchQuery.of(query)).getUrl();
                queryRunner.run(new InsertHistory(searchPlugin.getIdentifier(), query));
                Browser.openBrowser(this, url);
            } catch (PluginParseException e) {
                Logging.SEARCH_SVC.e("unable to parse plugin", e);
            }
        }
        finish();
    }

}
