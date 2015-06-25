package org.jplot2d.element.impl;

import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.Plot;
import org.jplot2d.element.PlotAxis;

/**
 *
 */
public interface PlotAxisEx extends PlotAxis, AxisEx {

    PlotEx getParent();

    /**
     * Called by {@link Plot#addXAxis(PlotAxis)} or {@link Plot#addYAxis(PlotAxis)} to set the orientation of this axis.
     *
     * @param orientation the orientation
     */
    void setOrientation(AxisOrientation orientation);


}
