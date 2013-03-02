package org.jplot2d.element.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jplot2d.data.ImageData;
import org.jplot2d.element.ImageGraph;
import org.jplot2d.image.ColorMap;
import org.jplot2d.image.IntensityTransform;
import org.jplot2d.image.LimitsAlgorithm;
import org.jplot2d.image.MinMaxAlgorithm;

public class ImageMappingImpl extends ElementImpl implements ImageMappingEx {

	/**
	 * The max number of significant bits after applying limits. The max number is 16, for unsigned
	 * short data buffer.
	 */
	private static final int MAX_BITS = 16;

	private List<ImageGraphEx> graphs = new ArrayList<ImageGraphEx>();

	private LimitsAlgorithm algo = new MinMaxAlgorithm();

	private ColorMap colorMap;

	private double[] limits;

	private IntensityTransform intensityTransform;

	private double bias = 0.5;

	private double gain = 0.5;

	public ImageGraphEx getParent() {
		return (ImageGraphEx) parent;
	}

	public String getId() {
		return "ImageMapping@" + Integer.toHexString(System.identityHashCode(this));
	}

	public String getShortId() {
		return getFullId();
	}

	public String getFullId() {
		return "ImageMapping@" + Integer.toHexString(System.identityHashCode(this));
	}

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

	public void addImageGraph(ImageGraphEx graph) {
		graphs.add(graph);
		if (graphs.size() == 1) {
			parent = graphs.get(0);
		} else {
			parent = null;
		}
	}

	public void removeImageGraph(ImageGraphEx graph) {
		graphs.remove(graph);
		if (graphs.size() == 1) {
			parent = graphs.get(0);
		} else {
			parent = null;
		}
	}

	public ImageGraph[] getGraphs() {
		return graphs.toArray(new ImageGraph[graphs.size()]);
	}

	public LimitsAlgorithm getLimitsAlgorithm() {
		return algo;
	}

	public void setLimitsAlgorithm(LimitsAlgorithm algo) {
		this.algo = algo;
	}

	public void calcLimits() {
		ImageData[] ids = new ImageData[graphs.size()];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = graphs.get(i).getData();
		}
		limits = algo.getCalculator().calcLimits(ids);
	}

	public double[] getLimits() {
		return limits;
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

	public ColorMap getColorMap() {
		return colorMap;
	}

	public void setColorMap(ColorMap colorMap) {
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
	}

	public int getILUTInputBits() {
		int bits = 8;
		if (colorMap != null) {
			bits = colorMap.getInputBits();
		}
		if (intensityTransform != null || gain != 0.5 || bias != 0.5) {
			bits += 2;
		}
		if (bits > MAX_BITS) {
			bits = MAX_BITS;
		}
		return bits;
	}

	public int getILUTOutputBits() {
		int bits = 8;
		if (colorMap != null) {
			bits = colorMap.getInputBits();
		}
		if (bits > MAX_BITS) {
			bits = MAX_BITS;
		}
		return bits;
	}

	public short[] getILUT() {
		if (intensityTransform == null && gain == 0.5 && bias == 0.5) {
			return null;// do nothing
		}
		// create a lookup table
		// input bits: getInputDataBits()
		// output bits: getOutputDataBits()

		// the LUT index range is [0, lutIndexes], plus repeat the last value
		int lutIndexes = 1 << getILUTInputBits();

		// the output range is [0, outputRange - 1]
		int outputRange = 1 << getILUTOutputBits();

		short[] lut = new short[lutIndexes + 2];
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
