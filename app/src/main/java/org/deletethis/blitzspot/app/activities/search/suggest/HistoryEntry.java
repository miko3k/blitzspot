package org.deletethis.blitzspot.app.activities.search.suggest;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import org.deletethis.blitzspot.app.dao.DbConfig;
import org.deletethis.blitzspot.lib.db.DatabaseOperation;
import org.deletethis.blitzspot.lib.db.operations.RowHandler;
import org.deletethis.blitzspot.lib.db.operations.Selector;
import org.deletethis.blitzspot.lib.DatabaseUtilities;

import java.util.List;
import java.util.Optional;

public class HistoryEntry {
    private long historyId;
    private String pluginIdentifier;
    private String query;
    private String description;
    private String url;

    private HistoryEntry() {
    }

    public HistoryEntry clearMetadata() {
        HistoryEntry result = new HistoryEntry();
        result.historyId = this.historyId;
        result.pluginIdentifier = this.pluginIdentifier;
        result.query = this.query;
        return result;
    }

    public static DatabaseOperation<Void> deleteHistoryEntry(long id) {
        return (database, cancel) -> {
            database.delete(DbConfig.HISTORY, DbConfig.HISTORY_ID + " = " + id, null);
            return null;
        };
    }

    public static DatabaseOperation<ImmutableList<HistoryEntry>> getHistoryOperation(
            String pluginIdentifier,
            String query,
            int count) {

        return (database, cancel) -> {
            // without windowing functions... they exist in sqlite but only in very recent versions

            DatabaseOperation<List<Long>> list = Selector.rawQuery(
                    "select " + DbConfig.HISTORY_ID + " from " + DbConfig.HISTORY + " " +
                            "where " + DbConfig.HISTORY_NORMALIZED_QUERY + " like ? " +
                            "order by " + DbConfig.HISTORY_LAST_USED + " desc " +
                            "limit " + count,
                    "%" + DatabaseUtilities.normalize(query) + "%")
                    .list(RowHandler.LONG);

            List<Long> execute = list.execute(database, cancel);
            @SuppressWarnings("UnstableApiUsage")
            ImmutableList.Builder<HistoryEntry> result = ImmutableList.builderWithExpectedSize(execute.size());
            for (long historyId : execute) {
                DatabaseOperation<HistoryEntry> one = Selector.rawQuery(
                        "select * from " + DbConfig.CHOICES + " " +
                                "where " + DbConfig.CHOICE_HISTORY_ID + " = " + historyId + " " +
                                "order by " +
                                DbConfig.CHOICE_ENGINE_KEY + " = ? desc, " +
                                DbConfig.CHOICE_LAST_USED + " desc " +
                                "limit 1",
                        pluginIdentifier).one(
                        cursor -> {
                            HistoryEntry tmp = new HistoryEntry();

                            tmp.historyId = historyId;
                            tmp.pluginIdentifier = cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.CHOICE_ENGINE_KEY));
                            tmp.query = cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.CHOICE_QUERY));
                            tmp.description = cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.CHOICE_DESCRIPTION));
                            tmp.url = cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.CHOICE_URL));

                            return tmp;
                        });
                result.add(one.execute(database, cancel));
            }
            return result.build();
        };
    }

    public long getHistoryId() {
        return historyId;
    }

    public String getPluginIdentifier() {
        return MoreObjects.firstNonNull(pluginIdentifier, "");
    }

    public String getQuery() {
        return MoreObjects.firstNonNull(query, "");
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public Optional<String> getUrl() {
        return Optional.ofNullable(url);
    }

    @Override
    public String toString() {
        return "HistoryEntry{" +
                "id=" + historyId +
                ", pluginIdentifier='" + pluginIdentifier + '\'' +
                ", query='" + query + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
