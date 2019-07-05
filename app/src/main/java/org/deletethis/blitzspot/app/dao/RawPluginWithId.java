package org.deletethis.blitzspot.app.dao;

class RawPluginWithId {
    private final long id;
    private final byte [] data;
    private final double ordering;

    public RawPluginWithId(long id, byte [] data, double ordering) {
        this.id = id;
        this.data = data;
        this.ordering = ordering;
    }

    public long getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public double getOrdering() {
        return ordering;
    }
}
