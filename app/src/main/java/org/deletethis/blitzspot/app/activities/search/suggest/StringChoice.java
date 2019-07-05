package org.deletethis.blitzspot.app.activities.search.suggest;

import org.deletethis.search.parser.SearchQuery;

class StringChoice implements ChoiceProvider.Choice {
    private final String query;

    public StringChoice(String query) {
        this.query = query;
    }

    @Override
    public String getValue() {
        return query;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUri() {
        return null;
    }

    @Override
    public boolean isRemovable() {
        return false;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUsable() {
        return false;
    }

    @Override
    public SearchQuery getSearchQuery() {
        return SearchQuery.of(query);
    }


}
