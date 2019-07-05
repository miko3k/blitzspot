/*
 * blitzspot
 * Copyright (C) 2018-2019 Peter Hanula
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
