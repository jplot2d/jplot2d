/*
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
import org.jplot2d.util.Range;

/**
 * @author Jingjing Li
 */
public class RangeCellRenderer extends DigitsLimitableCellRenderer<Range> {

    private static final long serialVersionUID = 1L;

    @Override
    public String getValueText() {
        if (value == null) {
            return null;
        }
        return (value.isStartIncluded() ? "[" : "(") + NumberUtils.toString(value.getStart(), digits) + ", "
                + NumberUtils.toString(value.getEnd(), digits) + (value.isEndIncluded() ? "]" : ")");
    }
}
