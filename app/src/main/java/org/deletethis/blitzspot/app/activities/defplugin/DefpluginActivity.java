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

package org.deletethis.blitzspot.app.activities.defplugin;

import android.content.Intent;
import android.os.Bundle;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.dao.DbOpenHelper;
import org.deletethis.blitzspot.app.dao.SelectSearchPlugins;
import org.deletethis.blitzspot.lib.db.QueryRunner;
import org.deletethis.blitzspot.app.fragments.choose.ChooseFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class DefpluginActivity extends AppCompatActivity {
    // OUT
    public static final String PLUGIN = DefpluginActivity.class.getName() + ".PLUGIN";

    private ChooseFragment fragment;
    private QueryRunner queryRunner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defplugin_activity);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = (ChooseFragment)fragmentManager.findFragmentById(R.id.fragment);

        findViewById(R.id.cancel).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        queryRunner = new QueryRunner(DbOpenHelper.PROVIDER, this, this);
        queryRunner.run(SelectSearchPlugins.get(this), items ->
                    fragment.setDataSet(new MyDataSet(this, items, this::onItemClicked))
        );
    }

    private void onItemClicked(byte [] plugin) {
        Intent data = new Intent();
        if(plugin != null) {
            data.putExtra(PLUGIN, plugin);
        }

        setResult(RESULT_OK, data);
        finish();
    }
}
