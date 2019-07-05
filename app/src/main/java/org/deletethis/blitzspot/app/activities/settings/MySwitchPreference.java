package org.deletethis.blitzspot.app.activities.settings;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.SwitchPreference;

/**
 * A switch preference, which does not change it's state by clicking on it.
 *
 * Everything else works the same.
 *
 */
class MySwitchPreference extends SwitchPreference {
    public MySwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MySwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MySwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySwitchPreference(Context context) {
        super(context);
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onClick() {
        if(onClickListener != null)
            onClickListener.onClick(!isChecked());
    }

    public interface OnClickListener {
        void onClick(boolean checked);
    }
}
