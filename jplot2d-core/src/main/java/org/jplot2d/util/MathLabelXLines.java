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
package org.jplot2d.util;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.jplot2d.element.HAlign;
import org.jplot2d.element.VAlign;

/**
 * Composed of MathLabelComp, vertically group them. The base point of
 * MathLabelComp is string base point, and the location is relative to this
 * MathLabelXLines. Even there is only one MathLabelComp, XLines still need to
 * locate base point for the MathLabelComp.
 */
public class MathLabelXLines {

	/* device width of this component */
	private double _width, _height;

	private double _leftx, _topy;

	private HAlign _halign;

	private VAlign _valign;

	/** sub components list */
	private List<MathLabelComp> _subEL = new ArrayList<MathLabelComp>();

	/**
	 * @param me
	 *            can be <code>null</code>
	 * @param font
	 * @param halign
	 * @param valign
	 */
	protected MathLabelXLines(MathElement me, Font font, HAlign halign,
			VAlign valign) {
		_halign = halign;
		_valign = valign;

		if (me == null) {
			_width = 0;
			_height = 0;
			return;
		}

		if (me instanceof MathElement.XLines) {
			MathElement[] es = ((MathElement.XLines) me).getElements();
			_width = 0;
			double locy = 0;
			for (int i = 0; i < es.length; i++) {
				MathLabelComp ml = MathLabelComp.getBoxInstance(es[i], 0, 0,
						font, false);
				if (i > 0) {
					locy += ml.getLeading();
				}
				locy += ml.getAscent();
				ml.pan(0, locy);
				_subEL.add(ml);
				locy += ml.getDescent();

				if (_width < ml.getWidth()) {
					_width = ml.getWidth();
				}
			}
			_height = locy;
		} else {
			MathLabelComp mlc = MathLabelComp.getBoxInstance(me, 0, 0, font,
					false);
			mlc.pan(0, mlc.getAscent());
			_subEL.add(mlc);
			_width = mlc.getWidth();
			_height = mlc.getAscent() + mlc.getDescent();
		}

		relocate();
	}

	/**
	 * The initial layout make _topy 0, then pan them via relocate().
	 * 
	 * @param font
	 */
	protected void relayout(float fontSize) {
		_width = 0;

		double locy = 0;
		for (int i = 0; i < _subEL.size(); i++) {
			MathLabelComp ml = _subEL.get(i);
			ml.relayout(fontSize, 0, 0);
			if (i > 0) {
				locy += ml.getLeading();
			}
			locy += ml.getAscent();
			ml.pan(0, locy);
			locy += ml.getDescent();

			if (_width < ml.getWidth()) {
				_width = ml.getWidth();
			}
		}
		_height = locy;

		_topy = 0;
		relocate();
	}

	protected void pan(double xoff, double yoff) {
		if (xoff != 0 || yoff != 0) {
			for (MathLabelComp ml : _subEL) {
				ml.pan(xoff, yoff);
			}
		}
	}

	protected void draw(Graphics2D g2) {
		for (MathLabelComp ml : _subEL) {
			ml.draw(g2);
		}
	}

	/**
	 * Returns the bounds of this XLines. The x,y of the bounds is relative to
	 * its base point.
	 * 
	 * @return a Rectangle2D that is the bounds of this XLines
	 */
	protected Rectangle2D getBounds() {
		return new Rectangle2D.Double(_leftx, _topy, _width, _height);
	}

	protected void setHAlign(HAlign halign) {
		_halign = halign;
		relocate();
	}

	protected void setVAlign(VAlign valign) {
		_valign = valign;
		relocate();
	}

	private void relocate() {

		double xoff = 0, yoff = 0;
		for (MathLabelComp ml : _subEL) {
			switch (_halign) {
			case LEFT:
				xoff = -ml._x;
				break;
			case CENTER:
				xoff = -ml.getWidth() / 2 - ml._x;
				break;
			case RIGHT:
				xoff = -ml.getWidth() - ml._x;
				break;
			}
			ml.pan(xoff, 0);
		}
		switch (_halign) {
		case LEFT:
			_leftx = 0;
			break;
		case CENTER:
			_leftx = -_width / 2;
			break;
		case RIGHT:
			_leftx = -_width;
			break;
		}

		switch (_valign) {
		case TOP:
			yoff = -_topy;
			_topy = 0;
			break;
		case MIDDLE:
			yoff = -_height / 2 - _topy;
			_topy = -_height / 2;
			break;
		case BOTTOM:
			yoff = -_height - _topy;
			_topy = -_height;
			break;
		}
		for (MathLabelComp ml : _subEL) {
			ml.pan(0, yoff);
		}
	}

}