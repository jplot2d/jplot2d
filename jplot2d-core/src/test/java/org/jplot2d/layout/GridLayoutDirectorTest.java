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
		PlotEx sp2 = new PlotImpl();
		sp2.setPreferredContentSize(new DoubleDimension2D(640, 480));
		PlotMarginEx margin00 = sp2.getMargin();
		margin00.setAutoTop(false);
		margin00.setAutoLeft(false);
		margin00.setAutoBottom(false);
		margin00.setAutoRight(false);
		margin00.setTop(4);
		margin00.setLeft(4);
		margin00.setBottom(0);
		margin00.setRight(0);

		PlotEx sp1 = new PlotImpl();
		sp1.setPreferredContentSize(new DoubleDimension2D(320, 240));
		PlotMarginEx margin10 = sp1.getMargin();
		margin10.setAutoTop(false);
		margin10.setAutoLeft(false);
		margin10.setAutoBottom(false);
		margin10.setAutoRight(false);
		margin10.setTop(5);
		margin10.setLeft(0);
		margin10.setBottom(0);
		margin10.setRight(5);

		PlotEx sp3 = new PlotImpl();
		sp3.setPreferredContentSize(new DoubleDimension2D(320, 240));
		PlotMarginEx margin01 = sp3.getMargin();
		margin01.setAutoTop(false);
		margin01.setAutoLeft(false);
		margin01.setAutoBottom(false);
		margin01.setAutoRight(false);
		margin01.setTop(0);
		margin01.setLeft(6);
		margin01.setBottom(6);
		margin01.setRight(0);

		PlotEx sp4 = new PlotImpl();
		sp4.setPreferredContentSize(new DoubleDimension2D(320, 240));
		PlotMarginEx margin11 = sp4.getMargin();
		margin11.setAutoTop(false);
		margin11.setAutoLeft(false);
		margin11.setAutoBottom(false);
		margin11.setAutoRight(false);
		margin11.setTop(0);
		margin11.setLeft(0);
		margin11.setBottom(7);
		margin11.setRight(7);

		PlotEx plot = new PlotImpl();
		PlotMarginEx margin = plot.getMargin();
		margin.setAutoTop(false);
		margin.setAutoLeft(false);
		margin.setAutoBottom(false);
		margin.setAutoRight(false);
		margin.setTop(10);
		margin.setLeft(10);
		margin.setBottom(10);
		margin.setRight(10);
		LayoutDirector ld = new GridLayoutDirector();
		plot.setLayoutDirector(ld);
		plot.addSubplot(sp2, new GridConstraint(0, 1));
		plot.addSubplot(sp3, new GridConstraint(0, 0));
		plot.addSubplot(sp1, new GridConstraint(1, 1));
		plot.addSubplot(sp4, new GridConstraint(1, 0));

		checkDimension2D(ld.getPreferredContentSize(plot), 6 + 640 + 320 + 7, 5 + 480 + 240 + 7);
		checkDimension2D(ld.getPreferredSize(plot), 6 + 640 + 320 + 7 + 20, 5 + 480 + 240 + 7 + 20);

		plot.setSize(ld.getPreferredSize(plot));
		plot.validate();
		checkDouble(sp2.getMargin().getLeft(), 4);
		checkDouble(sp2.getMargin().getRight(), 0);
		checkDouble(sp2.getMargin().getTop(), 4);
		checkDouble(sp2.getMargin().getBottom(), 0);
		checkPoint2D(sp2.getLocation(), 6, 247);
		checkDimension2D(sp2.getContentConstrant(), 640, 480);
		checkDimension2D(sp2.getSize(), 4 + 640, 480 + 4);

		checkDouble(sp1.getMargin().getLeft(), 0);
		checkDouble(sp1.getMargin().getRight(), 5);
		checkDouble(sp1.getMargin().getTop(), 5);
		checkDouble(sp1.getMargin().getBottom(), 0);
		checkPoint2D(sp1.getLocation(), 646, 247);
		checkDimension2D(sp1.getContentConstrant(), 320, 480);
		checkDimension2D(sp1.getSize(), 320 + 5, 480 + 5);

		checkDouble(sp3.getMargin().getLeft(), 6);
		checkDouble(sp3.getMargin().getRight(), 0);
		checkDouble(sp3.getMargin().getTop(), 0);
		checkDouble(sp3.getMargin().getBottom(), 6);
		checkPoint2D(sp3.getLocation(), 6, 7);
		checkDimension2D(sp3.getContentConstrant(), 640, 240);
		checkDimension2D(sp3.getSize(), 6 + 640, 6 + 240);

		checkDouble(sp4.getMargin().getLeft(), 0);
		checkDouble(sp4.getMargin().getRight(), 7);
		checkDouble(sp4.getMargin().getTop(), 0);
		checkDouble(sp4.getMargin().getBottom(), 7);
		checkPoint2D(sp4.getLocation(), 646, 7);
		checkDimension2D(sp4.getContentConstrant(), 320, 240);
		checkDimension2D(sp4.getSize(), 320 + 7, 7 + 240);

		plot.setPreferredContentSize(new DoubleDimension2D(1213, 912));
		checkDimension2D(ld.getPreferredContentSize(plot), 1213, 912);
		plot.setSize(ld.getPreferredSize(plot));
		plot.validate();
		checkPoint2D(sp2.getLocation(), 6, 307);
		checkDimension2D(sp2.getSize(), 4 + 800, 600 + 4);
		checkPoint2D(sp1.getLocation(), 806, 307);
		checkDimension2D(sp1.getSize(), 400 + 5, 600 + 5);
		checkPoint2D(sp3.getLocation(), 6, 7);
		checkDimension2D(sp3.getSize(), 6 + 800, 6 + 300);
		checkPoint2D(sp4.getLocation(), 806, 7);
		checkDimension2D(sp4.getSize(), 400 + 7, 7 + 300);
	}

}
