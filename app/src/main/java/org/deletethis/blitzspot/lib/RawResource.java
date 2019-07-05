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

package org.deletethis.blitzspot.lib;

import android.content.res.Resources;
import android.util.SparseArray;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.RawRes;

public class RawResource {
    private static final SparseArray<byte[]> cache = new SparseArray<>();

    synchronized static public byte [] cachedLoad(Resources res, @RawRes int id) {
        byte [] result = cache.get(id);

        if(result != null)
            return result;

        try (InputStream inputStream = res.openRawResource(id)) {
            result = ByteStreams.toByteArray(inputStream);
            cache.put(id, result);

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }
}
