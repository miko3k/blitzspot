package org.deletethis.blitzspot.lib;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Browser {
    public static void openBrowser(Context context, Uri uri) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
        Logging.SYSTEM.i("Opening browser: " + uri);
        context.startActivity(browserIntent);
    }

    public static void openBrowser(Context context, String url) {
        openBrowser(context, Uri.parse(url));
    }

    public static void openPlayStore(Context context, String appPackageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }
}
