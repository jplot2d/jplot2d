package org.jplot2d.element.impl;

import org.jplot2d.element.Plot;

public interface PlotEx extends Plot, SubplotEx {

	/**
	 * Sets a WarningReceiver to receive all warning messages.
	 * 
	 * @param warningReceiver
	 */
	public void setWarningReceiver(WarningReceiver warningReceiver);

}
