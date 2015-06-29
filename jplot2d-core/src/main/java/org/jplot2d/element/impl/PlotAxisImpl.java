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

import org.jplot2d.element.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class PlotAxisImpl extends AxisImpl implements PlotAxisEx {

    @Nullable
    private AxisOrientation orientation;

    @Nonnull
    private AxisPosition position = AxisPosition.NEGATIVE_SIDE;

    private boolean showGridLines, showMinorGridLines;

    public PlotAxisImpl() {
        this(new AxisTitleImpl());
        setSelectable(true);
    }

    public PlotAxisImpl(AxisTitleEx axisTitle) {
        super(axisTitle);
    }

    @Override
    public String getId() {
        if (getParent() != null && orientation != null) {
            switch (orientation) {
                case HORIZONTAL:
                    int xidx = getParent().indexOfXAxis(this);
                    return "X" + xidx;
                case VERTICAL:
                    int yidx = getParent().indexOfYAxis(this);
                    return "Y" + yidx;
            }
        }
        return "Axis@" + Integer.toHexString(System.identityHashCode(this));
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
        if (getParent() == null || orientation == null) {
            return null;
        }

        try {
            switch (orientation) {
                case HORIZONTAL:
                    Method xmethod = Plot.class.getMethod("getXAxis", Integer.TYPE);
                    return new InvokeStep(xmethod, getParent().indexOfXAxis(this));
                case VERTICAL:
                    Method ymethod = Plot.class.getMethod("getYAxis", Integer.TYPE);
                    return new InvokeStep(ymethod, getParent().indexOfYAxis(this));
                default:
                    return null;
            }
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
    }

    @Override
    public PlotEx getParent() {
        return (PlotEx) super.getParent();
    }

    @Override
    public void setParent(ElementEx parent) {
        this.parent = parent;

        // being removed from a plot
        if (parent == null && tickManager != null) {
            if (tickManager.getParent() == null) {
                // quit the tick manager if axis is not its only member
                setTickManager(null);
            } else if (tickManager.getAxisTransform() != null) {
                if (tickManager.getAxisTransform().getParent() == null) {
                    // quit the range manager if tick manager is not its only member
                    tickManager.setAxisTransform(null);
                } else if (tickManager.getAxisTransform().getLockGroup() != null && tickManager.getAxisTransform().getLockGroup().getParent() == null) {
                    // quit the lock group if range manager is not the lock group's only member
                    tickManager.getAxisTransform().setLockGroup(null);
                }
            }
        }
    }

    @Override
    public Map<Element, Element> getMooringMap() {
        Map<Element, Element> result = new HashMap<>();

        if (tickManager != null && tickManager.getParent() == this) {
            AxisTransformEx axf = tickManager.getAxisTransform();
            if (axf != null && axf.getParent() == tickManager) {
                for (LayerEx layer : axf.getLayers()) {
                    result.put(axf, layer);
                }
            }
        }

        return result;
    }

    @Override
    @Nullable
    public AxisOrientation getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(@Nonnull AxisOrientation orientation) {
        this.orientation = orientation;
    }

    @Override
    @Nonnull
    public AxisPosition getPosition() {
        return position;
    }

    @Override
    public void setPosition(@Nonnull AxisPosition position) {
        this.position = position;
        invalidateThickness();
        if (isVisible()) {
            invalidateParent();
        }
    }

    @Override
    public boolean isGridLines() {
        return showGridLines;
    }

    @Override
    public void setGridLines(boolean showGridLines) {
        this.showGridLines = showGridLines;
        redraw(this);
    }

    @Override
    public boolean isMinorGridLines() {
        return showMinorGridLines;
    }

    @Override
    public void setMinorGridLines(boolean showGridLines) {
        this.showMinorGridLines = showGridLines;
        redraw(this);
    }

    @Override
    public ComponentEx copyStructure(@Nonnull Map<ElementEx, ElementEx> orig2copyMap) {
        PlotAxisImpl result = new PlotAxisImpl((AxisTitleEx) title.copyStructure(orig2copyMap));

        orig2copyMap.put(this, result);

        // copy or link axis tick manager
        if (tickManager != null) {
            AxisTickManagerEx atmCopy = (AxisTickManagerEx) orig2copyMap.get(tickManager);
            if (atmCopy == null) {
                atmCopy = (AxisTickManagerEx) tickManager.copyStructure(orig2copyMap);
            }
            result.tickManager = atmCopy;
            atmCopy.addAxis(result);
        }

        return result;
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        PlotAxisImpl axis = (PlotAxisImpl) src;

        this.orientation = axis.orientation;
        this.position = axis.position;
        this.showGridLines = axis.showGridLines;
        this.showMinorGridLines = axis.showMinorGridLines;
    }

    protected boolean isTickAscSide() {
        boolean axisHorizontal = (getOrientation() == AxisOrientation.HORIZONTAL);
        boolean axisPositiveSide = (getPosition() == AxisPosition.POSITIVE_SIDE);
        boolean outwardSide = (getTickSide() == AxisTickSide.OUTWARD);
        return (axisHorizontal == axisPositiveSide) == outwardSide;
    }

    protected boolean isLabelAscSide() {
        boolean axisHorizontal = (getOrientation() == AxisOrientation.HORIZONTAL);
        boolean axisPositiveSide = (getPosition() == AxisPosition.POSITIVE_SIDE);
        boolean outwardSide = (getLabelSide() == AxisLabelSide.OUTWARD);
        return (axisHorizontal == axisPositiveSide) == outwardSide;
    }

    @Nonnull
    protected HAlign getLabelHAlign() {
        boolean axisPositiveSide = (getPosition() == AxisPosition.POSITIVE_SIDE);
        boolean outwardSide = (getLabelSide() == AxisLabelSide.OUTWARD);
        if (axisPositiveSide == outwardSide) {
            return HAlign.LEFT;
        } else {
            return HAlign.RIGHT;
        }
    }

    protected boolean isTitleAscSide() {
        boolean axisHorizontal = (getOrientation() == AxisOrientation.HORIZONTAL);
        boolean axisPositiveSide = (getPosition() == AxisPosition.POSITIVE_SIDE);
        return axisHorizontal == axisPositiveSide;
    }

    /**
     * Draw ticks and grid lines.
     *
     * @param g Graphics
     */
    @Override
    protected void drawTicks(Graphics2D g) {
        if (tickManager == null || getParent() == null) {
            return;
        }

        Object tvs = tickManager.getTickValues();
        Object mvs = tickManager.getMinorTickValues();
        int tvslen = Array.getLength(tvs);
        int mvslen = Array.getLength(mvs);

		/*
         * implement notes: grid lines must be drawn before ticks, otherwise the tick may be overlapped.
		 */
        if (showGridLines && tvslen > 0) {
            Stroke oldStroke = g.getStroke();
            Color oldColor = g.getColor();

            Stroke gridLineStroke = new BasicStroke(((BasicStroke) oldStroke).getLineWidth() / 2, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10.0f, new float[]{2.0f, 2.0f}, 0.0f);
            g.setStroke(gridLineStroke);

            Color c = getEffectiveColor();
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() / 2));

            Dimension2D pcontentSize = getParent().getContentSize();
            PlotAxisEx[] orthoAxes = getOrthoAxes();
            for (int i = 0; i < tvslen; i++) {
                double xp = transTickToPaper(Array.getDouble(tvs, i));
                drawGridLine(g, xp, pcontentSize, orthoAxes);
            }

            g.setStroke(oldStroke);
            g.setColor(oldColor);
        }
        if (showMinorGridLines && mvslen > 0) {
            Stroke oldStroke = g.getStroke();
            Color oldColor = g.getColor();

            Stroke gridLineStroke = new BasicStroke(((BasicStroke) oldStroke).getLineWidth() / 4, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10.0f, new float[]{2.0f, 2.0f}, 0.0f);
            g.setStroke(gridLineStroke);

            Color c = getEffectiveColor();
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() / 4));

            Dimension2D pcontentSize = getParent().getContentSize();
            PlotAxisEx[] orthoAxes = getOrthoAxes();
            for (int i = 0; i < mvslen; i++) {
                double xp = transTickToPaper(Array.getDouble(mvs, i));
                drawGridLine(g, xp, pcontentSize, orthoAxes);
            }

            g.setStroke(oldStroke);
            g.setColor(oldColor);
        }

        super.drawTicks(g);
    }


    /**
     * Draw vertical grid lines on axis
     *
     * @param g           the Graphics2D
     * @param xp          x location
     * @param contentSize the content size
     * @param orthoAxes   the orthogonal axes of this axis
     */
    private void drawGridLine(Graphics2D g, double xp, Dimension2D contentSize, PlotAxisEx[] orthoAxes) {
        if (getOrientation() == AxisOrientation.HORIZONTAL) {
            /* avoid overlapping on the other Y axes */
            for (PlotAxisEx axis : orthoAxes) {
                if (Math.abs(axis.getLocation().getX() - xp) < DEFAULT_AXISLINE_WIDTH) {
                    return;
                }
            }
            if (position == AxisPosition.NEGATIVE_SIDE) {
                Shape line = new Line2D.Double(xp, 0, xp, contentSize.getHeight());
                g.draw(line);
            } else {
                Shape line = new Line2D.Double(xp, -contentSize.getHeight(), xp, 0);
                g.draw(line);
            }
        } else {
            /* avoid overlapping on the other X axes */
            for (PlotAxisEx axis : orthoAxes) {
                if (Math.abs(axis.getLocation().getY() - xp) < DEFAULT_AXISLINE_WIDTH) {
                    return;
                }
            }
            if (position == AxisPosition.NEGATIVE_SIDE) {
                Shape line = new Line2D.Double(xp, -contentSize.getWidth(), xp, 0);
                g.draw(line);
            } else {
                Shape line = new Line2D.Double(xp, 0, xp, contentSize.getWidth());
                g.draw(line);
            }
        }
    }


    @Nonnull
    private PlotAxisEx[] getOrthoAxes() {
        assert getParent() != null;

        java.util.List<PlotAxisEx> axes = new ArrayList<>();
        if (getOrientation() == AxisOrientation.HORIZONTAL) {
            Collections.addAll(axes, getParent().getYAxes());
        } else {
            Collections.addAll(axes, getParent().getXAxes());
        }
        return axes.toArray(new PlotAxisEx[axes.size()]);
    }


}
