package org.deletethis.blitzspot.app.activities.search.suggest;

import org.deletethis.search.parser.SearchQuery;
import org.deletethis.search.parser.Suggestion;

class PluginChoice implements ChoiceProvider.Choice {
    private final Suggestion suggestion;
    private final boolean usable;

    public PluginChoice(Suggestion suggestion, boolean usable) {
        this.suggestion = suggestion;
        this.usable = usable;
    }

    @Override
    public String getValue() {
        return suggestion.getValue();
    }

    @Override
    public String getDescription() {
        return suggestion.getDescription().orElse(null);
    }

    @Override
    public String getUri() {
        return suggestion.getUrl().orElse(null);
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
    public SearchQuery getSearchQuery() {
        return SearchQuery.of(suggestion);
    }

    @Override
    public boolean isUsable() {
        return usable;
    }
}
