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
 * Defines the conversion between physical units and device units. Every component has its own
 * physical coordinate system, and maintains a PhysicalTransform object.
 * <p>
 * The transform is defined by 3 parameters. The xoff is the physical distance between component
 * left bound to device left bound. The yoff is the physical distance between component bottom bound
 * to device top bound. The scale is the factor of device to physical.
 * 
 * @author Jingjing Li
 * 
 */
public class RotatablePhysicalTransform extends PhysicalTransform {

	private final double theta;

	public RotatablePhysicalTransform(double xoff, double yoff, double scale, double theta) {
		super(xoff, yoff, scale);
		this.theta = theta;
	}

	public RotatablePhysicalTransform(double xoff, double yoff, double scale) {
		this(xoff, yoff, scale, 0);
	}

	public RotatablePhysicalTransform(Point2D p, double scale) {
		this(p.getX(), p.getY(), scale, 0);
	}

	public RotatablePhysicalTransform clone() {
		return (RotatablePhysicalTransform) super.clone();
	}

	public boolean equals(Object obj) {
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		RotatablePhysicalTransform b = (RotatablePhysicalTransform) obj;
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
