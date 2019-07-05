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

import org.deletethis.search.parser.SearchQuery;

import java.util.function.LongConsumer;

class HistoryChoice implements ChoiceProvider.Choice {
    private final HistoryEntry historyEntry;
    private final LongConsumer delete;

    public HistoryChoice(HistoryEntry historyEntry, LongConsumer delete) {
        this.historyEntry = historyEntry;
        this.delete = delete;
    }

    @Override
    public String getValue() {
        return historyEntry.getQuery();
    }

    @Override
    public String getDescription() {
        return historyEntry.getDescription().orElse(null);
    }

    @Override
    public String getUri() {
        return historyEntry.getUrl().orElse(null);
    }

    @Override
    public boolean isRemovable() {
        return true;
    }

    @Override
    public void remove() {
        delete.accept(historyEntry.getHistoryId());
    }

    @Override
    public SearchQuery getSearchQuery() {
        return SearchQuery.of(historyEntry.getQuery());
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
