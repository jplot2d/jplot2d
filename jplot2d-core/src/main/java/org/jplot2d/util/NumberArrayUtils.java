/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * $Id: NumberArrayUtils.java,v 1.5 2010/08/27 07:35:22 jli Exp $
 */
package org.jplot2d.util;

import org.jplot2d.data.ArrayPair;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * This utilities offer some high performance routine for number array. Saving loop can improve
 * performance especially when the array is very large.
 *
 * @author Jingjing Li
 */
public class NumberArrayUtils {

    public static double[] range(int n) {
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = (double) i;
        }
        return x;
    }

    public static double[] copyAppend(double[] array, double[] append) {
        double[] result = new double[array.length + append.length];
        System.arraycopy(array, 0, result, 0, array.length);
        System.arraycopy(append, 0, result, array.length, append.length);
        return result;
    }

    /**
     * Double.NaN is ignored. If the array contains only Double.NaN, returns Double.NaN
     *
     * @param array the array of values
     * @return the min value
     */
    public static double min(double[] array) {
        double min = Double.NaN;
        int n = -1;
        for (int i = 0; i < array.length; i++) {
            if (!Double.isNaN(array[i])) {
                min = array[i];
                n = i;
                break;
            }
        }
        if (n == -1) {
            return Double.NaN;
        }
        for (int i = n + 1; i < array.length; i++) {
            if (min > array[i]) {
                min = array[i];
            }
        }
        return min;
    }

    /**
     * Double.NaN is ignored. If the array contains only Double.NaN, returns Double.NaN
     *
     * @param array the array of values
     * @return athe max value
     */
    public static double max(double[] array) {
        double max = Double.NaN;
        int n = -1;
        for (int i = 0; i < array.length; i++) {
            if (!Double.isNaN(array[i])) {
                max = array[i];
                n = i;
                break;
            }
        }
        if (n == -1) {
            return Double.NaN;
        }
        for (int i = n + 1; i < array.length; i++) {
            if (max < array[i]) {
                max = array[i];
            }
        }
        return max;
    }

    /**
     * Double.NaN is ignored. If the array contains only Double.NaN, returns { Double.NaN,
     * Double.NaN }.
     *
     * @param array the array of values
     * @return an array that contains min and max values
     */
    public static double[] minMax(double[] array) {
        double min = Double.NaN;
        double max = Double.NaN;
        /* find the 1st non-Nan idx and value */
        int n = -1;
        for (int i = 0; i < array.length; i++) {
            if (!Double.isNaN(array[i])) {
                min = array[i];
                max = array[i];
                n = i;
                break;
            }
        }
        if (n == -1) {
            return new double[]{Double.NaN, Double.NaN};
        }
        /* find min & max value */
        for (int i = n + 1; i < array.length; i++) {
            if (min > array[i]) {
                min = array[i];
            }
            if (max < array[i]) {
                max = array[i];
            }
        }
        return new double[]{min, max};
    }

    /**
     * @param array the array of values
     * @return the idx of min value.
     */
    public static int[] getIdxesOfValue(double[] array, double v) {
        int[] idxes = new int[4];
        int idxCount = 0;
        for (int i = 0; i < array.length; i++) {
            if (v == array[i]) {
                idxes[idxCount++] = i;
                if (idxCount == idxes.length) {
                    idxes = arrayCopy(idxes, idxes.length * 2);
                }
            }
        }
        return arrayCopy(idxes, idxCount);
    }

    /**
     * Returns the indexes of the min value of the given array. Double.NaN is ignored. If the array
     * contains only Double.NaN, returns int[0].
     *
     * @param array the array of values
     * @return the indexes.
     */
    public static int[] getIdxesOfMinValue(double[] array) {
        int[] idxes = new int[4];

        double min = Double.NaN;
        int n = -1;
        for (int i = 0; i < array.length; i++) {
            if (!Double.isNaN(array[i])) {
                min = array[i];
                n = i;
                break;
            }
        }
        if (n == -1) {
            return new int[0];
        }
        /* now n is 1st non-NaN index, min is the value */

        idxes[0] = n;
        int idxCount = 1;
        for (int i = n + 1; i < array.length; i++) {
            if (min > array[i]) {
                /* find new min, reset idxcount */
                min = array[i];
                idxes[0] = i;
                idxCount = 1;
            } else if (min == array[i]) {
                idxes[idxCount++] = i;
                if (idxCount == idxes.length) {
                    idxes = arrayCopy(idxes, idxes.length * 2);
                }
            }
        }
        return arrayCopy(idxes, idxCount);
    }

    /**
     * Copy an array into a new array with the given length.
     *
     * @param array  the array to be copied
     * @param length the number of array elements to be copied
     * @return a new array with the given length.
     */
    public static int[] arrayCopy(int[] array, int length) {
        int[] result = new int[length];
        length = Math.min(array.length, length);
        System.arraycopy(array, 0, result, 0, length);
        return result;
    }

    /**
     * Copy an array into a new array with the given length.
     *
     * @param array  the array to be copied
     * @param length the number of array elements to be copied
     * @return a new array with the given length.
     */
    public static double[] arrayCopy(double[] array, int length) {
        double[] result = new double[length];
        length = Math.min(array.length, length);
        System.arraycopy(array, 0, result, 0, length);
        return result;
    }

    public static double[] hypot(double[] x, double[] y) {
        int length = Math.min(x.length, y.length);
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = Math.hypot(x[i], y[i]);
        }
        return result;
    }

    /**
     * Returns the distance of the every point in ArrayPair to the base point given in bx and by.
     *
     * @param xy the points
     * @param bx the x value of the base point
     * @param by the y value of the base point
     * @return a array of distance values
     */
    public static double[] hypot(ArrayPair xy, double bx, double by) {
        int length = xy.size();
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = Math.hypot(xy.getPDouble(i) - bx, xy.getQDouble(i) - by);
        }
        return result;
    }

    public static double[] multiply(double[] v, double factor) {
        double[] r = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            r[i] = v[i] * factor;
        }
        return r;
    }

    public static float[] multiply(float[] v, double factor) {
        float[] r = new float[v.length];
        for (int i = 0; i < v.length; i++) {
            r[i] = (float) (v[i] * factor);
        }
        return r;
    }

    public static int[] getIdxesOfValueBetween(ArrayPair xy, double xLo, double xHi, double yLo,
                                               double yHi) {
        int length = xy.size();
        int[] idxes = new int[4];
        int idxCount = 0;
        for (int i = 0; i < length; i++) {
            double x = xy.getPDouble(i);
            double y = xy.getQDouble(i);
            if (xLo <= x && x <= xHi && yLo <= y && y <= yHi) {
                idxes[idxCount++] = i;
                if (idxCount == idxes.length) {
                    idxes = arrayCopy(idxes, idxes.length * 2);
                }
            }
        }
        return arrayCopy(idxes, idxCount);
    }

    public static long[] reverse(long array[]) {
        long[] result = new long[array.length];
        for (int x = 0, y = array.length - 1; x < array.length; x++, y--) {
            result[y] = array[x];
        }
        return result;
    }

    public static double[] reverse(double array[]) {
        double[] result = new double[array.length];
        for (int x = 0, y = array.length - 1; x < array.length; x++, y--) {
            result[y] = array[x];
        }
        return result;
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    public static Object subArray(Object array, int[] indexes) {
        Class<?> ct = array.getClass().getComponentType();
        int len = indexes.length;
        Object result = Array.newInstance(ct, len);
        int j = 0;
        for (int i : indexes) {
            System.arraycopy(array, i, result, j++, 1);
        }
        return result;
    }

    /**
     * Returns a string comma delimited without brackets.
     *
     * @param array the array of values
     * @return a string
     */
    public static String toString(Object array) {
        if (array instanceof double[]) {
            return toString((double[]) array);
        }
        if (array instanceof float[]) {
            return toString((float[]) array);
        }
        if (array instanceof long[]) {
            return toString((long[]) array);
        }
        if (array instanceof int[]) {
            return toString((int[]) array);
        }
        if (array instanceof short[]) {
            return toString((short[]) array);
        }
        if (array instanceof byte[]) {
            return toString((byte[]) array);
        }
        if (array instanceof boolean[]) {
            return toString((boolean[]) array);
        }

        throw new IllegalArgumentException();
    }

    public static String toString(double[] array) {
        StringBuilder b = new StringBuilder();
        if (array.length > 0) {
            b.append(NumberUtils.toString(array[0]));
        }
        for (int i = 1; i < array.length; i++)
            b.append(',').append(NumberUtils.toString(array[i]));
        return b.toString();
    }

    public static String toString(float[] array) {
        StringBuilder b = new StringBuilder();
        if (array.length > 0) {
            b.append(NumberUtils.toString(array[0]));
        }
        for (int i = 1; i < array.length; i++)
            b.append(',').append(NumberUtils.toString(array[i]));
        return b.toString();
    }

    public static String toString(long[] array) {
        StringBuilder b = new StringBuilder();
        if (array.length > 0) {
            b.append(array[0]);
        }
        for (int i = 1; i < array.length; i++)
            b.append(',').append(array[i]);
        return b.toString();
    }

    public static String toString(int[] array) {
        StringBuilder b = new StringBuilder();
        if (array.length > 0) {
            b.append(array[0]);
        }
        for (int i = 1; i < array.length; i++)
            b.append(',').append(array[i]);
        return b.toString();
    }

    public static String toString(short[] array) {
        StringBuilder b = new StringBuilder();
        if (array.length > 0) {
            b.append(array[0]);
        }
        for (int i = 1; i < array.length; i++)
            b.append(',').append(array[i]);
        return b.toString();
    }

    public static String toString(byte[] array) {
        StringBuilder b = new StringBuilder();
        if (array.length > 0) {
            b.append(array[0]);
        }
        for (int i = 1; i < array.length; i++)
            b.append(',').append(array[i]);
        return b.toString();
    }

    public static String toString(boolean[] array) {
        StringBuilder b = new StringBuilder();
        if (array.length > 0) {
            b.append(array[0]);
        }
        for (int i = 1; i < array.length; i++)
            b.append(',').append(array[i]);
        return b.toString();
    }

    @SuppressWarnings({"SimplifiableIfStatement", "BooleanMethodIsAlwaysInverted"})
    public static boolean equals(Object a, Object b) {
        if (a instanceof double[] && b instanceof double[]) {
            return Arrays.equals((double[]) a, (double[]) b);
        }
        if (a instanceof float[] && b instanceof float[]) {
            return Arrays.equals((float[]) a, (float[]) b);
        }
        if (a instanceof long[] && b instanceof long[]) {
            return Arrays.equals((long[]) a, (long[]) b);
        }
        if (a instanceof int[] && b instanceof int[]) {
            return Arrays.equals((int[]) a, (int[]) b);
        }
        if (a instanceof short[] && b instanceof short[]) {
            return Arrays.equals((short[]) a, (short[]) b);
        }
        if (a instanceof byte[] && b instanceof byte[]) {
            return Arrays.equals((byte[]) a, (byte[]) b);
        }
        if (a instanceof boolean[] && b instanceof boolean[]) {
            return Arrays.equals((boolean[]) a, (boolean[]) b);
        }
        return false;
    }

    public static Object clone(Object a) {
        if (a instanceof double[]) {
            return ((double[]) a).clone();
        }
        if (a instanceof float[]) {
            return ((float[]) a).clone();
        }
        if (a instanceof long[]) {
            return ((long[]) a).clone();
        }
        if (a instanceof int[]) {
            return ((int[]) a).clone();
        }
        if (a instanceof short[]) {
            return ((short[]) a).clone();
        }
        if (a instanceof byte[]) {
            return ((byte[]) a).clone();
        }
        if (a instanceof boolean[]) {
            return ((boolean[]) a).clone();
        }
        throw new IllegalArgumentException("The argument is not a number array.");
    }

}
