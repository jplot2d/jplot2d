package org.jplot2d.element.impl;

import java.awt.Graphics2D;

import org.jplot2d.data.ImageData;
import org.jplot2d.element.ImageMapping;

public class ImageGraphImpl extends GraphImpl implements ImageGraphEx {

	private ImageMappingEx mapping;

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
		// TODO Auto-generated method stub
		return null;
	}

	public void setData(ImageData graph) {
		// TODO Auto-generated method stub

	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setName(String text) {
		// TODO Auto-generated method stub

	}

	public void thisEffectiveColorChanged() {
		// the color for NaN?
	}

	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub

	}

}
