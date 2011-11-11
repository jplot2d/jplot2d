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
package org.jplot2d.notice;

import java.util.logging.Logger;

/**
 * This manage write all plot waning messages to java log.
 * 
 * @author Jingjing Li
 * 
 */
public class LoggingNotifier extends Notifier {

	private static final LoggingNotifier instance = new LoggingNotifier();

	public static Logger LOGGER = Logger.getLogger("org.jplot2d.env");

	private LoggingNotifier() {

	}

	public static LoggingNotifier getInstance() {
		return instance;
	}

	@Override
	protected void showNotices(NoticeType type) {
		logNotices(notices.toArray(new Notice[notices.size()]));
	}

	public static void logNotices(Notice[] notices) {
		if (notices.length == 0) {
			return;
		} else if (notices.length == 1) {
			LoggingNotifier.LOGGER.info("Notice: " + notices[0]);
		} else {
			StringBuilder sb = new StringBuilder("Multiple Notices:\n");
			for (Notice wm : notices) {
				sb.append("\t");
				sb.append(wm.getMessage());
				sb.append("\n");
			}
			LoggingNotifier.LOGGER.info(sb.toString());
		}
	}
}
