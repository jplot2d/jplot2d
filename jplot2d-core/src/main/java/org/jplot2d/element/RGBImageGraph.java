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
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.PropertyGroup;
import org.jplot2d.data.MultiBandImageData;

/**
 * A image graph which can display a 3-bands data and map them to RGB color.
 * 
 * @author Jingjing Li
 * 
 */
@PropertyGroup("Image")
public interface RGBImageGraph extends Graph {

	/**
	 * Returns the mapping of this image.
	 * 
	 * @return the mapping of this image
	 */
	@Hierarchy(HierarchyOp.GET)
	public RGBImageMapping getMapping();

	@Hierarchy(HierarchyOp.JOIN)
	public void setMapping(RGBImageMapping mapping);

	public MultiBandImageData getData();

	public void setData(MultiBandImageData data);

}
