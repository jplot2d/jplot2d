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

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.data.Graph;
import org.jplot2d.util.MathElement;

/**
 * @author Jingjing Li
 * 
 */
public interface GraphPlotter extends Component {

	@Hierarchy(HierarchyOp.GET)
	public Layer getParent();

	/**
	 * Returns the name displayed in the legend
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Sets the name displayed in the legend
	 * 
	 * @param name
	 *            the name displayed in the legend
	 */
	public void setName(String name);

	/**
	 * Returns the name displayed in the legend
	 * 
	 * @return the name
	 */
	public MathElement getNameModel();

	/**
	 * Sets the name displayed in the legend
	 * 
	 * @param name
	 *            the name displayed in the legend
	 */
	public void setNameModel(MathElement name);

	public Graph getGraph();

	public LegendItem getLegendItem();

}
