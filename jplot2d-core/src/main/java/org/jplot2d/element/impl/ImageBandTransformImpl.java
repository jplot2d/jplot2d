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
import org.jplot2d.element.RGBImageMapping;
import org.jplot2d.image.IntensityTransform;
import org.jplot2d.image.LimitsAlgorithm;
import org.jplot2d.image.MinMaxAlgorithm;
import org.jplot2d.util.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.Method;

public class ImageBandTransformImpl extends ElementImpl implements ImageBandTransformEx {

    @Nonnull
    private LimitsAlgorithm algo = new MinMaxAlgorithm();

    @Nullable
    private double[] limits;

    @Nullable
    private IntensityTransform intensityTransform;

    private double bias = 0.5;

    private double gain = 0.5;

    private boolean calcLimitsNeeded;

    public RGBImageMappingEx getParent() {
        return (RGBImageMappingEx) parent;
    }

    public String getId() {
        if (getParent() != null) {
            if (this == getParent().getRedTransform()) {
                return "ImageBandTransform(Red)";
            } else if (this == getParent().getGreenTransform()) {
                return "ImageBandTransform(Green)";
            } else if (this == getParent().getBlueTransform()) {
                return "ImageBandTransform(Blue)";
            }
        }
        return "ImageBandTransform@" + Integer.toHexString(System.identityHashCode(this));
    }

    public InvokeStep getInvokeStepFormParent() {
        if (parent == null) {
            return null;
        }

        Method method = null;
        try {
            if (this == getParent().getRedTransform()) {
                method = RGBImageMapping.class.getMethod("getRedTransform");
            } else if (this == getParent().getGreenTransform()) {
                method = RGBImageMapping.class.getMethod("getGreenTransform");
            } else if (this == getParent().getBlueTransform()) {
                method = RGBImageMapping.class.getMethod("getBlueTransform");
            }
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
        return new InvokeStep(method);
    }

    @Nonnull
    public LimitsAlgorithm getLimitsAlgorithm() {
        return algo;
    }

    public void setLimitsAlgorithm(@Nullable LimitsAlgorithm algo) {
        if (algo == null) {
            throw new IllegalArgumentException("Limits algorithm can not be null.");
        }
        this.algo = algo;
        redrawGraphs();
    }

    @Nullable
    public IntensityTransform getIntensityTransform() {
        return intensityTransform;
    }

    public void setIntensityTransform(@Nullable IntensityTransform it) {
        this.intensityTransform = it;
        redrawGraphs();
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
        redrawGraphs();
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
        redrawGraphs();
    }

    private void redrawGraphs() {
        for (RGBImageGraphEx graph : getParent().getGraphs()) {
            graph.mappingChanged();
        }
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        ImageBandTransformImpl imapping = (ImageBandTransformImpl) src;
        this.algo = imapping.algo;
        this.limits = imapping.limits;
        this.intensityTransform = imapping.intensityTransform;
        this.bias = imapping.bias;
        this.gain = imapping.gain;
        this.calcLimitsNeeded = imapping.calcLimitsNeeded;
    }

    public void invalidateLimits() {
        calcLimitsNeeded = true;
    }

    public void calcLimits(ImageDataBuffer[] dataBuffers, Dimension[] sizeArray) {
        if (calcLimitsNeeded || limits == null) {
            double[] newlimits = algo.getCalculator().calcLimits(dataBuffers, sizeArray);

            if (limits == null || newlimits == null || limits[0] != newlimits[0] || limits[1] != newlimits[1]) {
                limits = newlimits;
                redrawGraphs();
            }
        }
    }

    @Nullable
    public Range getLimits() {
        if (limits == null) {
            return null;
        } else {
            return new Range.Double(limits[0], limits[1]);
        }
    }

}
