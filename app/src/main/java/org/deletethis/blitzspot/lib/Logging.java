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

package org.deletethis.blitzspot.lib;

public class Logging {
    public final static LogWrapper SEARCH = new LogWrapper("SEARCH");
    public final static LogWrapper MYCROFT = new LogWrapper("MYCROFT");
    public final static LogWrapper ADD = new LogWrapper("ADD");
    public final static LogWrapper MAIN = new LogWrapper("MAIN");
    public final static LogWrapper SYSTEM = new LogWrapper("SYSTEM");
    public final static LogWrapper DB = new LogWrapper("DB");
    public final static LogWrapper INFO = new LogWrapper("INFO");
    public final static LogWrapper SEARCH_SVC = new LogWrapper("SEARCH_SVC");
    public final static LogWrapper SETTINGS = new LogWrapper("SETTINGS");
    public final static LogWrapper SERVICE = new LogWrapper("SERVICE");
    public final static LogWrapper BUTTON = new LogWrapper("BUTTON");
    public final static LogWrapper ICON = new LogWrapper("ICON");
}
