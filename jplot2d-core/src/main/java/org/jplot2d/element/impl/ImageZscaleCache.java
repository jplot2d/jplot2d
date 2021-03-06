/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element.impl;

import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.image.IntensityTransform;
import org.jplot2d.util.Range;

import javax.annotation.Nullable;
import java.util.WeakHashMap;

/**
 * The class calculate and cache z-scaled image band data.
 *
 * @author Jingjing Li
 */
public class ImageZscaleCache {

    /**
     * The max number of significant bits after applying limits. The max number is 16, for unsigned short data buffer.
     */
    private static final int MAX_BITS = 16;
    private static final WeakHashMap<Key, Object> map = new WeakHashMap<>();

    /**
     * Create a key for the given calculation arguments.
     *
     * @param dbuf               an ImageDataBuffer
     * @param w                  width
     * @param h                  height
     * @param limits             the low/high cut values
     * @param intensityTransform IntensityTransform
     * @param bias               the bias value
     * @param gain               the gain value
     * @param outputBits         the bits of output values
     * @return the key for the given calculation arguments
     */
    public static Key createKey(ImageDataBuffer dbuf, int w, int h, Range limits,
                                IntensityTransform intensityTransform, double bias, double gain, int outputBits) {
        return new Key(dbuf, w, h, limits, intensityTransform, bias, gain, outputBits);
    }

    /**
     * Create a cache entry for the given calculation key.
     */
    public static void cacheFor(Key key) {
        synchronized (map) {
            Object v = map.remove(key);
            map.put(key, v);
        }
    }

    /**
     * Returns the result for the given key.
     */
    public static Object getValue(Key key) {
        synchronized (map) {
            Object vref = map.get(key);
            if (vref == null) {
                vref = zscaleLimits(key.dbuf, key.w, key.h, key.limits, key.intensityTransform, key.bias, key.gain, key.outputBits);
                map.put(key, vref);
            }
            return vref;
        }
    }

    /**
     * z-scale the image according settings in the given key.
     *
     * @return the scaled data, in byte[] or short[]
     */
    public static Object zscaleLimits(ImageDataBuffer idb, int w, int h, Range limits,
                                      IntensityTransform intensityTransform, double bias, double gain, int outputBits) {

        int lutInputBits = getILUTInputBits(intensityTransform, bias, gain, outputBits);
        if (outputBits <= Byte.SIZE) {
            byte[] lut = createByteILUT(intensityTransform, bias, gain, lutInputBits, outputBits);
            return zscaleBytes(idb, 0, 0, w, h, limits, lut, lutInputBits);
        } else {
            short[] lut = createShortILUT(intensityTransform, bias, gain, lutInputBits, outputBits);
            return zscaleShorts(idb, 0, 0, w, h, limits, lut, lutInputBits);
        }

    }

    /**
     * Returns the number of significant bits that the ILUT index should match. When applying the limits, the generated
     * values should match the ILUT indexes. If createByteILUT/createShortILUT returns null, returns outputBits.
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
     * @return an ILUT
     */
    @Nullable
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
     * Apply the cuts and scale the data to an unsigned byte array.
     *
     * @param xoff         the x-offset of the image
     * @param yoff         the y-offset of the image
     * @param w            the image width
     * @param h            the image height
     * @param limits       the low and high cut value
     * @param lut          the look up table. The index is input value, and the value if output value.
     * @param lutInputBits the bits of input value. If the lut is null, it's the bits of return values.
     * @return an unsigned byte array
     */
    private static byte[] zscaleBytes(ImageDataBuffer idb, int xoff, int yoff, int w, int h, Range limits,
                                      byte[] lut, int lutInputBits) {

        byte[] result = new byte[w * h];

        // limits is null means there is no valid data
        if (limits == null) {
            return result;
        }

        double lowCut = limits.getMin();
        double highCut = limits.getMax();
        int outputRange = 1 << lutInputBits;
        double scale = outputRange / (highCut - lowCut);

        int n = 0;
        if (lut == null) {
            for (int r = yoff; r < yoff + h; r++) {
                for (int c = xoff; c < xoff + w; c++) {
                    double scaled = (idb.getDouble(c, r) - lowCut) * scale;
                    /*
                     * the scaled value may slightly larger than outputRange or slightly small than 0. the ilutIndex
					 * range is [0, outputRange)
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
                    double idelta = scaled - ilutIndex;

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
     * @return an ILUT
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
     * Apply the cuts and scale the data to an unsigned short array.
     *
     * @param xoff         the x-offset of the image
     * @param yoff         the y-offset of the image
     * @param w            the image width
     * @param h            the image height
     * @param limits       the low and high cut value
     * @param lut          the look up table. The index is input value, and the value if output value.
     * @param lutInputBits the bits of input value. If the lut is null, it's the bits of return values.
     * @return an unsigned short array
     */
    private static short[] zscaleShorts(ImageDataBuffer idb, int xoff, int yoff, int w, int h, Range limits,
                                        short[] lut, int lutInputBits) {

        short[] result = new short[w * h];

        // limits is null means there is no valid data
        if (limits == null) {
            return result;
        }

        double lowCut = limits.getMin();
        double highCut = limits.getMax();
        int outputRange = 1 << lutInputBits;
        double scale = outputRange / (highCut - lowCut);

        int n = 0;
        if (lut == null) {
            for (int r = yoff; r < yoff + h; r++) {
                for (int c = xoff; c < xoff + w; c++) {
                    double scaled = (idb.getDouble(c, r) - lowCut) * scale;
                    /*
                     * the scaled value may slightly larger than outputRange or slightly small than 0. the ilutIndex
					 * range is [0, outputRange)
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
                    double idelta = scaled - ilutIndex;

                    // the LUT output is unsigned short, apply the 0xffff bit mask.
                    int a = lut[ilutIndex] & 0xffff;
                    int b = lut[ilutIndex + 1] & 0xffff;
                    result[n++] = (short) (a + idelta * (b - a));
                }
            }
        }

        return result;
    }

    public static class Key {
        protected final ImageDataBuffer dbuf;
        protected final int w, h;
        protected final int outputBits;
        private final Range limits;
        private final IntensityTransform intensityTransform;
        private final double bias, gain;

        private Key(ImageDataBuffer dbuf, int w, int h, Range limits, IntensityTransform intensityTransform,
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
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            Key key = (Key) obj;
            boolean limitsMatch = key.limits == limits || (key.limits != null && key.limits.equals(limits));
            return key.dbuf.equals(dbuf) && key.w == w && key.h == h && limitsMatch
                    && key.intensityTransform == intensityTransform && key.bias == bias && key.gain == gain
                    && key.outputBits == outputBits;
        }

        public int hashCode() {
            long bits = 0;
            if (limits != null) {
                bits += java.lang.Double.doubleToLongBits(limits.getStart());
                bits += java.lang.Double.doubleToLongBits(limits.getEnd()) * 31;
            }
            bits += java.lang.Double.doubleToLongBits(bias) * 41;
            bits += java.lang.Double.doubleToLongBits(gain) * 47;

            return dbuf.hashCode() ^ (((int) bits) ^ ((int) (bits >> 32)));
        }
    }

}
