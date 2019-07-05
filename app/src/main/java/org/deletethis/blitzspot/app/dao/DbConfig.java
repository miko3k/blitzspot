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

public class DbConfig {
    public static final String ENGINES = "ENGINES";
    public static final String ENGINE_ID = "ID";
    public static final String ENGINE_DATA = "DATA";
    public static final String ENGINE_ORDERING = "ORDERING";

    public static final String HISTORY = "HISTORY";
    public static final String HISTORY_ID = "ID";
    public static final String HISTORY_NORMALIZED_QUERY = "NORMALIZED_QUERY";
    public static final String HISTORY_LAST_USED = "LAST_USED";

    public static final String CHOICES = "HISTORY_SUGGESTIONS";
    public static final String CHOICE_ID = "ID";
    public static final String CHOICE_HISTORY_ID = "HISTORY_ID";
    public static final String CHOICE_ENGINE_KEY = "ENGINE_KEY";
    public static final String CHOICE_LAST_USED = "LAST_USED";
    public static final String CHOICE_QUERY = "QUERY";
    public static final String CHOICE_DESCRIPTION = "DESCRIPTION";
    public static final String CHOICE_URL = "URL";

    public static final String FILE = "stuff.sqlite";
    public static final int VERSION = 1;
}
