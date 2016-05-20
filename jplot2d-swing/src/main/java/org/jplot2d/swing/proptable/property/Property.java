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

/**
 * A property to be displayed in PropertyTable.
 */
public interface Property<T> {

    String getName();

    String getDisplayName();

    String getShortDescription();

    int getDisplayDigits();

    Class<T> getType();

    T getValue();

    void setValue(T value);

    boolean isEditable();

    Property<?>[] getSubProperties();

    /**
     * If the returned value is not <code>null</code>, The table will use a JComboBox to edit this property.
     *
     * @return all available values
     */
    Object[] getAvailableValues();

}
