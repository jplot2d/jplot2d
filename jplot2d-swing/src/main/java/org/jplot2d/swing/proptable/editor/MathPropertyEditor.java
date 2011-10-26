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

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.jplot2d.tex.TeXMathUtils;

/**
 * 
 *
 */
public class MathPropertyEditor extends AbstractPropertyEditor {

	private Object value;

	public MathPropertyEditor() {
		editor = new JTextField();
		((JTextField) editor).setBorder(null);
	}

	public Object getValue() {
		try {
			value = TeXMathUtils.parseText(((JTextComponent) editor).getText());
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(editor, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}

		return value;
	}

	public void setValue(Object value) {
		this.value = value;

		if (value == null) {
			((JTextComponent) editor).setText("");
		} else {
			((JTextComponent) editor).setText(String.valueOf(value));
		}
	}
}
