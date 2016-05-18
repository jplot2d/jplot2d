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
package org.jplot2d.data;

import org.jplot2d.util.Range;

/**
 * Immutable. This class keep (x,y) image data and compute data feature such as max/min, NaN indexes.
 *
 * @author Jingjing Li
 */
@SuppressWarnings("unused")
public abstract class ImageData implements GraphData {

    protected final Range xboundary;
    protected final Range yboundary;
    protected final ImageCoordinateReference coordref;
    private final int imgWidth, imgHeight;
    private double xmin, xmax;
    private double ymin, ymax;
    private boolean hasPointOutsideXBounds, hasPointOutsideYBounds;

    protected ImageData(int w, int h, ImageCoordinateReference cr, Range xboundary, Range yboundary) {
        this.imgWidth = w;
        this.imgHeight = h;
        this.coordref = new ImageCoordinateReference(cr);
        this.xboundary = xboundary;
        this.yboundary = yboundary;

        updateRanges();
    }

    public int getWidth() {
        return imgWidth;
    }

    public int getHeight() {
        return imgHeight;
    }

    protected void updateRanges() {
        xmin = coordref.getXRefVal() - (coordref.getXRefPixel() + 0.5) * coordref.getXPixelSize();
        xmax = xmin + imgWidth * coordref.getXPixelSize();
        ymin = coordref.getYRefVal() - (coordref.getYRefPixel() + 0.5) * coordref.getYPixelSize();
        ymax = ymin + imgHeight * coordref.getYPixelSize();

        if (!inXBoundary(xmin)) {
            hasPointOutsideXBounds = true;
            int step = (int) Math.ceil((xboundary.getMin() - xmin) / coordref.getXPixelSize());
            xmin = xmin + step * coordref.getXPixelSize();
        }
        if (!inXBoundary(xmax)) {
            hasPointOutsideXBounds = true;
            int step = (int) Math.floor((xboundary.getMax() - xmin) / coordref.getXPixelSize());
            xmax = xmin + step * coordref.getXPixelSize();
        }
        if (!inYBoundary(ymin)) {
            hasPointOutsideYBounds = true;
            int step = (int) Math.ceil((yboundary.getMin() - ymin) / coordref.getYPixelSize());
            ymin = ymin + step * coordref.getYPixelSize();
        }
        if (!inYBoundary(ymax)) {
            hasPointOutsideYBounds = true;
            int step = (int) Math.floor((yboundary.getMax() - ymin) / coordref.getYPixelSize());
            ymax = ymin + step * coordref.getYPixelSize();
        }
    }

    /**
     * Returns <code>true</code> if the given x is in the x boundary
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean inXBoundary(double x) {
        return (xboundary == null) || xboundary.contains(x);
    }

    /**
     * Returns <code>true</code> if the given y is in the y boundary
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean inYBoundary(double y) {
        return (yboundary == null) || yboundary.contains(y);
    }

    public Range getXRange() {
        return new Range.Double(xmin, xmax);
    }

    public Range getYRange() {
        return new Range.Double(ymin, ymax);
    }

    public boolean hasPointOutsideXBounds() {
        return hasPointOutsideXBounds;
    }

    public boolean hasPointOutsideYBounds() {
        return hasPointOutsideYBounds;
    }

    public ImageCoordinateReference getCoordinateReference() {
        return new ImageCoordinateReference(coordref);
    }

    public abstract ImageData applyCoordinateReference(ImageCoordinateReference cr);

}
