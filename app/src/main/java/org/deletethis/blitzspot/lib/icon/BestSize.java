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

import android.graphics.Bitmap;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

/**
 * Simple utility class to find the image of size closest to the specified size
 */
class BestSize<T> {
    private final Function<T, Integer> getWidth;
    private final Function<T, Integer> getHeight;
    private final int wantedSize;
    private T best = null;
    private int bestDiff;

    private BestSize(Function<T,Integer> getWidth, Function<T,Integer> getHeight, int wantedSize) {
        this.getWidth = getWidth;
        this.getHeight = getHeight;
        this.wantedSize = wantedSize;
    }

    public static BestSize<ScaledBitmap> icons(int wantedSize) {
        return new BestSize<>(ScaledBitmap::getOriginalWidth, ScaledBitmap::getOriginalHeight, wantedSize);
    }

    public static BestSize<Bitmap> bitmaps(int wantedSize) {
        return new BestSize<>(Bitmap::getWidth, Bitmap::getHeight, wantedSize);
    }

    private int getSize(T item) {
        int w = getWidth.apply(item);
        int h = getHeight.apply(item);
        return (w<h) ? h : w;
    }

    private boolean isBetter(int diff) {
        // nothing yet, or exact result, it's the best for sure
        // if there are multiple exactly sized images, let's pick the first one
        if(best == null || (diff == 0 && bestDiff != 0)) {
            return true;
        }
        // bigger is always than smaller
        if(bestDiff < 0 && diff > 0) {
            return true;
        }
        // smaller is never better than bigger
        if(bestDiff > 0 && diff < 0) {
            return false;
        }
        // smaller the difference, the better
        return Math.abs(diff) < Math.abs(bestDiff);
    }


    void add(T item) {
        Objects.requireNonNull(item);

        int diff = getSize(item) - wantedSize;
        if(isBetter(diff)) {
            best = item;
            bestDiff = diff;
        }
    }

    void addAll(Iterable<T> item) {
        for(T t: item) {
            add(t);
        }
    }

    boolean isEmpty() {
        return best == null;
    }

    T getBestOrThrow() {
        if(best == null)
            throw new NoSuchElementException();

        return best;
    }
}
