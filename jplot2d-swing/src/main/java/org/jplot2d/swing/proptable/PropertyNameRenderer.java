/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.swing.proptable;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 
 *
 */
public class PropertyNameRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    public static PropertyNameRenderer DEFAULT_PROPERTY_RENDERER = new PropertyNameRenderer();

    private boolean valueSet;

    private String description;

    public void setValueSet(boolean b) {
        valueSet = b;
    }

    public void setDescription(String desc) {
        description = desc;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
        Font value_font = getFont();
        if (valueSet) {
            if (value_font == null)
                value_font = new Font("Dialog", Font.BOLD, 12);
            else
                value_font = new Font(value_font.getFamily(), Font.BOLD,
                        value_font.getSize());
        }
        setFont(value_font);
        if (description != null)
            this.setToolTipText(description);
        return this;
    }

}