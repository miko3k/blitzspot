package org.deletethis.blitzspot.app.activities.search.suggest;

import com.google.common.collect.ImmutableList;

import org.deletethis.search.parser.SearchQuery;

public interface ChoiceProvider {

    interface Choice {
        String getValue();
        String getDescription();
        String getUri();
        SearchQuery getSearchQuery();
        boolean isRemovable();
        void remove();
        boolean isUsable();
    }

    interface Listener {
        void onSearchChoices(ImmutableList<Choice> choiceList, Choice defaultChoice);
    }

    void setCount(int count);
    void setQuery(String text);
    void setQueryFromSuggestion(Choice choice);
    void cancelAll();
}
