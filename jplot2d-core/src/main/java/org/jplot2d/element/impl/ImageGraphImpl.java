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

import org.jplot2d.data.ImageCoordinateReference;
import org.jplot2d.data.SingleBandImageData;
import org.jplot2d.element.ImageMapping;
import org.jplot2d.image.ColorMap;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.transform.PaperTransform;
import org.jplot2d.util.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.image.*;
import java.util.Map;
import java.util.WeakHashMap;

public class ImageGraphImpl extends GraphImpl implements ImageGraphEx, IntermediateCacheEx {

    private static final WeakHashMap<Object, Object> cache = new WeakHashMap<>();

    @Nullable
    private ImageMappingEx mapping;

    @Nullable
    private SingleBandImageData data;

    public ImageGraphImpl() {
        super();
    }

    private static WritableRaster createRaster(RasterKey rasterKey) {

        // create a z-scaled bandData
        int width = rasterKey.bandKey.w;
        int height = rasterKey.bandKey.h;
        int lutOutputBits = rasterKey.bandKey.outputBits;
        Object bandData = ImageZscaleCache.getValue(rasterKey.bandKey);

        // create raster
        WritableRaster raster;
        if (lutOutputBits <= Byte.SIZE) {
            // create a SampleModel for byte data
            SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width, height, 1, width, new int[]{0});
            // and a DataBuffer with the image data
            DataBufferByte dbuffer = new DataBufferByte((byte[]) bandData, width * height);
            // create a raster
            raster = Raster.createWritableRaster(sampleModel, dbuffer, null);
        } else {
            // create a SampleModel for short data
            SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_USHORT, width, height, 1, width, new int[]{0});
            // and a DataBuffer with the image data
            DataBufferUShort dbuffer = new DataBufferUShort((short[]) bandData, width * height);
            // create a raster
            raster = Raster.createWritableRaster(sampleModel, dbuffer, null);
        }

        // create a child raster in viewport
        raster = raster.createWritableChild(rasterKey.portX, rasterKey.portY, rasterKey.portW, rasterKey.portH, 0, 0, null);

        // zoom raster to device size
        double xscale = rasterKey.xscale;
        double yscale = rasterKey.yscale;
        AffineTransform scaleAT = AffineTransform.getScaleInstance(xscale, yscale);
        int op;
        if (xscale > 1 || yscale > 1) {
            op = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
        } else {
            op = AffineTransformOp.TYPE_BILINEAR;
        }
        AffineTransformOp axop = new AffineTransformOp(scaleAT, op);
        raster = axop.filter(raster, null);

        return raster;
    }

    public void setParent(ElementEx parent) {
        this.parent = parent;

        // being removed
        if (parent == null) {
            if (mapping != null && mapping.getParent() == null) {
                setMapping(null);
            }
        }
    }

    public String getId() {
        if (getParent() != null) {
            return "ImageGraph" + getParent().indexOf(this);
        } else {
            return "ImageGraph@" + Integer.toHexString(System.identityHashCode(this));
        }
    }

    @Nullable
    public ImageMappingEx getMapping() {
        return mapping;
    }

    public void setMapping(@Nullable ImageMapping mapping) {
        if (this.mapping != null) {
            this.mapping.removeImageGraph(this);
        }
        this.mapping = (ImageMappingEx) mapping;
        if (this.mapping != null) {
            this.mapping.addImageGraph(this);
        }
    }

    @Nullable
    public SingleBandImageData getData() {
        return data;
    }

    public void setData(@Nullable SingleBandImageData data) {
        this.data = data;

        if (mapping != null) {
            mapping.invalidateLimits();
            redraw(this);
        }
    }

    public void thisEffectiveColorChanged() {
        // the color for NaN?
    }

    public void mappingChanged() {
        // release the cache holder if condition changed
        redraw(this);
    }

    public Object createCacheHolder() {
        RasterKey rasterKey = createRasterKey();
        if (rasterKey == null || mapping == null) {
            return null;
        }
        ImageZscaleCache.cacheFor(rasterKey.bandKey);
        ColoredImageKey imageKey = new ColoredImageKey(rasterKey, mapping.getColorMap());
        synchronized (cache) {
            Object raster = cache.remove(rasterKey);
            Object image = cache.remove(imageKey);
            cache.put(rasterKey, raster);
            cache.put(imageKey, image);
        }
        return imageKey;
    }

    @Override
    public ComponentEx copyStructure(@Nonnull Map<ElementEx, ElementEx> orig2copyMap) {
        ImageGraphImpl result = (ImageGraphImpl) super.copyStructure(orig2copyMap);

        // copy or link image mapping
        if (mapping != null) {
            ImageMappingEx mappingCopy = (ImageMappingEx) orig2copyMap.get(mapping);
            if (mappingCopy == null) {
                mappingCopy = (ImageMappingEx) mapping.copyStructure(orig2copyMap);
            }
            result.mapping = mappingCopy;
            mappingCopy.addImageGraph(result);
        }

        return result;
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        this.data = ((ImageGraphImpl) src).data;
    }

    public void draw(Graphics2D graphics) {
        if (data == null || mapping == null) {
            return;
        }
        if (getParent().getXAxisTransform() == null || getParent().getYAxisTransform() == null) {
            return;
        }
        // limits is null means there is no valid data
        if (mapping.getLimits() == null) {
            return;
        }

        RasterKey rasterKey = createRasterKey();
        if (rasterKey == null) {
            return;
        }

        // find a child raster in viewport
        long start = System.nanoTime();

        WritableRaster raster;
        synchronized (cache) {
            raster = (WritableRaster) cache.get(rasterKey);
            if (cache.get(rasterKey) == null) {
                raster = createRaster(rasterKey);
                cache.put(rasterKey, raster);
            } else {
                System.out.print("R");
            }
        }
        long end = System.nanoTime();


        // apply pseudo-color mapping
        ColoredImageKey imageKey = new ColoredImageKey(rasterKey, mapping.getColorMap());
        BufferedImage image;
        synchronized (cache) {
            image = (BufferedImage) cache.get(imageKey);
            if (cache.get(imageKey) == null) {
                image = mapping.colorImage(raster);
                cache.put(imageKey, image);
            }
        }
        long end2 = System.nanoTime();
        System.out.println("Raster " + (end - start) + " image " + (end2 - end));


        // draw the image
        ImageCoordinateReference cr = data.getCoordinateReference();
        PaperTransform pxf = getPaperTransform();
        Dimension2D paperSize = getParent().getSize();
        NormalTransform xntrans = getParent().getXAxisTransform().getNormalTransform();
        NormalTransform yntrans = getParent().getYAxisTransform().getNormalTransform();

        double xedge = cr.xPixelToValue(rasterKey.portX - 0.5);
        double yedge = cr.yPixelToValue(rasterKey.portY - 0.5);
        double xOrgVal = xedge + cr.getXPixelSize() / rasterKey.xscale / 2;
        double yOrgVal = yedge + cr.getYPixelSize() / rasterKey.yscale / 2;
        double xorig = pxf.getXPtoD(xntrans.convToNR(xOrgVal) * paperSize.getWidth());
        double yorig = pxf.getYPtoD(yntrans.convToNR(yOrgVal) * paperSize.getHeight());

        Graphics2D g = (Graphics2D) graphics.create();
        Shape clip = getPaperTransform().getPtoD(getBounds());
        g.setClip(clip);

        double xs = Math.signum(xntrans.getScale());
        double ys = Math.signum(yntrans.getScale());
        AffineTransform at = new AffineTransform(xs, 0.0, 0.0, -ys, xorig, yorig);
        g.drawRenderedImage(image, at);

        g.dispose();
    }

    @Nullable
    private RasterKey createRasterKey() {
        if (data == null || mapping == null) {
            return null;
        }
        if (getParent().getXAxisTransform() == null || getParent().getYAxisTransform() == null) {
            return null;
        }
        // limits is null means there is no valid data
        if (mapping.getLimits() == null) {
            return null;
        }

        int width = data.getWidth();
        int height = data.getHeight();
        ImageCoordinateReference cr = data.getCoordinateReference();
        NormalTransform xntrans = getParent().getXAxisTransform().getNormalTransform();
        NormalTransform yntrans = getParent().getYAxisTransform().getNormalTransform();

        Range xRange = xntrans.getValueRange();
        Range yRange = yntrans.getValueRange();
        int portX0 = (int) Math.round(cr.xValueToPixel(xRange.getMin()));
        int portX1 = (int) Math.round(cr.xValueToPixel(xRange.getMax()));
        int portY0 = (int) Math.round(cr.yValueToPixel(yRange.getMin()));
        int portY1 = (int) Math.round(cr.yValueToPixel(yRange.getMax()));
        if (portX0 >= width || portX1 < 0 || portY0 >= height || portY1 < 0) {
            return null;
        }
        if (portX0 < 0) {
            portX0 = 0;
        }
        if (portX1 >= width) {
            portX1 = width - 1;
        }
        if (portY0 < 0) {
            portY0 = 0;
        }
        if (portY1 >= height) {
            portY1 = height - 1;
        }
        // JDK bug: AffineTransformOp can't handle child raster contains x-offset and bottom-right pixel
        if (portX0 > 0 && portX1 == width - 1 && portY1 == height - 1) {
            portX0 = 0;
        }
        int portWidth = portX1 - portX0 + 1;
        int portHeight = portY1 - portY0 + 1;

        // find zoom scale
        PaperTransform pxf = getPaperTransform();
        Dimension2D paperSize = getParent().getSize();
        double xscale = pxf.getScale() / Math.abs(xntrans.getScale()) * paperSize.getWidth() * cr.getXPixelSize();
        double yscale = pxf.getScale() / Math.abs(yntrans.getScale()) * paperSize.getHeight() * cr.getYPixelSize();

        // create raster key
        ImageZscaleCache.Key bandKey = ImageZscaleCache.createKey(data.getDataBuffer(), data.getWidth(), data.getHeight(),
                mapping.getLimits(), mapping.getIntensityTransform(), mapping.getBias(), mapping.getGain(),
                mapping.getILUTOutputBits());
        return new RasterKey(bandKey, portX0, portY0, portWidth, portHeight, xscale, yscale);
    }

    protected class RasterKey {
        private final ImageZscaleCache.Key bandKey;
        private final int portX, portY, portW, portH;
        private final double xscale, yscale;

        public RasterKey(ImageZscaleCache.Key bandKey,
                         int portX, int portY, int portW, int portH,
                         double xscale, double yscale) {
            this.bandKey = bandKey;
            this.portX = portX;
            this.portY = portY;
            this.portW = portW;
            this.portH = portH;
            this.xscale = xscale;
            this.yscale = yscale;
        }

        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            RasterKey key = (RasterKey) obj;
            return key.portX == portX && key.portY == portY && key.portW == portW && key.portH == portH
                    && key.xscale == xscale && key.yscale == yscale && key.bandKey.equals(bandKey);
        }

        public int hashCode() {
            long bits = java.lang.Double.doubleToLongBits(portX);
            bits += java.lang.Double.doubleToLongBits(portY) * 31;
            bits += java.lang.Double.doubleToLongBits(portW) * 37;
            bits += java.lang.Double.doubleToLongBits(portH) * 41;
            bits += java.lang.Double.doubleToLongBits(xscale) * 43;
            bits += java.lang.Double.doubleToLongBits(yscale) * 47;
            return ((int) bits) ^ ((int) (bits >> 32)) ^ bandKey.hashCode();

        }
    }

    protected class ColoredImageKey {
        private final RasterKey rasterKey;
        private final ColorMap colorMap;

        private ColoredImageKey(@Nonnull RasterKey rasterKey, @Nullable ColorMap colorMap) {
            this.rasterKey = rasterKey;
            this.colorMap = colorMap;
        }

        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ColoredImageKey key = (ColoredImageKey) obj;
            return key.colorMap == colorMap && key.rasterKey.equals(rasterKey);
        }

        public int hashCode() {
            if (colorMap == null) {
                return ~rasterKey.hashCode();
            } else {
                return colorMap.hashCode() ^ rasterKey.hashCode();
            }
        }
    }

}
