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
package org.jplot2d.env;

import java.util.logging.Logger;

/**
 * A logger to log command to java logger "org.jplot2d.command".
 * 
 * @author Jingjing Li
 * 
 */
public class JavaLoggingCommandLogger implements CommandLogger {

	private static JavaLoggingCommandLogger instance = new JavaLoggingCommandLogger();

	private static Logger LOGGER = Logger.getLogger("org.jplot2d.command");

	public static JavaLoggingCommandLogger getInstance() {
		return instance;
	}

	private JavaLoggingCommandLogger() {

	}

	public void log(String cmd) {
		LOGGER.info("Command: " + cmd);
	}

}
