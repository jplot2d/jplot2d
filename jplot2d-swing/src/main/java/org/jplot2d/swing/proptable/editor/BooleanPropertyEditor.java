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
package org.jplot2d.swing.proptable.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

/**
 * BooleanAsCheckBoxPropertyEditor. <br>
 * 
 */
public class BooleanPropertyEditor extends AbstractPropertyEditor {

    public BooleanPropertyEditor() {
        editor = new JCheckBox();
        ((JCheckBox) editor).setOpaque(false);
        ((JCheckBox) editor).addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                firePropertyChange(
                        ((JCheckBox) editor).isSelected() ? Boolean.FALSE
                                : Boolean.TRUE, ((JCheckBox) editor)
                                .isSelected() ? Boolean.TRUE : Boolean.FALSE);
            }
        });
    }

    public Object getValue() {
        return ((JCheckBox) editor).isSelected() ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setValue(Object value) {
        ((JCheckBox) editor).setSelected(Boolean.TRUE.equals(value));
    }

}
