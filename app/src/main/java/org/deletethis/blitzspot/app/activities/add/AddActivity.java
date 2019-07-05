package org.deletethis.blitzspot.app.activities.add;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.activities.mycroft.MycroftActivity;
import org.deletethis.blitzspot.app.activities.mycroft.MycroftUri;
import org.deletethis.blitzspot.app.activities.predefined.PredefinedActivity;

public class AddActivity extends AppCompatActivity  {
    public final static Uri LOCAL_FILE = Uri.parse("https://local.file.invalid");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // we call finish every time, coz noHistory does not seem to apply
        // when starting a transcluent activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        // probably cannot be done via XML
        getWindow().setGravity(Gravity.END|Gravity.BOTTOM);

        TextView predefined = findViewById(R.id.predefined);
        predefined.setOnClickListener(v -> {
            Intent intent = new Intent(this, PredefinedActivity.class);
            startActivity(intent);
            finish();
        });

        TextView mycroft = findViewById(R.id.mycroft);
        mycroft.setOnClickListener(v -> {
            // we are using noHistory, this seems to animate against parent activity, and looks better
            // when the animation is a bit slower
            Intent intent = new Intent(this, MycroftActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.view_transition_fade_in_long,R.anim.view_transition_fade_out_long);
    });
        TextView mycroft_top100 = findViewById(R.id.mycroft_top100);
        mycroft_top100.setOnClickListener(v -> {
            // longer time, same as for the other mycroft
            Intent intent = new Intent(this, MycroftActivity.class);
            intent.setData(MycroftUri.TOP_100.getUri());
            startActivity(intent);
            overridePendingTransition(R.anim.view_transition_fade_in_long,R.anim.view_transition_fade_out_long);
        });
        TextView xmlFile = findViewById(R.id.xml_file);
        xmlFile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setData(LOCAL_FILE);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

        TextView close = findViewById(R.id.close);
        close.setOnClickListener(v -> finish());
    }

}
