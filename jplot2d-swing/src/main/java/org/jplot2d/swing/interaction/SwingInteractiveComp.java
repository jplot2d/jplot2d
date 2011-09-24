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
package org.jplot2d.swing.interaction;

import java.awt.Color;
import java.awt.Cursor;

import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.interaction.InteractiveComp;
import org.jplot2d.swing.JPlot2DComponent;

/**
 * @author Jingjing Li
 * 
 */
public class SwingInteractiveComp implements InteractiveComp {

	private final JPlot2DComponent comp;

	public SwingInteractiveComp(JPlot2DComponent comp, RenderEnvironment env) {
		this.comp = comp;
	}

	public void repaint() {
		comp.repaint();
	}

	public void setCursor(CursorStyle cursorStyle) {
		switch (cursorStyle) {
		case DEFAULT_CURSOR:
			comp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			break;
		case MOVE_CURSOR:
			comp.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			break;
		}

	}

	public Color getPlotBackground() {
		return comp.getPlotBackground();
	}

	public Color getForeground() {
		return comp.getForeground();
	}

	public Color getBackground() {
		return comp.getBackground();
	}

}
