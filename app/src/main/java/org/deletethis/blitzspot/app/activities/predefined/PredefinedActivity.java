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

package org.deletethis.blitzspot.app.activities.predefined;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.builtin.BuiltinItem;
import org.deletethis.blitzspot.app.builtin.PluginFactory;
import org.deletethis.blitzspot.app.dao.DbOpenHelper;
import org.deletethis.blitzspot.app.dao.InsertSearchPlugin;
import org.deletethis.blitzspot.lib.db.QueryRunner;
import org.deletethis.blitzspot.app.fragments.choose.ChooseFragment;
import org.deletethis.blitzspot.app.fragments.info.InfoFragment;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.search.parser.SearchPlugin;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

public class PredefinedActivity extends AppCompatActivity  {
    private Button startButton;
    private Button endButton;
    private ViewPager pager;
    private QueryRunner queryRunner;
    private MyAdapter adapter;
    private SearchPlugin currentPlugin;
    private PageFlipper pageFlipper;

    private ChooseFragment createChooseFragment(List<BuiltinItem> builtinItems) {
        ChooseFragment fragment = new ChooseFragment();
        fragment.setDataSet(new MyDataSet(builtinItems, this, this::itemSelected));
        return fragment;
    }

    private void updateButtons(int position) {

        if (position == 0) {
            startButton.setText(R.string.close);
            startButton.setOnClickListener(v -> finish());
        } else {
            startButton.setText(R.string.back);
            startButton.setOnClickListener(v -> pageFlipper.flip(false));
        }
        if(currentPlugin != null && adapter.getItem(position) instanceof InfoFragment) {
            endButton.setVisibility(View.VISIBLE);
            endButton.setText(R.string.add);
            endButton.setOnClickListener(v -> addAndFinish(currentPlugin));
        } else {
            endButton.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.predefined_activity);

        startButton = findViewById(R.id.startButton);
        endButton = findViewById(R.id.endButton);

        pager = findViewById(R.id.flipper);

        // who ... who knows if this is useful
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        for(Fragment f: supportFragmentManager.getFragments())
            transaction.remove(f);
        transaction.commitNowAllowingStateLoss();

        PluginFactory pluginFactory = PluginFactory.get(this);
        List<BuiltinItem> builtinItems = pluginFactory.getBuiltinItems();

        adapter = new MyAdapter(getSupportFragmentManager());
        adapter.addFragment(createChooseFragment(builtinItems));
        pageFlipper = new PageFlipper(pager, getResources().getInteger(android.R.integer.config_mediumAnimTime)*3/4);

        pager.setAdapter(adapter);
        pager.setPageTransformer(true, new FadePageTransformer());

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateButtons(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Logging.ADD.d("scroll state: " + state);

                if(state != ViewPager.SCROLL_STATE_IDLE)
                    return;

                int maxCount = pager.getCurrentItem() + 1;
                if(adapter.getCount() > maxCount) {
                    adapter.trimTo(maxCount);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        startButton.setVisibility(View.VISIBLE);
        updateButtons(0);
        queryRunner = new QueryRunner(DbOpenHelper.PROVIDER, this, this);
    }

    private void itemSelected(BuiltinItem item) {
        int current = pager.getCurrentItem();
        adapter.trimTo(current+1);

        item.getChildren().ifPresent(items -> {
            currentPlugin = null;
            adapter.addFragment(createChooseFragment(items));
        });

        item.getPlugin().ifPresent(plugin -> {
            currentPlugin = plugin;

            InfoFragment fragment = new InfoFragment();
            fragment.setPlugin(plugin);
            adapter.addFragment(fragment);
        });

        adapter.notifyDataSetChanged();
        pageFlipper.flip(true);
    }

    private void addAndFinish(SearchPlugin plugin) {
        queryRunner.runUncancellable(new InsertSearchPlugin(plugin.serialize()));
        finish();
    }
}
