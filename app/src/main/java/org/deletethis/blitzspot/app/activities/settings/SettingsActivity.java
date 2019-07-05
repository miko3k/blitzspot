package org.deletethis.blitzspot.app.activities.settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.lib.Browser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

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

        TextView hint = findViewById(R.id.hint);
        View hintContainer = findViewById(R.id.hintContainer);
        InstantViewModel model = ViewModelProviders.of(this).get(InstantViewModel.class);
        model.getHint().observe(this, hint::setText);
        model.getHintVisible().observe(this, visible -> {
            int time = getResources().getInteger(android.R.integer.config_shortAnimTime);
            if(visible) {
                hintContainer.setAlpha(0);
                hintContainer.setScaleX(0);
                hintContainer.setScaleY(0);

                hintContainer.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(time)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                hintContainer.setAlpha(1);
                                hintContainer.setScaleX(1);
                                hintContainer.setScaleY(1);
                            }
                        });
            } else {
                hintContainer.setAlpha(1);
                hintContainer.setScaleX(1);
                hintContainer.setScaleY(1);
                hintContainer.animate()
                        .alpha(0f)
                        .scaleX(0f)
                        .scaleY(0f)
                        .setDuration(time)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                hintContainer.setAlpha(1);
                                hintContainer.setScaleX(1);
                                hintContainer.setScaleY(1);
                                hintContainer.setVisibility(View.INVISIBLE);
                            }
                        });
            }
            hintContainer.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.view_transition_fade_in_long,R.anim.view_transition_fade_out_long);
    }
}
