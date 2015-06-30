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
import org.jplot2d.tex.MathElement;
import org.jplot2d.tex.MathLabel;
import org.jplot2d.transform.PaperTransform;
import org.jplot2d.util.DoubleDimension2D;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.*;
import java.lang.reflect.Array;

/**
 * @author Jingjing Li
 */
public abstract class AxisImpl extends ComponentImpl implements AxisEx {

    /**
     * the default width, 1.0 pt.
     */
    protected static final float DEFAULT_AXISLINE_WIDTH = 1.0f;

    /* the gap between label and tick */
    private static final double LABEL_GAP_RATIO = 1.0 / 4;

    /* the gap between title and label */
    private static final double TITLE_GAP_RATIO = 1.0 / 4;

    protected final AxisTitleEx title;

    @Nullable
    protected AxisTickManagerEx tickManager;

    private double locX, locY;

    private double length;

    private float axisLineWidth = DEFAULT_AXISLINE_WIDTH;

    private boolean tickVisible = true;

    @Nonnull
    private AxisTickSide tickSide = AxisTickSide.INWARD;

    private double tickHeight = 8.0;

    private double minorHeight = 4.0;

    private float tickLineWidth = DEFAULT_AXISLINE_WIDTH / 2;

    private boolean labelVisible = true;

    @Nonnull
    private AxisOrientation labelOrientation = AxisOrientation.HORIZONTAL;

    @Nonnull
    private AxisLabelSide labelSide = AxisLabelSide.OUTWARD;

    @Nullable
    private Color labelColor;

	/* thickness */

    private double asc, desc;

    private double labelOffset;

    private double labelRotation;

    private HAlign labelHAlign;

    private VAlign labelVAlign;

    private double titleOffset;

    private VAlign titleVAlign;

    private boolean thicknessCalculationNeeded = true;

    private Font actualLabelFont;

    public AxisImpl() {
        setSelectable(true);

        title = new AxisTitleImpl();
        title.setParent(this);
    }

    protected AxisImpl(AxisTitleEx title) {
        this.title = title;
        title.setParent(this);
    }

    /**
     * Returns the labels normal size.
     *
     * @return the labels normal size.
     */
    public static Dimension2D[] getLabelsPaperSize(MathElement[] labels, Font labelFont) {
        Dimension2D[] ss = new Dimension2D[labels.length];

        for (int i = 0; i < labels.length; i++) {
            MathLabel labelDrawer = new MathLabel(labels[i], labelFont, VAlign.MIDDLE, HAlign.CENTER);
            Rectangle2D rect = labelDrawer.getBounds();
            ss[i] = new DoubleDimension2D(rect.getWidth(), rect.getHeight());
        }

        return ss;
    }

    @Override
    public AxisContainerEx getParent() {
        return (AxisContainerEx) super.getParent();
    }

    @Override
    public void thisEffectiveColorChanged() {
        redraw(this);
    }

    @Override
    public void thisEffectiveFontChanged() {
        invalidateThickness();
        redraw(this);
    }

    @Override
    public Point2D getLocation() {
        return new Point2D.Double(locX, locY);
    }

    @Override
    public void setLocation(double locX, double locY) {
        if (getLocation().getX() != locX || getLocation().getY() != locY) {
            this.locX = locX;
            this.locY = locY;
            redraw(this);
        }
    }

    @Override
    public Dimension2D getSize() {
        if (getOrientation() == null) {
            return null;
        } else {
            switch (getOrientation()) {
                case HORIZONTAL:
                    return new DoubleDimension2D(getLength(), getThickness());
                case VERTICAL:
                    return new DoubleDimension2D(getThickness(), getLength());
                default:
                    return null;
            }
        }
    }

    @Override
    public Rectangle2D getBounds() {
        if (getParent() == null) {
            return null;
        } else {
            return new Rectangle2D.Double(0, -desc, getLength(), getThickness());
        }
    }

    @Override
    public PaperTransform getPaperTransform() {
        PaperTransform pxf = super.getPaperTransform();
        if (pxf == null) {
            return null;
        } else {
            if (getOrientation() == AxisOrientation.VERTICAL) {
                pxf = pxf.rotate(Math.PI / 2);
            }
            return pxf;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        invalidateParent();
    }

    /**
     * Invalidate the parent container.
     */
    protected void invalidateParent() {
        if (getParent() != null) {
            getParent().invalidate();
        }
    }

    @Nullable
    @Override
    public AxisTickManagerEx getTickManager() {
        return tickManager;
    }

    @Override
    public void setTickManager(@Nullable AxisTickManager tickManager) {
        if (this.tickManager != null) {
            this.tickManager.removeAxis(this);
        }
        this.tickManager = (AxisTickManagerEx) tickManager;
        if (this.tickManager != null) {
            this.tickManager.addAxis(this);
        }
    }

    @Override
    public double getLength() {
        return length;
    }

    @Override
    public void setLength(double length) {
        if (this.length != length) {
            this.length = length;
            if (tickManager != null && tickManager.getAxisTransform() != null && tickManager.getAxisTransform().getLockGroup() != null
                    && tickManager.getAxisTransform().getLockGroup().isAutoRange()) {
                tickManager.getAxisTransform().getLockGroup().reAutoRange();
            }
        }
    }

    @Override
    public float getAxisLineWidth() {
        return axisLineWidth;
    }

    @Override
    public void setAxisLineWidth(float width) {
        this.axisLineWidth = width;
        redraw(this);
    }

    @Override
    public boolean isTickVisible() {
        return tickVisible;
    }

    @Override
    public void setTickVisible(boolean visible) {
        this.tickVisible = visible;
        invalidateThickness();
        redraw(this);
    }

    @Nonnull
    @Override
    public AxisTickSide getTickSide() {
        return tickSide;
    }

    @Override
    public void setTickSide(@Nonnull AxisTickSide side) {
        this.tickSide = side;
        invalidateThickness();
        redraw(this);
    }

    @Override
    public double getTickHeight() {
        return tickHeight;
    }

    @Override
    public void setTickHeight(double height) {
        this.tickHeight = height;
        invalidateThickness();
        redraw(this);
    }

    @Override
    public double getMinorTickHeight() {
        return minorHeight;
    }

    @Override
    public void setMinorTickHeight(double height) {
        this.minorHeight = height;
    }

    @Override
    public float getTickLineWidth() {
        return tickLineWidth;
    }

    @Override
    public void setTickLineWidth(float width) {
        this.tickLineWidth = width;
        redraw(this);
    }

    @Override
    public boolean isLabelVisible() {
        return labelVisible;
    }

    @Override
    public void setLabelVisible(boolean visible) {
        this.labelVisible = visible;
        invalidateThickness();
        redraw(this);
    }

    @Nonnull
    @Override
    public AxisOrientation getLabelOrientation() {
        return labelOrientation;
    }

    @Override
    public void setLabelOrientation(@Nonnull AxisOrientation orientation) {
        this.labelOrientation = orientation;
        invalidateThickness();
        redraw(this);
    }

    private boolean isLabelSameOrientation() {
        return getOrientation() == getLabelOrientation();
    }

    @Nonnull
    @Override
    public AxisLabelSide getLabelSide() {
        return labelSide;
    }

    @Override
    public void setLabelSide(@Nonnull AxisLabelSide side) {
        this.labelSide = side;
        invalidateThickness();
        redraw(this);
    }

    @Override
    @Nullable
    public Color getLabelColor() {
        return labelColor;
    }

    @Override
    public void setLabelColor(@Nullable Color color) {
        this.labelColor = color;
        redraw(this);
    }

    private Font getActualLabelFont() {
        if (actualLabelFont != null) {
            return actualLabelFont;
        } else {
            return getEffectiveFont();
        }
    }

    @Override
    public void setActualFont(@Nonnull Font font) {
        if (!font.equals(this.actualLabelFont)) {
            this.actualLabelFont = font;
            invalidateThickness();
            redraw(this);
        }
    }

    @Override
    @Nonnull
    public AxisTitleEx getTitle() {
        return title;
    }

    @Override
    public double getThickness() {
        return asc + desc;
    }

    @Override
    public double getAsc() {
        return asc;
    }

    /**
     * always negative
     */
    @Override
    public double getDesc() {
        return desc;
    }

    @Override
    public void invalidateThickness() {
        thicknessCalculationNeeded = true;
    }

    @Override
    public void calcThickness() {

        if (!thicknessCalculationNeeded) {
            return;
        }
        thicknessCalculationNeeded = false;

        asc = 0;
        desc = 0;
        labelOffset = 0;
        labelVAlign = null;
        labelHAlign = null;
        titleOffset = 0;
        titleVAlign = null;

        if (isTickVisible()) {
            if (isTickBothSide() || isTickAscSide()) {
                asc += getTickHeight();
            }
            if (isTickBothSide() || !isTickAscSide()) {
                desc += getTickHeight();
            }
        }

        if (isLabelVisible()) {
            double labelHeight = getLabelHeight();
            if (isLabelAscSide()) {
                labelOffset = asc + labelHeight * LABEL_GAP_RATIO;
                if (isLabelSameOrientation()) {
                    asc = labelOffset + labelHeight;
                } else {
                    asc = labelOffset + getLabelsMaxPaperWidth();
                }
            } else {
                labelOffset = -desc - labelHeight * LABEL_GAP_RATIO;
                if (isLabelSameOrientation()) {
                    desc = -labelOffset + labelHeight;
                } else {
                    desc = -labelOffset + getLabelsMaxPaperWidth();
                }
            }
            if (isLabelSameOrientation()) {
                labelRotation = 0;
                labelHAlign = HAlign.CENTER;
                if (isLabelAscSide()) {
                    labelVAlign = VAlign.BOTTOM;
                } else {
                    labelVAlign = VAlign.TOP;
                }
            } else {
                if (getOrientation() == AxisOrientation.HORIZONTAL) {
                    labelRotation = Math.PI / 2.0;
                } else {
                    labelRotation = -Math.PI / 2.0;
                }
                labelVAlign = VAlign.MIDDLE;
                labelHAlign = getLabelHAlign();
            }
        }

        if (getTitle().isVisible() && getTitle().getTextModel() != null) {
            if (isTitleAscSide()) {
                titleVAlign = VAlign.BOTTOM;
                getTitle().setVAlign(titleVAlign);
                double titleHeight = getTitle().getSize().getHeight();
                titleOffset = asc + titleHeight * TITLE_GAP_RATIO;
                asc = titleOffset + titleHeight;
            } else {
                titleVAlign = VAlign.TOP;
                getTitle().setVAlign(titleVAlign);
                double titleHeight = getTitle().getSize().getHeight();
                titleOffset = -desc - titleHeight * TITLE_GAP_RATIO;
                desc = -titleOffset + titleHeight;
            }
        }

    }

    private boolean isTickBothSide() {
        return getTickSide() == AxisTickSide.BOTH;
    }

    protected abstract boolean isTickAscSide();

    protected abstract boolean isLabelAscSide();

    protected abstract boolean isTitleAscSide();

    /**
     * Return the label h-align when horizontal labels on vertical axis.
     *
     * @return the label h-align
     */
    @Nonnull
    protected abstract HAlign getLabelHAlign();

    /**
     * Returns the height label by axis effective font.
     *
     * @return the label height
     */
    private double getLabelHeight() {
        FontRenderContext frc = new FontRenderContext(null, false, true);
        LineMetrics lm = getEffectiveFont().getLineMetrics("Can be any string", frc);
        return (lm.getAscent() + lm.getDescent());
    }

    /**
     * Traverse all labels to find the Maximum Width. The width is calculated by axis effective font, not shrunk actual font.
     *
     * @return the maximum label width.
     */
    private double getLabelsMaxPaperWidth() {
        if (tickManager == null) {
            return 0;
        }
        Dimension2D[] labelsSize = getLabelsPaperSize(tickManager.getLabelModels(), getEffectiveFont());
        double maxWidth = 0;
        double maxHeight = 0;
        for (Dimension2D aLabelsSize : labelsSize) {
            if (maxWidth < aLabelsSize.getWidth()) {
                maxWidth = aLabelsSize.getWidth();
            }
            if (maxHeight < aLabelsSize.getHeight()) {
                maxHeight = aLabelsSize.getHeight();
            }
        }
        return maxWidth;
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        AxisImpl axis = (AxisImpl) src;

        this.locX = axis.locX;
        this.locY = axis.locY;
        this.length = axis.length;
        this.axisLineWidth = axis.axisLineWidth;
        this.tickVisible = axis.tickVisible;
        this.tickSide = axis.tickSide;
        this.tickHeight = axis.tickHeight;
        this.minorHeight = axis.minorHeight;
        this.tickLineWidth = axis.tickLineWidth;
        this.labelVisible = axis.labelVisible;
        this.labelOrientation = axis.labelOrientation;
        this.labelSide = axis.labelSide;
        this.labelColor = axis.labelColor;

        this.asc = axis.asc;
        this.desc = axis.desc;
        this.labelOffset = axis.labelOffset;
        this.labelRotation = axis.labelRotation;
        this.labelHAlign = axis.labelHAlign;
        this.labelVAlign = axis.labelVAlign;
        this.titleOffset = axis.titleOffset;
        this.titleVAlign = axis.titleVAlign;

        this.actualLabelFont = axis.actualLabelFont;
    }

    public void draw(Graphics2D graphics) {

        Graphics2D g = (Graphics2D) graphics.create();

        g.transform(getPaperTransform().getTransform());

        g.setColor(getEffectiveColor());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setStroke(new BasicStroke(axisLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        drawAxisLine(g);

        g.setStroke(new BasicStroke(tickLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        drawTicks(g);

        if (isLabelVisible()) {
            drawLabels(g, labelVAlign, labelHAlign);
        }

        if (getTitle().isVisible() && getTitle().getTextModel() != null) {
            drawTitle(graphics);
        }

        g.dispose();
    }

    private void drawAxisLine(Graphics2D g2) {
        Shape s = new Line2D.Double(0, 0, getLength(), 0);
        g2.draw(s);
    }

    /**
     * Draw ticks and grid lines.
     *
     * @param g Graphics
     */
    protected void drawTicks(Graphics2D g) {
        if (tickManager == null) {
            return;
        }

        Object tvs = tickManager.getTickValues();
        Object mvs = tickManager.getMinorTickValues();
        int tvslen = Array.getLength(tvs);
        int mvslen = Array.getLength(mvs);

        if (tickVisible) {
            for (int i = 0; i < tvslen; i++) {
                double xp = transTickToPaper(Array.getDouble(tvs, i));
                drawTic(g, xp, getTickHeight());
            }
            for (int i = 0; i < mvslen; i++) {
                double xp = transTickToPaper(Array.getDouble(mvs, i));
                drawTic(g, xp, getMinorTickHeight());
            }
        }
    }

    /**
     * Draw all labels of this axis.
     *
     * @param g         the Graphics2D
     * @param vertalign the vertical align
     * @param horzalign the horizontal align
     */
    private void drawLabels(Graphics2D g, VAlign vertalign, HAlign horzalign) {
        if (tickManager == null) {
            return;
        }

        Object tvs = tickManager.getTickValues();
        MathElement[] labels = tickManager.getLabelModels();

        Color color = getLabelColor();
        g.setColor(color);

        AffineTransform oldTransform = g.getTransform();

        for (int i = 0; i < labels.length; i++) {
            double x = Array.getDouble(tvs, i * tickManager.getLabelInterval());
            double xt = transTickToPaper(x);

            MathLabel label = new MathLabel(labels[i], getActualLabelFont(), vertalign, horzalign);

            g.translate(xt, labelOffset);
            if (labelRotation != 0) {
                g.rotate(labelRotation);
            }
            g.scale(1, -1);
            label.draw(g);

            g.setTransform(oldTransform);
        }

    }

    private void drawTitle(Graphics2D g) {
        double x = getLength() * 0.5;
        getTitle().draw(g, x, titleOffset);
    }

    private void drawTic(Graphics2D g, double xp, double ticHeight) {

        double yp0, yp1;

        if (isTickBothSide()) {
            yp0 = +ticHeight;
            yp1 = -ticHeight;
        } else if (isTickAscSide()) {
            yp0 = +ticHeight;
            yp1 = 0;
        } else {
            yp0 = 0;
            yp1 = -ticHeight;
        }

        Shape line = new Line2D.Double(xp, yp0, xp, yp1);
        g.draw(line);
    }

    /**
     * Transform tick value to paper value on this axis.
     *
     * @param tickValue the tick values
     * @return paper value
     */
    protected double transTickToPaper(double tickValue) {
        assert tickManager != null;
        assert tickManager.getAxisTransform() != null;

        double uv;
        if (tickManager.getTickTransform() != null) {
            uv = tickManager.getTickTransform().transformTick2User(tickValue);
        } else {
            uv = tickValue;
        }
        return tickManager.getAxisTransform().getNormalTransform().convToNR(uv) * length;
    }

}
