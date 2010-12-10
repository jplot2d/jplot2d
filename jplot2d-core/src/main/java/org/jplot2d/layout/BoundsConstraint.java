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
package org.jplot2d.layout;

import org.jplot2d.util.Insets2D;

/**
 * This constraint defines bounds relative to it's contaner's bounds.
 * 
 * @author Jingjing Li
 * 
 */
public class BoundsConstraint {

	private final Insets2D fixedInsets;

	private final Insets2D elasticInsets;

	/**
	 * 
	 */
	public BoundsConstraint() {
		this(new Insets2D(0, 0, 0, 0));
	}

	/**
	 * @param fixedMargin
	 *            the fixed margin in pt
	 */
	public BoundsConstraint(Insets2D fixedInsets) {
		this(fixedInsets, new Insets2D(0, 0, 0, 0));
	}

	/**
	 * @param fixedMargin
	 *            the fixed margin in pt
	 * @param elasticMargin
	 *            the elastic margin ratio.
	 */
	public BoundsConstraint(Insets2D fixedInsets, Insets2D elasticInsets) {
		this.fixedInsets = fixedInsets;
		this.elasticInsets = elasticInsets;
	}

	public Insets2D getFixedInsets() {
		return fixedInsets;
	}

	public Insets2D getelasticInsets() {
		return elasticInsets;
	}
}