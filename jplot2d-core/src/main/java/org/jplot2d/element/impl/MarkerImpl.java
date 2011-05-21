package org.jplot2d.element.impl;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class MarkerImpl extends ComponentImpl implements MarkerEx {

	private double locX, locY;

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

	public Rectangle2D getBounds() {
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
