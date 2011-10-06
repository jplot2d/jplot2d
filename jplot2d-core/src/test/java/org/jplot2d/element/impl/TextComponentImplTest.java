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

import static org.junit.Assert.*;

import java.awt.Font;

import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class TextComponentImplTest {

	private static class TextComponentImplStub extends TextComponentImpl {

		public InvokeStep getInvokeStepFormParent() {
			return null;
		}

	}

	@Test
	public void testInitProperties() {
		TextComponentImpl tc = new TextComponentImplStub();
		assertTrue(tc.isVisible());
		assertNull(tc.getTextModel());
		assertFalse(tc.canContribute());

		tc.setText("");
		assertNotNull(tc.getTextModel());
		assertTrue(tc.canContribute());
	}

	@Test
	public void testSetFont() {
		TextComponentImpl tc = new TextComponentImplStub();
		Font font = new Font("Serif", Font.PLAIN, 12);
		tc.setFont(font);
		assertEquals(tc.getEffectiveFont(), font);
	}

}
