package org.jplot2d.element.impl;

/**
 * A component contains axes.
 */
public interface AxisContainerEx extends ComponentEx {

    ComponentEx getParent();

    /**
     * Invalidates this component.
     */
    void invalidate();

}
