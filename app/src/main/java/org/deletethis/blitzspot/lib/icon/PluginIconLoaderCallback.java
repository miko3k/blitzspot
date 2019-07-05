package org.deletethis.blitzspot.lib.icon;

interface PluginIconLoaderCallback {
    void iconRetrieved(String url, ScaledBitmap scaledBitmap);
    void iconRetrievalFailed(String url, Throwable error, String debug);
}
