package org.deletethis.blitzspot.app.activities.search.suggest;

import org.deletethis.search.parser.SearchQuery;

import java.util.function.LongConsumer;

class HistoryChoice implements ChoiceProvider.Choice {
    private final HistoryEntry historyEntry;
    private final LongConsumer delete;

    public HistoryChoice(HistoryEntry historyEntry, LongConsumer delete) {
        this.historyEntry = historyEntry;
        this.delete = delete;
    }

    @Override
    public String getValue() {
        return historyEntry.getQuery();
    }

    @Override
    public String getDescription() {
        return historyEntry.getDescription().orElse(null);
    }

    @Override
    public String getUri() {
        return historyEntry.getUrl().orElse(null);
    }

    @Override
    public boolean isRemovable() {
        return true;
    }

    @Override
    public void remove() {
        delete.accept(historyEntry.getHistoryId());
    }

    @Override
    public SearchQuery getSearchQuery() {
        return SearchQuery.of(historyEntry.getQuery());
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
