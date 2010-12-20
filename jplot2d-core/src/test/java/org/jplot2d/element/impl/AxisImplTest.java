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
				new TextComponentImpl());
		ViewportAxisEx va = mock(ViewportAxisEx.class);
		when(va.getOrientation()).thenReturn(AxisOrientation.HORIZONTAL);
		when(va.getEffectiveFontName()).thenReturn("Lucida Bright");
		when(va.getEffectiveFontStyle()).thenReturn(Font.PLAIN);
		when(va.getEffectiveFontSize()).thenReturn(12.0f);
		axis.setParent(va);

		assertEquals(axis.getTickSide(), AxisTickSide.OUTWARD);
		assertEquals(axis.getLabelSide(), AxisLabelSide.OUTWARD);

		axis.setPosition(AxisPosition.NEGATIVE_SIDE);
		checkDouble(axis.getAsc(), 0);
		checkDouble(axis.getDesc(), 23.96875);

		axis.setTickSide(AxisTickSide.INWARD);
		checkDouble(axis.getAsc(), 10.0);
		checkDouble(axis.getDesc(), 13.96875);

		axis.setPosition(AxisPosition.POSITIVE_SIDE);
		checkDouble(axis.getAsc(), 13.96875);
		checkDouble(axis.getDesc(), 10.0);
	}

	@Test
	public void testTicknessY() {
		AxisImpl axis = new AxisImpl(mock(AxisTickEx.class),
				new TextComponentImpl());
		ViewportAxisEx va = mock(ViewportAxisEx.class);
		when(va.getOrientation()).thenReturn(AxisOrientation.VERTICAL);
		when(va.getEffectiveFontName()).thenReturn("Lucida Bright");
		when(va.getEffectiveFontStyle()).thenReturn(Font.PLAIN);
		when(va.getEffectiveFontSize()).thenReturn(12.0f);
		axis.setParent(va);

		when(axis.getTick().getLabelModels()).thenReturn(
				new MathElement[] { new MathElement.Mn("0"),
						new MathElement.Mn("5"), new MathElement.Mn("10") });
		Font font = axis.getEffectiveFont();
		when(axis.getTick().getActualLabelFont()).thenReturn(font);

		axis.setPosition(AxisPosition.NEGATIVE_SIDE);
		checkDouble(axis.getAsc(), 24.6015625);
		checkDouble(axis.getDesc(), 0);

		axis.setTickSide(AxisTickSide.INWARD);
		checkDouble(axis.getAsc(), 14.6015625);
		checkDouble(axis.getDesc(), 10.0);

		axis.setPosition(AxisPosition.POSITIVE_SIDE);
		checkDouble(axis.getAsc(), 10.0);
		checkDouble(axis.getDesc(), 14.6015625);

		when(axis.getTick().getLabelModels())
				.thenReturn(
						new MathElement[] { new MathElement.Mn("0.0"),
								new MathElement.Mn("5.0"),
								new MathElement.Mn("10.0") });
		axis.invalidateThickness();
		checkDouble(axis.getAsc(), 10.0);
		checkDouble(axis.getDesc(), 25.083984375);
	}
}
