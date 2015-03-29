/**
 * Copyright 2010-2014 Jingjing Li.
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
package org.jplot2d.layout;

import org.jplot2d.util.SparseDoubleArray;

public class GridCellInsets {

    private final SparseDoubleArray topPadding;

    private final SparseDoubleArray leftPadding;

    private final SparseDoubleArray bottomPadding;

    private final SparseDoubleArray rightPadding;

    private final double sumWidth, sumHeight;

    /**
     * @param topPadding    the key is row id
     * @param leftPadding   the key is column id
     * @param bottomPadding the key is row id
     * @param rightPadding  the key is column id
     */
    public GridCellInsets(SparseDoubleArray topPadding, SparseDoubleArray leftPadding, SparseDoubleArray bottomPadding,
                          SparseDoubleArray rightPadding) {
        this.topPadding = topPadding;
        this.leftPadding = leftPadding;
        this.bottomPadding = bottomPadding;
        this.rightPadding = rightPadding;

        double sumXpad = 0;
        for (int i = 0; i < leftPadding.size(); i++) {
            double left = leftPadding.valueAt(i);
            sumXpad += left;
        }
        for (int i = 0; i < rightPadding.size(); i++) {
            double right = rightPadding.valueAt(i);
            sumXpad += right;
        }
        sumWidth = sumXpad;

        double sumYpad = 0;
        for (int i = 0; i < topPadding.size(); i++) {
            double top = topPadding.valueAt(i);
            sumYpad += top;
        }
        for (int i = 0; i < bottomPadding.size(); i++) {
            double bottom = bottomPadding.valueAt(i);
            sumYpad += bottom;
        }
        sumHeight = sumYpad;
    }

    public double getTop(int row) {
        return topPadding.get(row);
    }

    public double getLeft(int col) {
        return leftPadding.get(col);
    }

    public double getBottom(int row) {
        return bottomPadding.get(row);
    }

    public double getRight(int col) {
        return rightPadding.get(col);
    }

    public double getSumWidth() {
        return sumWidth;
    }

    public double getSumHeight() {
        return sumHeight;
    }

}