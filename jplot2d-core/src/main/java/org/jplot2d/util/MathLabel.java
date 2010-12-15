/**
 * Copyright 2010 Jingjing Li.
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
package org.jplot2d.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.element.HAlign;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.element.VAlign;

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

	private MathElement _me;

	/**
	 * created even when _me==null
	 */
	private MathLabelXLines _mlc;

	private Font _font;

	private HAlign _halign;

	private VAlign _valign;

	/**
	 * The paper bounds relative to location
	 */
	private Rectangle2D bounds;

	public MathLabel(MathElement me, Font font) {
		this(me, font, VAlign.BOTTOM, HAlign.LEFT);
	}

	public MathLabel(MathElement me, Font font, VAlign valign, HAlign halign) {
		if (font == null) {
			throw new IllegalArgumentException("font cannot be null");
		}
		if (valign == null) {
			throw new IllegalArgumentException("valign cannot be null");
		}
		if (halign == null) {
			throw new IllegalArgumentException("halign cannot be null");
		}
		_font = font;
		_valign = valign;
		_halign = halign;
		_me = me;
		_mlc = new MathLabelXLines(_me, _font, _halign, _valign);

		_mlc.relayout(_font.getSize2D());
		Rectangle2D dbnds = _mlc.getBounds();
		bounds = new Rectangle2D.Double(dbnds.getX(),
				-(dbnds.getY() + dbnds.getHeight()), dbnds.getWidth(),
				dbnds.getHeight());
	}

	public void draw(Graphics2D g, PhysicalTransform pxf, Point2D loc,
			double angle, Color color) {
		if (g == null) {
			return;
		}

		/*
		 * construct an AffineTransform with NaN may cause drawString() run out
		 * of memory.
		 */
		AffineTransform af = AffineTransform.getTranslateInstance(
				pxf.getXPtoD(loc.getX()), pxf.getYPtoD(loc.getY()));
		af.scale(pxf.getScale(), pxf.getScale());
		af.rotate(-Math.PI * angle / 180.0);

		/* calculate device bounds */
		Rectangle2D bounds = _mlc.getBounds();
		GeneralPath gp = new GeneralPath(bounds);
		gp.transform(af);

		// g2.draw(_dbounds.getBounds());

		AffineTransform oldTransform = g.getTransform();
		RenderingHints oldRenderingHints = g.getRenderingHints();

		g.transform(af);
		g.setColor(color);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		// g2.drawRect(-1, -1, 2, 2);
		_mlc.draw(g);

		g.setRenderingHints(oldRenderingHints);
		/*
		 * workaround for "strokeState not update by setRenderingHints" (Sun Bug
		 * ID 6468831)
		 */
		g.setStroke(g.getStroke());
		g.setTransform(oldTransform);

	}

	public Font getFont() {
		return _font;
	}

	public HAlign getHAlign() {
		return _halign;
	}

	public VAlign getVAlign() {
		return _valign;
	}

	public MathElement getModel() {
		return _me;
	}

	/**
	 * Calculate the normal physical bounds of this label. The bounds is
	 * relative to its location point, original point is left-bottom. Normal
	 * scale is 1.
	 * 
	 * @return the normal physical bounds of this label.
	 */
	public Rectangle2D getBounds() {
		return bounds;
	}

}
