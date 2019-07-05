package org.deletethis.blitzspot.lib.db.operations;

import android.database.Cursor;

import org.deletethis.blitzspot.lib.db.DatabaseOperation;

@SuppressWarnings("unused")
class DatabaseOperations {
    private DatabaseOperations() { }

    /**
     * @param iterations should be about 10000000, but is heavily CPU dependent
     * @return a database operation which is quite slow
     */
    public static DatabaseOperation<Void> slow(long iterations) {
        return (db, cancel) -> {
            try(Cursor cursor = db.rawQuery("WITH RECURSIVE r(i) AS (" +
                            "  VALUES(0)" +
                            "  UNION ALL" +
                            "  SELECT i FROM r" +
                            "  LIMIT " + iterations +
                            ")" +
                            "SELECT i FROM r WHERE i = 1",
                    null,
                    cancel)) {

                //noinspection StatementWithEmptyBody
                while(cursor.moveToNext()) {

                }
            }
            return null;
        };
    }
}
