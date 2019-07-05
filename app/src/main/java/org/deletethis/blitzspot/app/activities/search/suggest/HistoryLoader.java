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

import org.deletethis.blitzspot.lib.db.QueryRunner;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.search.parser.SearchQuery;

class HistoryLoader implements LoadedList.Loader<HistoryEntry> {
    private final QueryRunner queryRunner;
    private final String pluginIdentifier;
    private long currentId = 1;

    HistoryLoader(QueryRunner queryRunner, String pluginIdentifier) {
        this.queryRunner = queryRunner;
        this.pluginIdentifier = pluginIdentifier;
    }

    @Override
    public void load(SearchQuery query, int count, LoadedList.Done<HistoryEntry> doneListener) {
        // TODO: implement individual query cancellation, emulate it with id so far
        ++currentId;
        long requestId = currentId;

        queryRunner.run(
                HistoryEntry.getHistoryOperation(pluginIdentifier, query.getAnyValue(), count),
                historyEntries -> {
                    if (requestId != currentId) {
                        Logging.SEARCH.e("Ignored database result: " + requestId + " != " + currentId);
                        return;
                    }
                    doneListener.done(historyEntries);
                    Logging.SEARCH.i("Loaded " + historyEntries.size() + " history entries from DB");
                });
    }

    @Override
    public void cancel() {
        // do nothing, query cancellation is not implemented
    }
}
