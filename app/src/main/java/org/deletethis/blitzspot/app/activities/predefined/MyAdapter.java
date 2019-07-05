package org.deletethis.blitzspot.app.activities.predefined;

import org.deletethis.blitzspot.lib.Logging;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

class MyAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> fragments = new ArrayList<>();

    public MyAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    public void addFragment(Fragment f) {
        fragments.add(f);
        Logging.ADD.d("size after add: " + fragments.size());
    }

    public void trimTo(int size) {
        fragments.subList(size, fragments.size()).clear();
        Logging.ADD.d("size after trim: " + fragments.size());
    }

    @SuppressWarnings("RedundantCast")
    @Override
    public int getItemPosition(@NonNull Object object) {
        if(fragments.contains((Fragment)object)) {
            return POSITION_UNCHANGED;
        } else {
            return POSITION_NONE;
        }
    }
}
