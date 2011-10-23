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

import static org.jplot2d.util.TestUtils.*;

import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.element.impl.PlotImpl;
import org.jplot2d.element.impl.PlotMarginEx;
import org.jplot2d.util.DoubleDimension2D;
import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class GridLayoutDirectorTest {

	@Test
	public void testLayout() {
		PlotEx sp00 = new PlotImpl();
		sp00.setPreferredContentSize(new DoubleDimension2D(640, 480));
		PlotMarginEx margin00 = sp00.getMargin();
		margin00.setAutoMarginTop(false);
		margin00.setAutoMarginLeft(false);
		margin00.setAutoMarginBottom(false);
		margin00.setAutoMarginRight(false);
		margin00.setMarginTop(4);
		margin00.setMarginLeft(4);
		margin00.setMarginBottom(0);
		margin00.setMarginRight(0);

		PlotEx sp10 = new PlotImpl();
		sp10.setPreferredContentSize(new DoubleDimension2D(320, 240));
		PlotMarginEx margin10 = sp10.getMargin();
		margin10.setAutoMarginTop(false);
		margin10.setAutoMarginLeft(false);
		margin10.setAutoMarginBottom(false);
		margin10.setAutoMarginRight(false);
		margin10.setMarginTop(5);
		margin10.setMarginLeft(0);
		margin10.setMarginBottom(0);
		margin10.setMarginRight(5);

		PlotEx sp01 = new PlotImpl();
		sp01.setPreferredContentSize(new DoubleDimension2D(320, 240));
		PlotMarginEx margin01 = sp01.getMargin();
		margin01.setAutoMarginTop(false);
		margin01.setAutoMarginLeft(false);
		margin01.setAutoMarginBottom(false);
		margin01.setAutoMarginRight(false);
		margin01.setMarginTop(0);
		margin01.setMarginLeft(6);
		margin01.setMarginBottom(6);
		margin01.setMarginRight(0);

		PlotEx sp11 = new PlotImpl();
		sp11.setPreferredContentSize(new DoubleDimension2D(320, 240));
		PlotMarginEx margin11 = sp11.getMargin();
		margin11.setAutoMarginTop(false);
		margin11.setAutoMarginLeft(false);
		margin11.setAutoMarginBottom(false);
		margin11.setAutoMarginRight(false);
		margin11.setMarginTop(0);
		margin11.setMarginLeft(0);
		margin11.setMarginBottom(7);
		margin11.setMarginRight(7);

		PlotEx plot = new PlotImpl();
		PlotMarginEx margin = plot.getMargin();
		margin.setAutoMarginTop(false);
		margin.setAutoMarginLeft(false);
		margin.setAutoMarginBottom(false);
		margin.setAutoMarginRight(false);
		margin.setMarginTop(10);
		margin.setMarginLeft(10);
		margin.setMarginBottom(10);
		margin.setMarginRight(10);
		LayoutDirector ld = new GridLayoutDirector();
		plot.setLayoutDirector(ld);
		plot.addSubplot(sp00, new GridConstraint(0, 0));
		plot.addSubplot(sp01, new GridConstraint(0, 1));
		plot.addSubplot(sp10, new GridConstraint(1, 0));
		plot.addSubplot(sp11, new GridConstraint(1, 1));

		checkDimension2D(ld.getPreferredContentSize(plot), 6 + 640 + 320 + 7, 5 + 480 + 240 + 7);
		checkDimension2D(ld.getPreferredSize(plot), 6 + 640 + 320 + 7 + 20, 5 + 480 + 240 + 7 + 20);

		plot.setSize(ld.getPreferredSize(plot));
		plot.validate();
		checkDouble(sp00.getMargin().getMarginLeft(), 4);
		checkDouble(sp00.getMargin().getMarginRight(), 0);
		checkDouble(sp00.getMargin().getMarginTop(), 4);
		checkDouble(sp00.getMargin().getMarginBottom(), 0);
		checkPoint2D(sp00.getLocation(), 6, 247);
		checkDimension2D(sp00.getContentConstrant(), 640, 480);
		checkDimension2D(sp00.getSize(), 4 + 640, 480 + 4);

		checkDouble(sp10.getMargin().getMarginLeft(), 0);
		checkDouble(sp10.getMargin().getMarginRight(), 5);
		checkDouble(sp10.getMargin().getMarginTop(), 5);
		checkDouble(sp10.getMargin().getMarginBottom(), 0);
		checkPoint2D(sp10.getLocation(), 646, 247);
		checkDimension2D(sp10.getContentConstrant(), 320, 480);
		checkDimension2D(sp10.getSize(), 320 + 5, 480 + 5);

		checkDouble(sp01.getMargin().getMarginLeft(), 6);
		checkDouble(sp01.getMargin().getMarginRight(), 0);
		checkDouble(sp01.getMargin().getMarginTop(), 0);
		checkDouble(sp01.getMargin().getMarginBottom(), 6);
		checkPoint2D(sp01.getLocation(), 6, 7);
		checkDimension2D(sp01.getContentConstrant(), 640, 240);
		checkDimension2D(sp01.getSize(), 6 + 640, 6 + 240);

		checkDouble(sp11.getMargin().getMarginLeft(), 0);
		checkDouble(sp11.getMargin().getMarginRight(), 7);
		checkDouble(sp11.getMargin().getMarginTop(), 0);
		checkDouble(sp11.getMargin().getMarginBottom(), 7);
		checkPoint2D(sp11.getLocation(), 646, 7);
		checkDimension2D(sp11.getContentConstrant(), 320, 240);
		checkDimension2D(sp11.getSize(), 320 + 7, 7 + 240);

		plot.setPreferredContentSize(new DoubleDimension2D(1213, 912));
		checkDimension2D(ld.getPreferredContentSize(plot), 1213, 912);
		plot.setSize(ld.getPreferredSize(plot));
		plot.validate();
		checkPoint2D(sp00.getLocation(), 6, 307);
		checkDimension2D(sp00.getSize(), 4 + 800, 600 + 4);
		checkPoint2D(sp10.getLocation(), 806, 307);
		checkDimension2D(sp10.getSize(), 400 + 5, 600 + 5);
		checkPoint2D(sp01.getLocation(), 6, 7);
		checkDimension2D(sp01.getSize(), 6 + 800, 6 + 300);
		checkPoint2D(sp11.getLocation(), 806, 7);
		checkDimension2D(sp11.getSize(), 400 + 7, 7 + 300);
	}

}
