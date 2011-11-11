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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * ComboBoxPropertyEditor. <br>
 * 
 */
public class ComboBoxPropertyEditor extends AbstractPropertyEditor {

    private class PComboBox extends JComboBox {

        private static final long serialVersionUID = 1L;

        private Object oldValue;

        private ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ComboBoxPropertyEditor.this.firePropertyChange(oldValue,
                        getSelectedItem());
            }
        };

        public PComboBox() {
            super();
            addActionListener(action);
        }

        public PComboBox(Object[] items) {
            super(items);
            addActionListener(action);
        }

        public void setSelectedItem(Object anObject) {
            oldValue = getSelectedItem();
            super.setSelectedItem(anObject);
        }
    }

    public ComboBoxPropertyEditor() {
        editor = new PComboBox();
    }

    public ComboBoxPropertyEditor(Object[] items) {
        editor = new PComboBox(items);
    }

    public Object getValue() {
        Object selected = ((JComboBox) editor).getSelectedItem();
        return selected;
    }

    public void setValue(Object value) {
        JComboBox combo = (JComboBox) editor;
        Object current = null;
        int index = -1;
        for (int i = 0, c = combo.getModel().getSize(); i < c; i++) {
            current = combo.getModel().getElementAt(i);
            if (value == current || (current != null && current.equals(value))) {
                index = i;
                break;
            }
        }
        ((JComboBox) editor).setSelectedIndex(index);
    }

    public void setAvailableValues(Object[] values) {
        ((JComboBox) editor).setModel(new DefaultComboBoxModel(values));
    }

}
