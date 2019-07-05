package org.deletethis.blitzspot.app.fragments.choose;

import org.deletethis.search.parser.IconAddress;

public interface DataSet {
    IconAddress getIcon(int position);
    String getName(int position);
    void selected(int position);
    default boolean isFancyArrowPresent(int position) { return false; }
    int getCount();
}
