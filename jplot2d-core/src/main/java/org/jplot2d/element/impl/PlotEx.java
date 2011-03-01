package org.jplot2d.element.impl;

import org.jplot2d.element.Plot;
import org.jplot2d.util.WarningReceiver;

public interface PlotEx extends Plot, SubplotEx {

	public boolean isRerenderNeeded();

	public void clearRerenderNeeded();

	/**
	 * Sets a WarningReceiver to receive all warning messages.
	 * 
	 * @param warningReceiver
	 */
	public void setWarningReceiver(WarningReceiver warningReceiver);

	/**
	 * Apply all pending changes on this plot. After this method is called, all
	 * axis range and layout are valid.
	 */
	public void commit();

}
