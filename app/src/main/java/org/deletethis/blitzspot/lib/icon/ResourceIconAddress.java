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

package org.deletethis.blitzspot.lib.icon;

import org.deletethis.search.parser.IconAddress;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

final class ResourceIconAddress implements IconAddress {
    @DrawableRes
    private final int resource;
    private final List<String> addresses;

    ResourceIconAddress(@DrawableRes int resource) {
        this.resource = resource;
        this.addresses = Collections.singletonList(toString());
    }

    @NonNull
    @Override
    public String toString() {
        return "RES:" + resource;
    }

    @NonNull
    @Override
    public Iterator<String> iterator() {
        return addresses.iterator();
    }

    @DrawableRes
    int getResource() {
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceIconAddress)) return false;
        ResourceIconAddress other = (ResourceIconAddress) o;
        return resource == other.resource;
    }

    @Override
    public int hashCode() {
        return resource;
    }
}
