/*
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
		testLayout(0, 0, 1, 1);
		testLayout(10, 12, 1, 1);
		testLayout(10, 12, 2, 3);
	}

	private void testLayout(double hgap, double vgap, int rightIndex, int topIndex) {
		PlotEx sp2 = new PlotImpl();
		sp2.setPreferredContentSize(new DoubleDimension2D(640, 480));
		PlotMarginEx margin2 = sp2.getMargin();
		margin2.setAutoTop(false);
		margin2.setAutoLeft(false);
		margin2.setAutoBottom(false);
		margin2.setAutoRight(false);
		margin2.setTop(4);
		margin2.setLeft(4);
		margin2.setBottom(0);
		margin2.setRight(0);

		PlotEx sp1 = new PlotImpl();
		sp1.setPreferredContentSize(new DoubleDimension2D(320, 240));
		PlotMarginEx margin1 = sp1.getMargin();
		margin1.setAutoTop(false);
		margin1.setAutoLeft(false);
		margin1.setAutoBottom(false);
		margin1.setAutoRight(false);
		margin1.setTop(5);
		margin1.setLeft(0);
		margin1.setBottom(0);
		margin1.setRight(5);

		PlotEx sp3 = new PlotImpl();
		sp3.setPreferredContentSize(new DoubleDimension2D(320, 240));
		PlotMarginEx margin3 = sp3.getMargin();
		margin3.setAutoTop(false);
		margin3.setAutoLeft(false);
		margin3.setAutoBottom(false);
		margin3.setAutoRight(false);
		margin3.setTop(0);
		margin3.setLeft(6);
		margin3.setBottom(6);
		margin3.setRight(0);

		PlotEx sp4 = new PlotImpl();
		sp4.setPreferredContentSize(new DoubleDimension2D(320, 240));
		PlotMarginEx margin4 = sp4.getMargin();
		margin4.setAutoTop(false);
		margin4.setAutoLeft(false);
		margin4.setAutoBottom(false);
		margin4.setAutoRight(false);
		margin4.setTop(0);
		margin4.setLeft(0);
		margin4.setBottom(7);
		margin4.setRight(7);

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
		LayoutDirector ld = new GridLayoutDirector(hgap, vgap);
		plot.setLayoutDirector(ld);
		plot.addSubplot(sp2, new GridConstraint(0, topIndex));
		plot.addSubplot(sp3, new GridConstraint(0, 0));
		plot.addSubplot(sp1, new GridConstraint(rightIndex, topIndex));
		plot.addSubplot(sp4, new GridConstraint(rightIndex, 0));

		checkDimension2D(ld.getPreferredContentSize(plot), 6 + 640 + hgap + 320 + 7, 5 + 480 + vgap + 240 + 7);
		checkDimension2D(ld.getPreferredSize(plot), 6 + 640 + hgap + 320 + 7 + 20, 5 + 480 + vgap + 240 + 7 + 20);

		plot.setSize(ld.getPreferredSize(plot));
		plot.validate();
		checkDouble(sp2.getMargin().getLeft(), 4);
		checkDouble(sp2.getMargin().getRight(), 0);
		checkDouble(sp2.getMargin().getTop(), 4);
		checkDouble(sp2.getMargin().getBottom(), 0);
		checkPoint2D(sp2.getLocation(), 6, 247 + vgap);
		checkDimension2D(sp2.getContentConstraint(), 640, 480);
		checkDimension2D(sp2.getSize(), 4 + 640, 480 + 4);

		checkDouble(sp1.getMargin().getLeft(), 0);
		checkDouble(sp1.getMargin().getRight(), 5);
		checkDouble(sp1.getMargin().getTop(), 5);
		checkDouble(sp1.getMargin().getBottom(), 0);
		checkPoint2D(sp1.getLocation(), 646 + hgap, 247 + vgap);
		checkDimension2D(sp1.getContentConstraint(), 320, 480);
		checkDimension2D(sp1.getSize(), 320 + 5, 480 + 5);

		checkDouble(sp3.getMargin().getLeft(), 6);
		checkDouble(sp3.getMargin().getRight(), 0);
		checkDouble(sp3.getMargin().getTop(), 0);
		checkDouble(sp3.getMargin().getBottom(), 6);
		checkPoint2D(sp3.getLocation(), 6, 7);
		checkDimension2D(sp3.getContentConstraint(), 640, 240);
		checkDimension2D(sp3.getSize(), 6 + 640, 6 + 240);

		checkDouble(sp4.getMargin().getLeft(), 0);
		checkDouble(sp4.getMargin().getRight(), 7);
		checkDouble(sp4.getMargin().getTop(), 0);
		checkDouble(sp4.getMargin().getBottom(), 7);
		checkPoint2D(sp4.getLocation(), 646 + hgap, 7);
		checkDimension2D(sp4.getContentConstraint(), 320, 240);
		checkDimension2D(sp4.getSize(), 320 + 7, 7 + 240);

		plot.setPreferredContentSize(new DoubleDimension2D(1213 + hgap, 912 + vgap));
		checkDimension2D(ld.getPreferredContentSize(plot), 1213 + hgap, 912 + vgap);
		plot.setSize(ld.getPreferredSize(plot));
		plot.validate();
		checkPoint2D(sp2.getLocation(), 6, 307 + vgap);
		checkDimension2D(sp2.getSize(), 4 + 800, 600 + 4);
		checkPoint2D(sp1.getLocation(), 806 + hgap, 307 + vgap);
		checkDimension2D(sp1.getSize(), 400 + 5, 600 + 5);
		checkPoint2D(sp3.getLocation(), 6, 7);
		checkDimension2D(sp3.getSize(), 6 + 800, 6 + 300);
		checkPoint2D(sp4.getLocation(), 806 + hgap, 7);
		checkDimension2D(sp4.getSize(), 400 + 7, 7 + 300);
	}

}
