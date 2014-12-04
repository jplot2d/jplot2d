/**
 * Copyright 2010-2013 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
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

public class RGBImageGraphImpl extends GraphImpl implements RGBImageGraphEx, IntermediateCacheEx {

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
		MultiBandImageData olddata = this.data;
		this.data = data;

		if (olddata == null || data == null || data.getDataBuffer()[0] != olddata.getDataBuffer()[0]) {
			mapping.getRedTransform().recalcLimits();
		}
		if (olddata == null || data == null || data.getDataBuffer()[1] != olddata.getDataBuffer()[1]) {
			mapping.getGreenTransform().recalcLimits();
		}
		if (olddata == null || data == null || data.getDataBuffer()[2] != olddata.getDataBuffer()[2]) {
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

	public Object createCacheHolder() {
		if (data == null) {
			return null;
		}

		ImageZscaleCache.Key[] cacheHolder = new ImageZscaleCache.Key[3];
		ImageBandTransformEx redTrans = mapping.getRedTransform();
		ImageBandTransformEx greenTrans = mapping.getGreenTransform();
		ImageBandTransformEx blueTrans = mapping.getBlueTransform();
		cacheHolder[0] = ImageZscaleCache.createCacheFor(data.getDataBuffer()[0], data.getWidth(), data.getHeight(),
				redTrans.getLimits(), redTrans.getIntensityTransform(), redTrans.getBias(), redTrans.getGain(), 8);
		cacheHolder[1] = ImageZscaleCache.createCacheFor(data.getDataBuffer()[0], data.getWidth(), data.getHeight(),
				greenTrans.getLimits(), greenTrans.getIntensityTransform(), greenTrans.getBias(), greenTrans.getGain(),
				8);
		cacheHolder[2] = ImageZscaleCache.createCacheFor(data.getDataBuffer()[0], data.getWidth(), data.getHeight(),
				blueTrans.getLimits(), blueTrans.getIntensityTransform(), blueTrans.getBias(), blueTrans.getGain(), 8);

		return cacheHolder;
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
		if (data == null) {
			return;
		}

		ImageBandTransformEx redTrans = mapping.getRedTransform();
		ImageBandTransformEx greenTrans = mapping.getGreenTransform();
		ImageBandTransformEx blueTrans = mapping.getBlueTransform();
		double[] redLimits = mapping.getRedTransform().getLimits();
		double[] greenLimits = mapping.getGreenTransform().getLimits();
		double[] blueLimits = mapping.getBlueTransform().getLimits();

		// limits is null means there is no valid data
		if (redLimits == null && greenLimits == null && blueLimits == null) {
			return;
		}

		// find a proper region to process
		int width = data.getWidth();
		int height = data.getHeight();
		double xval = data.getXRange().getMin();
		double yval = data.getYRange().getMin();

		// apply limits to generate a raster
		WritableRaster raster = null;
		ImageDataBuffer[] idbs = data.getDataBuffer();
		int bands = idbs.length;
		byte[][] result = new byte[bands][];
		result[0] = (byte[]) ImageZscaleCache.getValue(idbs[0], width, height, redLimits,
				redTrans.getIntensityTransform(), redTrans.getBias(), redTrans.getGain(), 8);
		result[1] = (byte[]) ImageZscaleCache.getValue(idbs[1], width, height, greenLimits,
				greenTrans.getIntensityTransform(), greenTrans.getBias(), greenTrans.getGain(), 8);
		result[2] = (byte[]) ImageZscaleCache.getValue(idbs[2], width, height, blueLimits,
				blueTrans.getIntensityTransform(), blueTrans.getBias(), blueTrans.getGain(), 8);

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

}