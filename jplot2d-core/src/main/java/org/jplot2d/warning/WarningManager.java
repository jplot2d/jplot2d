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

import java.util.ArrayList;
import java.util.List;


/**
 * This interface receive and process plot waning messages.
 * 
 * @author Jingjing Li
 * 
 */
public abstract class WarningManager {

    protected final List<WarningMessage> warnings = new ArrayList<WarningMessage>();

    /**
     * Application call this method to add warnings. This method can called
     * multiple times.
     * 
     * @param msg
     */
    public final void warning(WarningMessage msg) {
        warnings.add(msg);
    }

	/**
	 * Plot call this method when command is committed.
	 */
	public void commit() {
		
	}

    /**
     * Discard all exist warnings
     */
    public final void reset() {
        warnings.clear();
    }

    /**
     * Application call this method to show the warnings. All warnings in the
     * queue are treated as the given warning type.
     */
    public final void processWarnings(WarningType type) {
        showWarnings(type);
        reset();
    }

    /**
     * To be overridden to show warnings
     */
    protected abstract void showWarnings(WarningType type);

}
