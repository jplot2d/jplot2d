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
package org.jplot2d.element.impl;

import java.util.WeakHashMap;

import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.image.IntensityTransform;

/**
 * The class calculate and cache z-scaled image band data.
 * 
 * @author Jingjing Li
 * 
 */
public class ImageZscaleCache {

	public static class Key {
		private final ImageDataBuffer dbuf;
		private final int w, h;
		private final double[] limits;
		private final IntensityTransform intensityTransform;
		private final double bias, gain;
		private final int outputBits;

		private Key(ImageDataBuffer dbuf, int w, int h, double[] limits, IntensityTransform intensityTransform,
				double bias, double gain, int outputBits) {
			this.dbuf = dbuf;
			this.w = w;
			this.h = h;
			this.limits = limits;
			this.intensityTransform = intensityTransform;
			this.bias = bias;
			this.gain = gain;
			this.outputBits = outputBits;
		}

		public boolean equals(Object obj) {
			if (obj instanceof ImageZscaleCache) {
				return false;
			}
			Key key = (Key) obj;
			boolean limitsMatch = key.limits == limits
					|| (key.limits != null && key != null && key.limits[0] == limits[0] && key.limits[1] == limits[1]);
			return key.dbuf.equals(dbuf) && key.w == w && key.h == h && limitsMatch
					&& key.intensityTransform == intensityTransform && key.bias == bias && key.gain == gain
					&& key.outputBits == outputBits;
		}

		public int hashCode() {
			/* we only use dbuf to produce hash code, since there is rare conflict in the cache map */
			return dbuf.hashCode();
		}
	}

	private static class ValueRef {
		private Object v;
	}

	/**
	 * The max number of significant bits after applying limits. The max number is 16, for unsigned short data buffer.
	 */
	private static final int MAX_BITS = 16;

	private static final WeakHashMap<Key, ValueRef> map = new WeakHashMap<Key, ValueRef>();

	/**
	 * Create a cache entry for the given calculation arguments.
	 * 
	 * @param dbuf
	 * @param w
	 * @param h
	 * @param limits
	 * @param intensityTransform
	 * @param bias
	 * @param gain
	 * @param outputBits
	 * @return the key for the given calculation arguments
	 */
	public static Key createCacheFor(ImageDataBuffer dbuf, int w, int h, double[] limits,
			IntensityTransform intensityTransform, double bias, double gain, int outputBits) {
		Key key = new Key(dbuf, w, h, limits, intensityTransform, bias, gain, outputBits);
		synchronized (map) {
			ValueRef vref = map.remove(key);
			if (vref == null) {
				vref = new ValueRef();
			}
			map.put(key, vref);
		}
		return key;
	}

	/**
	 * Returns the result for the given calculation arguments.
	 * 
	 * @param dbuf
	 * @param w
	 * @param h
	 * @param limits
	 * @param intensityTransform
	 * @param bias
	 * @param gain
	 * @param outputBits
	 * @return
	 */
	public static Object getValue(ImageDataBuffer dbuf, int w, int h, double[] limits,
			IntensityTransform intensityTransform, double bias, double gain, int outputBits) {
		Key key = new Key(dbuf, w, h, limits, intensityTransform, bias, gain, outputBits);
		ValueRef vref;
		synchronized (map) {
			vref = map.get(key);
		}
		if (vref == null) {
			return zscaleLimits(key);
		}
		synchronized (vref) {
			if (vref.v != null) {
				return vref.v;
			} else {
				// calculate v and store it
				vref.v = zscaleLimits(key);
				return vref.v;
			}
		}
	}

	private static Object zscaleLimits(Key key) {

		ImageDataBuffer idb = key.dbuf;
		int w = key.w;
		int h = key.h;
		double[] limits = key.limits;

		int lutInputBits = getILUTInputBits(key.intensityTransform, key.bias, key.gain, key.outputBits);
		if (key.outputBits <= Byte.SIZE) {
			byte[] lut = createByteILUT(key.intensityTransform, key.bias, key.gain, lutInputBits, key.outputBits);
			return zscaleBytes(idb, 0, 0, w, h, limits, lut, lutInputBits);
		} else {
			short[] lut = createShortILUT(key.intensityTransform, key.bias, key.gain, lutInputBits, key.outputBits);
			return zscaleShorts(idb, 0, 0, w, h, limits, lut, lutInputBits);
		}

	}

	/**
	 * Returns the number of significant bits that the ILUT index should match. When applying the limits, the generated
	 * values should match the ILUT indexes.
	 * 
	 * @return the number of significant bits
	 */
	private static int getILUTInputBits(IntensityTransform intensityTransform, double bias, double gain, int outputBits) {
		int bits = outputBits;
		if (intensityTransform != null || gain != 0.5 || bias != 0.5) {
			bits += 2;
		}
		if (bits > MAX_BITS) {
			bits = MAX_BITS;
		}
		return bits;
	}

	/**
	 * Returns the ILUT for processing data, for applying intensity transform and bias/gain.
	 * 
	 * @return
	 */
	private static byte[] createByteILUT(IntensityTransform intensityTransform, double bias, double gain,
			int inputBits, int outputBits) {

		if (intensityTransform == null && gain == 0.5 && bias == 0.5) {
			return null;
		}

		// the LUT index range is [0, lutIndexes], plus repeat the last value
		int lutIndexes = 1 << inputBits;

		// the output range is [0, outputRange - 1]
		int outputRange = 1 << outputBits;

		byte[] lut = new byte[lutIndexes + 2];
		for (int i = 0; i <= lutIndexes; i++) {
			// t range is [0, 1]
			double t = (double) i / lutIndexes;
			if (intensityTransform != null) {
				t = intensityTransform.transform(t);
			}
			if (bias != 0.5) {
				t = t / ((1.0 / bias - 2.0) * (1.0 - t) + 1.0);
			}
			if (gain != 0.5) {
				double f = (1.0 / gain - 2.0) * (1.0 - 2 * t);
				if (t < 0.5) {
					t = t / (f + 1.0);
				} else {
					t = (f - t) / (f - 1.0);
				}
			}
			int v = (int) (outputRange * t);
			if (v < 0) {
				v = 0;
			} else if (v >= outputRange) {
				v = outputRange - 1;
			}
			lut[i] = (byte) (v);
		}
		lut[lutIndexes + 1] = lut[lutIndexes];

		return lut;
	}

	/**
	 * Apply the cuts and scale the data to a unsigned byte array
	 * 
	 * @param xoff
	 * @param yoff
	 * @param w
	 * @param h
	 * @param limits
	 * @param lut
	 * @param lutInputBits
	 * @return
	 */
	private static byte[] zscaleBytes(ImageDataBuffer idb, int xoff, int yoff, int w, int h, double[] limits,
			byte[] lut, int lutInputBits) {

		byte[] result = new byte[w * h];

		// limits is null means there is no valid data
		if (limits == null) {
			return result;
		}

		double lowCut = limits[0];
		double highCut = limits[1];
		int outputRange = 1 << lutInputBits;
		double scale = outputRange / (highCut - lowCut);

		int n = 0;
		if (lut == null) {
			for (int r = yoff; r < yoff + h; r++) {
				for (int c = xoff; c < xoff + w; c++) {
					double scaled = (idb.getDouble(c, r) - lowCut) * scale;
					/*
					 * the scaled value may slightly larger than outputRange or slightly small than 0. the ilutIndex
					 * range is [0, outputRange]
					 */
					int ilutIndex = (int) scaled;
					if (ilutIndex >= outputRange) {
						ilutIndex = outputRange - 1;
					}
					result[n++] = (byte) ilutIndex;
				}
			}
		} else {
			for (int r = yoff; r < yoff + h; r++) {
				for (int c = xoff; c < xoff + w; c++) {
					double scaled = (idb.getDouble(c, r) - lowCut) * scale;
					/*
					 * the scaled value may slightly larger than outputRange or slightly small than 0. the ilutIndex
					 * range is [0, outputRange]
					 */
					int ilutIndex = (int) scaled;
					double idelta = ilutIndex - ilutIndex;

					// the LUT output is unsigned byte, apply the 0xff bit mask.
					int a = lut[ilutIndex] & 0xff;
					int b = lut[ilutIndex + 1] & 0xff;
					result[n++] = (byte) (a + idelta * (b - a));
				}
			}
		}

		return result;
	}

	/**
	 * Returns the ILUT for processing data, for applying intensity transform and bias/gain.
	 * 
	 * @return
	 */
	private static short[] createShortILUT(IntensityTransform intensityTransform, double bias, double gain,
			int inputBits, int outputBits) {

		if (intensityTransform == null && gain == 0.5 && bias == 0.5) {
			return null;
		}

		/*
		 * create a lookup table. The input bits is getInputDataBits(). The output bits is getOutputDataBits()
		 */

		// the LUT index range is [0, lutIndexes], plus repeat the last value
		int lutIndexes = 1 << inputBits;

		// the output range is [0, outputRange - 1]
		int outputRange = 1 << outputBits;

		short[] lut = new short[lutIndexes + 2];
		for (int i = 0; i <= lutIndexes; i++) {
			// t range is [0, 1]
			double t = (double) i / lutIndexes;
			if (intensityTransform != null) {
				t = intensityTransform.transform(t);
			}
			if (bias != 0.5) {
				t = t / ((1.0 / bias - 2.0) * (1.0 - t) + 1.0);
			}
			if (gain != 0.5) {
				double f = (1.0 / gain - 2.0) * (1.0 - 2 * t);
				if (t < 0.5) {
					t = t / (f + 1.0);
				} else {
					t = (f - t) / (f - 1.0);
				}
			}
			int v = (int) (outputRange * t);
			if (v < 0) {
				v = 0;
			} else if (v >= outputRange) {
				v = outputRange - 1;
			}
			lut[i] = (short) (v);
		}
		lut[lutIndexes + 1] = lut[lutIndexes];

		return lut;
	}

	/**
	 * Apply the cuts and scale the data to a unsigned short array
	 * 
	 * @param xoff
	 * @param yoff
	 * @param w
	 * @param h
	 * @param limits
	 * @param lut
	 *            the LUT output bits is no more than 15
	 * @param lutInputBits
	 * @return
	 */
	private static short[] zscaleShorts(ImageDataBuffer idb, int xoff, int yoff, int w, int h, double[] limits,
			short[] lut, int lutInputBits) {

		short[] result = new short[w * h];

		// limits is null means there is no valid data
		if (limits == null) {
			return result;
		}

		double lowCut = limits[0];
		double highCut = limits[1];
		int outputRange = 1 << lutInputBits;
		double scale = outputRange / (highCut - lowCut);

		int n = 0;
		if (lut == null) {
			for (int r = yoff; r < yoff + h; r++) {
				for (int c = xoff; c < xoff + w; c++) {
					double scaled = (idb.getDouble(c, r) - lowCut) * scale;
					/*
					 * the scaled value may slightly larger than outputRange or slightly small than 0. the ilutIndex
					 * range is [0, outputRange]
					 */
					int ilutIndex = (int) scaled;
					if (ilutIndex >= outputRange) {
						ilutIndex = outputRange - 1;
					}
					result[n++] = (short) ilutIndex;
				}
			}
		} else {
			for (int r = yoff; r < yoff + h; r++) {
				for (int c = xoff; c < xoff + w; c++) {
					double scaled = (idb.getDouble(c, r) - lowCut) * scale;
					/*
					 * the scaled value may slightly larger than outputRange or slightly small than 0. the ilutIndex
					 * range is [0, outputRange]
					 */
					int ilutIndex = (int) scaled;
					double idelta = ilutIndex - ilutIndex;

					// the LUT output is unsigned short, apply the 0xffff bit mask.
					int a = lut[ilutIndex] & 0xffff;
					int b = lut[ilutIndex + 1] & 0xffff;
					result[n++] = (short) (a + idelta * (b - a));
				}
			}
		}

		return result;
	}

}
