package org.deletethis.blitzspot.app.activities.search;

import org.deletethis.blitzspot.app.activities.search.suggest.ChoiceProvider;

interface AdapterListener {
    void onSearchSuggestionUsed(ChoiceProvider.Choice choice);
    void onSearchSuggestionClicked(ChoiceProvider.Choice choice);
    void onSearchSuggestionRemoved(ChoiceProvider.Choice choice);
}
