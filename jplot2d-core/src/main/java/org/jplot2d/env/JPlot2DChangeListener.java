/**
 * 
 */
package org.jplot2d.env;

import java.util.EventListener;

/**
 * Listen to the structure change, such as add or remove layer, add or remove
 * annotation. Or properties change of a plot component.
 * 
 * @author Jingjing Li
 * 
 */
public interface JPlot2DChangeListener extends EventListener {

    void componentCreated(JPlot2DChangeEvent evt);

    void componentRemoved(JPlot2DChangeEvent evt);

    void enginePropertiesChanged(JPlot2DChangeEvent evt);
    
    void batchModeChanged(JPlot2DChangeEvent evt);
}
