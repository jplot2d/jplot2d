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
package org.jplot2d.swing.proptable.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

/**
 * ComboBoxPropertyEditor. <br>
 * 
 */
public class ComboBoxPropertyEditor extends AbstractPropertyEditor<JComboBox> {

	private Object oldValue;

	private ActionListener action = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			firePropertyChange(oldValue, editor.getSelectedItem());
		}
	};

	public ComboBoxPropertyEditor(Object[] items) {
		editor = new JComboBox(items);
	}

	public Object getValue() {
		Object selected = editor.getSelectedItem();
		return selected;
	}

	public void setValue(Object value) {
		editor.setSelectedItem(value);
		editor.addActionListener(action);
	}

}
