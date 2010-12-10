package org.jplot2d.element.impl;

import org.jplot2d.element.Layer;

public interface LayerEx extends Layer, ContainerEx {

	public SubplotEx getParent();

	public ViewportAxisEx getXViewportAxis();

	public ViewportAxisEx getYViewportAxis();

}
