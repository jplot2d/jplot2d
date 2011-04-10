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
import static org.mockito.Mockito.*;

import org.jplot2d.element.AxisPosition;
import org.jplot2d.element.impl.AxisEx;
import org.jplot2d.element.impl.AxisLockGroupEx;
import org.jplot2d.element.impl.AxisRangeManagerEx;
import org.jplot2d.element.impl.LegendEx;
import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.element.impl.PlotImpl;
import org.jplot2d.element.impl.TitleEx;
import org.jplot2d.layout.SimpleLayoutDirector.AxesInSubplot;
import org.jplot2d.util.DoubleDimension2D;
import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class SimpleLayoutDirectorTest {

	@Test
	public void testCalcMargin() {
		LegendEx legend = mock(LegendEx.class);
		PlotEx subplot = new PlotImpl(legend) {
		};
		AxesInSubplot ais = new AxesInSubplot();
		checkDouble(SimpleLayoutDirector.calcLeftMargin(subplot, ais), 0);
		checkDouble(SimpleLayoutDirector.calcRightMargin(subplot, ais), 0);
		checkDouble(SimpleLayoutDirector.calcTopMargin(subplot, ais), 0);
		checkDouble(SimpleLayoutDirector.calcBottomMargin(subplot, ais), 0);

		// add axes

		AxisEx left = mock(AxisEx.class);
		when(left.getAsc()).thenReturn(8.0);
		when(left.getDesc()).thenReturn(2.0);

		AxisEx right = mock(AxisEx.class);
		when(right.getAsc()).thenReturn(2.0);
		when(right.getDesc()).thenReturn(8.0);

		AxisEx top = mock(AxisEx.class);
		when(top.getAsc()).thenReturn(5.0);
		when(top.getDesc()).thenReturn(2.5);

		AxisEx bottom = mock(AxisEx.class);
		when(bottom.getAsc()).thenReturn(2.5);
		when(bottom.getDesc()).thenReturn(5.0);

		ais.leftAxes.add(left);
		ais.rightAxes.add(right);
		ais.topAxes.add(top);
		ais.bottomAxes.add(bottom);
		checkDouble(SimpleLayoutDirector.calcLeftMargin(subplot, ais), 8.0);
		checkDouble(SimpleLayoutDirector.calcRightMargin(subplot, ais), 8.0);
		checkDouble(SimpleLayoutDirector.calcTopMargin(subplot, ais), 5.0);
		checkDouble(SimpleLayoutDirector.calcBottomMargin(subplot, ais), 5.0);

		subplot.getMargin().setExtraLeft(10.0);
		subplot.getMargin().setExtraRight(9.0);
		subplot.getMargin().setExtraTop(8.0);
		subplot.getMargin().setExtraBottom(7.0);
		checkDouble(SimpleLayoutDirector.calcLeftMargin(subplot, ais), 18.0);
		checkDouble(SimpleLayoutDirector.calcRightMargin(subplot, ais), 17.0);
		checkDouble(SimpleLayoutDirector.calcTopMargin(subplot, ais), 13.0);
		checkDouble(SimpleLayoutDirector.calcBottomMargin(subplot, ais), 12.0);

		// add title
		TitleEx title0 = mock(TitleEx.class);
		when(title0.canContribute()).thenReturn(true);
		when(title0.getPosition()).thenReturn(TitleEx.Position.TOPCENTER);
		when(title0.getSize()).thenReturn(new DoubleDimension2D(20, 12));
		TitleEx title1 = mock(TitleEx.class);
		when(title1.canContribute()).thenReturn(true);
		when(title1.getPosition()).thenReturn(TitleEx.Position.TOPCENTER);
		when(title1.getSize()).thenReturn(new DoubleDimension2D(20, 8));
		subplot.addTitle(title0);
		subplot.addTitle(title1);
		checkDouble(SimpleLayoutDirector.calcLeftMargin(subplot, ais), 18.0);
		checkDouble(SimpleLayoutDirector.calcRightMargin(subplot, ais), 17.0);
		checkDouble(SimpleLayoutDirector.calcTopMargin(subplot, ais), 38.0);
		checkDouble(SimpleLayoutDirector.calcBottomMargin(subplot, ais), 12.0);

		// add legend
		when(legend.canContribute()).thenReturn(true);
		when(legend.getPosition()).thenReturn(LegendEx.Position.BOTTOMCENTER);
		when(legend.getSize()).thenReturn(new DoubleDimension2D(100, 40));
		checkDouble(SimpleLayoutDirector.calcLeftMargin(subplot, ais), 18.0);
		checkDouble(SimpleLayoutDirector.calcRightMargin(subplot, ais), 17.0);
		checkDouble(SimpleLayoutDirector.calcTopMargin(subplot, ais), 38.0);
		checkDouble(SimpleLayoutDirector.calcBottomMargin(subplot, ais), 60.0);

		// no auto margin
		subplot.getMargin().setAutoMarginLeft(false);
		subplot.getMargin().setAutoMarginRight(false);
		subplot.getMargin().setAutoMarginTop(false);
		subplot.getMargin().setAutoMarginBottom(false);
		checkDouble(SimpleLayoutDirector.calcLeftMargin(subplot, ais), 10.0);
		checkDouble(SimpleLayoutDirector.calcRightMargin(subplot, ais), 9.0);
		checkDouble(SimpleLayoutDirector.calcTopMargin(subplot, ais), 8.0);
		checkDouble(SimpleLayoutDirector.calcBottomMargin(subplot, ais), 7.0);
	}

	@Test
	public void testLayoutMargin() {
		LegendEx legend = mock(LegendEx.class);
		when(legend.canContribute()).thenReturn(true);
		when(legend.getSize()).thenReturn(new DoubleDimension2D(30, 10));

		PlotEx subplot = new PlotImpl(legend) {
		};
		subplot.setSize(300, 200);

		// add axes
		AxisRangeManagerEx rm = mock(AxisRangeManagerEx.class);
		AxisLockGroupEx lg = mock(AxisLockGroupEx.class);
		when(rm.getLockGroup()).thenReturn(lg);

		AxisEx left = mock(AxisEx.class);
		when(left.getRangeManager()).thenReturn(rm);
		when(left.canContribute()).thenReturn(true);
		when(left.getPosition()).thenReturn(AxisPosition.NEGATIVE_SIDE);
		when(left.getAsc()).thenReturn(8.0);
		when(left.getDesc()).thenReturn(2.0);

		AxisEx right = mock(AxisEx.class);
		when(right.getRangeManager()).thenReturn(rm);
		when(right.canContribute()).thenReturn(true);
		when(right.getPosition()).thenReturn(AxisPosition.POSITIVE_SIDE);
		when(right.getAsc()).thenReturn(2.0);
		when(right.getDesc()).thenReturn(8.0);

		AxisEx top = mock(AxisEx.class);
		when(top.getRangeManager()).thenReturn(rm);
		when(top.canContribute()).thenReturn(true);
		when(top.getPosition()).thenReturn(AxisPosition.POSITIVE_SIDE);
		when(top.getAsc()).thenReturn(5.0);
		when(top.getDesc()).thenReturn(2.5);

		AxisEx bottom = mock(AxisEx.class);
		when(bottom.getRangeManager()).thenReturn(rm);
		when(bottom.canContribute()).thenReturn(true);
		when(bottom.getPosition()).thenReturn(AxisPosition.NEGATIVE_SIDE);
		when(bottom.getAsc()).thenReturn(2.5);
		when(bottom.getDesc()).thenReturn(5.0);

		subplot.addYAxis(left);
		subplot.addYAxis(right);
		subplot.addXAxis(top);
		subplot.addXAxis(bottom);

		subplot.validate();
		checkRectangle2D(subplot.getContentBounds(), 8.0, 5.0, 300 - 8 - 8,
				200 - 5 - 5);

	}

}
