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

package org.deletethis.blitzspot.app.activities.jump;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.activities.LaunchSearchActivity;
import org.deletethis.blitzspot.app.dao.DbOpenHelper;
import org.deletethis.blitzspot.app.dao.SelectSearchPlugins;
import org.deletethis.blitzspot.lib.db.QueryRunner;
import org.deletethis.blitzspot.app.fragments.choose.ChooseFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class JumpActivity extends AppCompatActivity {
    public static final String QUERY = JumpActivity.class.getName() + ".QUERY";

    private ChooseFragment fragment;
    private String query;
    private CheckBox editableCheckbox;
    private TextView queryTextView;

    private String getClipboardText() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData cd = clipboardManager.getPrimaryClip();
        if(cd == null)
            return null;

        if(cd.getItemCount() == 0)
            return null;

        ClipData.Item itemAt = cd.getItemAt(0);
        return itemAt.coerceToText(this).toString();
    }

    // https://medium.com/@fergaral/working-with-clipboard-data-on-android-10-f641bc4b6a31
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            query = getClipboardText();
            if(query == null) {
                query = "";
            }
            query = query.trim();
            queryTextView.setText(query);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jump_activity);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = (ChooseFragment)fragmentManager.findFragmentById(R.id.fragment);
        editableCheckbox = findViewById(R.id.editable_select);
        queryTextView = findViewById(R.id.jump_query);

        findViewById(R.id.cancel).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        query = getIntent().getStringExtra(QUERY);
        if(query == null || query.isEmpty()) {
            // I think we always take this path, noone uses query anymore
        }

        // we leave the checkbox initially visible, it's much more common case than
        // invisible, and it looks bad, when it appears later. We modify visibility,
        // not the enabled state, because the button has color overrides, which
        // do not respond well to changing of enabled state. We could also write proper
        // selectors but we are lazy.
        QueryRunner queryRunner = new QueryRunner(DbOpenHelper.PROVIDER, this, this);
        queryRunner.run(SelectSearchPlugins.get(this), items -> {
            if(items.isEmpty()) {
                fragment.setMessage(getString(R.string.no_plugins_message));
                editableCheckbox.setVisibility(View.INVISIBLE);
            } else {
                fragment.setDataSet(new MyDataSet(items, this::onItemClicked));
                editableCheckbox.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onItemClicked(byte [] plugin) {

        boolean edit = editableCheckbox.isChecked();
        LaunchSearchActivity.launch(this, query, plugin, edit, false);
        finish();
    }
}
