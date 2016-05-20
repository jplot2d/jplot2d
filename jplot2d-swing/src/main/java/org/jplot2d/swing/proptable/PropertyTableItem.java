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
package org.jplot2d.swing.proptable;

import org.jplot2d.swing.proptable.property.Property;

/**
 * Represent a line in PropertyTableModel.
 *
 * @author Jingjing Li
 */
public class PropertyTableItem {
    /**
     *
     */
    private final PropertyTableModel propertyTableModel;

    private final String name;

    private Property<?> property;

    private final PropertyTableItem parent;

    private boolean hasToggle = true;

    private boolean visible = true;

    PropertyTableItem(PropertyTableModel propertyTableModel, String name, PropertyTableItem parent) {
        this.propertyTableModel = propertyTableModel;
        this.name = name;
        this.parent = parent;
        // this is not a property but a category, always has toggle
        this.hasToggle = true;
    }

    PropertyTableItem(PropertyTableModel propertyTableModel, Property<?> property, PropertyTableItem parent) {
        this.propertyTableModel = propertyTableModel;
        this.name = property.getDisplayName();
        this.property = property;
        this.parent = parent;

        // properties toggle if there are sub-properties
        Property<?>[] subProperties = property.getSubProperties();
        hasToggle = subProperties != null && subProperties.length > 0;
    }

    public String getName() {
        return name;
    }

    public boolean isProperty() {
        return property != null;
    }

    @SuppressWarnings("rawtypes")
    public Property getProperty() {
        return property;
    }

    public PropertyTableItem getParent() {
        return parent;
    }

    public int getDepth() {
        int depth = 0;
        if (parent != null) {
            depth = parent.getDepth();
            if (parent.isProperty())
                ++depth;
        }
        return depth;
    }

    public boolean hasToggle() {
        return hasToggle;
    }

    public void toggle() {
        if (hasToggle()) {
            visible = !visible;
            this.propertyTableModel.republish();
        }
    }

    public boolean isVisible() {
        return (parent == null || parent.isVisible())
                && (!hasToggle || visible);
    }

    public String getToolTipText() {
        if (property == null) {
            return "Group " + getName();
        }
        return property.getShortDescription();
    }
}