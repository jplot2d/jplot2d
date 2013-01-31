package org.jplot2d.element.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jplot2d.element.ImageGraph;

public class ImageMappingImpl extends ElementImpl implements ImageMappingEx {

	private List<ImageGraphEx> graphs = new ArrayList<ImageGraphEx>();

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

}
