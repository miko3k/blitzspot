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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ResultSetHandler<T> {
    static <T> ResultSetHandler<Optional<T>> optional(RowHandler<T> item) {
        return cursor -> {
            if(!cursor.moveToNext()) {
                return Optional.empty();
            }
            T result = item.handle(cursor);

            if(cursor.moveToNext()) {
                throw new IllegalStateException("more then one row returned");
            }
            return Optional.of(result);
        };
    }

    static <T> ResultSetHandler<T> one(RowHandler<T> item) {
        return cursor -> {
            if(!cursor.moveToNext()) {
                throw new IllegalStateException("no rows returned");
            }
            T result = item.handle(cursor);

            if(cursor.moveToNext()) {
                throw new IllegalStateException("more then one row returned");
            }
            return result;
        };
    }

    static <T> ResultSetHandler<List<T>> list(RowHandler<T> item) {
        return cursor -> {
            ArrayList<T> result = new ArrayList<>();
            while(cursor.moveToNext()) {
                T obj = item.handle(cursor);
                if(obj != null) {
                    result.add(obj);
                }
            }
            return result;
        };
    }

    T handle(Cursor cursor);
}
