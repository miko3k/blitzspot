package org.deletethis.blitzspot.app.activities.search.suggest.dummy;


import org.deletethis.blitzspot.app.activities.search.suggest.ChoiceListBuilder;
import org.deletethis.blitzspot.app.activities.search.suggest.ChoiceProvider;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class DummyChoiceProvider implements ChoiceProvider, Consumer<DummyChoice> {
    private String text;
    private int count;
    private final Listener listener;
    private final Set<String> removed = new HashSet<>();

    public DummyChoiceProvider(Listener listener) {
        this.text = "";
        this.count = 0;
        this.listener = listener;
    }

    @Override
    public void accept(DummyChoice dummySuggestion) {
        removed.add(dummySuggestion.getValue());
        generateSuggestions();
    }

    private void generateSuggestions() {
        ChoiceListBuilder suggestions = new ChoiceListBuilder(count);

        int currentCount = 0;
        for(int i = 0; currentCount < count; ++i) {
            String value = text + i;
            if(removed.contains(value))
                continue;

            String url = null;
            String description = null;
            boolean removable = false;

            if(i % 3 == 1) {
                description = "This is " + value;
                removable = true;
            }
            if(i % 3 == 2) {
                url = "http://www.google.com";
            }

            DummyChoice choice = new DummyChoice(value, description, url, removable ? this : null);
            if(i == 1) {
                suggestions.add(choice);
            } else {
                suggestions.addAndSetDefault(choice);
            }
            currentCount++;
        }

        listener.onSearchChoices(suggestions.buildChoices(), suggestions.getDefault());
    }

    @Override
    public void setCount(int count) {
        this.count = count;
        generateSuggestions();
    }

    @Override
    public void setQuery(String text) {
        this.text = text;
        generateSuggestions();
    }

    @Override
    public void setQueryFromSuggestion(Choice choice) {
        setQuery(choice.getValue());
    }

    @Override
    public void cancelAll() {

    }
}

