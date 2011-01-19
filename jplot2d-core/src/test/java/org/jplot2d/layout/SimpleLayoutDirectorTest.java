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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.jplot2d.element.impl.AxisEx;
import org.jplot2d.element.impl.SubplotMarginEx;
import org.jplot2d.element.impl.SubplotMarginImpl;
import org.jplot2d.layout.SimpleLayoutDirector.AxesInSubplot;
import org.jplot2d.util.Insets2D;
import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class SimpleLayoutDirectorTest {

	@Test
	public void calcMarginTest() {
		SubplotMarginEx margin = new SubplotMarginImpl();
		AxesInSubplot ais = new AxesInSubplot();
		assertEquals(SimpleLayoutDirector.calcMargin(margin, ais),
				new Insets2D(0, 0, 0, 0));

		AxisEx left = mock(AxisEx.class);
		when(left.getAsc()).thenReturn(0.4);
		when(left.getDesc()).thenReturn(0.1);

		AxisEx right = mock(AxisEx.class);
		when(right.getAsc()).thenReturn(0.1);
		when(right.getDesc()).thenReturn(0.4);

		AxisEx top = mock(AxisEx.class);
		when(top.getAsc()).thenReturn(0.25);
		when(top.getDesc()).thenReturn(0.125);

		AxisEx bottom = mock(AxisEx.class);
		when(bottom.getAsc()).thenReturn(0.125);
		when(bottom.getDesc()).thenReturn(0.25);

		ais.leftAxes.add(left);
		ais.rightAxes.add(right);
		ais.topAxes.add(top);
		ais.bottomAxes.add(bottom);
		assertEquals(SimpleLayoutDirector.calcMargin(margin, ais),
				new Insets2D(0.25, 0.4, 0.25, 0.4));
	}

}
