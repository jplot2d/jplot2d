/**
 * Copyright 2010, 2011 Jingjing Li.
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
package org.jplot2d.element.impl;

import java.awt.geom.Rectangle2D;

/**
 * @author Jingjing Li
 * 
 */
public class TitleImpl extends TextComponentImpl implements TitleEx {

	private Position position = Position.TOPCENTER;

	private double gapFactor = 0.25;

	/**
	 * A cached bounds to meet the oldValue-calcSize-invalidate procedure in
	 * PlotImpl
	 */
	private Rectangle2D bounds = new Rectangle2D.Double();

	public String getSelfId() {
		if (getParent() != null) {
			return "Title" + getParent().indexOf(this);
		} else {
			return "Title@"
					+ Integer.toHexString(System.identityHashCode(this));
		}
	}

	public PlotEx getParent() {
		return (PlotEx) super.getParent();
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public double getGapFactor() {
		return gapFactor;
	}

	public void setGapFactor(double factor) {
		this.gapFactor = factor;
		if (canContribute()) {
			invalidatePlot();
		}
	}

	/**
	 * Invalidate the parent plot when its position is not null.
	 */
	private void invalidatePlot() {
		if (getParent() != null && position != null) {
			getParent().invalidate();
		}
	}

	public Rectangle2D getBounds() {
		return bounds;
	}

	public void calcSize() {
		bounds = super.getBounds();
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		TitleImpl tc = (TitleImpl) src;
		this.position = tc.position;
		this.gapFactor = tc.gapFactor;
	}

}
