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

package org.deletethis.blitzspot.app.activities.search.suggest.dummy;

import org.deletethis.blitzspot.app.activities.search.suggest.ChoiceProvider;
import org.deletethis.search.parser.SearchQuery;

import java.util.function.Consumer;

class DummyChoice implements ChoiceProvider.Choice {
    private final String value;
    private final String description;
    private final String url;
    private final Consumer<DummyChoice> remove;

    public DummyChoice(String value, String description, String url, Consumer<DummyChoice> remove) {
        this.value = value;
        this.description = description;
        this.url = url;
        this.remove = remove;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getUri() {
        return url;
    }

    @Override
    public SearchQuery getSearchQuery() {
        return SearchQuery.of(value);
    }

    @Override
    public boolean isRemovable() {
        return remove != null;
    }

    @Override
    public void remove() {
        remove.accept(this);
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
