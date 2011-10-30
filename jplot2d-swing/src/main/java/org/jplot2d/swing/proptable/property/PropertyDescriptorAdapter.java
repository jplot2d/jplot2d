/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.swing.proptable.property;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jplot2d.env.PropertyInfo;

/**
 * An adapter to use PropertyDescriptor in MainProperty manner.
 * 
 */
class PropertyDescriptorAdapter<T> extends MainProperty<T> {

	private PropertyInfo descriptor;

	public PropertyDescriptorAdapter(PropertyInfo descriptor) {
		super();
		this.descriptor = descriptor;
	}

	public String getName() {
		return descriptor.getName();
	}

	public String getDisplayName() {
		return descriptor.getDisplayName();
	}

	public String getShortDescription() {
		return descriptor.getShortDescription();
	}

	@SuppressWarnings("unchecked")
	public Class<T> getType() {
		return (Class<T>) descriptor.getPropertyType();
	}

	/**
	 * Load property from the given object, by the PropertyDescriptor's read method.
	 */
	@SuppressWarnings("unchecked")
	public void readFromObject(Object object) {
		Method method = descriptor.getReadMethod();
		if (method != null) {
			try {
				setValue((T) method.invoke(object));
			} catch (InvocationTargetException e) {
				// should not happen
			} catch (IllegalArgumentException e) {
				// should not happen
			} catch (IllegalAccessException e) {
				// should not happen
			}
		}
	}

	/**
	 * Write property to the given object, by the PropertyDescriptor's write method.
	 * 
	 * @throws Throwable
	 */
	public void writeToObject(Object object) throws Throwable {
		Method method = descriptor.getWriteMethod();
		if (method != null) {
			try {
				method.invoke(object, new Object[] { getValue() });
			} catch (InvocationTargetException e) {
				throw e.getCause();
			}
		}
	}

	public boolean isEditable() {
		return super.isEditable() && descriptor.getWriteMethod() != null;
	}

}