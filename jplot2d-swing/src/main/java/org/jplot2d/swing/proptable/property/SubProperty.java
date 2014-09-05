/**
 * Copyright 2010-2014 Jingjing Li.
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
package org.jplot2d.swing.proptable.property;

/**
 * The sub-property has no sub-property, and has no method to readFromObject/writeToObject
 * 
 * @author Jingjing Li
 * 
 */
public abstract class SubProperty<T> implements Property<T> {

	protected MainProperty<?> mainProp;

	public SubProperty(MainProperty<?> mainProp) {
		this.mainProp = mainProp;
	}

	public String getName() {
		return null;
	}

	public String getDisplayName() {
		return getName();
	}

	public String getShortDescription() {
		return getName();
	}

	public int getDisplayDigits() {
		return mainProp.getDisplayDigits();
	}

	public Property<?>[] getSubProperties() {
		return null;
	}

	public Class<T> getType() {
		return null;
	}

	public boolean isEditable() {
		return mainProp.isEditable();
	}

	public Object[] getAvailableValues() {
		return null;
	}

}
