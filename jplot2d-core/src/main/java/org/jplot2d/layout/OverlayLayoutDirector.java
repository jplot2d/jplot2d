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
package org.jplot2d.layout;

import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.util.Insets2D;

/**
 * This LayoutDirector overlay all subplots over the top of each other. The 1st
 * subplot is the base subplot, which fill all area of the subplot. All other
 * subplots are put inside the base subplot's content area. The bounds are
 * defined in {@link BoundsConstraint} .
 * 
 * 
 * @author Jingjing Li
 * 
 */
public class OverlayLayoutDirector extends SimpleLayoutDirector {

	public void layout(PlotEx subplot) {

		super.layout(subplot);

		// layout overlay subplots
		layoutSubplots(subplot);

	}

	/**
	 * layout overlay subplots except the base.
	 * 
	 * @param subplots
	 */
	private void layoutSubplots(PlotEx subplot) {
		if (subplot.getSubplots().length == 0) {
			return;
		}

		double baseX = subplot.getContentBounds().getX();
		double baseY = subplot.getContentBounds().getY();
		double baseW = subplot.getContentBounds().getWidth();
		double baseH = subplot.getContentBounds().getHeight();

		for (PlotEx sp : subplot.getSubplots()) {

			double spacingL = 0;
			double spacingR = 0;
			double spacingT = 0;
			double spacingB = 0;

			BoundsConstraint bc = (BoundsConstraint) getConstraint(sp);
			if (bc != null) {
				Insets2D fixed = bc.getFixedInsets();
				Insets2D elastic = bc.getElasticInsets();
				spacingL = fixed.getLeft() + elastic.getLeft() * baseW;
				spacingR = fixed.getRight() + elastic.getRight() * baseW;
				spacingT = fixed.getTop() + elastic.getTop() * baseH;
				spacingB = fixed.getBottom() + elastic.getBottom() * baseH;
			}

			double spbX = baseX + spacingL;
			double spbY = baseY + spacingB;
			double spbW = baseW - spacingL - spacingR;
			double spbH = baseH - spacingT - spacingB;

			// locate subplot
			sp.setLocation(spbX, spbY);
			sp.setSize(spbW, spbH);
		}

	}

}
