package org.deletethis.blitzspot.lib.icon;

import androidx.lifecycle.MutableLiveData;

class RemoteImage {
    private static final long RETRY_AFTER_MS = 120_000;

    public enum Status {
        UNLOADED,
        FAILED,
        LOADED,
        LOADING
    }

    private final MutableLiveData<ScaledBitmap> data = new MutableLiveData<>();
    private final String url;
    private Status status = Status.UNLOADED;
    private long lastLoadTimestamp;

    RemoteImage(String url) {
        this.url = url;
    }

    MutableLiveData<ScaledBitmap> getLiveData() {
        return data;
    }

    public String getUrl() {
        return url;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    boolean needLoad(long now) {
        switch (status) {
            case UNLOADED: return true;
            case FAILED: return now - lastLoadTimestamp > RETRY_AFTER_MS;
            default: return false;
        }
    }

    void setLastLoadTimestamp(long lastLoadTimestamp) {
        this.lastLoadTimestamp = lastLoadTimestamp;
    }
}
