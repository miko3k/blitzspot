package org.deletethis.blitzspot.app.activities.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.lib.Logging;

public class SearchEditText extends androidx.appcompat.widget.AppCompatEditText  {
    public SearchEditText(Context context) {
        this(context, null);
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.searchEditTextStyle);
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Runnable onBack;

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        final boolean consume = super.onKeyPreIme(keyCode, event);
        Logging.SEARCH.i("onPreIme, code: " + keyCode + ", event: " + event + ", consume: " + consume);
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if(onBack != null)
                onBack.run();
        }
        return consume;
    }
}
