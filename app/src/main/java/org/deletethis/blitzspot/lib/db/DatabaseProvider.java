package org.deletethis.blitzspot.lib.db;

import android.content.Context;

public interface DatabaseProvider {
    /** This method must be fast, and should not block. Operations on database might be invoked
     * from a different thread */
    Database open(Context context);
}
