package org.jplot2d.element.impl;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.LookupOp;
import java.awt.image.WritableRaster;
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

	private static ColorSpace grayCS = ColorSpace.getInstance(ColorSpace.CS_GRAY);

	private static ColorModel byteGrayCM = new ComponentColorModel(grayCS, new int[] { 8 }, false, true,
			Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

	private List<ImageGraphEx> graphs = new ArrayList<ImageGraphEx>();

	private LimitsAlgorithm algo = new MinMaxAlgorithm();

	private ColorMap colorMap;

	private double[] limits;

	private IntensityTransform intensityTransform;

	private double bias;

	private double gain;

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
	}

	public double getBias() {
		return bias;
	}

	public void setBias(double bias) {
		this.bias = bias;
	}

	public double getGain() {
		return gain;
	}

	public void setGain(double gain) {
		this.gain = gain;
	}

	public ColorMap getColorMap() {
		return colorMap;
	}

	public void setColorMap(ColorMap colorMap) {
		this.colorMap = colorMap;
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		ImageMappingImpl imapping = (ImageMappingImpl) src;
		this.algo = imapping.algo;
		this.colorMap = imapping.colorMap;
		this.limits = imapping.limits;
	}

	public void processImage(WritableRaster raster) {
		if (intensityTransform != null) {
			LookupOp intensityLookup = new LookupOp(intensityTransform.getLookupTable(), null);
			intensityLookup.filter(raster, raster);
		}
	}

	public BufferedImage colorImage(WritableRaster raster) {
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
		ColorModel colorCM = new DirectColorModel(cs, 24, 0x00ff0000, 0x0000ff00, 0x000000ff, 0x0, false,
				DataBuffer.TYPE_INT);

		ColorConvertOp colorConv = new ColorConvertOp(grayCS, cs, null);
		WritableRaster destRaster = colorCM.createCompatibleWritableRaster(raster.getWidth(), raster.getHeight());
		colorConv.filter(raster, destRaster);

		// and finally apply the color lookup table
		// LookupOp colorLookup = new LookupOp(_colorLookupTable, null);
		// colorLookup.filter(colorImage, colorImage);

		return new BufferedImage(colorCM, destRaster, false, null);
	}

}
