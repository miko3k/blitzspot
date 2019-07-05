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

package org.deletethis.blitzspot.app.activities.search.suggest;

import com.google.common.collect.ImmutableList;

import org.deletethis.search.parser.SearchQuery;

import java.util.Objects;

class LoadedList<T> {

    interface Done<T> {
        void done(ImmutableList<T> data);
    }
    interface Loader<T> {
        void load(SearchQuery query, int count, Done<T> doneListener);
        void cancel();
    }
    interface ChangeListner {
        void onChange();
    }

    private SearchQuery query;
    private int count;
    private ImmutableList<T> data;
    private final ChangeListner changeListner;
    private final Loader<T> loader;

    LoadedList(ChangeListner changeListner, Loader<T> loader, SearchQuery query, int count) {
        this.query = Objects.requireNonNull(query);
        this.count = count;
        this.data = ImmutableList.of();
        this.changeListner = changeListner;
        this.loader = loader;
    }

    private void reload(SearchQuery requestedQuery, int requestedCount) {
        SearchQuery notNullQuery = requestedQuery;
        if(notNullQuery == null)
            notNullQuery = SearchQuery.of("");

        loader.load(notNullQuery, requestedCount, loadedData -> {
            query = requestedQuery;
            count = requestedCount;

            // change from empty to empty will not trigger the listener
            if(loadedData == null || loadedData.isEmpty()) {
                if(!data.isEmpty()) {
                    data = ImmutableList.of();
                    changeListner.onChange();
                }
            } else {
                data = loadedData;
                changeListner.onChange();
            }
        });
    }

    void reload() {
        reload(query, count);
    }

    public void setQuery(SearchQuery requestedQuery) {
        Objects.requireNonNull(requestedQuery);

        if(hasQuery(requestedQuery.getAnyValue())) {
            return;
        }

        reload(requestedQuery, count);
    }

    private String getQuery() {
        return query.getAnyValue();
    }

    void setCount(int count) {
        if(count <= this.count || count <= data.size()) {
            if(this.count != count) {
                this.count = count;
                changeListner.onChange();
            }
        } else {
            reload(query, count);
        }
    }

    private boolean hasQuery(String query) {
        return Objects.equals(query, getQuery());
    }

    ImmutableList<T> getDataForQuery(String query) {
        if(hasQuery(query))
            return data;
        else
            return ImmutableList.of();
    }

    public void cancel() {
        loader.cancel();
    }
}
