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

public interface ChoiceProvider {

    interface Choice {
        String getValue();
        String getDescription();
        String getUri();
        SearchQuery getSearchQuery();
        boolean isRemovable();
        void remove();
        boolean isUsable();
    }

    interface Listener {
        void onSearchChoices(ImmutableList<Choice> choiceList, Choice defaultChoice);
    }

    void setCount(int count);
    void setQuery(String text);
    void setQueryFromSuggestion(Choice choice);
    void cancelAll();
}
