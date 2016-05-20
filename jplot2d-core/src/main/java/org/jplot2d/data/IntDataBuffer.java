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

import java.nio.IntBuffer;

/**
 * This class extends ImageDataBuffer and stores data internally as integers.
 *
 * @author Jingjing Li
 */
public abstract class IntDataBuffer extends ImageDataBuffer {

    public IntDataBuffer(ImageMaskBuffer mask) {
        super(mask);
    }

    @Override
    public byte getByte(int x, int y) {
        return (byte) get(x, y);
    }

    @Override
    public short getShort(int x, int y) {
        return (short) get(x, y);
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

    public abstract int get(int x, int y);

    public static class Array extends IntDataBuffer {
        private final int[] data;
        private final int offset;

        public Array(int[] data) {
            this(data, 0, null);
        }

        public Array(int[] data, ImageMaskBuffer mask) {
            this(data, 0, mask);
        }

        public Array(int[] data, int offset, ImageMaskBuffer mask) {
            super(mask);
            this.data = data;
            this.offset = offset;
        }

        public int get(int x, int y) {
            return data[offset + x + y];
        }

    }

    public static class Array2D extends IntDataBuffer {
        private final int[][] data;
        private final int xoffset, yoffset;

        public Array2D(int[][] data) {
            this(data, 0, 0, null);
        }

        public Array2D(int[][] data, ImageMaskBuffer mask) {
            this(data, 0, 0, mask);
        }

        public Array2D(int[][] data, int xoffset, int yoffset, ImageMaskBuffer mask) {
            super(mask);
            this.data = data;
            this.xoffset = xoffset;
            this.yoffset = yoffset;
        }

        public int get(int x, int y) {
            return data[yoffset + y][xoffset + x];
        }

    }

    public static class NioBuffer extends IntDataBuffer {
        private final IntBuffer data;
        private final int offset;

        public NioBuffer(IntBuffer data) {
            this(data, 0, null);
        }

        public NioBuffer(IntBuffer data, ImageMaskBuffer mask) {
            this(data, 0, mask);
        }

        public NioBuffer(IntBuffer data, int offset, ImageMaskBuffer mask) {
            super(mask);
            this.data = data;
            this.offset = offset;
        }

        public int get(int x, int y) {
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

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                if (!isMasked(i, j)) {
                    int v = get(i, j);
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