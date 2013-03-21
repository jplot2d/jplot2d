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


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyEditor;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jplot2d.swing.proptable.cellrenderer.TableCellRendererFactory;
import org.jplot2d.swing.proptable.cellrenderer.TableCellRendererRegistry;
import org.jplot2d.swing.proptable.editor.PropertyEditorFactory;
import org.jplot2d.swing.proptable.editor.PropertyEditorRegistry;
import org.jplot2d.swing.proptable.property.Property;

/**
 * 
 * @author Jingjing Li
 */
public class PropertyTable extends JTable {

    private static final int HOTSPOT_SIZE = 18;

    private static final String TREE_EXPANDED_ICON_KEY = "Tree.expandedIcon";

    private static final String TREE_COLLAPSED_ICON_KEY = "Tree.collapsedIcon";

    private static final String PANEL_BACKGROUND_COLOR_KEY = "Panel.background";

    private static class CellBorder implements Border {

        private int indentWidth; // space before hotspot

        private boolean showToggle;

        private boolean toggleState;

        private Color bgColor;

        private Icon expandedIcon = (Icon) UIManager
                .get(TREE_EXPANDED_ICON_KEY);

        private Icon collapsedIcon = (Icon) UIManager
                .get(TREE_COLLAPSED_ICON_KEY);

        private Insets insets = new Insets(1, 0, 1, 1);

        private boolean isProperty;

        public void configure(PropertyTable table, PropertyTableItem item) {
            isProperty = item.isProperty();
            toggleState = item.isVisible();
            showToggle = item.hasToggle();
            if (!isProperty) {
                bgColor = table.getGridColor();
            }
            indentWidth = getIndent(item);
            insets.left = indentWidth + (showToggle ? HOTSPOT_SIZE : 0) + 2;

        }

        public Insets getBorderInsets(Component c) {
            return insets;
        }

        public void paintBorder(Component c, Graphics g, int x, int y,
                int width, int height) {
            if (!isProperty) {
                Color oldColor = g.getColor();
                g.setColor(bgColor);
                g.fillRect(x, y, x + HOTSPOT_SIZE - 2, y + height);
                g.setColor(oldColor);
            }

            if (showToggle) {
                Icon drawIcon = (toggleState ? expandedIcon : collapsedIcon);
                drawIcon.paintIcon(c, g, x + indentWidth
                        + (HOTSPOT_SIZE - 2 - drawIcon.getIconWidth()) / 2, y
                        + (height - drawIcon.getIconHeight()) / 2);
            }
        }

        public boolean isBorderOpaque() {
            return true;
        }

    }

    /**
     * A {@link TableCellRenderer} for property names.
     */
    private static class NameRenderer extends DefaultTableCellRenderer {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private CellBorder border;

        public NameRenderer() {
            super();
            border = new CellBorder();
        }

        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
            setBorder(border);

            PropertyTableItem item = (PropertyTableItem) value;
            // configure the border
            border.configure((PropertyTable) table, item);

            if (item.isProperty()) {
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
            } else {
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(table.getGridColor());
                    setForeground(table.getGridColor().darker());
                }
            }

            setEnabled(isSelected || !item.isProperty() ? true : item
                    .getProperty().isEditable());
            setFont(getFont().deriveFont(
                    item.isProperty() ? Font.PLAIN : Font.BOLD));
            setText(item.getName());

            return this;
        }
    }

    private static class CategoryValueRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getGridColor());
                setForeground(table.getGridColor().darker());
            }
            setText("");
            return this;
        }
    }

    private static class CategoryVisibilityToggle extends MouseAdapter {
        public void mouseReleased(MouseEvent event) {
            PropertyTable table = (PropertyTable) event.getComponent();
            int row = table.rowAtPoint(event.getPoint());
            int column = table.columnAtPoint(event.getPoint());
            if (row != -1 && column == 0) {
                // if we clicked on an Item, see if we clicked on its hotspot
                PropertyTableItem item = table.getModel().getPropertyTableItem(
                        row);
                int x = event.getX() - getIndent(item);
                if (x > 0 && x < HOTSPOT_SIZE)
                    item.toggle();
            }
        }
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private TableCellRenderer categoryValueRenderer;

    private TableCellRenderer nameRenderer;

    private TableCellRendererFactory rendererFactory;

    private PropertyEditorFactory editorFactory;

    public PropertyTable() {
        super(new PropertyTableModel());

        // hide the table header, we do not need it
        Dimension nullSize = new Dimension(0, 0);
        getTableHeader().setPreferredSize(nullSize);
        getTableHeader().setMinimumSize(nullSize);
        getTableHeader().setMaximumSize(nullSize);
        getTableHeader().setVisible(false);

        // table header not being visible, make sure we can still resize the
        // columns
        new HeaderlessColumnResizer(this);

        // setRowHeight(22);

        setGridColor(UIManager.getColor(PANEL_BACKGROUND_COLOR_KEY));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setColumnSelectionAllowed(false);
        setRowSelectionAllowed(true);
        setFillsViewportHeight(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        addMouseListener(new CategoryVisibilityToggle());

        nameRenderer = new NameRenderer();
        categoryValueRenderer = new CategoryValueRenderer();

        // force the JTable to commit the edit when it loose focus
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // default renderers and editors
        setTableCellRendererFactory(new TableCellRendererRegistry());
        setEditorFactory(new PropertyEditorRegistry());
    }

    public TableCellRenderer getCellRenderer(int row, int column) {

        PropertyTableModel model = getModel();
        PropertyTableItem item = model.getPropertyTableItem(row);

        switch (column) {
        case PropertyTableModel.NAME_COLUMN:
            // name column gets a custom renderer
            ((JComponent) nameRenderer).setToolTipText(item.getToolTipText());
            return nameRenderer;

        case PropertyTableModel.VALUE_COLUMN: {
            if (!item.isProperty())
                return categoryValueRenderer;

            // property value column gets the renderer from the factory, but
            // wrapped
            Property<?> property = item.getProperty();
            TableCellRenderer renderer = getTableCellRendererFactory()
                    .createTableCellRenderer(property);

            Class<?> type = getWrapperClass(property.getType());
            if (renderer == null) {
                renderer = getDefaultRenderer(type);
            }
            if (renderer instanceof JCheckBox) {
                ((JCheckBox) renderer).setHorizontalAlignment(JCheckBox.LEFT);
                ((JCheckBox) renderer).setEnabled(property.isEditable());
            }
            return renderer;
        }
        default:
            // when will this happen, given the above?
            return super.getCellRenderer(row, column);
        }

    }

    public TableCellEditor getCellEditor(int row, int column) {

        if (column == 0) {
            return null;
        }

        PropertyTableItem item = getModel().getPropertyTableItem(row);
        if (!item.isProperty())
            return null;

        Property<?> property = item.getProperty();
        PropertyEditor editor = getEditorFactory().createPropertyEditor(
                property);

        if (editor != null) {
            return new PropertyCellEditor(editor);
        }

        return null;
    }

    public PropertyTableModel getModel() {
        return (PropertyTableModel) super.getModel();
    }

    public void setTableCellRendererFactory(TableCellRendererFactory factory) {
        rendererFactory = factory;
    }

    public TableCellRendererFactory getTableCellRendererFactory() {
        return rendererFactory;
    }

    public void setEditorFactory(PropertyEditorFactory factory) {
        editorFactory = factory;
    }

    public final PropertyEditorFactory getEditorFactory() {
        return editorFactory;
    }

    static int getIndent(PropertyTableItem item) {
        int indent = 0;

        if (item.isProperty()) {
            // it is a property, it has no parent or a category, and no child
            if ((item.getParent() == null || !item.getParent().isProperty())
                    && !item.hasToggle()) {
                indent = HOTSPOT_SIZE;
            } else {
                // it is a property with children
                if (item.hasToggle()) {
                    indent = item.getDepth() * HOTSPOT_SIZE;
                } else {
                    indent = (item.getDepth() + 1) * HOTSPOT_SIZE;
                }
            }

            indent += HOTSPOT_SIZE;

        } else {
            // category has no indent
            indent = 0;
        }
        return indent;
    }

    protected void createDefaultRenderers() {
        super.createDefaultRenderers();
        /*
         * remove the internal renderer for Float & Double, because they use
         * NumberFormat to convert to String, which convert NaN into an
         * undisplayable character.
         */
        setDefaultRenderer(Float.class, null);
        setDefaultRenderer(Double.class, null);
    }

    private Class<?> getWrapperClass(Class<?> primitive) {
        if (primitive == byte.class) {
            return Byte.class;
        } else if (primitive == short.class) {
            return Short.class;
        } else if (primitive == int.class) {
            return Integer.class;
        } else if (primitive == long.class) {
            return Long.class;
        } else if (primitive == float.class) {
            return Float.class;
        } else if (primitive == double.class) {
            return Double.class;
        } else if (primitive == boolean.class) {
            return Boolean.class;
        } else if (primitive == char.class) {
            return Character.class;
        }
        return primitive;
    }

}