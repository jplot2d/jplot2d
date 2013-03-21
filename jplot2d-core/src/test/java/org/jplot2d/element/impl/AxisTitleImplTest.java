/**
 * Copyright 2010, 2011 Jingjing Li.
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

import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.Font;

import org.jplot2d.element.VAlign;
import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class AxisTitleImplTest {

	@Test
	public void testInvalidateThicknessAndRedraw() {
		AxisTitleImpl title = new AxisTitleImpl();
		title.setVAlign(VAlign.BOTTOM);

		AxisEx axis = mock(AxisEx.class);
		when(axis.isVisible()).thenReturn(true);
		when(axis.canContribute()).thenReturn(true);
		when(axis.getEffectiveColor()).thenReturn(Color.BLACK);
		when(axis.getEffectiveFontName()).thenReturn("Lucida Bright");
		when(axis.getEffectiveFontStyle()).thenReturn(Font.PLAIN);
		when(axis.getEffectiveFontSize()).thenReturn(12.0f);
		title.setParent(axis);

		title.setFontName("Lucida Bright");
		title.setFontStyle(Font.PLAIN);
		title.setFontSize(18.0f);
		title.setColor(Color.BLACK);
		title.setVAlign(VAlign.BOTTOM);
		verify(axis, never()).invalidateThickness();
		verify(axis, never()).setRedrawNeeded(true);

		title.setFontName("Roman");
		verify(axis, times(1)).invalidateThickness();
		verify(axis, times(1)).setRedrawNeeded(true);
		title.setFontStyle(Font.BOLD);
		verify(axis, times(2)).invalidateThickness();
		verify(axis, times(2)).setRedrawNeeded(true);
		title.setFontSize(10.0f);
		verify(axis, times(3)).invalidateThickness();
		verify(axis, times(3)).setRedrawNeeded(true);
		title.setText("AxA");
		verify(axis, times(4)).invalidateThickness();
		verify(axis, times(4)).setRedrawNeeded(true);
		title.setColor(Color.RED);
		verify(axis, times(4)).invalidateThickness();
		verify(axis, times(5)).setRedrawNeeded(true);
		title.setVAlign(VAlign.TOP);
		verify(axis, times(4)).invalidateThickness();
		verify(axis, times(6)).setRedrawNeeded(true);

		title.setVisible(false);
		verify(axis, times(5)).invalidateThickness();
		verify(axis, times(7)).setRedrawNeeded(true);
		title.setFontName("Lucida Bright");
		title.setFontStyle(Font.PLAIN);
		title.setFontSize(12.0f);
		title.setColor(Color.BLACK);
		title.setVAlign(VAlign.BOTTOM);
		verify(axis, times(5)).invalidateThickness();
		verify(axis, times(7)).setRedrawNeeded(true);
		title.setVisible(true);
		verify(axis, times(6)).invalidateThickness();
		verify(axis, times(8)).setRedrawNeeded(true);

		title.setFont(new Font("Roman", Font.BOLD, 10));
		verify(axis, times(7)).invalidateThickness();
		verify(axis, times(9)).setRedrawNeeded(true);
		title.setFont(null);
		verify(axis, times(8)).invalidateThickness();
		verify(axis, times(10)).setRedrawNeeded(true);
		title.setFontScale(1.6f);
		verify(axis, times(9)).invalidateThickness();
		verify(axis, times(11)).setRedrawNeeded(true);
	}

}
