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

/**
 * @author Jingjing Li
 * 
 */
public class TitleImpl extends TextComponentImpl implements TitleEx {

	private Position position = Position.TOPCENTER;

	private double gapFactor = 0.25;

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

	public void thisEffectiveFontChanged() {
		super.thisEffectiveFontChanged();
		if (canContribute()) {
			invalidatePlot();
		}
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
	}

	/**
	 * Invalidate the parent plot when its position is not null.
	 */
	private void invalidatePlot() {
		if (getParent() != null && position != null) {
			getParent().invalidate();
		}
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		TitleImpl tc = (TitleImpl) src;
		this.position = tc.position;
		this.gapFactor = tc.gapFactor;
	}

}
