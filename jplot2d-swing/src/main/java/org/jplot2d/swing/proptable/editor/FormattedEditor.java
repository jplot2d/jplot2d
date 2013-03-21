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

import java.text.Format;

import javax.swing.JFormattedTextField;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicFormattedTextFieldUI;

/**
 * 
 * @author Jingjing Li
 */
public class FormattedEditor extends AbstractPropertyEditor<JFormattedTextField> {

	public FormattedEditor(Format format) {
		editor = new JFormattedTextField(format);
		// workaround for mac jdk bug
		if (isMacOSX() && UIManager.getLookAndFeel().isNativeLookAndFeel()) {
			editor.setUI(new BasicFormattedTextFieldUI());
		}
		editor.setBorder(null);
	}

	public void setValue(Object value) {
		editor.setValue(value);
	}

	public Object getValue() {
		if (editor.getText().length() == 0) {
			return null;
		}
		return editor.getValue();
	}

	private boolean isMacOSX() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.startsWith("mac os x");
	}

}