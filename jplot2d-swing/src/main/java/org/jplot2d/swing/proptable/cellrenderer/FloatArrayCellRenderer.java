/**
 * Copyright 2010-2014 Jingjing Li.
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
package org.jplot2d.swing.proptable.cellrenderer;

import org.jplot2d.util.NumberUtils;

/**
 * @author Jingjing Li
 * 
 */
public class FloatArrayCellRenderer extends DigitsLimitableCellRenderer<float[]> {

	private static final long serialVersionUID = 1L;

	public String getValueText() {
		if (value == null) {
			return null;
		}

		return toString(value, digits);
	}

	public static String toString(float[] value, int digits) {
		StringBuilder b = new StringBuilder();
		if (value.length > 0) {
			b.append(NumberUtils.toString(value[0], digits));
		}
		for (int i = 1; i < value.length; i++) {
			b.append(',').append(NumberUtils.toString(value[i], digits));
		}
		return b.toString();
	}

}
