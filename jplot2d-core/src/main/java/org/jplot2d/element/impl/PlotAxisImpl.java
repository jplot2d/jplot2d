package org.jplot2d.element.impl;

import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.AxisPosition;
import org.jplot2d.element.Element;
import org.jplot2d.element.Plot;

import javax.annotation.Nonnull;
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
        if (getParent() != null) {
            switch (getOrientation()) {
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
        if (parent == null) {
            return null;
        }

        try {
            switch (getOrientation()) {
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
    public Map<Element, Element> getMooringMap() {
        Map<Element, Element> result = new HashMap<>();

        if (tickManager != null && tickManager.getParent() == this) {
            AxisTransformEx rman = tickManager.getAxisTransform();
            if (rman.getParent() == tickManager) {
                for (LayerEx layer : rman.getLayers()) {
                    result.put(rman, layer);
                }
            }
        }

        return result;
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

        this.position = axis.position;
        this.showGridLines = axis.showGridLines;
        this.showMinorGridLines = axis.showMinorGridLines;

    }

    /**
     * Draw ticks and grid lines.
     *
     * @param g Graphics
     */
    @Override
    protected void drawTicks(Graphics2D g) {
        if (tickManager == null) {
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


    private PlotAxisEx[] getOrthoAxes() {
        java.util.List<PlotAxisEx> axes = new ArrayList<>();
        if (getOrientation() == AxisOrientation.HORIZONTAL) {
            Collections.addAll(axes, getParent().getYAxes());
        } else {
            Collections.addAll(axes, getParent().getXAxes());
        }
        return axes.toArray(new PlotAxisEx[axes.size()]);
    }


}
