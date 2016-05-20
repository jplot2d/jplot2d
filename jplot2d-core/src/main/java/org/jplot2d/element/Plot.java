/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;
import org.jplot2d.layout.LayoutDirector;
import org.jplot2d.sizing.SizeMode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

/**
 * Plot has a content area in the center, surrounded by margin area. The margin area holds axes, titles and legend.
 * <p>
 * Plot can contains a group of layers that stack over each other, their viewports have the same bounds as the content area.
 * <p>
 * Plot can also contains a group of subplots, which are laid out by LayoutDirector.
 *
 * @author Jingjing Li
 */
@SuppressWarnings("unused")
@PropertyGroup("Plot")
public interface Plot extends PComponent {

    @Hierarchy(HierarchyOp.GET)
    Plot getParent();

    /**
     * Returns the size mode of this plot.
     *
     * @return the size mode
     */
    @Property(order = 0, styleable = false)
    @Nullable
    SizeMode getSizeMode();

    /**
     * Sets a size mode to manage the size and scale of this plot according to its container size.
     * The mode must be set when displaying a plot in a swing/swt component.
     *
     * @param mode the size mode
     */
    void setSizeMode(@Nullable SizeMode mode);

    /**
     * Returns the container size in device coordinate system.
     *
     * @return the container size
     */
    @Property(order = 1, styleable = false)
    Dimension2D getContainerSize();

    /**
     * Sets the container size. The container size is given in device coordinate system and used by size mode to derive
     * paper size and scale. The default value is 640x480 pixels.
     * <p>
     * If the size mode is <code>null</code> when calling this method, an IllegalStateException will be thrown.
     *
     * @param size the container size
     */
    void setContainerSize(Dimension2D size);

    /**
     * Returns the scale of this plot. The scale is ratio device size to paper size.
     *
     * @return the scale of this plot
     */
    @Property(order = 2, styleable = false)
    double getScale();

    /**
     * Sets scale of this plot. This method only take effect when size mode is <code>null</code>. Otherwise the scale is
     * decided by size mode.
     *
     * @param scale the scale
     */
    void setScale(double scale);

    /**
     * Sets the paper size of this plot. This method only take effect when size mode is <code>null</code>. Otherwise the
     * size is decided by size mode.
     *
     * @param size the paper size
     */
    void setSize(Dimension2D size);

    /**
     * Sets the paper size of this plot. This method only take effect when size mode is <code>null</code>. Otherwise the
     * size is decided by size mode.
     * <p>
     * For subplot, the size is set by layout manager.
     *
     * @param width  the paper width
     * @param height the paper height
     */
    void setSize(double width, double height);

    /**
     * Gets the layout director for this plot.
     *
     * @return the layout director for this plot.
     */
    @Nonnull
    @Property(order = 10, styleable = false)
    LayoutDirector getLayoutDirector();

    /**
     * Sets the layout director for this plot.
     *
     * @param director the layout director
     */
    void setLayoutDirector(@Nonnull LayoutDirector director);

    /**
     * Returns the constraint of the specified subplot in the current LayoutManager.
     *
     * @param subplot The subplot whose constraint is being set
     * @return the constraint
     * @throws IllegalArgumentException if the subplot is not contained by this plot
     */
    Object getConstraint(Plot subplot);

    /**
     * Sets the constraint of the specified subplot in the current LayoutManager.
     *
     * @param subplot    The subplot whose constraint is being set
     * @param constraint the constraint
     * @throws IllegalArgumentException if the subplot is not contained by this plot
     */
    void setConstraint(Plot subplot, Object constraint);

    /**
     * Returns the the preferred content area size.
     *
     * @return the the preferred content area size
     */
    @Nonnull
    @Property(order = 12)
    Dimension2D getPreferredContentSize();

    /**
     * Sets the preferred content area size.
     *
     * @param size the size in Dimension2D
     */
    void setPreferredContentSize(@Nonnull Dimension2D size);

    /**
     * Sets the preferred content area size
     *
     * @param width  the width
     * @param height the height
     */
    void setPreferredContentSize(double width, double height);

    /**
     * Gets the location of this plot in its parent plot. The origin of a plot is the bottom-left
     * corner of the content box (the intersect point of left axis and bottom axis).
     * For root plot, the returned value is always (0,0)
     *
     * @return an instance of <code>Point</code> representing the base point of this plot
     */
    @Property(order = 13, styleable = false)
    Point2D getLocation();

    /**
     * Moves this plot to a new location.
     * The new location is specified by point and is given in the parent's paper coordinate space.
     * <p>
     * Notice: This method should be called when the parent plot's layout director does not manage subplots,
     * such as <code>SimpleLayoutDirector</code>, otherwise the location will be overwrite by the layout director.
     * <p>
     * For root plot, this method has no effect.
     *
     * @param loc the point defining the origin of the new location
     */
    void setLocation(Point2D loc);

    void setLocation(double locX, double locY);

    /**
     * Returns the size of content area.
     *
     * @return the size of content area.
     */
    @Property(order = 14, styleable = false)
    Dimension2D getContentSize();

    /**
     * Returns the margin of this plot.
     *
     * @return the margin of this plot
     */
    @Hierarchy(HierarchyOp.GET)
    PlotMargin getMargin();

    /**
     * Returns the legend of this plot.
     *
     * @return the legend of this plot
     */
    @Hierarchy(HierarchyOp.GET)
    Legend getLegend();

    /**
     * Gets the nth colorbar in this plot.
     *
     * @param index the index of the colorbar to get.
     * @return the nth colorbar in this plot
     */
    @Hierarchy(HierarchyOp.GET)
    Colorbar getColorbar(int index);

    /**
     * Returns all colorbar in the order of added.
     *
     * @return all colorbar
     */
    @Hierarchy(HierarchyOp.GETARRAY)
    Colorbar[] getColorbars();

    @Hierarchy(HierarchyOp.ADD)
    void addColorbar(Colorbar colorbar);

    @Hierarchy(HierarchyOp.REMOVE)
    void removeColorbar(Colorbar colorbar);

    /**
     * Gets the nth title in this plot.
     *
     * @param index the index of the title to get.
     * @return the nth title in this plot
     */
    @Hierarchy(HierarchyOp.GET)
    Title getTitle(int index);

    /**
     * Returns all titles in the order of added.
     *
     * @return all titles
     */
    @Hierarchy(HierarchyOp.GETARRAY)
    Title[] getTitles();

    @Hierarchy(HierarchyOp.ADD)
    void addTitle(Title title);

    @Hierarchy(HierarchyOp.REMOVE)
    void removeTitle(Title title);

    @Hierarchy(HierarchyOp.GET)
    PlotAxis getXAxis(int index);

    @Hierarchy(HierarchyOp.GET)
    PlotAxis getYAxis(int index);

    @Hierarchy(HierarchyOp.GETARRAY)
    PlotAxis[] getXAxes();

    @Hierarchy(HierarchyOp.GETARRAY)
    PlotAxis[] getYAxes();

    @Hierarchy(HierarchyOp.ADD)
    void addXAxis(PlotAxis axis);

    @Hierarchy(HierarchyOp.ADD)
    void addYAxis(PlotAxis axis);

    /**
     * Add the given axes created by {@link ElementFactory#createAxes(int)} as x-axes
     *
     * @param axes the axes to be added
     */
    @Hierarchy(HierarchyOp.ADD)
    void addXAxes(PlotAxis[] axes);

    /**
     * Add the given axes created by {@link ElementFactory#createAxes(int)} as y-axes
     *
     * @param axes the axes to be added
     */
    @Hierarchy(HierarchyOp.ADD)
    void addYAxes(PlotAxis[] axes);

    /**
     * Removes the specified X axis from this plot if it is present.
     *
     * @param axis the X axis to be removed
     */
    @Hierarchy(HierarchyOp.REMOVE)
    void removeXAxis(PlotAxis axis);

    /**
     * Removes the specified Y axis from this plot if it is present.
     *
     * @param axis the Y axis to be removed
     */
    @Hierarchy(HierarchyOp.REMOVE)
    void removeYAxis(PlotAxis axis);

    /**
     * Gets the nth layer in this plot.
     *
     * @param index the index of the layer to get.
     * @return the nth layer in this plot
     */
    @Hierarchy(HierarchyOp.GET)
    Layer getLayer(int index);

    /**
     * Returns all layers in the order of added.
     *
     * @return all layers
     */
    @Hierarchy(HierarchyOp.GETARRAY)
    Layer[] getLayers();

    /**
     * Add a layer to this plot. The layer will not associate with any axis range manager.
     * Before the layer associate axis transforms by {@link Layer#setAxesTransform(AxisTransform, AxisTransform)},
     * all its subcomponents are invisible.
     *
     * @param layer the layer to be added
     */
    @Hierarchy(HierarchyOp.ADD)
    void addLayer(Layer layer);

    /**
     * Add a layer to this plot. The layer will associate with the given X/Y axis range manager
     * to control which part of graph show in the plot viewport.
     *
     * @param layer         the layer to be added
     * @param xRangeManager the x axis range manager
     * @param yRangeManager the y axis range manager
     */
    @Hierarchy(HierarchyOp.ADD_REF2)
    void addLayer(Layer layer, AxisTransform xRangeManager, AxisTransform yRangeManager);

    /**
     * Add a layer to this plot. The layer will associate with the given X/Y axis' range manager
     * to control which part of graph show in the plot viewport.
     * Equivalent to {@link #addLayer(Layer, AxisTransform, AxisTransform)
     * addLayer(layer, xaxis.getTickManager().getRangeManager(), yaxis.getTickManager().getRangeManager())}
     *
     * @param layer the layer to be added
     * @param xaxis the x axis
     * @param yaxis the y axis
     */
    @Hierarchy(HierarchyOp.ADD_REF2)
    void addLayer(Layer layer, PlotAxis xaxis, PlotAxis yaxis);

    @Hierarchy(HierarchyOp.REMOVE)
    void removeLayer(Layer layer);

    /**
     * Gets the nth subplot in this plot.
     *
     * @param n the index of the component to get.
     * @return the nth subplot in this subplot
     */
    @Hierarchy(HierarchyOp.GET)
    Plot getSubplot(int n);

    /**
     * Returns all subplots in the order of added.
     *
     * @return all subplots
     */
    @Hierarchy(HierarchyOp.GETARRAY)
    Plot[] getSubplots();

    /**
     * Add a subplot with a constraint to this plot.
     *
     * @param subplot    the subplot to be added
     * @param constraint an object expressing layout constraints
     */
    @Hierarchy(HierarchyOp.ADD)
    void addSubplot(Plot subplot, Object constraint);

    /**
     * remove the specified subplot from this plot.
     *
     * @param subplot the subplot to be removed
     */
    @Hierarchy(HierarchyOp.REMOVE)
    void removeSubplot(Plot subplot);

    /**
     * Zoom the given range to entire X axis. The behavior is like, creating a temporary AxisRangeLockGroup
     * to group all AxisRangeLockGroups which zoomable are <code>true</code> in this plot, and zoom the range on it.
     *
     * @param start the normalized start
     * @param end   the normalized end
     */
    void zoomXRange(double start, double end);

    /**
     * Zoom the given range to entire Y axis. The behavior is like, creating a temporary AxisRangeLockGroup
     * to group all AxisRangeLockGroups which zoomable are <code>true</code> in this plot, and zoom the range on it.
     *
     * @param start the normalized start
     * @param end   the normalized end
     */
    void zoomYRange(double start, double end);

    /**
     * Adaptive zoom the x range for all axes in this plot.
     * Only axes whose AxisRangeLockGroups is zoomable are zoomed.
     */
    void adaptiveZoomX();

    /**
     * Adaptive zoom the y range for all axes in this plot.
     * Only axes whose AxisRangeLockGroups is zoomable are zoomed.
     */
    void adaptiveZoomY();

}
