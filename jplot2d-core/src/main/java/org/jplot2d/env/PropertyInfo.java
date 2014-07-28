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
package org.jplot2d.env;

import java.beans.IntrospectionException;
import java.lang.reflect.Method;

/**
 * Information of a property. If a property is read-only, {@link #getWriteMethod()} will return <code>null</code>.
 * 
 * @author Jingjing Li
 * 
 */
public class PropertyInfo implements Comparable<PropertyInfo> {

	private final String name;
	private final Method readMethod;
	private final Method writeMethod;
	private final Class<?> type;
	private String displayName;
	private String shortDescription;
	private boolean readOnly;
	private int order;
	private boolean styleable;

	public PropertyInfo(String pname, Method reader, Method writer) throws IntrospectionException {
		name = pname;
		readMethod = reader;
		writeMethod = writer;
		type = findPropertyType(readMethod, writeMethod);
	}

	/**
	 * Gets the programmatic name of this feature.
	 * 
	 * @return The programmatic name of the property/method/event
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the Class object for the property.
	 * 
	 * @return The Java type info for the property. Note that the "Class" object may describe a built-in Java type such
	 *         as "int". The result may be "null" if this is an indexed property that does not support non-indexed
	 *         access.
	 *         <p>
	 *         This is the type that will be returned by the ReadMethod.
	 */
	public Class<?> getPropertyType() {
		return type;
	}

	/**
	 * Gets the method that should be used to read the property value.
	 * 
	 * @return The method that should be used to read the property value. May return null if the property can't be read.
	 */
	public Method getReadMethod() {
		return readMethod;
	}

	/**
	 * Gets the method that should be used to write the property value.
	 * 
	 * @return The method that should be used to write the property value. May return null if the property can't be
	 *         written.
	 */
	public Method getWriteMethod() {
		return writeMethod;
	}

	/**
	 * Gets the localized display name of this feature.
	 * 
	 * @return The localized display name for the property/method/event. This defaults to the same as its programmatic
	 *         name from getName.
	 */
	public String getDisplayName() {
		if (displayName == null) {
			return getName();
		}
		return displayName;
	}

	/**
	 * Sets the localized display name of this feature.
	 * 
	 * @param displayName
	 *            The localized display name for the property/method/event.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Gets the short description of this feature.
	 * 
	 * @return A localized short description associated with this property/method/event. This defaults to be the display
	 *         name.
	 */
	public String getShortDescription() {
		if (shortDescription == null) {
			return getDisplayName();
		}
		return shortDescription;
	}

	/**
	 * You can associate a short descriptive string with a feature. Normally these descriptive strings should be less
	 * than about 40 characters.
	 * 
	 * @param text
	 *            A (localized) short description to be associated with this property/method/event.
	 */
	public void setShortDescription(String text) {
		shortDescription = text;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isStyleable() {
		return styleable;
	}

	public void setStyleable(boolean styleable) {
		this.styleable = styleable;
	}

	/**
	 * Returns the property type that corresponds to the read and write method. The type precedence is given to the
	 * readMethod. If both read and write methods are null, IntrospectionException is thrown.
	 * 
	 * @return the type of the property descriptor.
	 * @throws IntrospectionException
	 *             if the read or write method is invalid
	 */
	private Class<?> findPropertyType(Method readMethod, Method writeMethod) throws IntrospectionException {
		Class<?> propertyType = null;

		if (readMethod != null) {
			Class<?>[] params = readMethod.getParameterTypes();
			if (params.length != 0) {
				throw new IntrospectionException("bad read method arg count: " + readMethod);
			}
			propertyType = readMethod.getReturnType();
			if (propertyType == Void.TYPE) {
				throw new IntrospectionException("read method " + readMethod.getName() + " returns void");
			}
		}
		if (writeMethod != null) {
			Class<?> params[] = writeMethod.getParameterTypes();
			if (params.length != 1) {
				throw new IntrospectionException("bad write method arg count: " + writeMethod);
			}
			if (propertyType != null && propertyType != params[0]) {
				throw new IntrospectionException("type mismatch between read and write methods");
			}
			propertyType = params[0];
		}

		if (propertyType == null) {
			throw new IntrospectionException("Both read and write methods are null");
		} else {
			return propertyType;
		}
	}

	public int compareTo(PropertyInfo o) {
		return order - o.order;
	}

	public String toString() {
		return name;
	}

}
