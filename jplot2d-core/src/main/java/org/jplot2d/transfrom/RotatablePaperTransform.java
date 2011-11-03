/**
 * 
 */
package org.jplot2d.transfrom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Defines the conversion between paper units and device units. Every component has its own paper
 * coordinate system, and maintains a PaperTransform object.
 * <p>
 * The transform is defined by 4 parameters. The xoff is the paper distance between component left
 * bound to device left bound. The yoff is the paper distance between component bottom bound to
 * device top bound. The scale is the factor of device to paper. The theta is the conter-clockwise
 * rotation angle in radians.
 * 
 * @author Jingjing Li
 * 
 */
public class RotatablePaperTransform extends PaperTransform {

	private final double theta;

	public RotatablePaperTransform(double xoff, double yoff, double scale, double theta) {
		super(xoff, yoff, scale);
		this.theta = theta;
	}

	public RotatablePaperTransform(double xoff, double yoff, double scale) {
		this(xoff, yoff, scale, 0);
	}

	public RotatablePaperTransform(Point2D p, double scale) {
		this(p.getX(), p.getY(), scale, 0);
	}

	public RotatablePaperTransform clone() {
		return (RotatablePaperTransform) super.clone();
	}

	public boolean equals(Object obj) {
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		RotatablePaperTransform b = (RotatablePaperTransform) obj;
		return super.equals(b) && theta == b.theta;
	}

	public double getXPtoD(double xp) {
		throw new UnsupportedOperationException();
	}

	public double getYPtoD(double yp) {
		throw new UnsupportedOperationException();
	}

	public double getXDtoP(double xd) {
		throw new UnsupportedOperationException();
	}

	public double getYDtoP(double yd) {
		throw new UnsupportedOperationException();
	}

	public Point2D getPtoD(Point2D p) {
		return getTransform().transform(p, null);
	}

	public Point2D getDtoP(Point2D d) {
		try {
			return getTransform().inverseTransform(d, null);
		} catch (NoninvertibleTransformException e) {
			return null;
		}
	}

	public Shape getPtoD(Rectangle2D rect) {
		return getTransform().createTransformedShape(rect);
	}

	/**
	 * Returns an AffineTransform to transform shapes from PCS to DCS.
	 */
	public AffineTransform getTransform() {
		AffineTransform af = super.getTransform();
		if (theta != 0) {
			af.rotate(theta);
		}
		return af;
	}

	public String toString() {
		return super.toString() + " theta" + theta;
	}

}
