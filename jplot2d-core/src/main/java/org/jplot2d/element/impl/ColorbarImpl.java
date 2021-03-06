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

import org.jplot2d.data.DoubleDataBuffer;
import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.element.ColorbarPosition;
import org.jplot2d.element.ImageMapping;
import org.jplot2d.element.Plot;
import org.jplot2d.image.ColorMap;
import org.jplot2d.image.IntensityTransform;
import org.jplot2d.transform.PaperTransform;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * The colorbar contains 2 axis. The origin point is bottom-left corner of the bar (not include axis ticks and title).
 *
 * @author Jingjing Li
 */
public class ColorbarImpl extends ContainerImpl implements ColorbarEx, IntermediateCacheEx {

    private static final WeakHashMap<ImageKey, BufferedImage> cache = new WeakHashMap<>();

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
    private ImageMappingEx mapping;

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

    private static BufferedImage createImage(ImageKey key) {
        int width = key.w;
        int height = key.h;
        Range limits = key.limits;

        // create ImageDataBuffer
        double[] line = new double[width];
        double step = limits.getSpan() / width;
        if (key.inverted) {
            int sc = width - 1;
            for (int i = 0; i < width; i++, sc--) {
                line[i] = step * sc;
            }
        } else {
            for (int i = 0; i < width; i++) {
                line[i] = step * i;
            }
        }
        double[][] double2d = new double[height][width];
        for (int r = 0; r < height; r++) {
            double2d[r] = line;
        }
        ImageDataBuffer idb = new DoubleDataBuffer.Array2D(double2d);

        // create image
        int bandBits = key.outputBits;
        Object bandData = ImageZscaleCache.zscaleLimits(idb, width, height, limits,
                key.intensityTransform, key.bias, key.gain, bandBits);
        return ImageGraphImpl.createImage(bandData, bandBits, width, height, key.colorMap);
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
        return mapping;
    }

    @Override
    public void setImageMapping(@Nullable ImageMapping mapping) {
        this.mapping = (ImageMappingEx) mapping;
        if (isVisible()) {
            invalidatePlot();
        }
    }

    @Override
    public void linkImageMapping(@Nullable ImageMappingEx mapping) {
        this.mapping = mapping;
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
        switch (position) {
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
        switch (position) {
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
        switch (position) {
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
        switch (position) {
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

    @Nullable
    public Object createCacheHolder() {
        if (mapping == null) {
            return null;
        }

        PaperTransform pxf = getPaperTransform();
        int width = (int) (pxf.getScale() * length);
        int height = (int) (pxf.getScale() * barWidth);

        ImageKey key = new ImageKey(axisTransform.isInverted(), width, height, mapping);
        synchronized (cache) {
            BufferedImage image = cache.remove(key);
            cache.put(key, image);
        }
        return key;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void draw(Graphics2D graphics) {

        drawImage(graphics);

        Graphics2D g = (Graphics2D) graphics.create();

        g.transform(getPaperTransform().getTransform());

        g.setColor(getEffectiveColor());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setStroke(new BasicStroke(axisLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        switch (position) {
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

    private void drawImage(Graphics2D graphics) {
        if (mapping == null) {
            return;
        }

        Range limits = mapping.getLimits();

        // limits is null means there is no valid data
        if (limits == null) {
            return;
        }

        // find a proper region to process
        PaperTransform pxf = getPaperTransform();
        int width = (int) (pxf.getScale() * length);
        int height = (int) (pxf.getScale() * barWidth);

        // create image
        ImageKey key = new ImageKey(axisTransform.isInverted(), width, height, mapping);
        BufferedImage image;
        synchronized (cache) {
            image = cache.get(key);
            if (cache.get(key) == null) {
                image = createImage(key);
                cache.put(key, image);
            }
        }

        // draw the image
        Graphics2D g = (Graphics2D) graphics.create();
        // Shape clip = getPaperTransform().getPtoD(getBounds());
        // g.setClip(clip);

        double xorig = pxf.getXPtoD(0);
        double yorig = pxf.getYPtoD(0);
        AffineTransform at = AffineTransform.getTranslateInstance(xorig, yorig);
        switch (position) {
            case LEFT:
            case RIGHT:
                at.rotate(-Math.PI / 2);
                break;
            case TOP:
            case BOTTOM:
                at.scale(1, -1);
                break;
        }
        g.drawRenderedImage(image, at);

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

    private class ImageKey {
        private final boolean inverted;
        private final int w, h;
        private final Range limits;
        private final IntensityTransform intensityTransform;
        private final double bias, gain;
        private final int outputBits;
        private final ColorMap colorMap;

        private ImageKey(boolean inverted, int w, int h, @Nonnull ImageMappingEx mapping) {
            this.inverted = inverted;
            this.w = w;
            this.h = h;
            this.limits = mapping.getLimits();
            this.intensityTransform = mapping.getIntensityTransform();
            this.bias = mapping.getBias();
            this.gain = mapping.getGain();
            this.outputBits = mapping.getILUTOutputBits();
            this.colorMap = mapping.getColorMap();
        }

        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ImageKey key = (ImageKey) obj;
            boolean limitsMatch = key.limits == limits || (key.limits != null && key.limits.equals(limits));
            return key.inverted == inverted && key.w == w && key.h == h && limitsMatch
                    && key.intensityTransform == intensityTransform && key.bias == bias && key.gain == gain
                    && key.outputBits == outputBits && key.colorMap == colorMap;
        }

        public int hashCode() {
            long bits = java.lang.Double.doubleToLongBits(w);
            if (inverted) {
                bits = ~bits;
            }
            bits += java.lang.Double.doubleToLongBits(h) * 31;
            if (limits != null) {
                bits += java.lang.Double.doubleToLongBits(limits.getStart()) * 37;
                bits += java.lang.Double.doubleToLongBits(limits.getEnd()) * 41;
            }
            bits += java.lang.Double.doubleToLongBits(bias) * 43;
            bits += java.lang.Double.doubleToLongBits(gain) * 47;
            int hash = (((int) bits) ^ ((int) (bits >> 32)));
            if (colorMap == null) {
                return hash;
            } else {
                return colorMap.hashCode() ^ hash;
            }
        }
    }


}
