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

import java.util.ArrayList;
import java.util.List;

/**
 * This interface receive and process plot waning messages.
 * 
 * @author Jingjing Li
 * 
 */
public abstract class Notifier {

	protected final List<Notice> notices = new ArrayList<Notice>();

	/**
	 * Application call this method to add notices. This method can called multiple times.
	 * 
	 * @param notice
	 */
	public final void notify(Notice notice) {
		notices.add(notice);
	}

	/**
	 * Discard all exist notices
	 */
	public final void reset() {
		notices.clear();
	}

	/**
	 * Application call this method to show the notices. All notices in the queue are treated as the
	 * given notice type.
	 */
	public final void processNotices(NoticeType type) {
		showNotices(type);
		reset();
	}

	/**
	 * To be overridden to show notices
	 */
	protected abstract void showNotices(NoticeType type);

}
