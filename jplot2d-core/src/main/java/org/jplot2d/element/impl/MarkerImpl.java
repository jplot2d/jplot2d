package org.jplot2d.element.impl;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;

import org.jplot2d.element.Plot;

public class MarkerImpl extends ComponentImpl implements MarkerEx {

	private double locX, locY;

	public InvokeStep getInvokeStepFormParent() {
		if (parent == null) {
			return null;
		}

		Method method;
		try {
			method = Plot.class.getMethod("getMarker");
		} catch (NoSuchMethodException e) {
			throw new Error(e);
		}
		return new InvokeStep(method);
	}

	public LayerEx getParent() {
		return (LayerEx) super.getParent();
	}

	public void thisEffectiveColorChanged() {
		// TODO Auto-generated method stub

	}

	public void thisEffectiveFontChanged() {
		// TODO Auto-generated method stub

	}

	public Dimension2D getSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub

	}

	public Point2D getLocation() {
		return new Point2D.Double(locX, locY);
	}

	public final void setLocation(Point2D p) {
		setLocation(p.getX(), p.getY());
	}

	public void setLocation(double locX, double locY) {
		if (getLocation().getX() != locX || getLocation().getY() != locY) {
			this.locX = locX;
			this.locY = locY;
		}
	}

}
