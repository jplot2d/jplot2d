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


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.LabelUI;

import org.jplot2d.swing.proptable.cellrenderer.FontCellRenderer;

/**
 * FontPropertyEditor.<br>
 * 
 */
public class FontPropertyEditor extends AbstractPropertyEditor {

    private FontCellRenderer label;

    private JButton button;

    private Font font;

    private FontChooserDialog fontDialog;

    public FontPropertyEditor() {
        editor = new JPanel(new BorderLayout(0, 0));
        ((JPanel) editor).add(getLabel(), "Center");
        ((JPanel) editor).add(getButton(), "East");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectFont();
            }
        });
        ((JPanel) editor).setOpaque(false);
    }

    private FontCellRenderer getLabel() {
        if (label == null) {
            label = new FontCellRenderer();
            label.setUI((LabelUI) UIManager.getUI(label));
            label.setOpaque(false);
        }
        return label;
    }

    private JButton getButton() {
        if (button == null) {
            button = new FixedButton();
        }
        return button;
    }

    public Font getValue() {
        return font;
    }

    public void setValue(Object value) {
        font = (Font) value;
        label.setValue(value);
    }

    protected void selectFont() {
        if (fontDialog == null) {
            Window win = SwingUtilities.getWindowAncestor(editor);
            fontDialog = new FontChooserDialog(win, true);
        }

        fontDialog.setFontValue(getValue());
        fontDialog.setLocationRelativeTo(editor);
        fontDialog.setVisible(true);

        if (fontDialog.isOK()) {
            Font oldFont = font;
            setValue(fontDialog.getFontValue());
            firePropertyChange(oldFont, font);
        }

    }

}
