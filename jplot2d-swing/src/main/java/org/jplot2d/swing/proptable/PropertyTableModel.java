/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.swing.proptable;


import org.jplot2d.swing.proptable.property.MainProperty;
import org.jplot2d.swing.proptable.property.Property;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertyTableModel extends AbstractTableModel {

    public static final int NAME_COLUMN = 0;
    public static final int VALUE_COLUMN = 1;
    private static final Logger logger = Logger.getLogger("org.jplot2d");

    private final List<PropertyTableItem> model = new ArrayList<>();

    private final List<PropertyTableItem> publishedModel = new ArrayList<>();

    private final Object engine;

    private final PropertiesModel pm;

    private Component comp;

    private final PropertyChangeListener pcl = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            /*
             * call the plot engine API to commit the change. Exception is caught and displayed in a
			 * dialog. The batch controller will call refresh to display new values in this table.
			 */
            MainProperty<?> prop = (MainProperty<?>) evt.getSource();
            try {
                prop.writeToObject(engine);
            } catch (Throwable e) {
                JOptionPane.showMessageDialog(comp, e.getMessage(), e.getClass().getSimpleName(), JOptionPane.WARNING_MESSAGE);
                logger.log(Level.WARNING, e.getMessage(), e);
                refresh();
            }
        }

    };

    public PropertyTableModel() {
        super();
        this.engine = null;
        this.pm = null;
    }

    public PropertyTableModel(Object engine, PropertiesModel propModel) {
        super();
        if (engine == null) {
            this.engine = null;
            this.pm = null;
            return;
        }

        this.engine = engine;
        this.pm = propModel;

        for (PropertyGroup group : pm) {
            String category = group.getName();
            PropertyTableItem categoryItem = new PropertyTableItem(this, category, null);
            model.add(categoryItem);
            addPropertiesToModel(group, categoryItem);
        }
        for (PropertyTableItem item : model) {
            PropertyTableItem parent = item.getParent();
            if (parent == null || parent.isVisible())
                publishedModel.add(item);
        }

		/* load properties from engine */
        for (PropertyTableItem item : model) {
            Property<?> property = item.getProperty();
            if (property instanceof MainProperty<?>) {
                ((MainProperty<?>) property).readFromObject(engine);
                ((MainProperty<?>) property).addPropertyChangeListener(pcl);
            }
        }

        pm.checkEditable(null);
    }

    /**
     * Add the specified properties to the model using the specified parent.
     *
     * @param localProperties the properties to add to the end of the model
     * @param parent          the {@link PropertyTableItem} parent of these properties, null if none
     */
    private void addPropertiesToModel(Iterable<Property<?>> localProperties,
                                      PropertyTableItem parent) {
        for (Property<?> property : localProperties) {
            PropertyTableItem propertyItem = new PropertyTableItem(this, property, parent);
            model.add(propertyItem);

            // add any sub-properties
            Property<?>[] subProperties = property.getSubProperties();
            if (subProperties != null && subProperties.length > 0) {
                addPropertiesToModel(Arrays.asList(subProperties), propertyItem);
            }
        }
    }

    public int getRowCount() {
        return publishedModel.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int row, int column) {
        PropertyTableItem item = getPropertyTableItem(row);
        if (item.isProperty() && column == VALUE_COLUMN) {
            return item.getProperty().getValue();
        } else {
            return item;
        }
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return "property";
        } else {
            return "value";
        }
    }

    @SuppressWarnings("unchecked")
    public void setValueAt(Object value, int row, int column) {
        PropertyTableItem item = getPropertyTableItem(row);
        if (item.isProperty() && column == VALUE_COLUMN) {
            item.getProperty().setValue(value);
        }
    }

    public boolean isCellEditable(int row, int column) {
        PropertyTableItem item = getPropertyTableItem(row);
        return item.isProperty() && column == VALUE_COLUMN && item.getProperty().isEditable();
    }

    /**
     * Get the current property sheet element at the specified row.
     */
    PropertyTableItem getPropertyTableItem(int rowIndex) {
        return publishedModel.get(rowIndex);
    }

    public Object getEngine() {
        return engine;
    }

    /**
     * fold the toggled group, derive a table view
     */
    void republish() {
        publishedModel.clear();
        for (PropertyTableItem item : model) {
            PropertyTableItem parent = item.getParent();
            if (parent == null || parent.isVisible())
                publishedModel.add(item);
        }
        fireTableDataChanged();
    }

    /**
     * Refresh the table by reloading the data from engine.
     */
    public void refresh() {
        if (engine == null) {
            // ignore refresh on null engine.
            return;
        }

		/* load properties from engine */
        for (PropertyTableItem item : model) {
            Property<?> property = item.getProperty();
            if (property instanceof MainProperty<?>) {
                MainProperty<?> mp = (MainProperty<?>) property;
                mp.removePropertyChangeListener(pcl);
                mp.readFromObject(engine);
                mp.addPropertyChangeListener(pcl);
            }
        }
        pm.checkEditable(null);
        fireTableDataChanged();
    }

    /**
     * Set the table component to be the parent of message dialog.
     *
     * @param dialogParent the table component to be the parent of message dialog
     */
    public void setDialogParent(Component dialogParent) {
        comp = dialogParent;
    }

}