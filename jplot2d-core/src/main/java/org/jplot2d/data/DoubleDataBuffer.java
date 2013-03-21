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

import java.nio.DoubleBuffer;

/**
 * This class extends ImageDataBuffer and stores data internally as floats.
 * 
 * @author Jingjing Li
 * 
 */
public abstract class DoubleDataBuffer extends ImageDataBuffer {

	public DoubleDataBuffer(ImageMaskBuffer mask) {
		super(mask);
	}

	public double getDouble(int x, int y) {
		return get(x, y);
	}

	public abstract double get(int x, int y);

	public static class Array extends DoubleDataBuffer {
		private final double[] data;
		private final int offset;

		public Array(double[] data) {
			this(data, 0, null);
		}

		public Array(double[] data, ImageMaskBuffer mask) {
			this(data, 0, mask);
		}

		public Array(double[] data, int offset, ImageMaskBuffer mask) {
			super(mask);
			this.data = data;
			this.offset = offset;
		}

		public double get(int x, int y) {
			return data[offset + x + y];
		}

	}

	public static class Array2D extends DoubleDataBuffer {
		private final double[][] data;
		private final int xoffset, yoffset;

		public Array2D(double[][] data) {
			this(data, 0, 0, null);
		}

		public Array2D(double[][] data, ImageMaskBuffer mask) {
			this(data, 0, 0, mask);
		}

		public Array2D(double[][] data, int xoffset, int yoffset, ImageMaskBuffer mask) {
			super(mask);
			this.data = data;
			this.xoffset = xoffset;
			this.yoffset = yoffset;
		}

		public double get(int x, int y) {
			return data[yoffset + y][xoffset + x];
		}

	}

	public static class NioBuffer extends DoubleDataBuffer {
		private final DoubleBuffer data;
		private final int offset;

		public NioBuffer(DoubleBuffer data) {
			this(data, 0, null);
		}

		public NioBuffer(DoubleBuffer data, ImageMaskBuffer mask) {
			this(data, 0, mask);
		}

		public NioBuffer(DoubleBuffer data, int offset, ImageMaskBuffer mask) {
			super(mask);
			this.data = data;
			this.offset = offset;
		}

		public double get(int x, int y) {
			return data.get(offset + x + y);
		}

	}

	public double[] calcMinMax(int w, int h) {
		double min = Double.NaN;
		double max = Double.NaN;

		/* find the 1st non-Nan idx and value */
		int m = -1;
		int n = -1;

		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				double v = get(i, j);
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
				double v = get(i, j);
				if (!isMasked(i, j) && (v != Double.POSITIVE_INFINITY) && (v != Double.NEGATIVE_INFINITY)) {
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