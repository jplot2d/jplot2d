package org.jplot2d.element.impl;

import java.awt.Graphics2D;
import java.awt.Shape;

import org.jplot2d.data.ImageData;
import org.jplot2d.element.ImageMapping;

public class ImageGraphImpl extends GraphImpl implements ImageGraphEx {

	private ImageMappingEx mapping;
	private ImageData data;

	public ImageGraphImpl() {
		super();
	}

	public ImageMapping getMapping() {
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

	public void draw(Graphics2D graphics) {
		if (getData() == null) {
			return;
		}

		Graphics2D g = (Graphics2D) graphics.create();
		Shape clip = getPaperTransform().getPtoD(getBounds());
		g.setClip(clip);

		g.dispose();
	}

}
