/*
 * Copyright 2010-2013 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.data;

import java.nio.ByteBuffer;

/**
 * This class extends ImageDataBuffer and stores data internally as bytes.
 *
 * @author Jingjing Li
 */
public abstract class ByteDataBuffer extends ImageDataBuffer {

    public ByteDataBuffer(ImageMaskBuffer mask) {
        super(mask);
    }

    @Override
    public byte getByte(int x, int y) {
        return get(x, y);
    }

    @Override
    public short getShort(int x, int y) {
        return get(x, y);
    }

    @Override
    public int getInt(int x, int y) {
        return get(x, y);
    }

    @Override
    public float getFloat(int x, int y) {
        return get(x, y);
    }

    @Override
    public double getDouble(int x, int y) {
        return get(x, y);
    }

    public abstract byte get(int x, int y);

    public static class Array extends ByteDataBuffer {
        private final byte[] data;
        private final int offset;

        public Array(byte[] data) {
            this(data, 0, null);
        }

        public Array(byte[] data, ImageMaskBuffer mask) {
            this(data, 0, mask);
        }

        public Array(byte[] data, int offset, ImageMaskBuffer mask) {
            super(mask);
            this.data = data;
            this.offset = offset;
        }

        public byte get(int x, int y) {
            return data[offset + x + y];
        }

    }

    public static class Array2D extends ByteDataBuffer {
        private final byte[][] data;
        private final int xoffset, yoffset;

        public Array2D(byte[][] data) {
            this(data, 0, 0, null);
        }

        public Array2D(byte[][] data, ImageMaskBuffer mask) {
            this(data, 0, 0, mask);
        }

        public Array2D(byte[][] data, int xoffset, int yoffset, ImageMaskBuffer mask) {
            super(mask);
            this.data = data;
            this.xoffset = xoffset;
            this.yoffset = yoffset;
        }

        public byte get(int x, int y) {
            return data[yoffset + y][xoffset + x];
        }

    }

    public static class NioBuffer extends ByteDataBuffer {
        private final ByteBuffer data;
        private final int offset;

        public NioBuffer(ByteBuffer data) {
            this(data, 0, null);
        }

        public NioBuffer(ByteBuffer data, ImageMaskBuffer mask) {
            this(data, 0, mask);
        }

        public NioBuffer(ByteBuffer data, int offset, ImageMaskBuffer mask) {
            super(mask);
            this.data = data;
            this.offset = offset;
        }

        public byte get(int x, int y) {
            return data.get(offset + x + y);
        }

    }

    @Override
    public double countValid(int w, int h) {
        if (mask == null) {
            return w * h;
        } else {
            int count = 0;
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    if (!isMasked(i, j)) {
                        count++;
                    }
                }
            }
            return count;
        }
    }

    @Override
    public double[] calcMinMax(int w, int h) {

        byte min = Byte.MAX_VALUE;
        byte max = Byte.MIN_VALUE;

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                if (!isMasked(i, j)) {
                    byte v = get(i, j);
                    if (min > v) {
                        min = v;
                    }
                    if (max < v) {
                        max = v;
                    }
                }
            }
        }

        if (min > max) {
            return null;
        }
        return new double[]{min, max};

    }
}