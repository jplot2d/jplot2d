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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * A TableCellEditor that adapt to PropertyEditor
 *
 * @author Jingjing Li
 */
public class PropertyCellEditor extends AbstractCellEditor implements
        TableCellEditor {

    private static final long serialVersionUID = 1L;

    private final PropertyEditor editor;

    protected final JComponent editorComponent;

    protected int clickCountToStart;

    private final FocusListener focusListener = new FocusListener() {
        public void focusGained(final FocusEvent e) {
            if (!(e.getSource() instanceof JTextField))
                return;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ((JTextField) e.getSource()).selectAll();
                }
            });
        }

        public void focusLost(final FocusEvent e) {
            if (!(e.getSource() instanceof JTextField))
                return;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ((JTextField) e.getSource()).select(0, 0);
                }
            });
        }
    };

    private final ActionListener commitActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            stopCellEditing();
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private final ActionListener cancelActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            cancelCellEditing();
        }
    };

    private final PropertyChangeListener pcListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            stopCellEditing();
        }
    };

    public PropertyCellEditor(PropertyEditor propertyEditor) {
        this.editor = propertyEditor;
        this.editorComponent = (JComponent) editor.getCustomEditor();
        this.clickCountToStart = 1;

        if (editorComponent instanceof JTextField) {
            JTextField field = (JTextField) editorComponent;
            field.addFocusListener(focusListener);
            field.addActionListener(commitActionListener);
            field.registerKeyboardAction(cancelActionListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED);
        }

        editor.addPropertyChangeListener(pcListener);
    }

    public Object getCellEditorValue() {
        return editor.getValue();
    }

    public void setClickCountToStart(int count) {
        clickCountToStart = count;
    }

    public int getClickCountToStart() {
        return clickCountToStart;
    }

    public boolean isCellEditable(EventObject anEvent) {
        return !(anEvent instanceof MouseEvent) || ((MouseEvent) anEvent).getClickCount() >= clickCountToStart;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    public boolean stopCellEditing() {
        removeListeners();
        fireEditingStopped();
        return true;
    }

    public void cancelCellEditing() {
        removeListeners();
        fireEditingCanceled();
    }

    private void removeListeners() {
        if (editorComponent instanceof JTextField) {
            JTextField field = (JTextField) editorComponent;
            field.removeFocusListener(focusListener);
            field.removeActionListener(commitActionListener);
            field.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        }

        editor.removePropertyChangeListener(pcListener);
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        if (editorComponent instanceof JCheckBox) {
            /* in order to avoid a "flashing" effect when clicking a checkbox in a table,
             * it is important for the editor to have as a border the same border that the renderer has,
             * and have as the background the same color as the renderer has.
             * This is primarily only needed for JCheckBox since this editor doesn't fill all the visual space of the table cell, unlike a text field.
             */
            TableCellRenderer renderer = table.getCellRenderer(row, column);
            Component c = renderer.getTableCellRendererComponent(table, value, isSelected, true, row, column);
            if (c != null) {
                editorComponent.setOpaque(true);
                editorComponent.setBackground(c.getBackground());
                if (c instanceof JComponent) {
                    editorComponent.setBorder(((JComponent) c).getBorder());
                }
            } else {
                editorComponent.setOpaque(false);
            }
        } else if (editorComponent instanceof JTextField) {
            JTextField field = (JTextField) editorComponent;
            field.setSelectedTextColor(table.getSelectionForeground());
            field.setSelectionColor(table.getSelectionBackground());
        } else {
            editorComponent.setForeground(table.getSelectionForeground());
            editorComponent.setBackground(table.getSelectionBackground());
        }
        editorComponent.setFont(table.getFont());
        editor.setValue(value);

        return editorComponent;
    }

}