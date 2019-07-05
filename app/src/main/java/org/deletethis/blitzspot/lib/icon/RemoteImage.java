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
