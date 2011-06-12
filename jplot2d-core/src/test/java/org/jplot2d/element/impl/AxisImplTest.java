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
package org.jplot2d.element.impl;

import static org.jplot2d.util.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Font;

import org.jplot2d.element.AxisLabelSide;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.AxisPosition;
import org.jplot2d.element.AxisTickSide;
import org.jplot2d.util.MathElement;
import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class AxisImplTest {

	@Test
	public void testTicknessX() {
		AxisImpl axis = new AxisImpl(mock(AxisTickEx.class),
				new AxisTitleImpl());
		axis.setOrientation(AxisOrientation.HORIZONTAL);
		assertEquals(axis.getTickSide(), AxisTickSide.OUTWARD);
		assertEquals(axis.getLabelSide(), AxisLabelSide.OUTWARD);

		PlotEx sp = mock(PlotEx.class);
		when(sp.getEffectiveFontName()).thenReturn("Lucida Bright");
		when(sp.getEffectiveFontStyle()).thenReturn(Font.PLAIN);
		when(sp.getEffectiveFontSize()).thenReturn(12.0f);
		axis.setParent(sp);
		AxisRangeManagerEx va = mock(AxisRangeManagerEx.class);
		axis.setRangeManager(va);

		when(axis.getTick().getLabelModels()).thenReturn(
				new MathElement[] { new MathElement.Mn("0"),
						new MathElement.Mn("5"), new MathElement.Mn("10") });
		Font font = axis.getEffectiveFont();
		when(axis.getTick().getActualLabelFont()).thenReturn(font);

		axis.setPosition(AxisPosition.NEGATIVE_SIDE);
		axis.calcThickness();
		checkDouble(axis.getAsc(), 0);
		checkDouble(axis.getDesc(), 25.4609375);

		axis.setTickSide(AxisTickSide.INWARD);
		axis.calcThickness();
		checkDouble(axis.getAsc(), 8.0);
		checkDouble(axis.getDesc(), 17.4609375);

		axis.setPosition(AxisPosition.POSITIVE_SIDE);
		axis.calcThickness();
		checkDouble(axis.getAsc(), 17.4609375);
		checkDouble(axis.getDesc(), 8.0);
	}

	@Test
	public void testTicknessY() {
		AxisImpl axis = new AxisImpl(mock(AxisTickEx.class),
				new AxisTitleImpl());
		axis.setOrientation(AxisOrientation.VERTICAL);
		assertEquals(axis.getTickSide(), AxisTickSide.OUTWARD);
		assertEquals(axis.getLabelSide(), AxisLabelSide.OUTWARD);

		PlotEx sp = mock(PlotEx.class);
		when(sp.getEffectiveFontName()).thenReturn("Lucida Bright");
		when(sp.getEffectiveFontStyle()).thenReturn(Font.PLAIN);
		when(sp.getEffectiveFontSize()).thenReturn(12.0f);
		axis.setParent(sp);
		AxisRangeManagerEx va = mock(AxisRangeManagerEx.class);
		axis.setRangeManager(va);

		when(axis.getTick().getLabelModels()).thenReturn(
				new MathElement[] { new MathElement.Mn("0"),
						new MathElement.Mn("5"), new MathElement.Mn("10") });
		Font font = axis.getEffectiveFont();
		when(axis.getTick().getActualLabelFont()).thenReturn(font);

		axis.setPosition(AxisPosition.NEGATIVE_SIDE);
		axis.calcThickness();
		checkDouble(axis.getAsc(), 26.09375);
		checkDouble(axis.getDesc(), 0);

		axis.setTickSide(AxisTickSide.INWARD);
		axis.calcThickness();
		checkDouble(axis.getAsc(), 18.09375);
		checkDouble(axis.getDesc(), 8.0);

		axis.setPosition(AxisPosition.POSITIVE_SIDE);
		axis.calcThickness();
		checkDouble(axis.getAsc(), 8.0);
		checkDouble(axis.getDesc(), 18.09375);

		when(axis.getTick().getLabelModels())
				.thenReturn(
						new MathElement[] { new MathElement.Mn("0.0"),
								new MathElement.Mn("5.0"),
								new MathElement.Mn("10.0") });
		axis.invalidateThickness();
		axis.calcThickness();
		checkDouble(axis.getAsc(), 8.0);
		checkDouble(axis.getDesc(), 28.576171875);
	}

	@Test
	public void testSetLength() {
		AxisEx axis = new AxisImpl();
		AxisRangeManagerEx arm = new AxisRangeManagerImpl();
		AxisRangeLockGroupEx alg = mock(AxisRangeLockGroupEx.class);
		when(alg.isAutoRange()).thenReturn(true);
		axis.setRangeManager(arm);
		arm.setLockGroup(alg);

		verify(alg, times(0)).reAutoRange();
		axis.setLength(100);
		verify(alg, times(1)).reAutoRange();
	}
}
