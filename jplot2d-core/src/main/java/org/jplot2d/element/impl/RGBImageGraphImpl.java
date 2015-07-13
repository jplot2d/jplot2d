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
import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.data.MultiBandImageData;
import org.jplot2d.element.RGBImageMapping;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.transform.PaperTransform;
import org.jplot2d.util.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.image.*;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class RGBImageGraphImpl extends GraphImpl implements RGBImageGraphEx, IntermediateCacheEx {

    private static final WeakHashMap<ImageKey, BufferedImage> cache = new WeakHashMap<>();

    @Nullable
    private RGBImageMappingEx mapping;

    @Nullable
    private MultiBandImageData data;

    public RGBImageGraphImpl() {
        super();
        setZOrder(-1);
    }

    private static BufferedImage createImage(ImageKey key) {
        int width = key.redKey.w;
        int height = key.redKey.h;
        byte[][] result = new byte[3][];
        result[0] = (byte[]) ImageZscaleCache.getValue(key.redKey);
        result[1] = (byte[]) ImageZscaleCache.getValue(key.greenKey);
        result[2] = (byte[]) ImageZscaleCache.getValue(key.blueKey);

        SampleModel sm = new BandedSampleModel(DataBuffer.TYPE_BYTE, width, height, 3);
        DataBufferByte dbuffer = new DataBufferByte(result, width * height);
        WritableRaster raster = Raster.createWritableRaster(sm, dbuffer, null);

        // assembly a BufferedImage
        int[] bitsArray = new int[]{8, 8, 8};
        ColorModel destCM = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), bitsArray, false, true,
                Transparency.OPAQUE, raster.getSampleModel().getDataType());
        return new BufferedImage(destCM, raster, false, null);
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
            return "RGBImageGraph" + getParent().indexOf(this);
        } else {
            return "RGBImageGraph@" + Integer.toHexString(System.identityHashCode(this));
        }
    }

    @Nullable
    public RGBImageMappingEx getMapping() {
        return mapping;
    }

    public void setMapping(@Nullable RGBImageMapping mapping) {
        if (this.mapping != null) {
            this.mapping.removeImageGraph(this);
        }
        this.mapping = (RGBImageMappingEx) mapping;
        if (this.mapping != null) {
            this.mapping.addImageGraph(this);
        }
    }

    @Nullable
    public MultiBandImageData getData() {
        return data;
    }

    public void setData(@Nullable MultiBandImageData data) {
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
        redraw(this);
    }

    public Object createCacheHolder() {
        ImageKey imageKey = createImageKey();
        if (imageKey == null) {
            return null;
        }

        ImageZscaleCache.cacheFor(imageKey.redKey);
        ImageZscaleCache.cacheFor(imageKey.greenKey);
        ImageZscaleCache.cacheFor(imageKey.blueKey);

        synchronized (cache) {
            BufferedImage image = cache.remove(imageKey);
            cache.put(imageKey, image);
        }
        return imageKey;
    }

    @Override
    public ComponentEx copyStructure(@Nonnull Map<ElementEx, ElementEx> orig2copyMap) {
        RGBImageGraphImpl result = (RGBImageGraphImpl) super.copyStructure(orig2copyMap);

        // copy or link image mapping
        if (mapping != null) {
            RGBImageMappingEx mappingCopy = (RGBImageMappingEx) orig2copyMap.get(mapping);
            if (mappingCopy == null) {
                mappingCopy = (RGBImageMappingEx) mapping.copyStructure(orig2copyMap);
            }
            result.mapping = mappingCopy;
            mappingCopy.addImageGraph(result);
        }

        return result;
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        this.data = ((RGBImageGraphImpl) src).data;
    }

    public void draw(Graphics2D graphics) {
        if (data == null || mapping == null) {
            return;
        }
        if (getParent().getXAxisTransform() == null || getParent().getYAxisTransform() == null) {
            return;
        }

        // apply limits to generate a raster
        ImageKey imageKey = createImageKey();
        BufferedImage image;
        synchronized (cache) {
            image = cache.get(imageKey);
            if (cache.get(imageKey) == null) {
                image = createImage(imageKey);
                cache.put(imageKey, image);
            }
        }

        // AffineTransform to zoom and vertical flip image
        ImageCoordinateReference cr = data.getCoordinateReference();
        PaperTransform pxf = getPaperTransform();
        NormalTransform xntrans = getParent().getXAxisTransform().getNormalTransform();
        NormalTransform yntrans = getParent().getYAxisTransform().getNormalTransform();
        Dimension2D paperSize = getParent().getSize();
        double xorig = pxf.getXPtoD(xntrans.convToNR(cr.xPixelToValue(-0.5)) * paperSize.getWidth());
        double yorig = pxf.getYPtoD(yntrans.convToNR(cr.yPixelToValue(-0.5)) * paperSize.getHeight());
        double xscale = pxf.getScale() / xntrans.getScale() * paperSize.getWidth() * data.getCoordinateReference().getXPixelSize();
        double yscale = pxf.getScale() / yntrans.getScale() * paperSize.getHeight() * data.getCoordinateReference().getYPixelSize();
        AffineTransform at = new AffineTransform(xscale, 0.0, 0.0, -yscale, xorig, yorig);

        // draw the image
        Graphics2D g = (Graphics2D) graphics.create();
        Shape clip = getPaperTransform().getPtoD(getBounds());
        g.setClip(clip);
        Map<Object, Object> hints = new HashMap<>();
        if (xscale > 1 || yscale > 1 || xscale < -1 || yscale < -1) {
            hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        } else {
            hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
        g.addRenderingHints(hints);
        g.drawImage(image, at, null);
        g.dispose();
    }

    @Nullable
    private ImageKey createImageKey() {
        if (data == null || mapping == null) {
            return null;
        }

        ImageBandTransformEx redTrans = mapping.getRedTransform();
        ImageBandTransformEx greenTrans = mapping.getGreenTransform();
        ImageBandTransformEx blueTrans = mapping.getBlueTransform();
        Range redLimits = mapping.getRedTransform().getLimits();
        Range greenLimits = mapping.getGreenTransform().getLimits();
        Range blueLimits = mapping.getBlueTransform().getLimits();

        // limits is null means there is no valid data
        if (redLimits == null && greenLimits == null && blueLimits == null) {
            return null;
        }

        int width = data.getWidth();
        int height = data.getHeight();

        ImageDataBuffer[] idbs = data.getDataBuffer();
        ImageZscaleCache.Key redKey = ImageZscaleCache.createKey(idbs[0], width, height, redLimits,
                redTrans.getIntensityTransform(), redTrans.getBias(), redTrans.getGain(), 8);
        ImageZscaleCache.Key greenKey = ImageZscaleCache.createKey(idbs[1], width, height, greenLimits,
                greenTrans.getIntensityTransform(), greenTrans.getBias(), greenTrans.getGain(), 8);
        ImageZscaleCache.Key blueKey = ImageZscaleCache.createKey(idbs[2], width, height, blueLimits,
                blueTrans.getIntensityTransform(), blueTrans.getBias(), blueTrans.getGain(), 8);

        return new ImageKey(redKey, greenKey, blueKey);
    }

    private class ImageKey {
        private final ImageZscaleCache.Key redKey, greenKey, blueKey;

        private ImageKey(ImageZscaleCache.Key redKey, ImageZscaleCache.Key greenKey, ImageZscaleCache.Key blueKey) {
            this.redKey = redKey;
            this.greenKey = greenKey;
            this.blueKey = blueKey;
        }

        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ImageKey key = (ImageKey) obj;
            return key.redKey.equals(redKey) && key.greenKey.equals(greenKey) && key.blueKey.equals(blueKey);
        }

        public int hashCode() {
            return redKey.hashCode() ^ greenKey.hashCode() ^ blueKey.hashCode();
        }
    }

}
