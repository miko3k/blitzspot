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

import java.nio.ByteBuffer;

public class ImmutablePoint {
    private final int x, y;

    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public byte [] serialize() {
        byte [] data = new byte[8];
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        byteBuffer.putInt(x);
        byteBuffer.putInt(y);
        return data;
    }

    public static ImmutablePoint deserialize(byte [] bytes) {
        ByteBuffer body = ByteBuffer.wrap(bytes);
        int x = body.getInt();
        int y = body.getInt();
        return new ImmutablePoint(x, y);
    }
}
