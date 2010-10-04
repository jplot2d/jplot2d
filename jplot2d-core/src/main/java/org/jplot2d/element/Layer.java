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
package org.jplot2d.element;

import org.jplot2d.util.MathElement;

/**
 * A layer can contains an dataset and optionally some markers. Every layer has
 * its own viewport to show data line and markers.
 * 
 * @author Jingjing Li
 * 
 */
public interface Layer extends Component {

	/**
	 * Returns the name displayed in the legend
	 * 
	 * @return the name
	 */
	public MathElement getName();

	/**
	 * Sets the name displayed in the legend
	 * 
	 * @param name
	 *            the name displayed in the legend
	 */
	public void setName(MathElement name);

	/**
	 * Returns the data model of this layer.
	 * 
	 * @return the data model of this layer.
	 */
	public DataModel getDataModel();

	/**
	 * Sets the data model to be shown in this layer.
	 * 
	 * @param dataModel
	 *            the data model to be shown in this layer.
	 */
	public void setDataModel(DataModel dataModel);

	/**
	 * Returns the marker.
	 * 
	 * @param id
	 *            the index of markers
	 * @return the marker.
	 */
	public Marker getMarker(int idx);

	/**
	 * Add a new marker to this layer.
	 * 
	 * @param marker
	 *            the marker to be added
	 */
	public void addMarker(Marker marker);

}
