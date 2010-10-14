package org.jplot2d.element.impl;

import org.jplot2d.element.MainAxis;

public interface MainAxisEx extends MainAxis, AxisEx {

	public LayerEx[] getLayers();

	public AuxAxisEx[] getAuxAxes();

	public void addLayer(LayerEx layer);

	public void removeLayer(LayerEx layer);

}
