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

package org.deletethis.blitzspot.app.dao;

import org.deletethis.search.parser.PatchBuilder;
import org.deletethis.search.parser.SearchPlugin;

public class PluginWithId {
    private final long id;
    private final SearchPlugin engine;
    private final double ordering;

    public PluginWithId(long id, SearchPlugin engine, double ordering) {
        this.id = id;
        this.engine = engine;
        this.ordering = ordering;
    }

    public long getId() {
        return id;
    }

    public SearchPlugin getSearchPlugin() {
        return engine;
    }

    public double getOrdering() {
        return ordering;
    }

    public byte[] serialize() {
        return engine.serialize();
    }

    public PatchBuilder patch() {
        return engine.patch();
    }
}
