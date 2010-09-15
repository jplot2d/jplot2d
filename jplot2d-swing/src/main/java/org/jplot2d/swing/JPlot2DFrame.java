/**
 * Copyright 2010 Jingjing Li.
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
package org.jplot2d.swing;

import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jplot2d.element.Plot;
import org.jplot2d.env.RenderEnvironment;

/**
 * @author Jingjing Li
 * 
 */
public class JPlot2DFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger("org.jplot2d.swing");

	public JPlot2DFrame(Plot plot) {
		super();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().add(new JPlot2DComponent(plot));

	}

}
