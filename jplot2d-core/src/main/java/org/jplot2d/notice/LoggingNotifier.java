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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This notifier write all messages to java log.
 * 
 * @author Jingjing Li
 * 
 */
public class LoggingNotifier extends AbstractNotifier {

	private static final LoggingNotifier instance = new LoggingNotifier();

	private static final Logger LOGGER = LoggerFactory.getLogger("org.jplot2d.notice");

	private LoggingNotifier() {

	}

	public static LoggingNotifier getInstance() {
		return instance;
	}

	@Override
	public void showNotices(NoticeType type) {
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
