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

package org.deletethis.blitzspot.app.activities.mycroft;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import org.deletethis.blitzspot.app.Intents;
import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.activities.info.InfoActivity;
import org.deletethis.blitzspot.app.dao.DbOpenHelper;
import org.deletethis.blitzspot.app.dao.InsertSearchPlugin;
import org.deletethis.blitzspot.lib.db.QueryRunner;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.blitzspot.lib.Browser;

public class MycroftActivity extends AppCompatActivity implements UriSelectionDialog.UriSelectionCallback {
    // could not come up with a decent name, this one comes from random.org
    private static final String JS_NAME = "ehpyxunknwssmdcjjvqvbwudgypaxwoehdasujkbi";
    private static final int REQUEST_CODE_ADD = 4;

    private WebView webView;
    private Handler handler;
    private TextView title;
    private TextView url;
    private WebClient webClient;
    private SharedPreferences preferences;
    private QueryRunner queryRunner;


    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.handler = new Handler();

        setContentView(R.layout.mycroft_activity);

        View toolbar = findViewById(R.id.toolbar);

        url = findViewById(R.id.url);
        title = findViewById(R.id.title);

        WebClient.Params clientParams = new WebClient.Params();
        clientParams.setUrlConsumer(this::onUriVisited);
        clientParams.setForeginUrlConsumer(this::openForeignUrl);

        ChromeClient.Params chromeParams = new ChromeClient.Params();
        chromeParams.setProgress(findViewById(R.id.progress));
        chromeParams.setAlert(this::alert);

        webClient = new WebClient(this, clientParams);
        preferences = getPreferences(MODE_PRIVATE);

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(webClient);
        webView.setOnTouchListener(new HideAddressBar(handler, toolbar, webView, getResources().getDimension(R.dimen.mycroftToolbarHideSpeed)));
        webView.setWebChromeClient(new ChromeClient(chromeParams));
        webView.addJavascriptInterface(new WebViewCallback(handler, this::addPlugin), JS_NAME);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowContentAccess(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadWithOverviewMode(true);

        Intent intent = getIntent();
        Uri data = intent.getData();
        if(data == null) {
            data = MycroftUri.HOME.getUri();
        }
        webView.loadUrl(data.toString());
        findViewById(R.id.back).setOnClickListener(v -> finish());
        findViewById(R.id.refresh).setOnClickListener(v -> webView.reload());
        title.setOnClickListener(this::showAddressDropDown);
        url.setOnClickListener(v -> openForeignUrl(Uri.parse(url.getText().toString())));
        queryRunner = new QueryRunner(DbOpenHelper.PROVIDER, this, this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.view_transition_fade_out);

    }

    private void showAddressDropDown(View v) {
        DialogFragment newFragment = new UriSelectionDialog();
        newFragment.show(getSupportFragmentManager(), "wtf belongs here");
    }

    private void onUriVisited(String uri) {
        url.setText(uri);
        title.setText(MycroftUri.findName(Uri.parse(uri)));
    }

    @Override
    public void onUriSelected(Uri uri) {
        webView.loadUrl(uri.toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void openForeignUrl(Uri uri) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content),
                R.string.link_in_browser,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.open, v -> Browser.openBrowser(this, uri));
        snackbar.show();
    }

    private void alert(String message, JsResult result) {
        // dismiss right away, let's not block the browser
        result.cancel();
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), message,
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void addPlugin(String url) {
        Intent intent = InfoActivity.createIntent(this, Uri.parse(url), getString(R.string.add));
        startActivityForResult(intent, REQUEST_CODE_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK && data != null) {
            byte[] engineData = Intents.getPluginExtra(data);

            queryRunner.run(new InsertSearchPlugin(engineData),
                    unused -> Logging.MYCROFT.i("search engine actually added"));

            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content),
                    R.string.search_engine_added,
                    Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        handler.removeCallbacksAndMessages(null);
    }
}
