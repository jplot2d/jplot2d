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
package org.jplot2d.swing.proptable.cellrenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 
 * 
 */
public class ColorCellRenderer extends DefaultTableCellRenderer.UIResource {

	private static final long serialVersionUID = 1L;

	private Color color;

	private String getColorText() {
		if (color == null) {
			return "null";
		}

		Color c = color;
		if (c.equals(Color.black))
			return "black";
		else if (c.equals(Color.blue))
			return "blue";
		else if (c.equals(Color.cyan))
			return "cyan";
		else if (c.equals(Color.darkGray))
			return "darkGray";
		else if (c.equals(Color.gray))
			return "gray";
		else if (c.equals(Color.green))
			return "green";
		else if (c.equals(Color.lightGray))
			return "lightGray";
		else if (c.equals(Color.magenta))
			return "magenta";
		else if (c.equals(Color.orange))
			return "orange";
		else if (c.equals(Color.pink))
			return "pink";
		else if (c.equals(Color.red))
			return "red";
		else if (c.equals(Color.white))
			return "white";
		else if (c.equals(Color.yellow))
			return "yellow";
		return "[" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + "]";
	}

	public void setValue(Object value) {
		this.color = (Color) value;
		setText(getColorText());
		setIcon(new ColorIcon());
	}

	public class ColorIcon implements Icon {

		public int getIconHeight() {
			return getHeight() - 3;
		}

		public int getIconWidth() {
			return getIconHeight();
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			Color oldColor = g.getColor();

			if (color != null) {
				g.setColor(color);
				g.fillRect(x, y, getIconWidth(), getIconHeight());
			}

			g.setColor(UIManager.getColor("controlDkShadow"));
			g.drawRect(x, y, getIconWidth(), getIconHeight());

			g.setColor(oldColor);
		}

	}

}