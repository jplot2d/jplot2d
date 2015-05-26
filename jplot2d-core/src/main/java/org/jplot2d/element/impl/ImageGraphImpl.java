/**
 * Copyright 2010-2013 Jingjing Li.
 * <p/>
 * This file is part of jplot2d.
 * <p/>
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element.impl;

import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.data.SingleBandImageData;
import org.jplot2d.element.ImageMapping;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.transform.PaperTransform;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.image.*;
import java.util.Map;

public class ImageGraphImpl extends GraphImpl implements ImageGraphEx, IntermediateCacheEx {

    private ImageMappingEx mapping;
    private SingleBandImageData data;

    public ImageGraphImpl() {
        super();
    }

    public String getId() {
        if (getParent() != null) {
            return "ImageGraph" + getParent().indexOf(this);
        } else {
            return "ImageGraph@" + Integer.toHexString(System.identityHashCode(this));
        }
    }

    public ImageMappingEx getMapping() {
        return mapping;
    }

    public void setMapping(ImageMapping mapping) {
        if (this.mapping != null) {
            this.mapping.removeImageGraph(this);
        }
        this.mapping = (ImageMappingEx) mapping;
        if (this.mapping != null) {
            this.mapping.addImageGraph(this);
        }
    }

    public SingleBandImageData getData() {
        return data;
    }

    public void setData(SingleBandImageData data) {
        this.data = data;

        mapping.recalcLimits();
        redraw(this);
    }

    public void thisEffectiveColorChanged() {
        // the color for NaN?
    }

    public void mappingChanged() {
        // release the cache holder if condition changed
        redraw(this);
    }

    public Object createCacheHolder() {
        if (data == null) {
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
        ImageMappingEx mappingCopy = (ImageMappingEx) orig2copyMap.get(mapping);
        if (mappingCopy == null) {
            mappingCopy = (ImageMappingEx) mapping.copyStructure(orig2copyMap);
        }
        result.mapping = mappingCopy;
        mappingCopy.addImageGraph(result);

        return result;
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        this.data = ((ImageGraphImpl) src).data;
    }

    public void draw(Graphics2D graphics) {
        if (data == null) {
            return;
        }

        double[] limits = mapping.getLimits();

        // limits is null means there is no valid data
        if (limits == null) {
            return;
        }

        // find a proper region to process
        int width = data.getWidth();
        int height = data.getHeight();
        double xval = data.getXRange().getMin();
        double yval = data.getYRange().getMin();

        ImageDataBuffer idb = data.getDataBuffer();

        int lutOutputBits = mapping.getILUTOutputBits();
        WritableRaster raster;
        Object result = ImageZscaleCache.getValue(idb, width, height, limits, mapping.getIntensityTransform(),
                mapping.getBias(), mapping.getGain(), lutOutputBits);
        if (lutOutputBits <= 8) {
            // create a SampleModel for byte data
            SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width, height, 1, width,
                    new int[]{0});
            // and a DataBuffer with the image data
            DataBufferByte dbuffer = new DataBufferByte((byte[]) result, width * height);
            // create a raster
            raster = Raster.createWritableRaster(sampleModel, dbuffer, null);
        } else {
            // create a SampleModel for short data
            SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_USHORT, width, height, 1, width,
                    new int[]{0});
            // and a DataBuffer with the image data
            DataBufferUShort dbuffer = new DataBufferUShort((short[]) result, width * height);
            // create a raster
            raster = Raster.createWritableRaster(sampleModel, dbuffer, null);
        }

        // zoom raster to device size
        PaperTransform pxf = getPaperTransform();
        NormalTransform xntrans = getParent().getXAxisTransform().getNormalTransform();
        NormalTransform yntrans = getParent().getYAxisTransform().getNormalTransform();
        Dimension2D paperSize = getParent().getSize();
        double xorig = pxf.getXPtoD(xntrans.convToNR(xval) * paperSize.getWidth());
        double yorig = pxf.getYPtoD(yntrans.convToNR(yval) * paperSize.getHeight());
        double xscale = pxf.getScale() / xntrans.getScale() * paperSize.getWidth() * data.getCoordinateReference().getXPixelSize();
        double yscale = pxf.getScale() / yntrans.getScale() * paperSize.getHeight() * data.getCoordinateReference().getYPixelSize();
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
        BufferedImage image = colorImage(mapping.getILUTOutputBits(), raster);

        // draw the image
        Graphics2D g = (Graphics2D) graphics.create();
        Shape clip = getPaperTransform().getPtoD(getBounds());
        g.setClip(clip);

        AffineTransform at = new AffineTransform(1, 0.0, 0.0, -1, xorig, yorig);
        g.drawRenderedImage(image, at);

        g.dispose();
    }

    /**
     * Apply the color LUT to the given raster. If the given raster only has a band, it will be duplicated to meet the
     * output band number.
     *
     * @param bits   the number of bits for the single band raster
     * @param raster the raster
     * @return a BufferedImage
     */
    private BufferedImage colorImage(int bits, WritableRaster raster) {

        int destNumComps;
        if (mapping.getColorMap() == null) {
            destNumComps = 3;
        } else {
            destNumComps = mapping.getColorMap().getColorModel().getNumComponents();
        }

        // duplicate the source band to as many bands as the number of dest CM
        if (destNumComps > 1) {
            SampleModel scm = raster.getSampleModel();
            SampleModel dupSM = new BandedSampleModel(scm.getDataType(), scm.getWidth(), scm.getHeight(), destNumComps);
            int singleBandSize = raster.getDataBuffer().getSize();

            if (raster.getDataBuffer().getDataType() == DataBuffer.TYPE_BYTE) {
                // create a new raster which has duplicate bands
                byte[] singleBandData = ((DataBufferByte) raster.getDataBuffer()).getData();

                byte[][] dupDataArray = new byte[destNumComps][];
                for (int i = 0; i < destNumComps; i++) {
                    dupDataArray[i] = singleBandData;
                }

                DataBufferByte dbuffer = new DataBufferByte(dupDataArray, singleBandSize);
                raster = Raster.createWritableRaster(dupSM, dbuffer, null);
            } else {
                // create a new raster which has duplicate bands
                short[] singleBandData = ((DataBufferUShort) raster.getDataBuffer()).getData();

                short[][] dupDataArray = new short[destNumComps][];
                for (int i = 0; i < destNumComps; i++) {
                    dupDataArray[i] = singleBandData;
                }

                DataBufferUShort dbuffer = new DataBufferUShort(dupDataArray, singleBandSize);
                raster = Raster.createWritableRaster(dupSM, dbuffer, null);
            }
        }

        if (mapping.getColorMap() == null) {
            // assembly a BufferedImage with sRGB color space
            int[] bitsArray = new int[]{bits, bits, bits};
            ColorModel destCM = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), bitsArray, false,
                    true, Transparency.OPAQUE, raster.getSampleModel().getDataType());

            return new BufferedImage(destCM, raster, false, null);
        } else {
            // lookup and create a BufferedImage
            ColorModel destCM = mapping.getColorMap().getColorModel();
            WritableRaster destRaster = destCM.createCompatibleWritableRaster(raster.getWidth(), raster.getHeight());
            LookupOp op = new LookupOp(mapping.getColorMap().getLookupTable(), null);
            op.filter(raster, destRaster);

            return new BufferedImage(destCM, destRaster, false, null);
        }

    }

}
