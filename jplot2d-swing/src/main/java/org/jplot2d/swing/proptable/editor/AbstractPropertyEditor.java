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
package org.jplot2d.swing.proptable.editor;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

/**
 * Once the editor component change, {@link #firePropertyChange(Object, Object)}
 * should be called to notify all the listeners.
 * 
 */
public abstract class AbstractPropertyEditor implements PropertyEditor {

    protected Component editor;

    private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    public boolean isPaintable() {
        return false;
    }

    public void paintValue(Graphics gfx, Rectangle box) {
    }

    public boolean supportsCustomEditor() {
        return false;
    }

    public Component getCustomEditor() {
        return editor;
    }

    public String getJavaInitializationString() {
        return null;
    }

    public String getAsText() {
        return null;
    }

    public void setAsText(String text) throws IllegalArgumentException {
    }

    public String[] getTags() {
        return null;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(Object oldValue, Object newValue) {
        listeners.firePropertyChange("value", oldValue, newValue);
    }

}
