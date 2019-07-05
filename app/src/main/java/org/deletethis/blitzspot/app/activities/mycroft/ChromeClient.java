package org.deletethis.blitzspot.app.activities.mycroft;

import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

class ChromeClient extends WebChromeClient {
    public interface AlertCallback {
        void onAlert(String message, JsResult jsResult);
    }

    public static class Params {
        private View progress;
        private AlertCallback alert;

        public void setProgress(View progress) {
            this.progress = progress;
        }

        public void setAlert(AlertCallback alert) {
            this.alert = alert;
        }
    }

    private final Params params;

    public ChromeClient(Params params) {
        this.params = params;
    }

    public void onProgressChanged(WebView view, int progress) {
        View v = params.progress;
        if(v == null)
            return;

        if(progress < 100 && v.getVisibility() == ProgressBar.GONE){
            v.setVisibility(ProgressBar.VISIBLE);
        }

        v.setScaleX(progress / 100f);
        if(progress == 100) {
            v.setVisibility(ProgressBar.GONE);
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        if(params.alert != null) {
            params.alert.onAlert(message, result);
            return true;
        } else {
            return false;
        }
    }
}
