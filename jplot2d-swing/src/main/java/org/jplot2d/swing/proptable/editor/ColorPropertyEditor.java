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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.LabelUI;

import org.jplot2d.swing.proptable.cellrenderer.ColorCellRenderer;

/**
 * ColorPropertyEditor. <br>
 * 
 */
public class ColorPropertyEditor extends AbstractPropertyEditor {

	private ColorCellRenderer label;

	private JButton button;

	private Color color;

	public ColorPropertyEditor() {
		editor = new JPanel(new BorderLayout(0, 0));
		((JPanel) editor).add(getLabel(), "Center");
		((JPanel) editor).add(getButton(), "East");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectColor();
			}
		});
		((JPanel) editor).setOpaque(false);
	}

	private ColorCellRenderer getLabel() {
		if (label == null) {
			label = new ColorCellRenderer();
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

	public Object getValue() {
		return color;
	}

	public void setValue(Object value) {
		color = (Color) value;
		label.setValue(color);
	}

	protected void selectColor() {
		String title = "Pick a color";
		Color selectedColor = ColorChooser.showDialog(editor, title, color);

		if (selectedColor != null) {
			label.setValue(selectedColor);
			color = selectedColor;
		}
	}

}
