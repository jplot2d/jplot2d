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

import org.jplot2d.element.HAlign;
import org.jplot2d.element.LegendPosition;
import org.jplot2d.element.Plot;
import org.jplot2d.element.VAlign;
import org.jplot2d.util.DoubleDimension2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Jingjing Li
 */
public class LegendImpl extends ComponentImpl implements LegendEx {

    private static final double VERTICAL_BORDER = 2;

    private static final double HORIZONTAL_BORDER = 8;

    private static final double COLUMN_SPACE = 8;

    private static final Color BORDER_COLOR = Color.BLACK;

    private boolean enabled = true;

    private double locX, locY;

    private double width, height;

    private LegendPosition position = LegendPosition.BOTTOMCENTER;

    private HAlign halign = HAlign.CENTER;

    private VAlign valign = VAlign.TOP;

    private double rowSpacingFactor = 0.125;

    private boolean borderVisible = true;

    private Dimension2D maxItemSize;

    private int columns = 1, rows = 1;

    private double lengthConstraint = Double.NaN;

    private boolean sizeCalculationNeeded;

    private boolean layoutItemsNeeded;

    /**
     * If this legend is enabled, or top level legend, the collection contains all items managed by this legend, include
     * items pushed by disabled legend of subplots. If this legend is disabled, the collection is empty.
     */
    private final Collection<LegendItemEx> items = new ArrayList<>();

    /**
     * The visible item count. only be maintained when this legend is enabled or is top level legend.
     */
    private int visibleItemNum;

    public LegendImpl() {
        setSelectable(true);
        setMovable(true);
    }

    public String getId() {
        if (getParent() != null) {
            return "Legend";
        } else {
            return "Legend@" + Integer.toHexString(System.identityHashCode(this));
        }
    }

    public InvokeStep getInvokeStepFormParent() {
        if (parent == null) {
            return null;
        }

        Method method;
        try {
            method = Plot.class.getMethod("getLegend");
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
        return new InvokeStep(method);
    }

    public PlotEx getParent() {
        return (PlotEx) super.getParent();
    }

    /**
     * This method should be called when the visibility or position changed.
     */
    private void invalidatePlot() {
        if (getParent() != null) {
            getParent().invalidate();
        }
    }

    /*
     * Only contribute contents when it has visible items.
     */
    public boolean canContribute() {
        return visibleItemNum > 0;
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (canContribute() && getPosition() != LegendPosition.FREE) {
            invalidatePlot();
        }
    }

    public void thisEffectiveColorChanged() {
        redraw(this);
    }

    public void thisEffectiveFontChanged() {
        for (LegendItemEx item : items) {
            item.legendEffectiveFontChanged();
        }
    }

    public Dimension2D getSize() {
        return new DoubleDimension2D(width, height);
    }

    public Rectangle2D getBounds() {
        double x, y;
        switch (getHAlign()) {
            case RIGHT:
                x = -width;
                break;
            case CENTER:
                x = -width / 2;
                break;
            default:
                x = 0;
        }
        switch (getVAlign()) {
            case TOP:
                y = -height;
                break;
            case MIDDLE:
                y = -height / 2;
                break;
            default:
                y = 0;
        }
        return new Rectangle2D.Double(x, y, width, height);
    }

    public Point2D getLocation() {
        return new Point2D.Double(locX, locY);
    }

    public void setLocation(Point2D loc) {
        setLocation(loc.getX(), loc.getY());
    }

    public void setLocation(double locX, double locY) {
        directLocation(locX, locY);
        setPosition(LegendPosition.FREE);
    }

    public void directLocation(double locX, double locY) {
        if (this.locX != locX || this.locY != locY) {
            this.locX = locX;
            this.locY = locY;
            redraw(this);
        }
    }

    public LegendPosition getPosition() {
        return position;
    }

    public void setPosition(LegendPosition position) {
        if (position == null) {
            position = LegendPosition.FREE;
        }
        if (this.position != position) {
            this.position = position;
            if (isVisible() && canContribute()) {
                invalidatePlot();
            }
        }
    }

    public HAlign getHAlign() {
        return halign;
    }

    public void setHAlign(HAlign halign) {
        this.halign = halign;
        layoutItemsNeeded = true;
    }

    public VAlign getVAlign() {
        return valign;
    }

    public void setVAlign(VAlign valign) {
        this.valign = valign;
        layoutItemsNeeded = true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        boolean oldContribution = this.canContribute();

        this.enabled = enabled;
        if (enabled) {
            LegendEx oldLegend = getEnabledLegend(getParent());
            LegendItemEx[] allItems = oldLegend.getItems();
            for (LegendItemEx item : allItems) {
                if (getEnabledLegend(item.getParent().getParent().getParent()) == this) {
                    oldLegend.removeLegendItem(item);
                    this.addLegendItem(item);
                }
            }
        } else {
            LegendEx newLegend = getEnabledLegend(getParent());
            if (newLegend != this) {
                for (LegendItemEx item : items) {
                    newLegend.addLegendItem(item);
                }
                items.clear();
                visibleItemNum = 0;
                maxItemSize = null;
            }
        }

        if (isVisible() && oldContribution != this.canContribute() && getPosition() != LegendPosition.FREE) {
            invalidatePlot();
        }
    }

    public void putItemsToEnabledLegend() {
        if (!isEnabled() && items.size() > 0) {
            LegendEx newLegend = getEnabledLegend(getParent());
            for (LegendItemEx item : items) {
                newLegend.addLegendItem(item);
            }
            items.clear();
            visibleItemNum = 0;
            maxItemSize = null;
        }
    }

    /**
     * Return an enable legend who can host legend items for the given plot. This method always return a legend. If all
     * legend is disabled, the top level legend is returned.
     *
     * @param plot the plot
     * @return an enable legend
     */
    private static LegendEx getEnabledLegend(PlotEx plot) {
        if (plot.getLegend().isEnabled()) {
            return plot.getLegend();
        } else if (plot.getParent() == null) {
            return plot.getLegend();
        } else {
            return getEnabledLegend(plot.getParent());
        }
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        if (columns < 1) {
            throw new IllegalArgumentException("Columns number must great than 0.");
        }
        this.columns = columns;

        sizeCalculationNeeded = true;
        redraw(this);
    }

    public double getRowSpacingFactor() {
        return rowSpacingFactor;
    }

    public void setRowSpacingFactor(double factor) {
        rowSpacingFactor = factor;
        sizeCalculationNeeded = true;
    }

    public boolean isBorderVisible() {
        return borderVisible;
    }

    public void setBorderVisible(boolean visible) {
        borderVisible = visible;
        redraw(this);
    }

    public LegendItemEx[] getItems() {
        return items.toArray(new LegendItemEx[items.size()]);
    }

    public void addLegendItem(LegendItemEx item) {
        if (isEnabled() || getParent().getParent() == null) {
            items.add(item);
            item.setLegend(this);
            if (isItemVisible(item)) {
                incVisibleItemNum();
                maxItemSize = null;
                sizeCalculationNeeded = true;
                if (isVisible()) {
                    redraw(this);
                }
            }
        } else {
            getParent().getParent().getLegend().addLegendItem(item);
        }
    }

    public void removeLegendItem(LegendItemEx item) {
        if (isEnabled() || getParent().getParent() == null) {
            if (isItemVisible(item)) {
                decVisibleItemNum();
                if (item.getSize().equals(maxItemSize)) {
                    maxItemSize = null;
                }
                sizeCalculationNeeded = true;
                redraw(this);
            }
            items.remove(item);
            item.setLegend(null);
        } else {
            getParent().getParent().getLegend().removeLegendItem(item);
        }
    }

    public void itemVisibilityChanged(LegendItemImpl item) {
        if (item.canContribute()) {
            if (item.isVisible()) {
                incVisibleItemNum();
                maxItemSize = null;
            } else {
                decVisibleItemNum();
                if (item.getSize().equals(maxItemSize)) {
                    maxItemSize = null;
                }
            }
            sizeCalculationNeeded = true;
            redraw(this);
        }
    }

    public void itemContribitivityChanged(LegendItemImpl item) {
        if (item.isVisible()) {
            if (item.canContribute()) {
                incVisibleItemNum();
            } else {
                decVisibleItemNum();
            }
            maxItemSize = null;
            sizeCalculationNeeded = true;
            redraw(this);
        }
    }

    /**
     * Returns <code>true</code> when both visible and contributable are <code>true</code>. Otherwise returns
     * <code>false</code>.
     *
     * @param item the legend item
     * @return <code>true</code> when both visible and contributable are <code>true</code>
     */
    private boolean isItemVisible(LegendItemEx item) {
        return item.isVisible() && item.canContribute();
    }

    private void incVisibleItemNum() {
        visibleItemNum++;
        if (visibleItemNum == 1 && isVisible()) {
            invalidatePlot();
        }
    }

    private void decVisibleItemNum() {
        visibleItemNum--;
        if (visibleItemNum == 0 && isVisible()) {
            invalidatePlot();
        }
    }

    public void itemSizeChanged(LegendItemEx item) {
        if (item.isVisible()) {
            maxItemSize = null;
            sizeCalculationNeeded = true;
            redraw(this);
        }
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        LegendImpl legend = (LegendImpl) src;
        enabled = legend.enabled;
        locX = legend.locX;
        locY = legend.locY;
        width = legend.width;
        height = legend.height;
        position = legend.position;
        halign = legend.halign;
        valign = legend.valign;
        rowSpacingFactor = legend.rowSpacingFactor;
        borderVisible = legend.borderVisible;
        maxItemSize = legend.maxItemSize;
        rows = legend.rows;
        columns = legend.columns;
        lengthConstraint = legend.lengthConstraint;
        sizeCalculationNeeded = legend.sizeCalculationNeeded;
        layoutItemsNeeded = legend.layoutItemsNeeded;
        visibleItemNum = legend.visibleItemNum;
    }

    public double getThickness() {
        switch (getPosition()) {
            case TOPLEFT:
            case TOPCENTER:
            case TOPRIGHT:
            case BOTTOMLEFT:
            case BOTTOMCENTER:
            case BOTTOMRIGHT:
                return getSize().getHeight();
            case LEFTTOP:
            case LEFTMIDDLE:
            case LEFTBOTTOM:
            case RIGHTTOP:
            case RIGHTMIDDLE:
            case RIGHTBOTTOM:
                return getSize().getWidth();
            default:
                return 0;
        }
    }

    public void calcSize() {

        if (visibleItemNum == 0) {
            width = 0;
            height = 0;
            sizeCalculationNeeded = false;
            return;
        }

        PlotEx plot = getParent();
        switch (getPosition()) {
            case TOPLEFT:
            case TOPCENTER:
            case TOPRIGHT:
            case BOTTOMLEFT:
            case BOTTOMCENTER:
            case BOTTOMRIGHT: {
                double legendWidth = plot.getSize().getWidth() - plot.getMargin().getExtraLeft()
                        - plot.getMargin().getExtraRight();
                setLengthConstraint(legendWidth);
                break;
            }
            case LEFTTOP:
            case LEFTMIDDLE:
            case LEFTBOTTOM:
            case RIGHTTOP:
            case RIGHTMIDDLE:
            case RIGHTBOTTOM: {
                double contentHeight = plot.getContentSize().getHeight();
                setLengthConstraint(contentHeight);
                break;
            }
            default:
                // calculate rows
                int nrow = visibleItemNum / columns;
                if (visibleItemNum % columns > 0) {
                    nrow++;
                }
                rows = nrow;
        }

        if (sizeCalculationNeeded) {
            switch (getPosition()) {
                case TOPLEFT:
                case TOPCENTER:
                case TOPRIGHT:
                case BOTTOMLEFT:
                case BOTTOMCENTER:
                case BOTTOMRIGHT: {
                    fitColumnsToWidth(lengthConstraint);
                    break;
                }
                case LEFTTOP:
                case LEFTMIDDLE:
                case LEFTBOTTOM:
                case RIGHTTOP:
                case RIGHTMIDDLE:
                case RIGHTBOTTOM: {
                    fitRowsToHeight(lengthConstraint);
                    break;
                }
            }

            width = (getMaxItemSize().getWidth() + COLUMN_SPACE) * columns - COLUMN_SPACE + 2 * HORIZONTAL_BORDER;
            height = getMaxItemSize().getHeight() * rows + getMaxItemSize().getHeight() * rowSpacingFactor * (rows - 1)
                    + 2 * VERTICAL_BORDER;

            sizeCalculationNeeded = false;
            layoutItemsNeeded = true;
        }

        if (layoutItemsNeeded) {
            layoutItems();
        }
    }

    private void setLengthConstraint(double length) {
        if (lengthConstraint != length) {
            lengthConstraint = length;
            sizeCalculationNeeded = true;
        }
    }

    /**
     * Calculate the proper column number to fit the given width.
     *
     * @param width the width to fit for
     */
    private void fitColumnsToWidth(double width) {
        Dimension2D lisize = getMaxItemSize();

        // the max possible columns
        double maxColumns = (width - 2 * HORIZONTAL_BORDER + COLUMN_SPACE) / (lisize.getWidth() + COLUMN_SPACE);
        int ncol = (int) maxColumns;
        if (ncol < 1) {
            ncol = 1;
        } else if (ncol > visibleItemNum) {
            ncol = visibleItemNum;
        }
        int nrow = visibleItemNum / ncol;
        if (visibleItemNum % ncol > 0) {
            nrow++;
        }

        columns = ncol;
        rows = nrow;
    }

    /**
     * Calculate the proper column number to fit the given height.
     *
     * @param height the height to fit for
     */
    private void fitRowsToHeight(double height) {
        Dimension2D lisize = getMaxItemSize();

        double maxRows = ((height - 2 * VERTICAL_BORDER) / lisize.getHeight() + rowSpacingFactor)
                / (1 + rowSpacingFactor);
        int nrow = (int) maxRows;
        if (nrow < 1) {
            nrow = 1;
        } else if (nrow > visibleItemNum) {
            nrow = visibleItemNum;
        }
        int ncol = visibleItemNum / nrow;
        if (visibleItemNum % nrow > 0) {
            ncol++;
        }

        columns = ncol;
        rows = nrow;
    }

    /**
     * Locate all items of this legend.
     */
    private void layoutItems() {
        if ((visibleItemNum == 0) || !isVisible()) {
            return;
        }

        double[] lipx = new double[columns];
        double[] lipy = new double[rows];
        for (int i = 0; i < columns; i++) {
            lipx[i] = HORIZONTAL_BORDER + i * getMaxItemSize().getWidth() + i * COLUMN_SPACE;
        }
        for (int i = 0; i < rows; i++) {
            lipy[i] = VERTICAL_BORDER + (rows - i - 0.5) * getMaxItemSize().getHeight() + (rows - 1 - i)
                    * getMaxItemSize().getHeight() * rowSpacingFactor;
        }

        Rectangle2D bounds = getBounds();
        double xoff = bounds.getX();
        double yoff = bounds.getY();
        int col = 0, row = 0;
        for (LegendItemEx item : items) {
            if (isItemVisible(item)) {
                item.setLocation(lipx[col] + xoff, lipy[row] + yoff);
                col++;
                if (col >= columns) {
                    col = 0;
                    row++;
                }
            }
        }
    }

    /**
     * find largest legend item size
     */
    private Dimension2D getMaxItemSize() {
        if (maxItemSize != null) {
            return maxItemSize;
        }

        double maxWidth = 0;
        double maxHeight = 0;
        for (LegendItemEx item : items) {
            if (isItemVisible(item)) {
                Dimension2D psize = item.getSize();
                double pwidth = psize.getWidth();
                double pheight = psize.getHeight();
                if (maxWidth < pwidth) {
                    maxWidth = pwidth;
                }
                if (maxHeight < pheight) {
                    maxHeight = pheight;
                }
            }
        }

        maxItemSize = new DoubleDimension2D(maxWidth, maxHeight);
        return maxItemSize;
    }

    public void draw(Graphics2D graphics) {
        if (!canContribute()) {
            return;
        }

        // drawBounds(graphics);

        // transform to relative paper space
        Graphics2D g = (Graphics2D) graphics.create();
        g.transform(getPaperTransform().getTransform());

        if (borderVisible) {
            Object aahint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(BORDER_COLOR);
            g.draw(getBounds());
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aahint);
        }

        for (LegendItemEx item : items) {
            if (isItemVisible(item)) {
                item.draw(g);
            }
        }

        g.dispose();
    }

}
