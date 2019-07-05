package org.deletethis.blitzspot.app.activities.info;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.VolleyError;

import org.deletethis.blitzspot.app.Intents;
import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.fragments.info.InfoFragment;
import org.deletethis.blitzspot.lib.volley.VolleySingleton;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class InfoActivity extends AppCompatActivity  {
    private final static String EXTRA_ACTION = InfoActivity.class.getName() + ".ACTION";
    private final static String EXTRA_MESSAGE = InfoActivity.class.getName() + ".MESSAGE";

    private byte [] engineData;
    private Button buttonStart;
    private Button buttonEnd;
    private boolean hasAction;
    private InfoFragment fragment;

    public static Intent createIntent(Context context, Uri uri, String action) {
        Intent intent = new Intent(context, InfoActivity.class);
        intent.setData(uri);
        if(action != null)
            intent.putExtra(EXTRA_ACTION, action);

        return intent;
    }

    public static Intent createIntent(Context context, byte [] source, String action) {
        Intent intent = new Intent(context, InfoActivity.class);
        Intents.putPluginExtra(intent, source);
        if(action != null)
            intent.putExtra(EXTRA_ACTION, action);
        return intent;
    }

    public static Intent createMessageIntent(Context context, String message, String action) {
        Intent intent = new Intent(context, InfoActivity.class);
        intent.putExtra(EXTRA_MESSAGE, Objects.requireNonNull(message));
        if(action != null)
            intent.putExtra(EXTRA_ACTION, action);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = (InfoFragment)fragmentManager.findFragmentById(R.id.fragment);

        buttonStart = findViewById(R.id.buttonStart);
        buttonEnd = findViewById(R.id.buttonEnd);

        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_ACTION)) {
            hasAction = true;
            String action = intent.getStringExtra(EXTRA_ACTION);
            buttonStart.setText(R.string.cancel);
            buttonStart.setOnClickListener(v -> finish());
            buttonEnd.setText(action);
            buttonEnd.setEnabled(false);
            buttonEnd.setOnClickListener(v -> {
                Intent data = new Intent();
                Intents.putPluginExtra(data, engineData);
                setResult(RESULT_OK, data);
                finish();
            });
        } else {
            hasAction = false;
            buttonStart.setVisibility(View.INVISIBLE);
            buttonEnd.setText(R.string.close);
            buttonEnd.setOnClickListener(v -> finish());
        }

        Uri uri = intent.getData();
        String message = intent.getStringExtra(EXTRA_MESSAGE);

        if(uri != null) {
            ByteRequest byteRequest = new ByteRequest(uri.toString(), this::showPlugin, this::showError);
            VolleySingleton.getInstance(this).add(byteRequest, this);
            fragment.setMessage(getString(R.string.downloading_plugin));
        } else if(message != null) {
            fragment.setMessage(message);
        } else {
            showPlugin(Intents.getPluginExtra(intent));
        }
    }

    private void showPlugin(byte [] data) {
        this.engineData = data;

        boolean success = fragment.setPluginData(data);
        if(hasAction)
            buttonEnd.setEnabled(success);
    }

    private void showError(@SuppressWarnings("unused") VolleyError unused) {
        fragment.setMessage(getString(R.string.downloading_plugin_failed));
    }

    @Override
    protected void onPause() {
        super.onPause();

        VolleySingleton.getInstance(this).cancel(this);
    }
}
