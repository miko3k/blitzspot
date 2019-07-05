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
