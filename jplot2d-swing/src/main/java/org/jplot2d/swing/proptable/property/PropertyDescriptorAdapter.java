/*
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

import org.jplot2d.env.PropertyInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An adapter to use PropertyDescriptor in MainProperty manner.
 */
class PropertyDescriptorAdapter<T> extends MainProperty<T> {

    private final PropertyInfo descriptor;

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

    public int getDisplayDigits() {
        return descriptor.getDisplayDigits();
    }

    @SuppressWarnings("unchecked")
    public Class<T> getType() {
        return (Class<T>) descriptor.getPropertyType();
    }

    /**
     * Load property from the given object, by the read method of PropertyDescriptor.
     */
    @SuppressWarnings("unchecked")
    public void readFromObject(Object object) {
        Method method = descriptor.getReadMethod();
        if (method != null) {
            try {
                setValue((T) method.invoke(object));
            } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException ignored) {
                // should not happen
            }
        }
    }

    /**
     * Write property to the given object, by the write method of PropertyDescriptor.
     *
     * @throws Throwable
     */
    public void writeToObject(Object object) throws Throwable {
        Method method = descriptor.getWriteMethod();
        if (method != null) {
            try {
                method.invoke(object, getValue());
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    public boolean isEditable() {
        return descriptor.getWriteMethod() != null && !descriptor.isReadOnly();
    }

}
