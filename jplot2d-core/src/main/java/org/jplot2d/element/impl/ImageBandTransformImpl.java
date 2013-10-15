package org.jplot2d.element.impl;

import java.awt.Dimension;
import java.lang.reflect.Method;

import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.element.RGBImageMapping;
import org.jplot2d.image.IntensityTransform;
import org.jplot2d.image.LimitsAlgorithm;
import org.jplot2d.image.MinMaxAlgorithm;

public class ImageBandTransformImpl extends ElementImpl implements ImageBandTransformEx {

	private LimitsAlgorithm algo = new MinMaxAlgorithm();

	private double[] limits;

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

	public LimitsAlgorithm getLimitsAlgorithm() {
		return algo;
	}

	public void setLimitsAlgorithm(LimitsAlgorithm algo) {
		this.algo = algo;
		redrawGraphs();
	}

	public IntensityTransform getIntensityTransform() {
		return intensityTransform;
	}

	public void setIntensityTransform(IntensityTransform it) {
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

	public void recalcLimits() {
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

	public double[] getLimits() {
		return limits;
	}

}
