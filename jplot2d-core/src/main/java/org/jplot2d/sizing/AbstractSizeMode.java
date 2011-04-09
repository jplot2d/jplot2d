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
package org.jplot2d.sizing;

import java.awt.geom.Dimension2D;

import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public abstract class AbstractSizeMode implements SizeMode {

	protected PlotEx plot;

	protected boolean autoPack = false;

	protected double width, height;

	protected double scale = 1;

	private PhysicalTransform pxf;

	public AbstractSizeMode() {

	}

	public PlotEx getPlot() {
		return plot;
	}

	public void setPlot(PlotEx plot) {
		this.plot = plot;
	}

	public boolean isAutoPack() {
		return autoPack;
	}

	public PhysicalTransform getPhysicalTransform() {
		if (pxf == null) {
			pxf = new PhysicalTransform(0.0, getSize().getHeight(), scale);
		}
		return pxf;
	}

	public Dimension2D getSize() {
		return new DoubleDimension2D(width, height);
	}

	protected void updatePxf() {
		pxf = null;
	}

}
