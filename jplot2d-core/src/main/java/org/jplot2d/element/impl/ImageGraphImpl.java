package org.jplot2d.element.impl;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;

import org.jplot2d.data.FloatDataBuffer;
import org.jplot2d.data.ImageData;
import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.data.SingleBandImageData;
import org.jplot2d.element.ImageMapping;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.transform.PaperTransform;

public class ImageGraphImpl extends GraphImpl implements ImageGraphEx {

	/**
	 * The number of significant bits after applying limits. The max number is 16, for ushort data buffer.
	 */
	private static final int RAW_BITS = 14;

	/** size of a lookup table for full short range */
	private static final int LOOKUP_SIZE = 1 << RAW_BITS; // 16384

	/** maximum image value allowed */
	private static final int LOOKUP_MAX = LOOKUP_SIZE - 1;

	private static ColorSpace grayCS = ColorSpace.getInstance(ColorSpace.CS_GRAY);
	private static ColorModel ushortGrayCM = new ComponentColorModel(grayCS, new int[] { RAW_BITS }, false, true,
			Transparency.OPAQUE, DataBuffer.TYPE_USHORT);

	private ImageMappingEx mapping;
	private ImageData data;

	public ImageGraphImpl() {
		super();
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

	public ImageData getData() {
		return data;
	}

	public void setData(ImageData data) {
		this.data = data;
	}

	public void thisEffectiveColorChanged() {
		// the color for NaN?
	}

	@Override
	public ComponentEx copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
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
		if (getData() == null) {
			return;
		}

		Graphics2D g = (Graphics2D) graphics.create();
		Shape clip = getPaperTransform().getPtoD(getBounds());
		g.setClip(clip);

		double[] limits = mapping.getLimits();

		// find a proper region to process
		int xoff = 0;
		int yoff = 0;
		int width = data.getWidth();
		int height = data.getHeight();
		double xval = data.getXRange().getMin();
		double yval = data.getYRange().getMin();

		// apply limits to generate a raster
		WritableRaster raster = null;
		if (data instanceof SingleBandImageData) {
			ImageDataBuffer idb = ((SingleBandImageData) data).getDataBuffer();
			if (idb instanceof FloatDataBuffer) {
				raster = zscaleLimits(((FloatDataBuffer) idb), xoff, yoff, width, height, limits[0], limits[1]);
			}
		}

		// apply intensity transform and bias/gain
		mapping.processImage(raster);

		// zoom raster to device size
		PaperTransform pxf = getPaperTransform();
		NormalTransform xntrans = getParent().getXAxisTransform().getNormalTransform();
		NormalTransform yntrans = getParent().getYAxisTransform().getNormalTransform();
		Dimension2D paperSize = getParent().getSize();
		double xorig = pxf.getXPtoD(xntrans.convToNR(xval) * paperSize.getWidth());
		double yorig = pxf.getYPtoD(yntrans.convToNR(yval) * paperSize.getHeight());
		double xscale = pxf.getScale() / xntrans.getScale() * paperSize.getWidth() * data.getXcdelt();
		double yscale = pxf.getScale() / yntrans.getScale() * paperSize.getHeight() * data.getYcdelt();
		AffineTransform at = new AffineTransform(xscale, 0.0, 0.0, -yscale, xorig, yorig);
		AffineTransformOp axop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		raster = axop.filter(raster, null);

		BufferedImage image = mapping.colorImage(raster);

		g.drawImage(image, 0, 0, null);

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
	private static WritableRaster zscaleLimits(ImageDataBuffer dbuf, int xoff, int yoff, int w, int h, double lowCut,
			double highCut) {
		double scale = LOOKUP_SIZE / (highCut - lowCut); // scale to short range

		short[] result = new short[w * h];
		int n = 0;
		for (int r = yoff; r < yoff + h; r++) {
			for (int c = xoff; c < xoff + w; c++) {
				double scaled = (dbuf.getDouble(c, r) - lowCut) * scale;
				if (scaled > LOOKUP_MAX) {
					result[n] = (short) LOOKUP_MAX;
				} else if (scaled < 0) {
					result[n] = 0;
				} else if (scaled > 0) {
					result[n] = (short) (int) (scaled + 0.5);
				} else {
					result[n] = (short) (int) (scaled - 0.5);
				}
				n++;
			}
		}

		// create a SampleModel for short data
		SampleModel sampleModel = ushortGrayCM.createCompatibleSampleModel(w, h);
		// and a DataBuffer with the image data
		DataBufferUShort dbuffer = new DataBufferUShort(result, w * h);
		// create a raster
		return Raster.createWritableRaster(sampleModel, dbuffer, null);

	}

}
