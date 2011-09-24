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

import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.InteractiveComp;
import org.jplot2d.swt.JPlot2DComposite;

/**
 * @author Jingjing Li
 * 
 */
public class SwtInteractiveComp implements InteractiveComp {

	private final JPlot2DComposite comp;
	
	public SwtInteractiveComp(JPlot2DComposite comp, PlotEnvironment env) {
		this.comp = comp;
	}

	public void repaint() {
		comp.redraw();
	}

	public void setCursor(CursorStyle cursorStyle) {
		// TODO Auto-generated method stub
		
	}

	public void popupMenu(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	public Color getPlotBackground() {
		// TODO Auto-generated method stub
		return comp.getPlotBackground();
	}

	public Color getForeground() {
		// TODO Auto-generated method stub
		return null;
	}

	public Color getBackground() {
		// TODO Auto-generated method stub
		return null;
	}

}
