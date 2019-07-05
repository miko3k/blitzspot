package org.deletethis.blitzspot.lib.icon;

import org.deletethis.search.parser.IconAddress;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

final class ResourceIconAddress implements IconAddress {
    @DrawableRes
    private final int resource;
    private final List<String> addresses;

    ResourceIconAddress(@DrawableRes int resource) {
        this.resource = resource;
        this.addresses = Collections.singletonList(toString());
    }

    @NonNull
    @Override
    public String toString() {
        return "RES:" + resource;
    }

    @NonNull
    @Override
    public Iterator<String> iterator() {
        return addresses.iterator();
    }

    @DrawableRes
    int getResource() {
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceIconAddress)) return false;
        ResourceIconAddress other = (ResourceIconAddress) o;
        return resource == other.resource;
    }

    @Override
    public int hashCode() {
        return resource;
    }
}
