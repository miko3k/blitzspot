package org.deletethis.blitzspot.app.activities.predefined;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class ForgetfulViewPager extends ViewPager {
    public ForgetfulViewPager(@NonNull Context context) {
        super(context);
    }

    public ForgetfulViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(null);
    }
}
