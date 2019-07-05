package org.deletethis.blitzspot.app;

import android.content.Intent;

public class Intents {
    private static final String EXTRA_PLUGIN = Intents.class.getName() + ".PLUGIN";

    public static void putPluginExtra(Intent intent, byte [] source) {
        intent.putExtra(EXTRA_PLUGIN, source);
    }
    public static byte[] getPluginExtra(Intent intent) {
        return intent.getByteArrayExtra(EXTRA_PLUGIN);
    }
}
