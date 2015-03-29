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
package org.jplot2d.image;

import org.jplot2d.data.ByteDataBuffer;
import org.jplot2d.data.DoubleDataBuffer;
import org.jplot2d.data.FloatDataBuffer;
import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.data.IntDataBuffer;
import org.jplot2d.data.ShortDataBuffer;
import org.jplot2d.util.DoubleBottomNFinder;
import org.jplot2d.util.DoubleTopNFinder;
import org.jplot2d.util.FloatBottomNFinder;
import org.jplot2d.util.FloatTopNFinder;
import org.jplot2d.util.IntBottomNFinder;
import org.jplot2d.util.IntTopNFinder;
import org.jplot2d.util.ShortBottomNFinder;
import org.jplot2d.util.ShortTopNFinder;

import java.awt.Dimension;

/**
 * The limits calculator to produce the upper and lower limits to based on the specified percentage. A histogram of the
 * data is created and the limits are set to display the percentage, about the mean value.
 *
 * @author Jingjing Li
 */
public class PercentCalculator implements LimitsCalculator {

    private final double percentage;

    public PercentCalculator(double percentage) {
        this.percentage = percentage;
    }

    @SuppressWarnings("ConstantConditions")
    public double[] calcLimits(ImageDataBuffer[] dbufArray, Dimension[] sizeArray) {

        if (percentage == 100) {
            return MinMaxCalculator.calcMinMax(dbufArray, sizeArray);
        }

        int numberCount = 0;
        for (int a = 0; a < dbufArray.length; a++) {
            numberCount += dbufArray[a].countValid(sizeArray[a].width, sizeArray[a].height);
        }
        if (numberCount == 0) {
            return null;
        } else if (numberCount == 1) {
            return MinMaxCalculator.calcMinMax(dbufArray, sizeArray);
        }

        double cutoff = numberCount * (100 - percentage) / 100.0 / 2.0;

        int maxDataByes = 0;
        for (ImageDataBuffer dbuf : dbufArray) {
            int dataBytes = 0;
            if (dbuf instanceof ByteDataBuffer) {
                dataBytes = 1;
            } else if (dbuf instanceof ShortDataBuffer) {
                dataBytes = 2;
            } else if (dbuf instanceof IntDataBuffer) {
                dataBytes = 4;
            } else if (dbuf instanceof FloatDataBuffer) {
                dataBytes = 6;
            } else if (dbuf instanceof DoubleDataBuffer) {
                dataBytes = 8;
            }
            if (maxDataByes < dataBytes) {
                maxDataByes = dataBytes;
            }
        }

        switch (maxDataByes) {
            case 1:
                return calcByteLimits(dbufArray, sizeArray, cutoff);
            case 2:
                return calcShortLimits(dbufArray, sizeArray, cutoff);
            case 4:
                return calcIntLimits(dbufArray, sizeArray, cutoff);
            case 6:
                return calcFloatLimits(dbufArray, sizeArray, cutoff);
            case 8:
                return calcDoubleLimits(dbufArray, sizeArray, cutoff);
            default:
                throw new IllegalArgumentException("Unsupported ImageDataBuffer");
        }
    }

    /**
     * Calculate low & high cut values for the given ImageDataBuffer array. If the array contains no data, null is returned.
     *
     * @param dbufArray an array of ImageDataBuffer
     * @param sizeArray the size of the given ImageDataBuffer array
     * @param cutoff    the number of values that should be cut-off on each side
     * @return the low & high cut values
     */
    private static double[] calcByteLimits(ImageDataBuffer[] dbufArray, Dimension[] sizeArray, double cutoff) {

        // the cuts value is between cutoffLess and cutoffMore
        double offsetrate = (int) cutoff + 1 - cutoff;
        int cutoffLess = (int) cutoff + 1;
        int cutoffMore = (int) cutoff + 2;

        // calculate by value index count
        int valueN = 2 << Byte.SIZE;
        int[] valueCounter = new int[valueN];
        for (int a = 0; a < dbufArray.length; a++) {
            ImageDataBuffer dbuf = dbufArray[a];
            int w = sizeArray[a].width;
            int h = sizeArray[a].height;

            if (!dbuf.hasMasks()) {
                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++) {
                        byte v = ((ByteDataBuffer) dbuf).get(i, j);
                        valueCounter[v - Byte.MIN_VALUE]++;
                    }
                }
            } else {
                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++) {
                        if (!dbuf.isMasked(i, j)) {
                            byte v = ((ByteDataBuffer) dbuf).get(i, j);
                            valueCounter[v - Byte.MIN_VALUE]++;
                        }
                    }
                }
            }
        }

        // calculate low cut
        int bottomMax = -1;
        int bottomMax2nd = -1;
        int cutN = 0;
        for (int i = 0; i < valueN; i++) {
            cutN += valueCounter[i];
            if (cutN >= cutoffMore) {
                bottomMax = i + Byte.MIN_VALUE;
                bottomMax2nd = bottomMax;
                break;
            } else if (cutN >= cutoffLess) {
                bottomMax2nd = i + Byte.MIN_VALUE;
                for (int j = i + 1; j < valueN; j++) {
                    if (valueCounter[j] > 0) {
                        bottomMax = j + Byte.MIN_VALUE;
                        break;
                    }
                }
                break;
            }
        }

        double lowCut = bottomMax * (1 - offsetrate) + bottomMax2nd * offsetrate;

        // calculate high cut
        int topMin = -Byte.MIN_VALUE;
        int topMin2nd = -Byte.MIN_VALUE;
        cutN = 0;
        for (int i = valueN - 1; i >= 0; i--) {
            cutN += valueCounter[i];
            if (cutN >= cutoffMore) {
                topMin = i + Byte.MIN_VALUE;
                topMin2nd = topMin;
                break;
            } else if (cutN >= cutoffLess) {
                topMin2nd = i + Byte.MIN_VALUE;
                for (int j = i - 1; j >= 0; j--) {
                    if (valueCounter[j] > 0) {
                        topMin = j + Byte.MIN_VALUE;
                        break;
                    }
                }
                break;
            }
        }
        double highCut = topMin * (1 - offsetrate) + topMin2nd * offsetrate;

        return new double[]{lowCut, highCut};
    }

    private static double[] calcShortLimits(ImageDataBuffer[] dbufArray, Dimension[] sizeArray, double cutoff) {

        // the cuts value is between cutoffMore-1 and cutoffMore
        double offsetrate = (int) cutoff + 1 - cutoff;
        int cutoffMore = (int) cutoff + 2;

        short[] lowCuts = new short[cutoffMore];
        short[] highCuts = new short[cutoffMore];

        // fill the cuts array
        int m = -1;
        int n = -1;
        int c = 0;
        for (int a = 0; a < dbufArray.length; a++) {
            ImageDataBuffer dbuf = dbufArray[a];
            int w = sizeArray[a].width;
            int h = sizeArray[a].height;

            if (!dbuf.hasMasks()) {
                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++) {
                        short v = dbuf.getShort(i, j);
                        lowCuts[c] = v;
                        highCuts[c] = v;
                        if (++c == cutoffMore) {
                            m = i;
                            n = j;
                            break;
                        }
                    }
                    if (n != -1) {
                        break;
                    }
                }
            } else {
                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++) {
                        if (!dbuf.isMasked(i, j)) {
                            short v = dbuf.getShort(i, j);
                            lowCuts[c] = v;
                            highCuts[c] = v;
                            if (++c == cutoffMore) {
                                m = i;
                                n = j;
                                break;
                            }
                        }
                    }
                    if (n != -1) {
                        break;
                    }
                }
            }
        }

        // build the cuts array
        ShortBottomNFinder bf = new ShortBottomNFinder(lowCuts);
        ShortTopNFinder tf = new ShortTopNFinder(highCuts);

        n++;
        for (int a = 0; a < dbufArray.length; a++) {
            ImageDataBuffer dbuf = dbufArray[a];
            int w = sizeArray[a].width;
            int h = sizeArray[a].height;

            if (!dbuf.hasMasks()) {
                for (int i = m; i < h; i++) {
                    for (int j = n; j < w; j++) {
                        short v = dbuf.getShort(i, j);
                        bf.check(v);
                        tf.check(v);
                    }
                    n = 0;
                }
            } else {
                for (int i = m; i < h; i++) {
                    for (int j = n; j < w; j++) {
                        if (!dbuf.isMasked(i, j)) {
                            short v = dbuf.getShort(i, j);
                            bf.check(v);
                            tf.check(v);
                        }
                    }
                    n = 0;
                }
            }
        }

        double lowCut = bf.getMax() * (1 - offsetrate) + bf.getMax2nd() * offsetrate;
        double highCut = tf.getMin() * (1 - offsetrate) + tf.getMin2nd() * offsetrate;

        return new double[]{lowCut, highCut};
    }

    private static double[] calcIntLimits(ImageDataBuffer[] dbufArray, Dimension[] sizeArray, double cutoff) {

        // the cuts value is between cutoffMore-1 and cutoffMore
        double offsetrate = (int) cutoff + 1 - cutoff;
        int cutoffMore = (int) cutoff + 2;

        int[] lowCuts = new int[cutoffMore];
        int[] highCuts = new int[cutoffMore];

        // fill the cuts array
        int m = -1;
        int n = -1;
        int c = 0;
        for (int a = 0; a < dbufArray.length; a++) {
            ImageDataBuffer dbuf = dbufArray[a];
            int w = sizeArray[a].width;
            int h = sizeArray[a].height;

            if (!dbuf.hasMasks()) {
                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++) {
                        int v = dbuf.getInt(i, j);
                        lowCuts[c] = v;
                        highCuts[c] = v;
                        if (++c == cutoffMore) {
                            m = i;
                            n = j;
                            break;
                        }
                    }
                    if (n != -1) {
                        break;
                    }
                }
            } else {
                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++) {
                        if (!dbuf.isMasked(i, j)) {
                            int v = dbuf.getInt(i, j);
                            lowCuts[c] = v;
                            highCuts[c] = v;
                            if (++c == cutoffMore) {
                                m = i;
                                n = j;
                                break;
                            }
                        }
                    }
                    if (n != -1) {
                        break;
                    }
                }
            }
        }

        // build the cuts array
        IntBottomNFinder bf = new IntBottomNFinder(lowCuts);
        IntTopNFinder tf = new IntTopNFinder(highCuts);

        n++;
        for (int a = 0; a < dbufArray.length; a++) {
            ImageDataBuffer dbuf = dbufArray[a];
            int w = sizeArray[a].width;
            int h = sizeArray[a].height;

            if (!dbuf.hasMasks()) {
                for (int i = m; i < h; i++) {
                    for (int j = n; j < w; j++) {
                        int v = dbuf.getInt(i, j);
                        bf.check(v);
                        tf.check(v);
                    }
                    n = 0;
                }
            } else {
                for (int i = m; i < h; i++) {
                    for (int j = n; j < w; j++) {
                        if (!dbuf.isMasked(i, j)) {
                            int v = dbuf.getInt(i, j);
                            bf.check(v);
                            tf.check(v);
                        }
                    }
                    n = 0;
                }
            }
        }

        double lowCut = bf.getMax() * (1 - offsetrate) + bf.getMax2nd() * offsetrate;
        double highCut = tf.getMin() * (1 - offsetrate) + tf.getMin2nd() * offsetrate;

        return new double[]{lowCut, highCut};
    }

    @SuppressWarnings("ConstantConditions")
    private static double[] calcFloatLimits(ImageDataBuffer[] dbufArray, Dimension[] sizeArray, double cutoff) {

        // the cuts value is between cutoffMore-1 and cutoffMore
        double offsetrate = (int) cutoff + 1 - cutoff;
        int cutoffMore = (int) cutoff + 2;

        float[] lowCuts = new float[cutoffMore];
        float[] highCuts = new float[cutoffMore];

        // fill the cuts array
        int m = -1;
        int n = -1;
        int c = 0;
        for (int a = 0; a < dbufArray.length; a++) {
            ImageDataBuffer dbuf = dbufArray[a];
            int w = sizeArray[a].width;
            int h = sizeArray[a].height;

            if (!dbuf.hasMasks()) {
                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++) {
                        float v = dbuf.getFloat(i, j);
                        if (v == v && v != Float.POSITIVE_INFINITY && v != Float.NEGATIVE_INFINITY) {
                            lowCuts[c] = v;
                            highCuts[c] = v;
                            if (++c == cutoffMore) {
                                m = i;
                                n = j;
                                break;
                            }
                        }
                    }
                    if (n != -1) {
                        break;
                    }
                }
            } else {
                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++) {
                        if (!dbuf.isMasked(i, j)) {
                            float v = dbuf.getFloat(i, j);
                            if (v == v && v != Float.POSITIVE_INFINITY && v != Float.NEGATIVE_INFINITY) {
                                lowCuts[c] = v;
                                highCuts[c] = v;
                                if (++c == cutoffMore) {
                                    m = i;
                                    n = j;
                                    break;
                                }
                            }
                        }
                    }
                    if (n != -1) {
                        break;
                    }
                }
            }
        }

        // build the cuts array
        FloatBottomNFinder bf = new FloatBottomNFinder(lowCuts);
        FloatTopNFinder tf = new FloatTopNFinder(highCuts);

        n++;
        for (int a = 0; a < dbufArray.length; a++) {
            ImageDataBuffer dbuf = dbufArray[a];
            int w = sizeArray[a].width;
            int h = sizeArray[a].height;

            if (!dbuf.hasMasks()) {
                for (int i = m; i < h; i++) {
                    for (int j = n; j < w; j++) {
                        float v = dbuf.getFloat(i, j);
                        if (v == v && v != Float.POSITIVE_INFINITY && v != Float.NEGATIVE_INFINITY) {
                            bf.check(v);
                            tf.check(v);
                        }
                    }
                    n = 0;
                }
            } else {
                for (int i = m; i < h; i++) {
                    for (int j = n; j < w; j++) {
                        if (!dbuf.isMasked(i, j)) {
                            float v = dbuf.getFloat(i, j);
                            if (v == v && v != Float.POSITIVE_INFINITY && v != Float.NEGATIVE_INFINITY) {
                                bf.check(v);
                                tf.check(v);
                            }
                        }
                    }
                    n = 0;
                }
            }
        }

        double lowCut = bf.getMax() * (1 - offsetrate) + bf.getMax2nd() * offsetrate;
        double highCut = tf.getMin() * (1 - offsetrate) + tf.getMin2nd() * offsetrate;

        return new double[]{lowCut, highCut};
    }

    private static double[] calcDoubleLimits(ImageDataBuffer[] dbufArray, Dimension[] sizeArray, double cutoff) {

        // the cuts value is between cutoffMore-1 and cutoffMore
        double offsetrate = (int) cutoff + 1 - cutoff;
        int cutoffMore = (int) cutoff + 2;

        double[] lowCuts = new double[cutoffMore];
        double[] highCuts = new double[cutoffMore];

        // fill the cuts array
        int m = -1;
        int n = -1;
        int c = 0;
        for (int a = 0; a < dbufArray.length; a++) {
            ImageDataBuffer dbuf = dbufArray[a];
            int w = sizeArray[a].width;
            int h = sizeArray[a].height;

            if (!dbuf.hasMasks()) {
                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++) {
                        double v = dbuf.getDouble(i, j);
                        if (v == v && v != Float.POSITIVE_INFINITY && v != Float.NEGATIVE_INFINITY) {
                            lowCuts[c] = v;
                            highCuts[c] = v;
                            if (++c == cutoffMore) {
                                m = i;
                                n = j;
                                break;
                            }
                        }
                    }
                    if (n != -1) {
                        break;
                    }
                }
            } else {
                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++) {
                        if (!dbuf.isMasked(i, j)) {
                            double v = dbuf.getDouble(i, j);
                            if (v == v && v != Float.POSITIVE_INFINITY && v != Float.NEGATIVE_INFINITY) {
                                lowCuts[c] = v;
                                highCuts[c] = v;
                                if (++c == cutoffMore) {
                                    m = i;
                                    n = j;
                                    break;
                                }
                            }
                        }
                    }
                    if (n != -1) {
                        break;
                    }
                }
            }
        }

        // build the cuts array
        DoubleBottomNFinder bf = new DoubleBottomNFinder(lowCuts);
        DoubleTopNFinder tf = new DoubleTopNFinder(highCuts);

        n++;
        for (int a = 0; a < dbufArray.length; a++) {
            ImageDataBuffer dbuf = dbufArray[a];
            int w = sizeArray[a].width;
            int h = sizeArray[a].height;

            if (!dbuf.hasMasks()) {
                for (int i = m; i < h; i++) {
                    for (int j = n; j < w; j++) {
                        double v = dbuf.getDouble(i, j);
                        if (v == v && v != Double.POSITIVE_INFINITY && v != Double.NEGATIVE_INFINITY) {
                            bf.check(v);
                            tf.check(v);
                        }
                    }
                    n = 0;
                }
            } else {
                for (int i = m; i < h; i++) {
                    for (int j = n; j < w; j++) {
                        if (!dbuf.isMasked(i, j)) {
                            double v = dbuf.getDouble(i, j);
                            if (v == v && v != Double.POSITIVE_INFINITY && v != Double.NEGATIVE_INFINITY) {
                                bf.check(v);
                                tf.check(v);
                            }
                        }
                    }
                    n = 0;
                }
            }
        }

        double lowCut = bf.getMax() * (1 - offsetrate) + bf.getMax2nd() * offsetrate;
        double highCut = tf.getMin() * (1 - offsetrate) + tf.getMin2nd() * offsetrate;

        return new double[]{lowCut, highCut};
    }

}
