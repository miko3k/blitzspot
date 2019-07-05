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

package org.deletethis.blitzspot.app.activities.mycroft;

import android.net.Uri;

import org.deletethis.blitzspot.app.R;

import java.util.Objects;

public enum MycroftUri {
    HOME("https://mycroftproject.com", R.string.home),
    SEARCH("https://mycroftproject.com/search-engines.html", R.string.search),
    TOP_100("https://mycroftproject.com/dlstats.html", R.string.top_100),
    SUPPORT("https://mycroftproject.com/support-mycroft.html", R.string.support_mycroft);

    private final Uri uri;
    private final int name;

    private final static int GENERIC_NAME = R.string.mycroft_project;

    MycroftUri(String uri, int name) {
        this.name = name;
        this.uri = Uri.parse(uri);
    }

    public Uri getUri() {
        return uri;
    }

    public int getName() {
        return name;
    }

    private static String normalizePath(String path) {
        if(path == null)
            return null;
        while(path.startsWith("/"))
            path = path.substring(1);
        if(path.isEmpty())
            return null;
        return path;
    }

    private static MycroftUri find(Uri req) {
        for(MycroftUri current: MycroftUri.values()) {
            Uri c = current.getUri();

            if(!Objects.equals(c.getHost(), req.getHost())) continue;
            if(!Objects.equals(normalizePath(c.getPath()), normalizePath(req.getPath()))) continue;

            return current;
        }
        return null;
    }

    public static int findName(Uri uri) {
        MycroftUri mycroftUri = find(uri);
        return mycroftUri == null ? GENERIC_NAME : mycroftUri.getName();
    }
}
