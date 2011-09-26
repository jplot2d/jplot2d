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
package org.jplot2d.swt.interaction;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.InteractiveComp;
import org.jplot2d.swt.JPlot2DComposite;

/**
 * @author Jingjing Li
 * 
 */
public class SwtInteractiveComp implements InteractiveComp {

	private final JPlot2DComposite comp;

	private final Cursor defaultCursor;

	private final Cursor moveCursor;

	public SwtInteractiveComp(JPlot2DComposite comp, PlotEnvironment env) {
		this.comp = comp;
		defaultCursor = new Cursor(comp.getDisplay(), SWT.CURSOR_ARROW);
		moveCursor = new Cursor(comp.getDisplay(), SWT.CURSOR_SIZEALL);
	}

	public void repaint() {
		comp.redraw();
	}

	public void setCursor(CursorStyle cursorStyle) {
		switch (cursorStyle) {
		case DEFAULT_CURSOR:
			comp.setCursor(defaultCursor);
			break;
		case MOVE_CURSOR:
			comp.setCursor(moveCursor);
			break;
		}
	}

	public Color getPlotBackground() {
		return comp.getPlotBackground();
	}

	public Color getForeground() {
		org.eclipse.swt.graphics.Color swtColor = comp.getForeground();
		return new Color(swtColor.getRed(), swtColor.getGreen(), swtColor.getBlue());
	}

	public Color getBackground() {
		org.eclipse.swt.graphics.Color swtColor = comp.getBackground();
		return new Color(swtColor.getRed(), swtColor.getGreen(), swtColor.getBlue());
	}

}
