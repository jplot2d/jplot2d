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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Main property who has value and can read from / write to a engine object
 * 
 * @author Jingjing Li
 * 
 * @param <T>
 *            the value type
 */
public abstract class MainProperty<T> implements Property<T> {

    private T value;

    private boolean editable = true;

    private transient PropertyChangeSupport listeners = new PropertyChangeSupport(
            this);

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        Object oldValue = this.value;
        this.value = value;
        if (value != oldValue && (value == null || !value.equals(oldValue))) {
            firePropertyChange(oldValue, getValue());
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
        Property<?>[] subProperties = getSubProperties();
        if (subProperties != null)
            for (Property<?> property : subProperties) {
                if (property instanceof MainProperty<?>) {
                    ((MainProperty<?>) property)
                            .addPropertyChangeListener(listener);
                }
            }

    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.removePropertyChangeListener(listener);
        Property<?>[] subProperties = getSubProperties();
        if (subProperties != null)
            for (Property<?> property : subProperties) {
                if (property instanceof MainProperty<?>) {
                    ((MainProperty<?>) property)
                            .removePropertyChangeListener(listener);
                }
            }
    }

    protected void firePropertyChange(Object oldValue, Object newValue) {
        listeners.firePropertyChange("value", oldValue, newValue);
    }

    public Property<?>[] getSubProperties() {
        return null;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean b) {
        editable = b;
    }

    /**
     * Load property from the given object.
     * 
     * @param object
     * @throws Throwable
     */
    public abstract void readFromObject(Object object);

    /**
     * Write property to the given object.
     * 
     * @param object
     * @throws Throwable
     */
    public abstract void writeToObject(Object object) throws Throwable;

    public Object[] getAvailableValues() {
        return null;
    }

    public String toString() {
        return "MainProperty " + getName();
    }

}
