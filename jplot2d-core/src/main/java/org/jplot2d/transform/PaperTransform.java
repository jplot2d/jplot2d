/**
 * Copyright 2010-2011 Jingjing Li.
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
package org.jplot2d.transform;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Defines the conversion between paper units and device units. Every component has its own paper
 * coordinate system, and maintains a PaperTransform object.
 * <p>
 * The transform is defined by 3 parameters. The xoff is the paper distance between component left
 * bound to device left bound. The yoff is the paper distance between component bottom bound to
 * device top bound. The scale is the factor of device to paper.
 * 
 * @author Jingjing Li
 * 
 */
public class PaperTransform implements Cloneable {

	private final double xoff, yoff, scale;

	public PaperTransform(double xoff, double yoff, double scale) {
		this.xoff = xoff;
		this.yoff = yoff;
		this.scale = scale;
	}

	public PaperTransform(Point2D p, double scale) {
		this(p.getX(), p.getY(), scale);
	}

	public PaperTransform clone() {
		try {
			return (PaperTransform) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	public Point2D getOffset() {
		return new Point2D.Double(xoff, yoff);
	}

	public double getScale() {
		return scale;
	}

	/**
	 * Concatenates this transform with a translation transformation.
	 * 
	 * @param tx
	 *            the distance by which coordinates are translated in the X axis direction
	 * @param ty
	 *            the distance by which coordinates are translated in the Y axis direction
	 */
	public PaperTransform translate(double tx, double ty) {
		return new PaperTransform(xoff + tx, yoff - ty, scale);
	}

	public PaperTransform rotate(double theta) {
		return new RotatablePaperTransform(xoff, yoff, scale, theta);
	}

	public boolean equals(Object obj) {
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		PaperTransform b = (PaperTransform) obj;
		return xoff == b.xoff && yoff == b.yoff && scale == b.scale;
	}

	public int hashCode() {
		long bits = Double.doubleToLongBits(xoff);
		bits ^= Double.doubleToLongBits(yoff) * 31;
		bits ^= Double.doubleToLongBits(scale) * 47;
		return (((int) bits) ^ ((int) (bits >> 32)));
	}

	public double getXPtoD(double xp) {
		return (xp + xoff) * scale;
	}

	public double getYPtoD(double yp) {
		return (yoff - yp) * scale;
	}

	/**
	 * Transform device units to paper for the x direction.
	 * 
	 * @param xd
	 *            device x coordinate
	 * 
	 * @return paper x coordinate
	 */
	public double getXDtoP(double xd) {
		return xd / scale - xoff;
	}

	/**
	 * Transform device units to paper for the y direction.
	 * 
	 * @param yd
	 *            device y coordinate
	 * @return paper y coordinate
	 */
	public double getYDtoP(double yd) {
		return yoff - yd / scale;
	}

	public Point2D getPtoD(Point2D p) {
		return new Point2D.Double(getXPtoD(p.getX()), getYPtoD(p.getY()));
	}

	public Point2D getDtoP(Point2D d) {
		return new Point2D.Double(getXDtoP(d.getX()), getYDtoP(d.getY()));
	}

	/**
	 * Returns a shape in device coordinate system.
	 * 
	 * @param p
	 * @return
	 */
	public Shape getPtoD(Rectangle2D p) {
		double x = getXPtoD(p.getX());
		double y = getYPtoD(p.getMaxY());
		double w = p.getWidth() * scale;
		double h = p.getHeight() * scale;
		return new Rectangle2D.Double(x, y, w, h);
	}

	/**
	 * Returns an AffineTransform to transform shapes from PCS to DCS.
	 */
	public AffineTransform getTransform() {
		AffineTransform af = new AffineTransform();
		af.scale(scale, -scale);
		af.translate(xoff, -yoff);
		return af;
	}

	public String toString() {
		return "PaperTransform(" + xoff + "," + yoff + "," + scale + ")";
	}

}
