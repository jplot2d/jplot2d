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
import org.jplot2d.element.impl.SubplotMarginEx;
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
		SubplotMarginEx margin00 = sp00.getMargin();
		margin00.setAutoMarginTop(false);
		margin00.setAutoMarginLeft(false);
		margin00.setAutoMarginBottom(false);
		margin00.setAutoMarginRight(false);
		margin00.setMarginTop(4);
		margin00.setMarginLeft(4);
		margin00.setMarginBottom(0);
		margin00.setMarginRight(0);

		PlotEx sp01 = new PlotImpl();
		sp01.setPreferredContentSize(new DoubleDimension2D(320, 240));
		SubplotMarginEx margin01 = sp01.getMargin();
		margin01.setAutoMarginTop(false);
		margin01.setAutoMarginLeft(false);
		margin01.setAutoMarginBottom(false);
		margin01.setAutoMarginRight(false);
		margin01.setMarginTop(0);
		margin01.setMarginLeft(6);
		margin01.setMarginBottom(6);
		margin01.setMarginRight(0);

		PlotEx sp10 = new PlotImpl();
		sp10.setPreferredContentSize(new DoubleDimension2D(320, 240));
		SubplotMarginEx margin10 = sp10.getMargin();
		margin10.setAutoMarginTop(false);
		margin10.setAutoMarginLeft(false);
		margin10.setAutoMarginBottom(false);
		margin10.setAutoMarginRight(false);
		margin10.setMarginTop(5);
		margin10.setMarginLeft(0);
		margin10.setMarginBottom(0);
		margin10.setMarginRight(5);

		PlotEx sp11 = new PlotImpl();
		sp11.setPreferredContentSize(new DoubleDimension2D(320, 240));
		SubplotMarginEx margin11 = sp11.getMargin();
		margin11.setAutoMarginTop(false);
		margin11.setAutoMarginLeft(false);
		margin11.setAutoMarginBottom(false);
		margin11.setAutoMarginRight(false);
		margin11.setMarginTop(0);
		margin11.setMarginLeft(0);
		margin11.setMarginBottom(7);
		margin11.setMarginRight(7);

		PlotEx sp = new PlotImpl();
		SubplotMarginEx margin = sp.getMargin();
		margin.setAutoMarginTop(false);
		margin.setAutoMarginLeft(false);
		margin.setAutoMarginBottom(false);
		margin.setAutoMarginRight(false);
		margin.setMarginTop(10);
		margin.setMarginLeft(10);
		margin.setMarginBottom(10);
		margin.setMarginRight(10);
		LayoutDirector ld = new GridLayoutDirector();
		sp.setLayoutDirector(ld);
		sp.addSubplot(sp00, new GridConstraint(0, 0));
		sp.addSubplot(sp01, new GridConstraint(0, 1));
		sp.addSubplot(sp10, new GridConstraint(1, 0));
		sp.addSubplot(sp11, new GridConstraint(1, 1));

		checkDimension2D(ld.getPreferredContentSize(sp), 6 + 640 + 320 + 7,
				5 + 480 + 240 + 7);
		checkDimension2D(ld.getPreferredSize(sp), 6 + 640 + 320 + 7 + 20, 5
				+ 480 + 240 + 7 + 20);

		sp.setSize(ld.getPreferredSize(sp));
		sp.validate();
		checkDimension2D(sp00.getSize(), 6 + 640, 480 + 5);
		checkDimension2D(sp10.getSize(), 320 + 7, 480 + 5);
		checkDimension2D(sp01.getSize(), 6 + 640, 7 + 240);
		checkDimension2D(sp11.getSize(), 320 + 7, 7 + 240);
		checkRectangle2D(sp00.getContentConstrant(), 6, 0, 640, 480);
		checkRectangle2D(sp10.getContentConstrant(), 0, 0, 320, 480);
		checkRectangle2D(sp01.getContentConstrant(), 6, 7, 640, 240);
		checkRectangle2D(sp11.getContentConstrant(), 0, 7, 320, 240);

		sp.setPreferredContentSize(new DoubleDimension2D(1213, 912));
		checkDimension2D(ld.getPreferredContentSize(sp), 1213, 912);
		sp.setSize(ld.getPreferredSize(sp));
		sp.validate();
		checkDimension2D(sp00.getSize(), 6 + 800, 600 + 5);
		checkDimension2D(sp10.getSize(), 400 + 7, 600 + 5);
		checkDimension2D(sp01.getSize(), 6 + 800, 7 + 300);
		checkDimension2D(sp11.getSize(), 400 + 7, 7 + 300);
		checkRectangle2D(sp00.getContentConstrant(), 6, 0, 800, 600);
		checkRectangle2D(sp10.getContentConstrant(), 0, 0, 400, 600);
		checkRectangle2D(sp01.getContentConstrant(), 6, 7, 800, 300);
		checkRectangle2D(sp11.getContentConstrant(), 0, 7, 400, 300);

	}

}
