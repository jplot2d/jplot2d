/**
 * Copyright 2010 Jingjing Li.
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
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.jplot2d.element.Element;
import org.jplot2d.element.Layer;
import org.jplot2d.element.SubPlot;

/**
 * @author Jingjing Li
 * 
 */
public class SubPlotImpl extends ContainerImpl implements SubPlot {

	public void setPhySize(Dimension2D size) {
		// TODO Auto-generated method stub
		
	}

	public Rectangle2D getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	public Rectangle2D getBoundsP() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addLayer(Layer layer) {
		// TODO Auto-generated method stub
		
	}

	public Element[] getElements() {
		// return layers.toArray(new Element[layers.size()]);
		return null;
	}

	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	public SubPlotImpl deepCopy(Map<Element, Element> orig2copyMap) {
		// TODO Auto-generated method stub
		return null;
	}

}
