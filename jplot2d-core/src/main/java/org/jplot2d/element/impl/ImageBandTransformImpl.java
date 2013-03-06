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
	}

	public void calcLimits(ImageDataBuffer[] dataBuffers, Dimension[] sizeArray) {
		limits = algo.getCalculator().calcLimits(dataBuffers, sizeArray);
	}

	public double[] getLimits() {
		return limits;
	}

	public int getILUTInputBits() {
		if (intensityTransform != null || gain != 0.5 || bias != 0.5) {
			return 10;
		} else {
			return 8;
		}
	}

	public byte[] getILUT() {

		if (intensityTransform == null && gain == 0.5 && bias == 0.5) {
			return null;
		}

		/*
		 * create a lookup table. The input bits is getInputDataBits(). The output bits is getOutputDataBits()
		 */

		// the LUT index range is [0, lutIndexes], plus repeat the last value
		int lutIndexes = 1 << getILUTInputBits();

		// the output range is [0, 255]
		int outputRange = 255;

		byte[] lut = new byte[lutIndexes + 2];
		for (int i = 0; i <= lutIndexes; i++) {
			// t range is [0, 1]
			double t = (double) i / lutIndexes;
			if (intensityTransform != null) {
				t = intensityTransform.transform(t);
			}
			if (bias != 0.5) {
				t = t / ((1.0 / bias - 2.0) * (1.0 - t) + 1.0);
			}
			if (gain != 0.5) {
				double f = (1.0 / gain - 2.0) * (1.0 - 2 * t);
				if (t < 0.5) {
					t = t / (f + 1.0);
				} else {
					t = (f - t) / (f - 1.0);
				}
			}
			int v = (int) (outputRange * t);
			if (v < 0) {
				v = 0;
			} else if (v >= outputRange) {
				v = outputRange - 1;
			}
			lut[i] = (byte) (v);
		}
		lut[lutIndexes + 1] = lut[lutIndexes];

		return lut;
	}

}
