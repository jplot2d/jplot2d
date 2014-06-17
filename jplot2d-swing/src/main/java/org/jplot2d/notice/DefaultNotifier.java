/**
 * Copyright 2010-2013 Jingjing Li.
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
package org.jplot2d.notice;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jplot2d.notice.LoggingNotifier;
import org.jplot2d.notice.Notice;
import org.jplot2d.notice.NoticeType;

/**
 * The default notifier for plot. Warnings generated from mouse interaction will show in a dialog. Others will log to
 * java log.
 * 
 * @author Jingjing Li
 * 
 */
public class DefaultNotifier extends AbstractNotifier {

	private final Component plotComp;

	public DefaultNotifier(Component plotComp) {
		this.plotComp = plotComp;
	}

	@Override
	public void showNotices(NoticeType type) {
		if (notices.size() == 0) {
			return;
		}

		if (type instanceof UINoticeType) {
			final StringBuilder sb = new StringBuilder();
			for (Notice wm : notices) {
				sb.append(wm.getMessage());
				sb.append("\n");
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(plotComp, sb, "Warning", JOptionPane.WARNING_MESSAGE);
				}
			});
		} else {
			LoggingNotifier.logNotices(notices.toArray(new Notice[notices.size()]));
		}
	}

}
