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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;

/**
 * @author Jingjing Li
 * 
 */
public class DialogWarningManager extends WarningManager {

	private final MessageBox msgbox;

	public DialogWarningManager(Composite plotComp) {
		msgbox = new MessageBox(plotComp.getShell(), SWT.OK | SWT.ICON_WARNING
				| SWT.APPLICATION_MODAL);
	}

	@Override
	protected void showWarnings(WarningType type) {
		if (warnings.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (WarningMessage wm : warnings) {
				sb.append(wm.getMessage());
				sb.append("\n");
			}
			msgbox.setMessage(sb.toString());
			msgbox.open();
		}
	}

}
