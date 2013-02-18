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

import java.nio.FloatBuffer;

public abstract class FloatDataBuffer implements ImageDataBuffer {

	public double getDouble(int x, int y) {
		return get(x, y);
	}

	public abstract float get(int x, int y);

	public static class Array extends FloatDataBuffer {
		private final float[] data;
		private final boolean[] mask;

		public Array(float[] data) {
			this.data = data;
			this.mask = null;
		}

		public Array(float[] data, boolean[] mask) {
			this.data = data;
			this.mask = mask;
		}

		public float get(int x, int y) {
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

	public static class Array2D extends FloatDataBuffer {
		private final float[][] data;
		private final boolean[][] mask;

		public Array2D(float[][] data) {
			this.data = data;
			this.mask = null;
		}

		public Array2D(float[][] data, boolean[][] mask) {
			this.data = data;
			this.mask = mask;
		}

		public float get(int x, int y) {
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

	public static class NioBuffer extends FloatDataBuffer {
		private final FloatBuffer data;
		private final boolean[] mask;

		public NioBuffer(FloatBuffer data) {
			this.data = data;
			this.mask = null;
		}

		public NioBuffer(FloatBuffer data, boolean[] mask) {
			this.data = data;
			this.mask = mask;
		}

		public float get(int x, int y) {
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
		float min = Float.NaN;
		float max = Float.NaN;

		/* find the 1st non-Nan idx and value */
		int m = -1;
		int n = -1;

		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				float v = get(i, j);
				if (!isMasked(i, j) && v == v) {
					min = v;
					max = v;
					m = i;
					n = j;
					break;
				}
			}
			if (n != -1) {
				break;
			}
		}

		if (n == -1) {
			return null;
		}

		/* find min & max value */
		m++;
		for (int j = n; j < h; j++) {
			for (int i = m; i < w; i++) {
				float v = get(i, j);
				if (!isMasked(i, j) && (v != Float.POSITIVE_INFINITY) && (v != Float.NEGATIVE_INFINITY)) {
					if (min > v) {
						min = v;
					}
					if (max < v) {
						max = v;
					}
				}
			}
			m = 0;
		}

		return new double[] { min, max };

	}

}