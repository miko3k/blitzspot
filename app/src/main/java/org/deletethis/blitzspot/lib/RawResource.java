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
