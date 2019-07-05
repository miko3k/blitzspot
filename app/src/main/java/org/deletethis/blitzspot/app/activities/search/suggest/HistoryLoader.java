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
