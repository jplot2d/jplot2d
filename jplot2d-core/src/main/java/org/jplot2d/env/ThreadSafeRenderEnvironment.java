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
package org.jplot2d.env;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Jingjing Li
 * 
 */
public class ThreadSafeRenderEnvironment extends RenderEnvironment {

	public ThreadSafeRenderEnvironment() {

	}

	private final ReentrantLock lock = new ReentrantLock();

	@Override
	public Environment createDummyEnvironment() {
		return new ThreadSafeDummyEnvironment();
	}

	/**
	 * Begin a batch command sequence. This will block the auto re-layout and
	 * auto redraw temporarily.
	 * 
	 * @param msg
	 * @return
	 */
	public BatchToken beginBatch(String msg) {
		lock.lock();
		try {
			return super.beginBatch(msg);
		} finally {
			lock.unlock();
		}
	}

	public void endBatch(BatchToken token) throws WarningException {
		lock.lock();
		try {
			super.endBatch(token);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Begin a batch command sequence. This will lock the whole environment to
	 * prevent other thread from modifying.
	 * 
	 * @param msg
	 * @return
	 * @throws InterruptedException
	 */
	void beginCommand(String msg) {
		lock.lock();
		super.beginCommand(msg);
	}

	void endCommand() throws WarningException {
		super.endCommand();
		lock.unlock();
	}

	protected void beginUndoRedo(String msg) {
		lock.lock();
		super.beginUndoRedo(msg);
	}

	protected void endUndoRedo() {
		super.endUndoRedo();
		lock.unlock();
	}

}
