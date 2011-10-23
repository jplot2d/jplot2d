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

import java.awt.geom.Dimension2D;

import org.jplot2d.element.AxisPosition;
import org.jplot2d.element.HAlign;
import org.jplot2d.element.Legend;
import org.jplot2d.element.Title;
import org.jplot2d.element.VAlign;
import org.jplot2d.element.impl.AxisEx;
import org.jplot2d.element.impl.AxisRangeLockGroupEx;
import org.jplot2d.element.impl.AxisRangeManagerEx;
import org.jplot2d.element.impl.AxisTickManagerEx;
import org.jplot2d.element.impl.LegendEx;
import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.element.impl.PlotImpl;
import org.jplot2d.element.impl.TitleEx;
import org.jplot2d.layout.SimpleLayoutDirector.AxesInPlot;
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
		PlotEx plot = new PlotImpl(legend);

		AxesInPlot ais = new AxesInPlot();
		checkDouble(SimpleLayoutDirector.calcLeftMargin(plot, ais), 0);
		checkDouble(SimpleLayoutDirector.calcRightMargin(plot, ais), 0);
		checkDouble(SimpleLayoutDirector.calcTopMargin(plot, ais), 0);
		checkDouble(SimpleLayoutDirector.calcBottomMargin(plot, ais), 0);

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
		checkDouble(SimpleLayoutDirector.calcLeftMargin(plot, ais), 8.0);
		checkDouble(SimpleLayoutDirector.calcRightMargin(plot, ais), 8.0);
		checkDouble(SimpleLayoutDirector.calcTopMargin(plot, ais), 5.0);
		checkDouble(SimpleLayoutDirector.calcBottomMargin(plot, ais), 5.0);

		plot.getMargin().setExtraLeft(10.0);
		plot.getMargin().setExtraRight(9.0);
		plot.getMargin().setExtraTop(8.0);
		plot.getMargin().setExtraBottom(7.0);
		checkDouble(SimpleLayoutDirector.calcLeftMargin(plot, ais), 18.0);
		checkDouble(SimpleLayoutDirector.calcRightMargin(plot, ais), 17.0);
		checkDouble(SimpleLayoutDirector.calcTopMargin(plot, ais), 13.0);
		checkDouble(SimpleLayoutDirector.calcBottomMargin(plot, ais), 12.0);

		// add title
		TitleEx title0 = mock(TitleEx.class);
		when(title0.canContribute()).thenReturn(true);
		when(title0.getPosition()).thenReturn(TitleEx.Position.TOPCENTER);
		when(title0.getSize()).thenReturn(new DoubleDimension2D(20, 12));
		when(title0.getGapFactor()).thenReturn(0.25);
		TitleEx title1 = mock(TitleEx.class);
		when(title1.canContribute()).thenReturn(true);
		when(title1.getPosition()).thenReturn(TitleEx.Position.TOPCENTER);
		when(title1.getSize()).thenReturn(new DoubleDimension2D(20, 8));
		when(title1.getGapFactor()).thenReturn(0.25);
		plot.addTitle(title0);
		plot.addTitle(title1);
		checkDouble(SimpleLayoutDirector.calcLeftMargin(plot, ais), 18.0);
		checkDouble(SimpleLayoutDirector.calcRightMargin(plot, ais), 17.0);
		checkDouble(SimpleLayoutDirector.calcTopMargin(plot, ais), 38.0);
		checkDouble(SimpleLayoutDirector.calcBottomMargin(plot, ais), 12.0);

		// add legend
		when(legend.canContribute()).thenReturn(true);
		when(legend.getPosition()).thenReturn(LegendEx.Position.BOTTOMCENTER);
		when(legend.getSize()).thenReturn(new DoubleDimension2D(100, 40));
		checkDouble(SimpleLayoutDirector.calcLeftMargin(plot, ais), 18.0);
		checkDouble(SimpleLayoutDirector.calcRightMargin(plot, ais), 17.0);
		checkDouble(SimpleLayoutDirector.calcTopMargin(plot, ais), 38.0);
		checkDouble(SimpleLayoutDirector.calcBottomMargin(plot, ais),
				7.0 + 40.0 + SimpleLayoutDirector.LEGEND_GAP + 5.0);

		// no auto margin
		plot.getMargin().setAutoLeft(false);
		plot.getMargin().setLeft(7);
		plot.getMargin().setAutoRight(false);
		plot.getMargin().setRight(7);
		plot.getMargin().setAutoTop(false);
		plot.getMargin().setTop(7);
		plot.getMargin().setAutoBottom(false);
		plot.getMargin().setBottom(7);
		checkDouble(SimpleLayoutDirector.calcLeftMargin(plot, ais), 7 + 10.0);
		checkDouble(SimpleLayoutDirector.calcRightMargin(plot, ais), 7 + 9.0);
		checkDouble(SimpleLayoutDirector.calcTopMargin(plot, ais), 7 + 8.0);
		checkDouble(SimpleLayoutDirector.calcBottomMargin(plot, ais), 7 + 7.0);
	}

	@Test
	public void testLayoutMargin() {
		LegendEx legend = mock(LegendEx.class);
		when(legend.canContribute()).thenReturn(false);

		PlotEx plot = new PlotImpl(legend);
		plot.setSize(300, 200);

		// add axes
		AxisTickManagerEx tm = mock(AxisTickManagerEx.class);
		AxisRangeManagerEx rm = mock(AxisRangeManagerEx.class);
		AxisRangeLockGroupEx lg = mock(AxisRangeLockGroupEx.class);
		when(tm.getRangeManager()).thenReturn(rm);
		when(rm.getLockGroup()).thenReturn(lg);

		AxisEx left = mock(AxisEx.class);
		when(left.getTickManager()).thenReturn(tm);
		when(left.canContribute()).thenReturn(true);
		when(left.getPosition()).thenReturn(AxisPosition.NEGATIVE_SIDE);
		when(left.getAsc()).thenReturn(8.0);
		when(left.getDesc()).thenReturn(2.0);

		AxisEx right = mock(AxisEx.class);
		when(right.getTickManager()).thenReturn(tm);
		when(right.canContribute()).thenReturn(true);
		when(right.getPosition()).thenReturn(AxisPosition.POSITIVE_SIDE);
		when(right.getAsc()).thenReturn(2.0);
		when(right.getDesc()).thenReturn(8.0);

		AxisEx top = mock(AxisEx.class);
		when(top.getTickManager()).thenReturn(tm);
		when(top.canContribute()).thenReturn(true);
		when(top.getPosition()).thenReturn(AxisPosition.POSITIVE_SIDE);
		when(top.getAsc()).thenReturn(5.0);
		when(top.getDesc()).thenReturn(2.5);

		AxisEx bottom = mock(AxisEx.class);
		when(bottom.getTickManager()).thenReturn(tm);
		when(bottom.canContribute()).thenReturn(true);
		when(bottom.getPosition()).thenReturn(AxisPosition.NEGATIVE_SIDE);
		when(bottom.getAsc()).thenReturn(2.5);
		when(bottom.getDesc()).thenReturn(5.0);

		plot.addYAxis(left);
		plot.addYAxis(right);
		plot.addXAxis(top);
		plot.addXAxis(bottom);

		plot.validate();
		checkDouble(plot.getMargin().getLeft(), 8.0);
		checkDouble(plot.getMargin().getBottom(), 5.0);
		Dimension2D csize = plot.getContentSize();
		checkDimension2D(csize, 300 - 8 - 8, 200 - 5 - 5);
		verify(left).setLocation(0, 0);
		verify(bottom).setLocation(0, 0);
		verify(top).setLocation(0, csize.getHeight());
		verify(right).setLocation(csize.getWidth(), 0);
		verify(legend, never()).setLocation(anyDouble(), anyDouble());
		verify(legend, never()).setHAlign(any(HAlign.class));
		verify(legend, never()).setVAlign(any(VAlign.class));
	}

	@Test
	public void testLayoutLegendBottomCenter() {
		LegendEx legend = mock(LegendEx.class);
		when(legend.canContribute()).thenReturn(true);
		when(legend.getSize()).thenReturn(new DoubleDimension2D(30, 10));
		when(legend.getPosition()).thenReturn(Legend.Position.BOTTOMCENTER);

		PlotEx plot = new PlotImpl(legend);
		plot.setSize(300, 200);
		plot.getMargin().setExtraLeft(12.0);
		plot.getMargin().setExtraRight(12.0);
		plot.getMargin().setExtraTop(12.0);
		plot.getMargin().setExtraBottom(12.0);

		plot.validate();
		double bottomMargin = 12 + 10 + SimpleLayoutDirector.LEGEND_GAP;
		checkDouble(plot.getMargin().getLeft(), 0);
		checkDouble(plot.getMargin().getRight(), 0);
		checkDouble(plot.getMargin().getBottom(), 10 + SimpleLayoutDirector.LEGEND_GAP);
		checkDouble(plot.getMargin().getTop(), 0);
		Dimension2D csize = plot.getContentSize();
		checkDimension2D(csize, 300 - 12 - 12, 200 - 12 - bottomMargin);
		verify(legend).setLocation(csize.getWidth() / 2, -SimpleLayoutDirector.LEGEND_GAP);
		verify(legend).setHAlign(HAlign.CENTER);
		verify(legend).setVAlign(VAlign.TOP);
	}

	@Test
	public void testLayoutLegendTopCenter() {
		LegendEx legend = mock(LegendEx.class);
		when(legend.canContribute()).thenReturn(true);
		when(legend.getSize()).thenReturn(new DoubleDimension2D(30, 10));
		when(legend.getPosition()).thenReturn(Legend.Position.TOPCENTER);

		PlotEx plot = new PlotImpl(legend);
		plot.setSize(300, 200);
		plot.getMargin().setExtraLeft(12.0);
		plot.getMargin().setExtraRight(12.0);
		plot.getMargin().setExtraTop(12.0);
		plot.getMargin().setExtraBottom(12.0);

		plot.validate();
		double topMargin = 12 + 10 + SimpleLayoutDirector.LEGEND_GAP;
		checkDouble(plot.getMargin().getLeft(), 0);
		checkDouble(plot.getMargin().getRight(), 0);
		checkDouble(plot.getMargin().getBottom(), 0);
		checkDouble(plot.getMargin().getTop(), 10 + SimpleLayoutDirector.LEGEND_GAP);
		Dimension2D csize = plot.getContentSize();
		checkDimension2D(csize, 300 - 12 - 12, 200 - topMargin - 12);
		verify(legend).setLocation(csize.getWidth() / 2,
				csize.getHeight() + SimpleLayoutDirector.LEGEND_GAP);
		verify(legend).setHAlign(HAlign.CENTER);
		verify(legend).setVAlign(VAlign.BOTTOM);
	}

	@Test
	public void testLayoutLegendLeftMiddle() {
		LegendEx legend = mock(LegendEx.class);
		when(legend.canContribute()).thenReturn(true);
		when(legend.getSize()).thenReturn(new DoubleDimension2D(30, 10));
		when(legend.getPosition()).thenReturn(Legend.Position.LEFTMIDDLE);

		PlotEx plot = new PlotImpl(legend);
		plot.setSize(300, 200);
		plot.getMargin().setExtraLeft(12.0);
		plot.getMargin().setExtraRight(12.0);
		plot.getMargin().setExtraTop(12.0);
		plot.getMargin().setExtraBottom(12.0);

		plot.validate();
		double leftMargin = 12 + 30 + SimpleLayoutDirector.LEGEND_GAP;
		checkDouble(plot.getMargin().getLeft(), 30 + SimpleLayoutDirector.LEGEND_GAP);
		checkDouble(plot.getMargin().getRight(), 0);
		checkDouble(plot.getMargin().getBottom(), 0);
		checkDouble(plot.getMargin().getBottom(), 0);
		Dimension2D csize = plot.getContentSize();
		checkDimension2D(csize, 300 - leftMargin - 12, 200 - 12 - 12);
		verify(legend).setLocation(-SimpleLayoutDirector.LEGEND_GAP, csize.getHeight() / 2);
		verify(legend).setHAlign(HAlign.RIGHT);
		verify(legend).setVAlign(VAlign.MIDDLE);
	}

	@Test
	public void testLayoutLegendRightMiddle() {
		LegendEx legend = mock(LegendEx.class);
		when(legend.canContribute()).thenReturn(true);
		when(legend.getSize()).thenReturn(new DoubleDimension2D(30, 10));
		when(legend.getPosition()).thenReturn(Legend.Position.RIGHTMIDDLE);

		PlotEx plot = new PlotImpl(legend);
		plot.setSize(300, 200);
		plot.getMargin().setExtraLeft(12.0);
		plot.getMargin().setExtraRight(12.0);
		plot.getMargin().setExtraTop(12.0);
		plot.getMargin().setExtraBottom(12.0);

		plot.validate();
		double rightMargin = 12 + 30 + SimpleLayoutDirector.LEGEND_GAP;
		checkDouble(plot.getMargin().getLeft(), 0);
		checkDouble(plot.getMargin().getRight(), 30 + SimpleLayoutDirector.LEGEND_GAP);
		checkDouble(plot.getMargin().getBottom(), 0);
		checkDouble(plot.getMargin().getTop(), 0);
		Dimension2D csize = plot.getContentSize();
		checkDimension2D(csize, 300 - 12 - rightMargin, 200 - 12 - 12);
		verify(legend).setLocation(csize.getWidth() + SimpleLayoutDirector.LEGEND_GAP,
				csize.getHeight() / 2);
		verify(legend).setHAlign(HAlign.LEFT);
		verify(legend).setVAlign(VAlign.MIDDLE);
	}

	@Test
	public void testLayoutTitleBottomCenter() {
		TitleEx title0 = mock(TitleEx.class);
		when(title0.canContribute()).thenReturn(true);
		when(title0.getPosition()).thenReturn(Title.Position.BOTTOMCENTER);
		when(title0.getSize()).thenReturn(new DoubleDimension2D(30, 10));
		when(title0.getGapFactor()).thenReturn(0.25);
		TitleEx title1 = mock(TitleEx.class);
		when(title1.canContribute()).thenReturn(true);
		when(title1.getPosition()).thenReturn(Title.Position.BOTTOMCENTER);
		when(title1.getSize()).thenReturn(new DoubleDimension2D(30, 10));
		when(title1.getGapFactor()).thenReturn(0.25);

		PlotEx plot = new PlotImpl();
		plot.setSize(300, 200);
		plot.getMargin().setExtraLeft(12.0);
		plot.getMargin().setExtraRight(12.0);
		plot.getMargin().setExtraTop(12.0);
		plot.getMargin().setExtraBottom(12.0);
		plot.addTitle(title0);
		plot.addTitle(title1);

		plot.validate();
		double bottomMargin = 12 + 10 + 2.5 + 10 + 2.5;
		checkDouble(plot.getMargin().getLeft(), 0);
		checkDouble(plot.getMargin().getRight(), 0);
		checkDouble(plot.getMargin().getBottom(), 10 + 2.5 + 10 + 2.5);
		checkDouble(plot.getMargin().getTop(), 0);
		Dimension2D csize = plot.getContentSize();
		checkDimension2D(csize, 300 - 12 - 12, 200 - 12 - bottomMargin);
		verify(title1).setLocation(csize.getWidth() / 2, -2.5 - 10 - 2.5);
		verify(title1).setHAlign(HAlign.CENTER);
		verify(title1).setVAlign(VAlign.TOP);
		verify(title0).setLocation(csize.getWidth() / 2, -2.5);
		verify(title0).setHAlign(HAlign.CENTER);
		verify(title0).setVAlign(VAlign.TOP);
	}

	@Test
	public void testLayoutTitleTopCenter() {
		TitleEx title0 = mock(TitleEx.class);
		when(title0.canContribute()).thenReturn(true);
		when(title0.getPosition()).thenReturn(Title.Position.TOPCENTER);
		when(title0.getSize()).thenReturn(new DoubleDimension2D(30, 10));
		when(title0.getGapFactor()).thenReturn(0.25);
		TitleEx title1 = mock(TitleEx.class);
		when(title1.canContribute()).thenReturn(true);
		when(title1.getPosition()).thenReturn(Title.Position.TOPCENTER);
		when(title1.getSize()).thenReturn(new DoubleDimension2D(30, 10));
		when(title1.getGapFactor()).thenReturn(0.25);

		PlotEx plot = new PlotImpl();
		plot.setSize(300, 200);
		plot.getMargin().setExtraLeft(12.0);
		plot.getMargin().setExtraRight(12.0);
		plot.getMargin().setExtraTop(12.0);
		plot.getMargin().setExtraBottom(12.0);
		plot.addTitle(title0);
		plot.addTitle(title1);

		plot.validate();
		double topMargin = 12 + 10 + 2.5 + 10 + 2.5;
		checkDouble(plot.getMargin().getLeft(), 0);
		checkDouble(plot.getMargin().getRight(), 0);
		checkDouble(plot.getMargin().getBottom(), 0);
		checkDouble(plot.getMargin().getTop(), 10 + 2.5 + 10 + 2.5);
		Dimension2D csize = plot.getContentSize();
		checkDimension2D(csize, 300 - 12 - 12, 200 - topMargin - 12);
		verify(title0).setLocation(csize.getWidth() / 2, csize.getHeight() + 2.5 + 10 + 2.5);
		verify(title0).setHAlign(HAlign.CENTER);
		verify(title0).setVAlign(VAlign.BOTTOM);
		verify(title1).setLocation(csize.getWidth() / 2, csize.getHeight() + 2.5);
		verify(title1).setHAlign(HAlign.CENTER);
		verify(title1).setVAlign(VAlign.BOTTOM);
	}

	@Test
	public void testLayoutTitleTopBottomCenter() {
		TitleEx title0 = mock(TitleEx.class);
		when(title0.canContribute()).thenReturn(true);
		when(title0.getPosition()).thenReturn(Title.Position.TOPCENTER);
		when(title0.getSize()).thenReturn(new DoubleDimension2D(30, 10));
		when(title0.getGapFactor()).thenReturn(0.25);
		TitleEx title1 = mock(TitleEx.class);
		when(title1.canContribute()).thenReturn(true);
		when(title1.getPosition()).thenReturn(Title.Position.BOTTOMCENTER);
		when(title1.getSize()).thenReturn(new DoubleDimension2D(30, 10));
		when(title1.getGapFactor()).thenReturn(0.25);

		PlotEx plot = new PlotImpl();
		plot.setSize(300, 200);
		plot.getMargin().setExtraLeft(12.0);
		plot.getMargin().setExtraRight(12.0);
		plot.getMargin().setExtraTop(12.0);
		plot.getMargin().setExtraBottom(12.0);
		plot.addTitle(title0);
		plot.addTitle(title1);

		plot.validate();
		double topMargin = 12 + 10 + 2.5;
		double bottomMargin = 12 + 10 + 2.5;
		checkDouble(plot.getMargin().getLeft(), 0);
		checkDouble(plot.getMargin().getRight(), 0);
		checkDouble(plot.getMargin().getBottom(), 10 + 2.5);
		checkDouble(plot.getMargin().getTop(), 10 + 2.5);
		Dimension2D csize = plot.getContentSize();
		checkDimension2D(csize, 300 - 12 - 12, 200 - topMargin - bottomMargin);
		verify(title0).setLocation(csize.getWidth() / 2, csize.getHeight() + 2.5);
		verify(title0).setHAlign(HAlign.CENTER);
		verify(title0).setVAlign(VAlign.BOTTOM);
		verify(title1).setLocation(csize.getWidth() / 2, -2.5);
		verify(title1).setHAlign(HAlign.CENTER);
		verify(title1).setVAlign(VAlign.TOP);
	}

}
