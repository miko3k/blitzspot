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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.common.collect.ImmutableList;

import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.blitzspot.lib.volley.VolleyQueue;
import org.deletethis.search.parser.SearchPlugin;
import org.deletethis.search.parser.SearchQuery;
import org.deletethis.search.parser.Suggestion;
import org.deletethis.search.parser.SuggestionParseException;
import org.deletethis.search.parser.SuggestionRequest;

import java.util.List;

class SuggestionLoader implements LoadedList.Loader<Suggestion> {
    private final VolleyQueue volleyQueue;
    private final SearchPlugin searchPlugin;

    SuggestionLoader(VolleyQueue volleyQueue, SearchPlugin searchPlugin) {
        this.volleyQueue = volleyQueue;
        this.searchPlugin = searchPlugin;
    }

    @Override
    public void load(SearchQuery query, int count, LoadedList.Done<Suggestion> doneListener) {
        volleyQueue.cancel(this);

        if(!searchPlugin.supportsSuggestions()) {
            doneListener.done(null);
            return;
        }

        if(query.getAnyValue().isEmpty()) {
            doneListener.done(null);
            return;
        }

        SuggestionRequest suggestions = searchPlugin.getSuggestionRequest(query);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, suggestions.getUrl(),
                response -> {
                    // Display the first 500 characters of the response string.
                    try {
                        List<Suggestion> list = suggestions.parseResult(response);
                        doneListener.done(ImmutableList.copyOf(list));
                        return;
                    } catch (SuggestionParseException ex) {
                        Logging.SEARCH.d("Failed to parse: " + response);
                        Logging.SEARCH.d("Unable to parse suggestions", ex);
                    }
                    doneListener.done(null);
                },
                error -> {
                    Logging.SEARCH.e("Unable to load suggestions", error);
                    doneListener.done(null);
                }
        );
        volleyQueue.add(stringRequest, this);
    }

    @Override
    public void cancel() {
        volleyQueue.cancel(this);
    }
}
