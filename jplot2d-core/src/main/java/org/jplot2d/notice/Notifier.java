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

/**
 * This interface receive and process plot waning messages.
 * 
 * @author Jingjing Li
 * 
 */
public interface Notifier {

	/**
	 * Application call this method to add notices. This method can called multiple times.
	 * 
	 * @param notice
	 */
	public void notify(Notice notice);

	/**
	 * Discard all exist notices
	 */
	public void reset();

	/**
	 * Application call this method to show the notices. All notices in the queue are treated as the given notice type.
	 */
	public void processNotices(NoticeType type);

	/**
	 * To be overridden to show notices
	 */
	public void showNotices(NoticeType type);

}
