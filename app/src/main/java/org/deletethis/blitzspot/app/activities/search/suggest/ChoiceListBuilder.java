package org.deletethis.blitzspot.app.activities.search.suggest;

import com.google.common.collect.ImmutableList;

public class ChoiceListBuilder {
    private final ImmutableList.Builder<ChoiceProvider.Choice> choiceListBuilder;
    private ChoiceProvider.Choice def;
    private int count;

    @SuppressWarnings("UnstableApiUsage")
    public ChoiceListBuilder(int expectedSize) {
        this.choiceListBuilder = ImmutableList.builderWithExpectedSize(expectedSize);
    }

    public ImmutableList<ChoiceProvider.Choice> buildChoices() {
        return choiceListBuilder.build();
    }

    public ChoiceProvider.Choice getDefault() {
        return def;
    }

    public void add(ChoiceProvider.Choice choice) {
        choiceListBuilder.add(choice);
        ++count;
    }

    public void addAndSetDefault(ChoiceProvider.Choice choice) {
        add(choice);
        this.def = choice;
    }

    public int getCount() {
        return count;
    }
}
