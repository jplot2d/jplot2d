/*
 * Copyright 2010-2012 Jingjing Li.
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
package org.jplot2d.swing.proptable.editor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author Jingjing Li
 */
public class FloatEditor extends FormattedEditor {

    private static NumberFormat getFloatFormat() {
        DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        symbols.setNaN("NaN");
        format.setDecimalFormatSymbols(symbols);
        format.setMaximumFractionDigits(8);
        return format;
    }

    public FloatEditor() {
        super(getFloatFormat());
    }

    @Override
    public Object getValue() {
        Object v = super.getValue();
        if (v instanceof Number) {
            return ((Number) v).floatValue();
        }
        return v;
    }
}
