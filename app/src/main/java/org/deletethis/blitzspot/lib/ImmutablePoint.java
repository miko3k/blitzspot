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
