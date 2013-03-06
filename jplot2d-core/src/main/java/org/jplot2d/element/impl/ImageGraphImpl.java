package org.jplot2d.element.impl;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.LookupOp;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;

import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.data.SingleBandImageData;
import org.jplot2d.element.ImageMapping;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.transform.PaperTransform;

public class ImageGraphImpl extends GraphImpl implements ImageGraphEx {

	private ImageMappingEx mapping;
	private SingleBandImageData data;

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

	public SingleBandImageData getData() {
		return data;
	}

	public void setData(SingleBandImageData data) {
		this.data = data;
	}

	public void thisEffectiveColorChanged() {
		// the color for NaN?
	}

	public void mappingChanged() {
		redraw(this);
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

		double[] limits = mapping.getLimits();

		// find a proper region to process
		int xoff = 0;
		int yoff = 0;
		int width = data.getWidth();
		int height = data.getHeight();
		double xval = data.getXRange().getMin();
		double yval = data.getYRange().getMin();

		// apply limits to generate a raster
		ImageDataBuffer idb = ((SingleBandImageData) data).getDataBuffer();
		short[] result = zscaleLimits(idb, xoff, yoff, width, height, limits[0], limits[1]);

		// create a SampleModel for short data
		SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_USHORT, width, height, 1, width,
				new int[] { 0 });
		// and a DataBuffer with the image data
		DataBufferUShort dbuffer = new DataBufferUShort(result, width * height);
		// create a raster
		WritableRaster raster = Raster.createWritableRaster(sampleModel, dbuffer, null);

		// zoom raster to device size
		PaperTransform pxf = getPaperTransform();
		NormalTransform xntrans = getParent().getXAxisTransform().getNormalTransform();
		NormalTransform yntrans = getParent().getYAxisTransform().getNormalTransform();
		Dimension2D paperSize = getParent().getSize();
		double xorig = pxf.getXPtoD(xntrans.convToNR(xval) * paperSize.getWidth());
		double yorig = pxf.getYPtoD(yntrans.convToNR(yval) * paperSize.getHeight());
		double xscale = pxf.getScale() / xntrans.getScale() * paperSize.getWidth() * data.getXcdelt();
		double yscale = pxf.getScale() / yntrans.getScale() * paperSize.getHeight() * data.getYcdelt();
		AffineTransform scaleAT = AffineTransform.getScaleInstance(xscale, yscale);
		AffineTransformOp axop = new AffineTransformOp(scaleAT, AffineTransformOp.TYPE_BILINEAR);
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
	 * Apply the cuts and linear scale the array to a unsigned short array
	 * 
	 * @param array
	 * @param lowCut
	 * @param highCut
	 * @return
	 */
	private short[] zscaleLimits(ImageDataBuffer dbuf, int xoff, int yoff, int w, int h, double lowCut, double highCut) {

		int outputRange = 1 << mapping.getILUTInputBits();
		double scale = outputRange / (highCut - lowCut);

		short[] result = new short[w * h];
		int n = 0;
		short[] lut = mapping.getILUT();
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
					result[n++] = (short) ilutIndex;
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

					// the LUT output bits is no more than 15, the & 0xffff can omit.
					int a = lut[ilutIndex];
					int b = lut[ilutIndex + 1];
					result[n++] = (short) (a + idelta * (b - a));
				}
			}
		}

		return result;
	}

	/**
	 * Apply the color LUT to the given raster. If the given raster only has a band, it will be duplicated to meet the
	 * output band number.
	 * 
	 * @param bits
	 *            the number of bits for the single band raster
	 * @param raster
	 * @return
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

			// create a new raster which has duplicate bands
			short[] singleBandData = ((DataBufferUShort) raster.getDataBuffer()).getData();
			int singleBandSize = ((DataBufferUShort) raster.getDataBuffer()).getSize();

			int[] bitsArray = new int[destNumComps];
			short[][] dupDataArray = new short[destNumComps][];
			for (int i = 0; i < destNumComps; i++) {
				bitsArray[i] = bits;
				dupDataArray[i] = singleBandData;
			}

			SampleModel scm = raster.getSampleModel();
			SampleModel dupSM = new BandedSampleModel(scm.getDataType(), scm.getWidth(), scm.getHeight(), destNumComps);
			DataBufferUShort dbuffer = new DataBufferUShort(dupDataArray, singleBandSize);
			raster = Raster.createWritableRaster(dupSM, dbuffer, null);
		}

		if (mapping.getColorMap() == null) {
			// assembly a BufferedImage with sRGB color sapce
			int[] bitsArray = new int[] { bits, bits, bits };
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
