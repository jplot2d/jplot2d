package org.jplot2d.element.impl;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;

import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.data.MultiBandImageData;
import org.jplot2d.element.RGBImageMapping;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.transform.PaperTransform;

public class RGBImageGraphImpl extends GraphImpl implements RGBImageGraphEx {

	private RGBImageMappingEx mapping;
	private MultiBandImageData data;

	public RGBImageGraphImpl() {
		super();
	}

	public String getId() {
		if (getParent() != null) {
			return "RGBImageGraph" + getParent().indexOf(this);
		} else {
			return "RGBImageGraph@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public RGBImageMappingEx getMapping() {
		return mapping;
	}

	public void setMapping(RGBImageMapping mapping) {
		if (this.mapping != null) {
			this.mapping.removeImageGraph(this);
		}
		this.mapping = (RGBImageMappingEx) mapping;
		if (this.mapping != null) {
			this.mapping.addImageGraph(this);
		}
	}

	public MultiBandImageData getData() {
		return data;
	}

	public void setData(MultiBandImageData data) {
		ImageDataBuffer[] olddata = this.data.getDataBuffer();
		this.data = data;

		if (data.getDataBuffer()[0] != olddata[0]) {
			mapping.getRedTransform().recalcLimits();
		}
		if (data.getDataBuffer()[1] != olddata[1]) {
			mapping.getGreenTransform().recalcLimits();
		}
		if (data.getDataBuffer()[2] != olddata[2]) {
			mapping.getBlueTransform().recalcLimits();
		}
		redraw(this);
	}

	public void thisEffectiveColorChanged() {
		// the color for NaN?
	}

	public void mappingChanged() {
		redraw(this);
	}

	@Override
	public ComponentEx copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		RGBImageGraphImpl result = (RGBImageGraphImpl) super.copyStructure(orig2copyMap);

		// copy or link image mapping
		RGBImageMappingEx mappingCopy = (RGBImageMappingEx) orig2copyMap.get(mapping);
		if (mappingCopy == null) {
			mappingCopy = (RGBImageMappingEx) mapping.copyStructure(orig2copyMap);
		}
		result.mapping = mappingCopy;
		mappingCopy.addImageGraph(result);

		return result;
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		this.data = ((RGBImageGraphImpl) src).data;
	}

	public void draw(Graphics2D graphics) {
		if (getData() == null) {
			return;
		}

		// find a proper region to process
		int xoff = 0;
		int yoff = 0;
		int width = data.getWidth();
		int height = data.getHeight();
		double xval = data.getXRange().getMin();
		double yval = data.getYRange().getMin();

		// apply limits to generate a raster
		WritableRaster raster = null;
		ImageDataBuffer[] idbs = ((MultiBandImageData) data).getDataBuffer();
		int bands = idbs.length;
		byte[][] result = new byte[bands][];
		result[0] = zscaleLimits(idbs[0], xoff, yoff, width, height, mapping.getRedTransform());
		result[1] = zscaleLimits(idbs[1], xoff, yoff, width, height, mapping.getGreenTransform());
		result[2] = zscaleLimits(idbs[2], xoff, yoff, width, height, mapping.getBlueTransform());

		SampleModel sm = new BandedSampleModel(DataBuffer.TYPE_BYTE, width, height, bands);
		DataBufferByte dbuffer = new DataBufferByte(result, width * height);
		raster = Raster.createWritableRaster(sm, dbuffer, null);

		// assembly a BufferedImage
		int[] bitsArray = new int[] { 8, 8, 8 };
		ColorModel destCM = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), bitsArray, false, true,
				Transparency.OPAQUE, raster.getSampleModel().getDataType());
		BufferedImage image = new BufferedImage(destCM, raster, false, null);

		// AffineTransform to zoom and vertical flip image
		PaperTransform pxf = getPaperTransform();
		NormalTransform xntrans = getParent().getXAxisTransform().getNormalTransform();
		NormalTransform yntrans = getParent().getYAxisTransform().getNormalTransform();
		Dimension2D paperSize = getParent().getSize();
		double xorig = pxf.getXPtoD(xntrans.convToNR(xval) * paperSize.getWidth());
		double yorig = pxf.getYPtoD(yntrans.convToNR(yval) * paperSize.getHeight());
		double xscale = pxf.getScale() / xntrans.getScale() * paperSize.getWidth() * data.getXcdelt();
		double yscale = pxf.getScale() / yntrans.getScale() * paperSize.getHeight() * data.getYcdelt();
		AffineTransform at = new AffineTransform(xscale, 0.0, 0.0, -yscale, xorig, yorig);

		// draw the image
		Graphics2D g = (Graphics2D) graphics.create();
		Shape clip = getPaperTransform().getPtoD(getBounds());
		g.setClip(clip);

		g.drawRenderedImage(image, at);

		g.dispose();
	}

	/**
	 * Apply the cuts and linear scale the array to a unsigned short array
	 * 
	 * @param array
	 * @param lowCut
	 * @param highCut
	 * @return
	 */
	private byte[] zscaleLimits(ImageDataBuffer dbuf, int xoff, int yoff, int w, int h, ImageBandTransformEx ztrans) {

		// TODO: ztrans.getLimits() may returns null
		
		double lowCut = ztrans.getLimits()[0];
		double highCut = ztrans.getLimits()[1];

		int outputRange = 1 << ztrans.getILUTInputBits();
		double scale = outputRange / (highCut - lowCut);

		byte[] result = new byte[w * h];
		int n = 0;
		byte[] lut = ztrans.getILUT();
		if (lut == null) {
			for (int r = yoff; r < yoff + h; r++) {
				for (int c = xoff; c < xoff + w; c++) {
					double scaled = (dbuf.getDouble(c, r) - lowCut) * scale;
					/*
					 * the scaled value may slightly larger than outputRange or slightly small than 0. the ilutIndex
					 * range is [0, outputRange]
					 */
					int ilutIndex = (int) scaled;
					if (ilutIndex >= outputRange) {
						ilutIndex = outputRange - 1;
					}
					result[n++] = (byte) ilutIndex;
				}
			}
		} else {
			for (int r = yoff; r < yoff + h; r++) {
				for (int c = xoff; c < xoff + w; c++) {
					double scaled = (dbuf.getDouble(c, r) - lowCut) * scale;
					/*
					 * the scaled value may slightly larger than outputRange or slightly small than 0. the ilutIndex
					 * range is [0, outputRange]
					 */
					int ilutIndex = (int) scaled;
					double idelta = ilutIndex - ilutIndex;

					// the LUT output is unsigned byte, apply the 0xff bit mask.
					int a = lut[ilutIndex] & 0xff;
					int b = lut[ilutIndex + 1] & 0xff;
					result[n++] = (byte) (a + idelta * (b - a));
				}
			}
		}

		return result;
	}

}
