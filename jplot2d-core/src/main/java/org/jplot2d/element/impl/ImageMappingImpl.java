package org.jplot2d.element.impl;

import java.awt.Dimension;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.data.SingleBandImageData;
import org.jplot2d.image.ColorMap;
import org.jplot2d.image.IntensityTransform;
import org.jplot2d.image.LimitsAlgorithm;
import org.jplot2d.image.MinMaxAlgorithm;

public class ImageMappingImpl extends ElementImpl implements ImageMappingEx {

	private List<ImageGraphEx> graphs = new ArrayList<ImageGraphEx>();

	private LimitsAlgorithm algo = new MinMaxAlgorithm();

	private double[] limits;

	private IntensityTransform intensityTransform;

	private double bias = 0.5;

	private double gain = 0.5;

	private ColorMap colorMap;

	private boolean calcLimitsNeeded;

	public ImageGraphEx getParent() {
		return (ImageGraphEx) parent;
	}

	public String getId() {
		StringBuilder sb = new StringBuilder();
		sb.append("ImageMapping(");
		for (ImageGraphEx graph : graphs) {
			sb.append(graph.getId()).append(',');
		}
		sb.replace(sb.length() - 1, sb.length(), ")");
		return sb.toString();
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

	public ImageGraphEx[] getGraphs() {
		return graphs.toArray(new ImageGraphEx[graphs.size()]);
	}

	public LimitsAlgorithm getLimitsAlgorithm() {
		return algo;
	}

	public void setLimitsAlgorithm(LimitsAlgorithm algo) {
		this.algo = algo;
		redrawGraphs();
	}

	public void recalcLimits() {
		calcLimitsNeeded = true;
	}

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
		if (colorMap.getInputBits() > ColorMap.MAX_INPUT_BITS) {
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

	public int getILUTOutputBits() {
		if (colorMap == null) {
			return 8;
		} else {
			return colorMap.getInputBits();
		}
	}

}
