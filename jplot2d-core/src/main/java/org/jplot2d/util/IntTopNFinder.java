/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2012 Herschel Science Ground Segment Consortium
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
package org.jplot2d.util;

/**
 * find top N numbers among a number set.
 * 
 * @author Jingjing Li
 */
public class IntTopNFinder {

    private int[] a;

    public IntTopNFinder(int[] array) {
        this.a = array;
        if (a.length == 0) {
            throw new IllegalArgumentException("The array cannot be empty");
        }
        heapify();
    }

    public void check(int v) {
        if (v > a[0]) {
            a[0] = v;
            siftDown(0);
        }
    }

    public int getMin() {
        return a[0];
    }

    public int getMin2nd() {
        if (a.length == 1) {
            return a[0];
        } else if (a.length == 2) {
            return a[1];
        } else {
            return Math.min(a[1], a[2]);
        }
    }

    /**
     * build a min heap
     * 
     */
    private void heapify() {
        for (int i = (a.length - 2) / 2; i >= 0; i--) {
            siftDown(i);
        }
    }

    /**
     * @param s
     *            the heap top
     */
    private void siftDown(int s) {

        int root = s;

        while (root * 2 < a.length - 1) {

            int l = 2 * root + 1;
            int r = l + 1;

            int swap = root; // the child to swap with

            if (a[swap] > a[l]) {
                swap = l;
            }
            if (r < a.length && a[swap] > a[r]) {
                swap = r;
            }
            if (swap != root) {
                swap(root, swap);
                root = swap;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

}
