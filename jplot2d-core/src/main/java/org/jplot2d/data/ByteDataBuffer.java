/**
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

public abstract class ByteDataBuffer implements ImageDataBuffer {

	public double getDouble(int x, int y) {
		return get(x, y);
	}

	public abstract byte get(int x, int y);

	public static class Array extends ByteDataBuffer {
		private final byte[] data;
		private final boolean[] mask;

		public Array(byte[] data) {
			this.data = data;
			this.mask = null;
		}

		public Array(byte[] data, boolean[] mask) {
			this.data = data;
			this.mask = mask;
		}

		public byte get(int x, int y) {
			return data[x + y];
		}

		public boolean isMasked(int x, int y) {
			if (mask == null) {
				return false;
			} else {
				return mask[x + y];
			}
		}
	}

	public static class Array2D extends ByteDataBuffer {
		private final byte[][] data;
		private final boolean[][] mask;

		public Array2D(byte[][] data) {
			this.data = data;
			this.mask = null;
		}

		public Array2D(byte[][] data, boolean[][] mask) {
			this.data = data;
			this.mask = mask;
		}

		public byte get(int x, int y) {
			return data[y][x];
		}

		public boolean isMasked(int x, int y) {
			if (mask == null) {
				return false;
			} else {
				return mask[y][x];
			}
		}
	}

	public static class NioBuffer extends ByteDataBuffer {
		private final ByteBuffer data;
		private final boolean[] mask;

		public NioBuffer(ByteBuffer data) {
			this.data = data;
			this.mask = null;
		}

		public NioBuffer(ByteBuffer data, boolean[] mask) {
			this.data = data;
			this.mask = mask;
		}

		public byte get(int x, int y) {
			return data.get(x + y);
		}

		public boolean isMasked(int x, int y) {
			if (mask == null) {
				return false;
			} else {
				return mask[x + y];
			}
		}
	}

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
		return new double[] { min, max };

	}
}