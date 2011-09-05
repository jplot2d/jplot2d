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
package org.jplot2d.warning;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * @author Jingjing Li
 * 
 */
public class DialogWarningManager extends WarningManager {

	private final Component plotComp;

	public DialogWarningManager(Component plotComp) {
		this.plotComp = plotComp;
	}

	@Override
	protected void showWarnings(WarningType type) {
		if (warnings.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (WarningMessage wm : warnings) {
				sb.append(wm.getMessage());
				sb.append("\n");
			}
			JOptionPane.showMessageDialog(plotComp, sb, "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

}
