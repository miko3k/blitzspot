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

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.lib.Browser;

public class SettingsActivity extends AppCompatActivity  {

    private void doHelp(TextView tv) {
        int accent = ContextCompat.getColor(this, R.color.colorAccent);
        String s = tv.getText().toString();
        s = s.replace("ACCENT", String.format("#%06X", (0xFFFFFF & accent)));
        tv.setText(Html.fromHtml(s, Html.FROM_HTML_MODE_COMPACT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        View close = findViewById(R.id.settings_close_button);

        Button rateMe = findViewById(R.id.settings_rate_me_button);
        rateMe.setOnClickListener(v -> Browser.openPlayStore(this, getApplicationContext().getPackageName()));

        close.setOnClickListener(v -> finish());

        doHelp(findViewById(R.id.help1));
        doHelp(findViewById(R.id.help2));
        doHelp(findViewById(R.id.help3));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.view_transition_fade_in_long,R.anim.view_transition_fade_out_long);
    }
}
