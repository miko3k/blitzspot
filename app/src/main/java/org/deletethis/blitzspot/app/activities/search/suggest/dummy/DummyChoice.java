package org.deletethis.blitzspot.app.activities.search.suggest.dummy;

import org.deletethis.blitzspot.app.activities.search.suggest.ChoiceProvider;
import org.deletethis.search.parser.SearchQuery;

import java.util.function.Consumer;

class DummyChoice implements ChoiceProvider.Choice {
    private final String value;
    private final String description;
    private final String url;
    private final Consumer<DummyChoice> remove;

    public DummyChoice(String value, String description, String url, Consumer<DummyChoice> remove) {
        this.value = value;
        this.description = description;
        this.url = url;
        this.remove = remove;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getUri() {
        return url;
    }

    @Override
    public SearchQuery getSearchQuery() {
        return SearchQuery.of(value);
    }

    @Override
    public boolean isRemovable() {
        return remove != null;
    }

    @Override
    public void remove() {
        remove.accept(this);
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
