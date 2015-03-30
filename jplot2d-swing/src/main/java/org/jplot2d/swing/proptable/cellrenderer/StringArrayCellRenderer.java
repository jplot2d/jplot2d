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
package org.jplot2d.swing.proptable.cellrenderer;

/**
 * @author Jingjing Li
 */
public class StringArrayCellRenderer extends StringCellRenderer<Object[]> {

    private static final long serialVersionUID = 1L;

    public String getValueText() {
        if (value == null) {
            return null;
        }

        StringBuilder b = new StringBuilder();
        if (value.length > 0) {
            b.append(value[0]);
        }
        for (int i = 1; i < value.length; i++)
            b.append(',').append(value[i]);
        return b.toString();
    }

}
