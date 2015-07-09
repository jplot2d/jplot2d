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

import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.data.SingleBandImageData;
import org.jplot2d.image.ColorMap;
import org.jplot2d.image.IntensityTransform;
import org.jplot2d.image.LimitsAlgorithm;
import org.jplot2d.image.MinMaxAlgorithm;
import org.jplot2d.notice.Notice;
import org.jplot2d.util.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageMappingImpl extends ElementImpl implements ImageMappingEx {

    private final List<ImageGraphEx> graphs = new ArrayList<>();

    @Nonnull
    private LimitsAlgorithm algo = new MinMaxAlgorithm();

    @Nullable
    private double[] limits;

    @Nullable
    private IntensityTransform intensityTransform;

    private double bias = 0.5;

    private double gain = 0.5;

    @Nullable
    private ColorMap colorMap;

    private boolean calcLimitsNeeded;

    @Override
    public ImageGraphEx getParent() {
        return (ImageGraphEx) parent;
    }

    @Override
    public ElementEx getPrim() {
        if (graphs.size() == 0) {
            return null;
        } else {
            return graphs.get(0);
        }
    }

    @Override
    public void notify(Notice msg) {
        if (getPrim() != null) {
            getPrim().notify(msg);
        }
    }

    @Override
    public String getId() {
        StringBuilder sb = new StringBuilder();
        sb.append("ImageMapping(");
        for (ImageGraphEx graph : graphs) {
            sb.append(graph.getId()).append(',');
        }
        sb.replace(sb.length() - 1, sb.length(), ")");
        return sb.toString();
    }

    @Override
    public String getFullId() {
        return "ImageMapping@" + Integer.toHexString(System.identityHashCode(this));
    }

    @Override
    public InvokeStep getInvokeStepFormParent() {
        if (graphs.size() == 0) {
            return null;
        }

        Method method;
        try {
            method = ImageGraphEx.class.getMethod("getMapping");
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
        return new InvokeStep(method);
    }

    @Override
    public void addImageGraph(ImageGraphEx graph) {
        graphs.add(graph);
        invalidateLimits();
        if (graphs.size() == 1) {
            parent = graphs.get(0);
        } else {
            parent = null;
        }
    }

    @Override
    public void removeImageGraph(ImageGraphEx graph) {
        if (graphs.remove(graph)) {
            invalidateLimits();
        }
        if (graphs.size() == 1) {
            parent = graphs.get(0);
        } else {
            parent = null;
        }
    }

    @Override
    public ImageGraphEx[] getGraphs() {
        return graphs.toArray(new ImageGraphEx[graphs.size()]);
    }

    @Override
    @Nonnull
    public LimitsAlgorithm getLimitsAlgorithm() {
        return algo;
    }

    @Override
    public void setLimitsAlgorithm(@Nullable LimitsAlgorithm algo) {
        if (algo == null) {
            throw new IllegalArgumentException("Limits algorithm can not be null.");
        }
        this.algo = algo;
        redrawGraphs();
    }

    @Override
    public void invalidateLimits() {
        calcLimitsNeeded = true;
    }

    @Override
    public void calcLimits() {
        if (calcLimitsNeeded || limits == null) {
            calcLimitsNeeded = false;

            ImageDataBuffer[] ids = new ImageDataBuffer[graphs.size()];
            Dimension[] sizeArray = new Dimension[graphs.size()];
            int n = 0;
            for (int i = 0; i < ids.length; i++) {
                SingleBandImageData data = graphs.get(i).getData();
                if (data != null) {
                    ids[n] = data.getDataBuffer();
                    sizeArray[n] = new Dimension(data.getWidth(), data.getHeight());
                    n++;
                }
            }
            if (n != graphs.size()) {
                ids = Arrays.copyOf(ids, n);
                sizeArray = Arrays.copyOf(sizeArray, n);
            }
            double[] newlimits = algo.getCalculator().calcLimits(ids, sizeArray);

            if (limits == null || newlimits == null || limits[0] != newlimits[0] || limits[1] != newlimits[1]) {
                limits = newlimits;
                redrawGraphs();
            }
        }
    }

    @Override
    @Nullable
    public Range getLimits() {
        if (limits == null) {
            return null;
        } else {
            return new Range.Double(limits[0], limits[1]);
        }
    }

    @Override
    @Nullable
    public IntensityTransform getIntensityTransform() {
        return intensityTransform;
    }

    @Override
    public void setIntensityTransform(@Nullable IntensityTransform it) {
        this.intensityTransform = it;
        redrawGraphs();
    }

    @Override
    public double getBias() {
        return bias;
    }

    @Override
    public void setBias(double bias) {
        this.bias = bias;
        redrawGraphs();
    }

    @Override
    public double getGain() {
        return gain;
    }

    @Override
    public void setGain(double gain) {
        this.gain = gain;
        redrawGraphs();
    }

    @Override
    @Nullable
    public ColorMap getColorMap() {
        return colorMap;
    }

    @Override
    public void setColorMap(@Nullable ColorMap colorMap) {
        if (colorMap != null && colorMap.getInputBits() > ColorMap.MAX_INPUT_BITS) {
            throw new IllegalArgumentException("The colormap input bits is large than MAX_INPUT_BITS.");
        }
        this.colorMap = colorMap;
        redrawGraphs();
    }

    private void redrawGraphs() {
        for (ImageGraphEx graph : graphs) {
            graph.mappingChanged();
        }
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        ImageMappingImpl imapping = (ImageMappingImpl) src;
        this.algo = imapping.algo;
        this.limits = imapping.limits;
        this.intensityTransform = imapping.intensityTransform;
        this.bias = imapping.bias;
        this.gain = imapping.gain;
        this.colorMap = imapping.colorMap;
        this.calcLimitsNeeded = imapping.calcLimitsNeeded;
    }

    @Override
    public int getILUTOutputBits() {
        if (colorMap == null) {
            return 8;
        } else {
            return colorMap.getInputBits();
        }
    }

    @Nonnull
    public BufferedImage colorImage(@Nonnull WritableRaster raster) {

        int bits = getILUTOutputBits();
        int destNumComps;
        if (getColorMap() == null) {
            destNumComps = 3;
        } else {
            destNumComps = getColorMap().getColorModel().getNumComponents();
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

        if (getColorMap() == null) {
            // assembly a BufferedImage with sRGB color space
            int[] bitsArray = new int[]{bits, bits, bits};
            ColorModel destCM = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), bitsArray, false,
                    true, Transparency.OPAQUE, raster.getSampleModel().getDataType());

            return new BufferedImage(destCM, raster, false, null);
        } else {
            // lookup and create a BufferedImage
            ColorModel destCM = getColorMap().getColorModel();
            WritableRaster destRaster = destCM.createCompatibleWritableRaster(raster.getWidth(), raster.getHeight());
            LookupOp op = new LookupOp(getColorMap().getLookupTable(), null);
            op.filter(raster, destRaster);

            return new BufferedImage(destCM, destRaster, false, null);
        }

    }


}
