package org.jplot2d.data;

public class ImageCoordinateReference {

	protected double xRefVal = 0.0;

	protected double xRefPixel = 0.5;

	protected double xDelta = 1;

	protected double yRefVal = 0.0;

	protected double yRefPixel = 0.5;

	protected double yDelta = 1;

	public ImageCoordinateReference() {

	}

	public ImageCoordinateReference(ImageCoordinateReference cr) {
		this.xRefVal = cr.xRefVal;
		this.xRefPixel = cr.xRefPixel;
		this.xDelta = cr.xDelta;
		this.yRefVal = cr.yRefVal;
		this.yRefPixel = cr.yRefPixel;
		this.yDelta = cr.yDelta;
	}

	/**
	 * Get the coordinate reference value of the X axis
	 * 
	 * @return the coordinate reference value of the X axis
	 */
	public double getXcrval() {
		return xRefVal;
	}

	/**
	 * Set the coordinate reference value of the X axis
	 * 
	 * @param xcrval
	 *            the coordinate reference value of the X axis
	 */
	public void setXcrval(double xcrval) {
		xRefVal = xcrval;
	}

	/**
	 * Get the coordinate reference value of the Y axis
	 * 
	 * @return the coordinate reference value of the Y axis
	 */
	public double getYcrval() {
		return yRefVal;
	}

	/**
	 * Set the coordinate reference value of the Y axis
	 * 
	 * @param ycrval
	 *            the coordinate reference value of the Y axis
	 */
	public void setYcrval(double ycrval) {
		yRefVal = ycrval;
	}

	/**
	 * Get the coordinate reference pixel in the X axis
	 * 
	 * @return the coordinate reference pixel in the X axis
	 */
	public double getXcrpix() {
		return xRefPixel;
	}

	/**
	 * Set the coordinate reference pixel in the X axis
	 * 
	 * @param xcrpix
	 *            the coordinate reference pixel in the X axis
	 */
	public void setXcrpix(double xcrpix) {
		xRefPixel = xcrpix;
	}

	/**
	 * Get the coordinate reference pixel in the Y axis
	 * 
	 * @return the coordinate reference pixel in the Y axis
	 */
	public double getYcrpix() {
		return yRefPixel;
	}

	/**
	 * Set the coordinate reference pixel in the Y axis
	 * 
	 * @param ycrpix
	 *            the coordinate reference pixel in the Y axis
	 */
	public void setYcrpix(double ycrpix) {
		yRefPixel = ycrpix;
	}

	/**
	 * Get the pixel size in the X direction
	 * 
	 * @return the pixel size in the X direction
	 */
	public double getXcdelt() {
		return xDelta;
	}

	/**
	 * Set the pixel size in the X direction
	 * 
	 * @param xcdelt
	 *            the pixel size in the X direction
	 */
	public void setXcdelt(double xcdelt) {
		xDelta = xcdelt;
	}

	/**
	 * Get the pixel size in the Y direction
	 * 
	 * @return the pixel size in the Y direction
	 */
	public double getYcdelt() {
		return yDelta;
	}

	/**
	 * Set the pixel size in the Y direction
	 * 
	 * @param ycdelt
	 *            the pixel size in the Y direction
	 */
	public void setYcdelt(double ydelt) {
		yDelta = ydelt;
	}

}