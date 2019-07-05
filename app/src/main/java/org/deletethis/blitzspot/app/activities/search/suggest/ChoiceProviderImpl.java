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

import android.os.Handler;

import com.google.common.collect.ImmutableList;

import org.deletethis.blitzspot.lib.db.QueryRunner;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.blitzspot.lib.volley.VolleyQueue;
import org.deletethis.search.parser.SearchPlugin;
import org.deletethis.search.parser.SearchQuery;
import org.deletethis.search.parser.Suggestion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChoiceProviderImpl implements ChoiceProvider, Runnable {
    private final SearchPlugin searchPlugin;
    private final QueryRunner queryRunner;
    private String query;
    private int count;
    private final Listener listener;
    private final Handler handler;

    private final LoadedList<Suggestion> suggestions;
    private final LoadedList<HistoryEntry> history;

    public ChoiceProviderImpl(
            VolleyQueue volleyQueue,
            SearchPlugin searchPlugin,
            QueryRunner queryRunner,
            Listener listener,
            SearchQuery query,
            int count) {

        this.searchPlugin = searchPlugin;
        this.queryRunner = queryRunner;
        this.handler = new Handler();

        this.query = "";
        this.count = count;
        this.listener = listener;
        this.suggestions = new LoadedList<>(
                this::sendSuggestions,
                new SuggestionLoader(volleyQueue, searchPlugin),
                query,
                count);

        this.history = new LoadedList<>(
                this::sendSuggestions,
                new HistoryLoader(queryRunner, searchPlugin.getIdentifier()),
                query,
                count);
    }

    private ChoiceListBuilder assembleList() {
        ImmutableList<Suggestion> loadedSuggestions = suggestions.getDataForQuery(query);
        ImmutableList<HistoryEntry> loadedHistory = history.getDataForQuery(query);

        Logging.SEARCH.d("Assembling the list for query: " + query +
                ", count: " + count +
                ", suggestions: " + loadedSuggestions.size() +
                ", history: " + loadedHistory.size());

        // let's skip history which is present in suggestions or current query
        Set<String> unusableHistory = new HashSet<>();
        unusableHistory.add(query);
        for(Suggestion suggestion: loadedSuggestions) {
            unusableHistory.add(suggestion.getValue());
        }
        List<HistoryEntry> usableHistory = new ArrayList<>();
        for(HistoryEntry e: loadedHistory) {
            if(!unusableHistory.contains(e.getQuery()))
                usableHistory.add(e);
        }

        Suggestion ignore = null;
        int fromHistory = count/2;
        if(fromHistory > usableHistory.size())
            fromHistory = usableHistory.size();

        // if current query is a suggestion, add is as the first
        ChoiceListBuilder bld = new ChoiceListBuilder(count);
        for (Suggestion suggestion: loadedSuggestions) {
            if (suggestion.getValue().equals(query)) {
                bld.addAndSetDefault(new PluginChoice(suggestion, false));
                ignore = suggestion;
                break;
            }
        }
        // otherwise, add a dummy value
        if (ignore == null && !query.isEmpty()) {
            bld.addAndSetDefault(new StringChoice(query));
        }
        // fill the list with rest of suggestions
        for (Suggestion suggestion : loadedSuggestions) {
            if (bld.getCount() >= count - fromHistory) {
                break;
            }
            if (suggestion != ignore) {
                bld.add(new PluginChoice(suggestion, true));
            }
        }
        String currentPluginIdentifier = searchPlugin.getIdentifier();
        for(HistoryEntry entry: usableHistory) {
            if (bld.getCount() >= count) {
                break;
            }
            if(!currentPluginIdentifier.equals(entry.getPluginIdentifier()))
                entry = entry.clearMetadata();

            bld.add(new HistoryChoice(entry, this::deleteEntry));
        }
        return bld;
    }

    private void deleteEntry(long historyId) {
        queryRunner.run(HistoryEntry.deleteHistoryEntry(historyId), nothing -> history.reload());
    }

    public void run() {
        ChoiceListBuilder bld = assembleList();
        listener.onSearchChoices(bld.buildChoices(), bld.getDefault());
    }

    private void sendSuggestions() {

        handler.removeCallbacks(this);
        // delay sending a little bit to avoid flickering
        handler.postDelayed(this, 100);
    }

    @Override
    public void setCount(int count) {
        Logging.SEARCH.i("Choices new count: " + count);
        this.count = count;
        suggestions.setCount(count);
        history.setCount(count);
    }

    private void setQuery(SearchQuery searchQuery) {
        Logging.SEARCH.i("Choices new query: " + searchQuery);
        suggestions.setQuery(searchQuery);
        history.setQuery(searchQuery);

        if(!searchQuery.getAnyValue().equals(query)) {
            query = searchQuery.getAnyValue();
            sendSuggestions();
        }
    }

    @Override
    public void setQuery(String text) {
        setQuery(SearchQuery.of(text));
    }

    @Override
    public void setQueryFromSuggestion(Choice choice) {
        setQuery(choice.getSearchQuery());
    }

    @Override
    public void cancelAll() {
        history.cancel();
    }
}
