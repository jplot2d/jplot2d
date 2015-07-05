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
import org.jplot2d.data.SingleBandImageData;
import org.jplot2d.element.ImageMapping;
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

public class ImageGraphImpl extends GraphImpl implements ImageGraphEx, IntermediateCacheEx {

    @Nullable
    private ImageMappingEx mapping;

    @Nullable
    private SingleBandImageData data;

    public ImageGraphImpl() {
        super();
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
        if (data == null || mapping == null) {
            return null;
        }

        return ImageZscaleCache.createCacheFor(data.getDataBuffer(), data.getWidth(), data.getHeight(),
                mapping.getLimits(), mapping.getIntensityTransform(), mapping.getBias(), mapping.getGain(),
                mapping.getILUTOutputBits());
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

        double[] limits = mapping.getLimits();

        // limits is null means there is no valid data
        if (limits == null) {
            return;
        }

        // create a z-scaled raster
        int width = data.getWidth();
        int height = data.getHeight();

        ImageDataBuffer idb = data.getDataBuffer();

        int lutOutputBits = mapping.getILUTOutputBits();
        Object result = ImageZscaleCache.getValue(idb, width, height, limits, mapping.getIntensityTransform(),
                mapping.getBias(), mapping.getGain(), lutOutputBits);

        WritableRaster raster;
        if (lutOutputBits <= Byte.SIZE) {
            // create a SampleModel for byte data
            SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width, height, 1, width, new int[]{0});
            // and a DataBuffer with the image data
            DataBufferByte dbuffer = new DataBufferByte((byte[]) result, width * height);
            // create a raster
            raster = Raster.createWritableRaster(sampleModel, dbuffer, null);
        } else {
            // create a SampleModel for short data
            SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_USHORT, width, height, 1, width, new int[]{0});
            // and a DataBuffer with the image data
            DataBufferUShort dbuffer = new DataBufferUShort((short[]) result, width * height);
            // create a raster
            raster = Raster.createWritableRaster(sampleModel, dbuffer, null);
        }

        // create a child raster in viewport and zoom to device size
        NormalTransform xntrans = getParent().getXAxisTransform().getNormalTransform();
        NormalTransform yntrans = getParent().getYAxisTransform().getNormalTransform();

        Range xRange = xntrans.getValueRange();
        Range yRange = yntrans.getValueRange();
        ImageCoordinateReference cr = data.getCoordinateReference();
        int portX0 = (int) Math.round(cr.xValueToPixel(xRange.getMin()));
        if (portX0 < 0) {
            portX0 = 0;
        }
        int portX1 = (int) Math.round(cr.xValueToPixel(xRange.getMax()));
        if (portX1 >= width) {
            portX1 = width - 1;
        }
        int portY0 = (int) Math.round(cr.yValueToPixel(yRange.getMin()));
        if (portY0 < 0) {
            portY0 = 0;
        }
        int portY1 = (int) Math.round(cr.yValueToPixel(yRange.getMax()));
        if (portY1 >= height) {
            portY1 = height - 1;
        }
        // JDK bug: AffineTransformOp can't handle child raster contains x-offset and bottom-right pixel
        if (portX0 > 0 && portX1 == width - 1 && portY1 == height - 1) {
            portX0 = 0;
        }
        int portWidth = portX1 - portX0 + 1;
        int portHeight = portY1 - portY0 + 1;

        // AffineTransformOp can't handle child with parentX > 0
        raster = raster.createWritableChild(portX0, portY0, portWidth, portHeight, 0, 0, null);

        // zoom raster to device size
        PaperTransform pxf = getPaperTransform();
        Dimension2D paperSize = getParent().getSize();
        double xscale = pxf.getScale() / Math.abs(xntrans.getScale()) * paperSize.getWidth() * cr.getXPixelSize();
        double yscale = pxf.getScale() / Math.abs(yntrans.getScale()) * paperSize.getHeight() * cr.getYPixelSize();
        AffineTransform scaleAT = AffineTransform.getScaleInstance(xscale, yscale);
        int op;
        if (xscale > 1 || yscale > 1) {
            op = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
        } else {
            op = AffineTransformOp.TYPE_BILINEAR;
        }
        AffineTransformOp axop = new AffineTransformOp(scaleAT, op);
        raster = axop.filter(raster, null);

        // apply pseudo-color mapping
        BufferedImage image = mapping.colorImage(raster);

        // draw the image
        double xedge = cr.xPixelToValue(portX0 - 0.5);
        double yedge = cr.yPixelToValue(portY0 - 0.5);
        double xOrgVal = xedge + cr.getXPixelSize() / xscale / 2;
        double yOrgVal = yedge + cr.getYPixelSize() / yscale / 2;
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

}
