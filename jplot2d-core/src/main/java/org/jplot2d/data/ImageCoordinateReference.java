/**
 * Copyright 2010-2013 Jingjing Li.
 * <p/>
 * This file is part of jplot2d.
 * <p/>
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.data;

/**
 * Define the relationship between the image pixels and axis values, by reference pixel, reference value and pixel size.
 * By default, the reference pixel and reference value are 0, means the middle point of pixel 0 is on axis value 0.
 */
public class ImageCoordinateReference {

    protected double xRefVal = 0.0;

    protected double xRefPixel = 0.0;

    protected double xPixelSize = 1;

    protected double yRefVal = 0.0;

    protected double yRefPixel = 0.0;

    protected double yPixelSize = 1;

    public ImageCoordinateReference() {

    }

    public ImageCoordinateReference(ImageCoordinateReference cr) {
        this.xRefVal = cr.xRefVal;
        this.xRefPixel = cr.xRefPixel;
        this.xPixelSize = cr.xPixelSize;
        this.yRefVal = cr.yRefVal;
        this.yRefPixel = cr.yRefPixel;
        this.yPixelSize = cr.yPixelSize;
    }

    /**
     * Get the coordinate reference value of the X axis
     *
     * @return the coordinate reference value of the X axis
     */
    public double getXRefVal() {
        return xRefVal;
    }

    /**
     * Set the coordinate reference value of the X axis
     *
     * @param xRefVal the coordinate reference value of the X axis
     */
    public void setXRefVal(double xRefVal) {
        xRefVal = xRefVal;
    }

    /**
     * Get the coordinate reference value of the Y axis
     *
     * @return the coordinate reference value of the Y axis
     */
    public double getYRefVal() {
        return yRefVal;
    }

    /**
     * Set the coordinate reference value of the Y axis
     *
     * @param yRefVval the coordinate reference value of the Y axis
     */
    public void setYRefVal(double yRefVval) {
        yRefVal = yRefVval;
    }

    /**
     * Get the coordinate reference pixel in the X axis
     *
     * @return the coordinate reference pixel in the X axis
     */
    public double getXRefPixel() {
        return xRefPixel;
    }

    /**
     * Set the coordinate reference pixel in the X axis
     *
     * @param xRefPixel the coordinate reference pixel in the X axis
     */
    public void setXRefPixel(double xRefPixel) {
        xRefPixel = xRefPixel;
    }

    /**
     * Get the coordinate reference pixel in the Y axis
     *
     * @return the coordinate reference pixel in the Y axis
     */
    public double getYRefPixel() {
        return yRefPixel;
    }

    /**
     * Set the coordinate reference pixel in the Y axis
     *
     * @param yRefPixel the coordinate reference pixel in the Y axis
     */
    public void setYRefPixel(double yRefPixel) {
        yRefPixel = yRefPixel;
    }

    /**
     * Get the pixel size in the X direction
     *
     * @return the pixel size in the X direction
     */
    public double getXPixelSize() {
        return xPixelSize;
    }

    /**
     * Set the pixel size in the X direction
     *
     * @param xPixelSize the pixel size in the X direction
     */
    public void setXPixelSize(double xPixelSize) {
        this.xPixelSize = xPixelSize;
    }

    /**
     * Get the pixel size in the Y direction
     *
     * @return the pixel size in the Y direction
     */
    public double getYPixelSize() {
        return yPixelSize;
    }

    /**
     * Set the pixel size in the Y direction
     *
     * @param yPixelSize the pixel size in the Y direction
     */
    public void setYPixelSize(double yPixelSize) {
        this.yPixelSize = yPixelSize;
    }

}