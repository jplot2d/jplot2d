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

import java.awt.Component;
import java.text.Format;

import javax.swing.JFormattedTextField;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicFormattedTextFieldUI;

/**
 * 
 * @author Jingjing Li
 */
public class FormattedEditor extends AbstractPropertyEditor {

    private JFormattedTextField textField;

    public FormattedEditor(Format format) {
        textField = new JFormattedTextField(format);
        // workaround for mac jdk bug
        if (isMacOSX() && UIManager.getLookAndFeel().isNativeLookAndFeel()) {
            textField.setUI(new BasicFormattedTextFieldUI());
        }
        textField.setBorder(null);
    }

    public void setValue(Object value) {
        textField.setValue(value);
    }

    public Object getValue() {
        if (textField.getText().length() == 0) {
            return null;
        }
        return textField.getValue();
    }

    public Component getCustomEditor() {
        return textField;
    }

    private boolean isMacOSX() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.startsWith("mac os x");
    }

}