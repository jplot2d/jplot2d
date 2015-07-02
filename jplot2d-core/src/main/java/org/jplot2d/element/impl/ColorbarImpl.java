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
package org.jplot2d.element.impl;

import org.jplot2d.element.ColorbarPosition;
import org.jplot2d.element.ImageMapping;
import org.jplot2d.element.Plot;
import org.jplot2d.util.DoubleDimension2D;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * The colorbar contains 2 axis. The origin point is bottom-left corner of the bar (not include axis ticks and title).
 *
 * @author Jingjing Li
 */
public class ColorbarImpl extends ContainerImpl implements ColorbarEx {

    /**
     * the default width, 1.0 pt.
     */
    private static final float DEFAULT_AXISLINE_WIDTH = 1.0f;

    private final AxisTransformEx axisTransform;

    private final ColorbarAxisEx innerAxis;

    private final ColorbarAxisEx outerAxis;

    private double gap = 8;
    private double locX, locY;
    private double barWidth = 16;
    private double length;
    @Nonnull
    private ColorbarPosition position = ColorbarPosition.RIGHT;
    private float axisLineWidth = DEFAULT_AXISLINE_WIDTH;
    @Nullable
    private ImageMappingEx imageMapping;

    public ColorbarImpl() {
        this(new ColorbarAxisTransformImpl(), new ColorbarAxisImpl(0), new ColorbarAxisImpl(1));
        innerAxis.setSelectable(false);
        outerAxis.setSelectable(false);
        innerAxis.setTickHeight(4);
        innerAxis.setMinorTickHeight(2);
        outerAxis.setTickHeight(4);
        outerAxis.setMinorTickHeight(2);
        innerAxis.setLabelVisible(false);

        AxisTickManagerEx tickManager = new AxisTickManagerImpl();
        tickManager.setAxisTransform(axisTransform);
        tickManager.setAutoMinorTicks(false);
        innerAxis.setTickManager(tickManager);
        outerAxis.setTickManager(tickManager);

        setupAxes();
    }

    public ColorbarImpl(@Nonnull AxisTransformEx axf, @Nonnull ColorbarAxisEx innerAxis, @Nonnull ColorbarAxisEx outerAxis) {
        this.axisTransform = axf;
        this.innerAxis = innerAxis;
        this.outerAxis = outerAxis;
        this.innerAxis.setParent(this);
        this.outerAxis.setParent(this);
        setSelectable(true);
    }

    @Nonnull
    public AxisTransformEx getAxisTransform() {
        return axisTransform;
    }

    @Override
    public String getId() {
        if (getParent() != null) {
            int idx = getParent().indexOf(this);
            return "Colorbar" + idx;
        }
        return "Colorbar@" + Integer.toHexString(System.identityHashCode(this));
    }

    @Override
    public String getShortId() {
        if (getParent() != null) {
            String pid = getParent().getShortId();
            if (pid == null) {
                return getId();
            } else {
                return getId() + "." + pid;
            }
        } else {
            return getId();
        }
    }

    @Override
    public InvokeStep getInvokeStepFormParent() {
        if (parent == null) {
            return null;
        }

        try {
            Method method = Plot.class.getMethod("getColorbar", Integer.TYPE);
            return new InvokeStep(method, getParent().indexOf(this));
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
    }

    public PlotEx getParent() {
        return (PlotEx) super.getParent();
    }

    public void setParent(ElementEx parent) {
        this.parent = parent;

        // being removed from a plot
        if (parent == null) {
            setImageMapping(null);
        }
    }

    public ComponentEx[] getComponents() {
        return new ComponentEx[]{innerAxis, outerAxis};
    }

    @Override
    public void thisEffectiveColorChanged() {
        redraw(this);
        innerAxis.parentEffectiveColorChanged();
        outerAxis.parentEffectiveColorChanged();
    }

    @Override
    public void thisEffectiveFontChanged() {
        redraw(this);
        innerAxis.parentEffectiveFontChanged();
        outerAxis.parentEffectiveFontChanged();
    }

    @Override
    @Nullable
    public ImageMappingEx getImageMapping() {
        return imageMapping;
    }

    @Override
    public void setImageMapping(@Nullable ImageMapping mapping) {
        this.imageMapping = (ImageMappingEx) mapping;
        if (isVisible()) {
            invalidatePlot();
        }
    }

    @Nonnull
    public ColorbarAxisEx getInnerAxis() {
        return innerAxis;
    }

    @Nonnull
    public ColorbarAxisEx getOuterAxis() {
        return outerAxis;
    }

    @Override
    public void invalidate() {
        if (isVisible()) {
            invalidatePlot();
        }
    }

    @Override
    public Point2D getLocation() {
        return new Point2D.Double(locX, locY);
    }

    @Override
    public void directLocation(double locX, double locY) {
        this.locX = locX;
        this.locY = locY;
    }

    public Dimension2D getSize() {
        switch (getPosition()) {
            case LEFT:
            case RIGHT:
                return new DoubleDimension2D(getThickness(), length);
            case TOP:
            case BOTTOM:
                return new DoubleDimension2D(length, getThickness());
            default:
                return null;
        }
    }

    public Rectangle2D getBounds() {
        if (getParent() == null) {
            return null;
        }
        switch (getPosition()) {
            case LEFT:
            case RIGHT:
                return new Rectangle2D.Double(-getAsc(), 0, getThickness(), length);
            case TOP:
            case BOTTOM:
                return new Rectangle2D.Double(0, -getDesc(), length, getThickness());
            default:
                return null;
        }
    }


    public void setVisible(boolean visible) {
        super.setVisible(visible);
        invalidatePlot();
    }

    /**
     * Invalidate the parent plot.
     */
    private void invalidatePlot() {
        if (getParent() != null) {
            getParent().invalidate();
        }
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        if (this.length != length) {
            this.length = length;
            innerAxis.setLength(length);
            outerAxis.setLength(length);
        }
    }

    public double getAsc() {
        switch (getPosition()) {
            case LEFT:
            case TOP:
                return outerAxis.getAsc();
            case RIGHT:
            case BOTTOM:
                return innerAxis.getAsc();
            default:
                return 0;
        }
    }

    public double getDesc() {
        switch (getPosition()) {
            case LEFT:
            case TOP:
                return innerAxis.getDesc();
            case RIGHT:
            case BOTTOM:
                return outerAxis.getDesc();
            default:
                return 0;
        }
    }

    @Override
    public double getThickness() {
        return getBarWidth() + getAsc() + getDesc();
    }

    @Nonnull
    @Override
    public ColorbarPosition getPosition() {
        return position;
    }

    @Override
    public void setPosition(@Nonnull ColorbarPosition position) {
        this.position = position;
        setupAxes();
        if (isVisible()) {
            invalidatePlot();
        }
    }

    @Override
    public double getGap() {
        return gap;
    }

    @Override
    public void setGap(double gap) {
        this.gap = gap;
        if (isVisible()) {
            invalidatePlot();
        }
    }

    @Override
    public double getBarWidth() {
        return barWidth;
    }

    @Override
    public void setBarWidth(double width) {
        this.barWidth = width;
        setupAxes();
        if (isVisible()) {
            invalidatePlot();
        }
    }

    @Override
    public float getBorderLineWidth() {
        return axisLineWidth;
    }

    @Override
    public void setBorderLineWidth(float width) {
        axisLineWidth = width;
    }

    public void calcThickness() {
        innerAxis.calcThickness();
        outerAxis.calcThickness();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void setupAxes() {
        switch (position) {
            case LEFT:
                outerAxis.setLocation(0, 0);
                innerAxis.setLocation(barWidth, 0);
                break;
            case RIGHT:
                innerAxis.setLocation(0, 0);
                outerAxis.setLocation(barWidth, 0);
                break;
            case TOP:
                innerAxis.setLocation(0, 0);
                outerAxis.setLocation(0, barWidth);
                break;
            case BOTTOM:
                outerAxis.setLocation(0, 0);
                innerAxis.setLocation(0, barWidth);
                break;
        }
        innerAxis.invalidateThickness();
        outerAxis.invalidateThickness();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void draw(Graphics2D graphics) {

        Graphics2D g = (Graphics2D) graphics.create();

        g.transform(getPaperTransform().getTransform());

        g.setColor(getEffectiveColor());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setStroke(new BasicStroke(axisLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        switch (getPosition()) {
            case LEFT:
            case RIGHT:
                g.draw(new Line2D.Double(0, 0, barWidth, 0));
                g.draw(new Line2D.Double(0, length, barWidth, length));
            case TOP:
            case BOTTOM:
                g.draw(new Line2D.Double(0, 0, 0, barWidth));
                g.draw(new Line2D.Double(length, 0, length, barWidth));
        }

        g.dispose();
    }

    @Override
    public ComponentEx copyStructure(@Nonnull Map<ElementEx, ElementEx> orig2copyMap) {
        AxisTransformEx axfCopy = (AxisTransformEx) axisTransform.copyStructure(orig2copyMap);
        ColorbarAxisEx innerAxisCopy = (ColorbarAxisEx) innerAxis.copyStructure(orig2copyMap);
        ColorbarAxisEx outerAxisCopy = (ColorbarAxisEx) outerAxis.copyStructure(orig2copyMap);

        ColorbarImpl result = new ColorbarImpl(axfCopy, innerAxisCopy, outerAxisCopy);

        orig2copyMap.put(this, result);

        return result;
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        ColorbarImpl colorbar = (ColorbarImpl) src;

        this.gap = colorbar.gap;
        this.locX = colorbar.locX;
        this.locY = colorbar.locY;
        this.length = colorbar.length;
        this.barWidth = colorbar.barWidth;
        this.position = colorbar.position;
        this.axisLineWidth = colorbar.axisLineWidth;
    }


}
