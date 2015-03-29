/**
 * Copyright 2010-2014 Jingjing Li.
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
package org.jplot2d.element.impl;

import org.jplot2d.element.Plot;
import org.jplot2d.notice.Notifier;

import java.awt.geom.Dimension2D;
import java.util.Map;

import javax.annotation.Nonnull;

public interface PlotEx extends Plot, ContainerEx {

    public PlotEx getParent();

    public PlotEx copyStructure(@Nonnull Map<ElementEx, ElementEx> orig2copyMap);

    /**
     * Create a structural copy of this plot, without linking layer and axis transform.
     */
    public PlotEx copyStructureCascade(@Nonnull Map<ElementEx, ElementEx> orig2copyMap);

    /**
     * Returns the short id of this plot. The short id is composed of series of ids concatenated with dots. The 1st id
     * is the id of this element, the 2nd id is the id of the parent of this element, etc, until but not include the
     * root plot.
     *
     * @return the short id of this plot.
     */
    public String getShortId();

    /**
     * Determines whether this plot is valid. A plot is valid when it is correctly sized and positioned within its
     * parent plot and all its axes, titles, legend and subplot are also valid.
     *
     * @return <code>true</code> if the component is valid, <code>false</code> otherwise
     * @see #validate
     * @see #invalidate
     */
    public boolean isValid();

    /**
     * Invalidates this component. This component and all parents above it are marked as needing to be laid out. This
     * method can be called often, so it needs to execute quickly.
     *
     * @see #validate
     */
    public void invalidate();

    /**
     * Mark this component has a valid layout.
     *
     * @see #invalidate
     */
    public void validate();

    /**
     * Returns the flag that indicate if this plot is needed to be re-rendered. This flag will control if rendering
     * process is executed by all renderers of a RenderEnvironment.
     *
     * @return the flag that indicate if this plot is needed to be re-rendered
     */
    public boolean isRerenderNeeded();

    /**
     * Mark or clear the flag that indicate this plot artifact need to be re-rendered. Any component changes which do
     * not require redraw and require re-render the plot can call this method. eg, when a cacheable component removed.
     *
     * @param flag the flag that indicate this plot artifact need to be re-rendered
     */
    public void setRerenderNeeded(boolean flag);

    public Notifier getNotifier();

    /**
     * Sets a notifier to receive all notice messages.
     *
     * @param notifier the notifier to receive all notice messages
     */
    public void setNotifier(Notifier notifier);

    /**
     * Apply all pending changes on this plot. After this method is called, all axis range and layout are valid.
     */
    public void commit();

    public PlotMarginEx getMargin();

    public LegendEx getLegend();

    public int indexOf(TitleEx title);

    public TitleEx[] getTitles();

    public LayerEx getLayer(int index);

    public int indexOf(LayerEx layer);

    public LayerEx[] getLayers();

    public int indexOfXAxis(AxisEx axis);

    public int indexOfYAxis(AxisEx axis);

    public AxisEx[] getXAxes();

    public AxisEx[] getYAxes();

    public int indexOf(PlotEx subplot);

    public PlotEx[] getSubplots();

    public void parentPaperTransformChanged();

    /**
     * Sets the content size by layout director. All layers in this plot have the same viewport size.
     * <p/>
     * The layout manager guarantee this method is called after setting plot margin, and no matter if the content size
     * is changed.
     *
     * @param csize the content size
     */
    public void setContentSize(Dimension2D csize);

    /**
     * Returns the contents constraint of this plot.
     *
     * @return the contents constraint
     */
    public Dimension2D getContentConstraint();

    /**
     * Impose contents constraint on this plot. This method is called by a plot's layout director, when laying out
     * subplots of the plot.
     *
     * @param constraint the contents constraint
     */
    public void setContentConstraint(Dimension2D constraint);

}
