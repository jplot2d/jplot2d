/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.jplot2d.element.HAlign;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.element.VAlign;
import org.jplot2d.util.MathElement;

/**
 * This class override all methods of its ancestor. It accept a TeX-like string
 * as its text to render a math. The underlayer component is
 * {@link MathLabelComp}.
 * <p>
 * Only support left to right writing direction, and subscript superscript.
 * 
 * @author Jingjing Li
 */
public class MathLabel {

	private static final long serialVersionUID = -2489165388989968416L;

	private static Point2D DEFAULT_LOC = new Point2D.Double(0, 0);

	private MathElement _me;

	/**
	 * created even when _me==null
	 */
	private MathLabelXLines _mlc;

	private Color _color;

	private Font _font;

	private double _scale = 72;

	private PhysicalTransform _pxf;

	private HAlign _halign;

	private VAlign _valign;

	private double _angle;

	/**
	 * physical location set by setLocationP
	 */
	private double _locx, _locy;

	/**
	 * the device _dbounds
	 */
	private volatile Rectangle2D _dbounds;

	protected PropertyChangeSupport _changes = new PropertyChangeSupport(this);

	/**
	 * The physical bounds relative to location
	 */
	private Rectangle2D.Double _physicalBounds;

	public MathLabel(Font font) {
		this(font, DEFAULT_LOC, VAlign.BOTTOM, HAlign.LEFT);
	}

	public MathLabel(MathElement me, Font font, Point2D loc) {
		this(font, loc, VAlign.BOTTOM, HAlign.LEFT);
		this.setModel(me);
	}

	public MathLabel(Font font, Point2D loc, VAlign valign, HAlign halign) {
		_font = font;
		_valign = valign;
		_halign = halign;
		_locx = loc.getX();
		_locy = loc.getY();
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		_changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		_changes.removePropertyChangeListener(l);
	}

	public PhysicalTransform getPhysicalTransform() {
		return _pxf;
	}

	public void setPhysicalTransform(PhysicalTransform pTrf) {
		_pxf = pTrf;
	}

	public void draw(Graphics g) {
		if (g == null) {
			return;
		}

		Graphics2D g2 = (Graphics2D) g;

		/*
		 * construct an AffineTransform with NaN may cause drawString() run out
		 * of memory.
		 */
		AffineTransform af = AffineTransform.getTranslateInstance(_pxf
				.getXPtoD(_locx), _pxf.getYPtoD(_locy));
		af.rotate(-Math.PI * _angle / 180.0);

		double scale = _pxf.getScale();
		if (Double.isNaN(scale) || Double.isInfinite(scale) || scale == 0) {
			throw new IllegalArgumentException("scale cannot be " + scale);
		}

		flush(scale);
		/* calculate device bounds */
		Rectangle2D bounds = _mlc.getBounds();
		GeneralPath gp = new GeneralPath(bounds);
		gp.transform(af);
		_dbounds = gp.getBounds2D();

		// g2.draw(_dbounds.getBounds());

		AffineTransform oldTransform = g2.getTransform();
		RenderingHints oldRenderingHints = g2.getRenderingHints();

		g2.transform(af);
		g2.setColor(_color);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		// g2.drawRect(-1, -1, 2, 2);
		_mlc.draw(g2);

		g2.setRenderingHints(oldRenderingHints);
		/*
		 * workaround for "strokeState not update by setRenderingHints" (Sun Bug
		 * ID 6468831)
		 */
		g2.setStroke(g2.getStroke());
		g2.setTransform(oldTransform);

	}

	/**
	 * Get the label bounds in device units. The returned bounds is only correct
	 * after this label is drawn.
	 */
	public Rectangle2D getBounds() {
		return (_dbounds == null) ? null : (Rectangle2D) _dbounds.clone();
	}

	// ================================================================

	public Point2D.Double getLocationP() {
		return new Point2D.Double(_locx, _locy);
	}

	/**
	 * Pan all underlayer component to new position. loc is the anchor point of
	 * this label.
	 */
	public void setLocationP(Point2D loc) {
		this.setLocationP(loc.getX(), loc.getY());
	}

	public void setLocationP(double x, double y) {
		Point2D oldLoc = getLocationP();
		if (_locx == x && _locy == y) {
			return;
		}
		_locx = x;
		_locy = y;
		_changes.firePropertyChange("locationP", oldLoc, getLocationP());
	}

	public Color getColor() {
		return _color;
	}

	public void setColor(Color color) {
		if (_color == color) {
			return;
		}
		if (_color != null && _color.equals(color)) {
			return;
		}
		Color oldColor = _color;
		_color = color;
		_changes.firePropertyChange("color", oldColor, color);
	}

	public Font getFont() {
		return _font;
	}

	public void setFont(Font font) {
		if (_font == null || !_font.equals(font)) {
			Font oldFont = _font;
			_font = font;

			_mlc = new MathLabelXLines(_me, font, _halign, _valign);
			_scale = 72;
			updatePhysicalBounds();

			_changes.firePropertyChange("font", oldFont, font);
		}
	}

	public HAlign getHAlign() {
		return _halign;
	}

	/**
	 * Only the most outer MathLabel should be called.
	 * 
	 * @param halign
	 */
	public void setHAlign(HAlign halign) {
		if (_halign != halign) {
			HAlign old = _halign;
			_halign = halign;
			_mlc.setHAlign(halign);
			updatePhysicalBounds();
			_changes.firePropertyChange("hAlign", old, halign);
		}
	}

	public VAlign getVAlign() {
		return _valign;
	}

	/**
	 * Only the most outer MathLabel should be called.
	 * 
	 * @param valign
	 */
	public void setVAlign(VAlign valign) {
		if (_valign != valign) {
			VAlign old = _valign;
			_valign = valign;
			_mlc.setVAlign(valign);
			updatePhysicalBounds();
			_changes.firePropertyChange("vAlign", old, valign);
		}
	}

	public void setAlign(VAlign valign, HAlign halign) {
		this.setHAlign(halign);
		this.setVAlign(valign);
	}

	public double getAngle() {
		return _angle;
	}

	public void setAngle(double angle) {
		if (_angle != angle) {
			double oldAngle = _angle;
			_angle = angle;
			_changes.firePropertyChange("angle", oldAngle, angle);
		}
	}

	public MathElement getModel() {
		return _me;
	}

	public void setModel(MathElement model) {
		MathElement oldMe = _me;

		_me = model;

		_mlc = new MathLabelXLines(_me, _font, _halign, _valign);
		_scale = 72;
		updatePhysicalBounds();

		_changes.firePropertyChange("model", oldMe, _me);
	}

	/**
	 * update the scale when before drawing.
	 */
	private void flush(double scale) {

		if (_scale != scale) {
			_scale = scale;
			_mlc.relayout((float) (_font.getSize2D() * _scale / 72));
		}

	}

	private void updatePhysicalBounds() {
		if (_scale != 72) {
			_scale = 72;
			_mlc.relayout(_font.getSize2D());
		}
		Rectangle2D bounds = _mlc.getBounds();
		_physicalBounds = new Rectangle2D.Double(bounds.getX() / 72, -(bounds
				.getY() + bounds.getHeight()) / 72, bounds.getWidth() / 72,
				bounds.getHeight() / 72);
	}

	/**
	 * Calculate the normal physical bounds of this label. The bounds is
	 * relative to its location point, original point is left-bottom. Normal
	 * scale is 72.
	 * 
	 * @return the normal physical bounds of this label.
	 */
	public Rectangle2D getBoundsP() {
		return _physicalBounds;
	}

}
