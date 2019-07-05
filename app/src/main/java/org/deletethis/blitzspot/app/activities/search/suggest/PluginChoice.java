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
import org.deletethis.search.parser.Suggestion;

class PluginChoice implements ChoiceProvider.Choice {
    private final Suggestion suggestion;
    private final boolean usable;

    public PluginChoice(Suggestion suggestion, boolean usable) {
        this.suggestion = suggestion;
        this.usable = usable;
    }

    @Override
    public String getValue() {
        return suggestion.getValue();
    }

    @Override
    public String getDescription() {
        return suggestion.getDescription().orElse(null);
    }

    @Override
    public String getUri() {
        return suggestion.getUrl().orElse(null);
    }

    @Override
    public boolean isRemovable() {
        return false;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SearchQuery getSearchQuery() {
        return SearchQuery.of(suggestion);
    }

    @Override
    public boolean isUsable() {
        return usable;
    }
}
