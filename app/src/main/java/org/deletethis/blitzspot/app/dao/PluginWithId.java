package org.deletethis.blitzspot.app.dao;

import org.deletethis.search.parser.PatchBuilder;
import org.deletethis.search.parser.SearchPlugin;

public class PluginWithId {
    private final long id;
    private final SearchPlugin engine;
    private final double ordering;

    public PluginWithId(long id, SearchPlugin engine, double ordering) {
        this.id = id;
        this.engine = engine;
        this.ordering = ordering;
    }

    public long getId() {
        return id;
    }

    public SearchPlugin getSearchPlugin() {
        return engine;
    }

    public double getOrdering() {
        return ordering;
    }

    public byte[] serialize() {
        return engine.serialize();
    }

    public PatchBuilder patch() {
        return engine.patch();
    }
}
