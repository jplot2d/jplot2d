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

public class ImageGraphImpl extends GraphImpl implements ImageGraphEx, IntermediateCacheEx {

    private static final WeakHashMap<ImageKey, BufferedImage> cache = new WeakHashMap<>();

    @Nullable
    private ImageMappingEx mapping;

    @Nullable
    private SingleBandImageData data;

    public ImageGraphImpl() {
        super();
        setZOrder(-1);
    }

    @Nonnull
    public static BufferedImage createImage(Object bandData, int bandBits, int width, int height, ColorMap colorMap) {

        int dataType;
        if (bandBits <= Byte.SIZE) {
            dataType = DataBuffer.TYPE_BYTE;
        } else {
            dataType = DataBuffer.TYPE_USHORT;
        }

        // create raster
        WritableRaster raster;
        if (colorMap == null || colorMap.getColorModel().getNumComponents() == 1) {
            DataBuffer dbuffer;
            if (dataType == DataBuffer.TYPE_BYTE) {
                dbuffer = new DataBufferByte((byte[]) bandData, width * height);
            } else {
                dbuffer = new DataBufferUShort((short[]) bandData, width * height);
            }

            SampleModel sampleModel = new PixelInterleavedSampleModel(dataType, width, height, 1, width, new int[]{0});
            raster = Raster.createWritableRaster(sampleModel, dbuffer, null);
        } else {
            // create a new raster which has duplicate bands
            int destNumComps = colorMap.getColorModel().getNumComponents();
            DataBuffer dbuffer;
            if (dataType == DataBuffer.TYPE_BYTE) {
                byte[] singleBandData = (byte[]) bandData;
                byte[][] dupDataArray = new byte[destNumComps][];
                for (int i = 0; i < destNumComps; i++) {
                    dupDataArray[i] = singleBandData;
                }
                dbuffer = new DataBufferByte(dupDataArray, width * height);
            } else {
                short[] singleBandData = (short[]) bandData;
                short[][] dupDataArray = new short[destNumComps][];
                for (int i = 0; i < destNumComps; i++) {
                    dupDataArray[i] = singleBandData;
                }
                dbuffer = new DataBufferUShort(dupDataArray, width * height);
            }
            SampleModel dupSM = new BandedSampleModel(dataType, width, height, destNumComps);
            raster = Raster.createWritableRaster(dupSM, dbuffer, null);
        }

        // create image
        if (colorMap == null) {
            // assembly a BufferedImage with linear gray color space
            ColorModel destCM = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),
                    new int[]{bandBits}, false, true, Transparency.OPAQUE, dataType);
            return new BufferedImage(destCM, raster, false, null);
        } else {
            // lookup and create a BufferedImage
            ColorModel destCM = colorMap.getColorModel();
            WritableRaster destRaster = destCM.createCompatibleWritableRaster(raster.getWidth(), raster.getHeight());
            LookupOp op = new LookupOp(colorMap.getLookupTable(), null);
            op.filter(raster, destRaster);
            return new BufferedImage(destCM, destRaster, false, null);
        }
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
        ImageKey key = createImageKey();
        if (key == null) {
            return null;
        }
        ImageZscaleCache.cacheFor(key.bandKey);
        synchronized (cache) {
            BufferedImage image = cache.remove(key);
            cache.put(key, image);
        }
        return key;
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

        ImageKey key = createImageKey();
        if (key == null) {
            return;
        }

        // create image
        BufferedImage image;
        synchronized (cache) {
            image = cache.get(key);
            if (image == null) {
                image = createImage(ImageZscaleCache.getValue(key.bandKey), key.bandKey.outputBits,
                        key.bandKey.w, key.bandKey.h, mapping.getColorMap());
                cache.put(key, image);
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
        if (getParent().getXAxisTransform() == null || getParent().getYAxisTransform() == null) {
            return null;
        }
        // limits is null means there is no valid data
        if (mapping.getLimits() == null) {
            return null;
        }

        ImageZscaleCache.Key bandKey = ImageZscaleCache.createKey(data.getDataBuffer(), data.getWidth(), data.getHeight(),
                mapping.getLimits(), mapping.getIntensityTransform(), mapping.getBias(), mapping.getGain(),
                mapping.getILUTOutputBits());
        return new ImageKey(bandKey, mapping.getColorMap());
    }

    private class ImageKey {
        private final ImageZscaleCache.Key bandKey;
        private final ColorMap colorMap;

        public ImageKey(ImageZscaleCache.Key bandKey, ColorMap colorMap) {
            this.bandKey = bandKey;
            this.colorMap = colorMap;
        }

        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ImageKey key = (ImageKey) obj;
            return key.colorMap == colorMap && key.bandKey.equals(bandKey);
        }

        public int hashCode() {
            if (colorMap == null) {
                return bandKey.hashCode();
            } else {
                return bandKey.hashCode() ^ colorMap.hashCode();
            }
        }
    }

}
