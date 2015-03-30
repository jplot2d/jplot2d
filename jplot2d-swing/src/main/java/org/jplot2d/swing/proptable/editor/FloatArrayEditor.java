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

import org.jplot2d.util.NumberArrayUtils;

import javax.swing.JTextField;

/**
 * @author Jingjing Li
 */
public class FloatArrayEditor extends AbstractPropertyEditor<JTextField> {

    private float[] oldValue;

    public FloatArrayEditor() {
        editor = new JTextField();
        editor.setBorder(null);
    }

    public Object getValue() {
        String text = editor.getText();
        if (text == null || text.length() == 0) {
            return null;
        }
        String[] values = text.split(",");
        float[] result = new float[values.length];
        try {
            for (int i = 0; i < values.length; i++) {
                result[i] = Float.parseFloat(values[i]);
            }
            return result;
        } catch (NumberFormatException e) {
            return oldValue;
        }
    }

    public void setValue(Object value) {
        oldValue = (float[]) value;

        if (value == null) {
            editor.setText("");
        } else {
            editor.setText(NumberArrayUtils.toString((float[]) value));
        }
    }

}
