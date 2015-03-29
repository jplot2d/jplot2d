/**
 * Copyright 2010-2012 Jingjing Li.
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

import org.jplot2d.util.Range;

/**
 * Immutable. This class keep (x,y) image data and compute data feature such as max/min, NaN indexes.
 *
 * @author Jingjing Li
 */
public abstract class ImageData implements GraphData {

    private final int imgWidth, imgHeight;

    protected final Range xboundary;

    protected final Range yboundary;

    protected final ImageCoordinateReference coordref;

    private double xmin, xmax;

    private double ymin, ymax;

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
        xmin = coordref.xRefVal - (coordref.xRefPixel - 0.5) * coordref.xDelta;
        xmax = xmin + imgWidth * coordref.xDelta;
        ymin = coordref.yRefVal - (coordref.yRefPixel - 0.5) * coordref.yDelta;
        ymax = ymin + imgHeight * coordref.yDelta;

        if (!inXBoundary(xmin)) {
            xmin = xboundary.getMin();
        }
        if (!inXBoundary(xmax)) {
            xmax = xboundary.getMax();
        }
        if (!inYBoundary(ymin)) {
            ymin = yboundary.getMin();
        }
        if (!inXBoundary(ymax)) {
            ymax = yboundary.getMax();
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
        return false;
    }

    public boolean hasPointOutsideYBounds() {
        return false;
    }

    public ImageCoordinateReference getCoordinateReference() {
        return new ImageCoordinateReference(coordref);
    }

    public abstract ImageData applyCoordinateReference(ImageCoordinateReference cr);

    /**
     * Get the coordinate reference value of the X axis
     *
     * @return the coordinate reference value of the X axis
     */
    public double getXcrval() {
        return coordref.xRefVal;
    }

    /**
     * Get the coordinate reference value of the Y axis
     *
     * @return the coordinate reference value of the Y axis
     */
    public double getYcrval() {
        return coordref.yRefVal;
    }

    /**
     * Get the coordinate reference pixel in the X axis
     *
     * @return the coordinate reference pixel in the X axis
     */
    public double getXcrpix() {
        return coordref.xRefPixel;
    }

    /**
     * Get the coordinate reference pixel in the Y axis
     *
     * @return the coordinate reference pixel in the Y axis
     */
    public double getYcrpix() {
        return coordref.yRefPixel;
    }

    /**
     * Get the pixel size in the X direction
     *
     * @return the pixel size in the X direction
     */
    public double getXcdelt() {
        return coordref.xDelta;
    }

    /**
     * Get the pixel size in the Y direction
     *
     * @return the pixel size in the Y direction
     */
    public double getYcdelt() {
        return coordref.yDelta;
    }

}
