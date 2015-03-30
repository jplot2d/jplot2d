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
/**
 * $ $ License.
 *
 * Copyright $ L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jplot2d.swing.proptable;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.TableColumn;

/**
 * Allows table columns to be resized not only using the header but from any rows. Based on the BasicTableHeaderUI code.
 */
public class HeaderlessColumnResizer extends MouseInputAdapter {

    private static final Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);

    private int mouseXOffset;
    private Cursor otherCursor = resizeCursor;

    private final JTable table;

    public HeaderlessColumnResizer(JTable table) {
        this.table = table;
        table.addMouseListener(this);
        table.addMouseMotionListener(this);
    }

    private boolean canResize(TableColumn column) {
        return (column != null) && table.getTableHeader().getResizingAllowed() && column.getResizable();
    }

    private TableColumn getResizingColumn(Point p) {
        return getResizingColumn(p, table.columnAtPoint(p));
    }

    private TableColumn getResizingColumn(Point p, int column) {
        if (column == -1) {
            return null;
        }
        int row = table.rowAtPoint(p);
        Rectangle r = table.getCellRect(row, column, true);
        r.grow(-3, 0);
        if (r.contains(p)) {
            return null;
        }
        int midPoint = r.x + r.width / 2;
        int columnIndex;
        if (table.getTableHeader().getComponentOrientation().isLeftToRight()) {
            columnIndex = (p.x < midPoint) ? column - 1 : column;
        } else {
            columnIndex = (p.x < midPoint) ? column : column - 1;
        }
        if (columnIndex == -1) {
            return null;
        }
        return table.getTableHeader().getColumnModel().getColumn(columnIndex);
    }

    public void mousePressed(MouseEvent e) {
        table.getTableHeader().setDraggedColumn(null);
        table.getTableHeader().setResizingColumn(null);
        table.getTableHeader().setDraggedDistance(0);

        Point p = e.getPoint();

        // First find which header cell was hit
        int index = table.columnAtPoint(p);

        if (index != -1) {
            // The last 3 pixels + 3 pixels of next column are for resizing
            TableColumn resizingColumn = getResizingColumn(p, index);
            if (canResize(resizingColumn)) {
                table.getTableHeader().setResizingColumn(resizingColumn);
                if (table.getTableHeader().getComponentOrientation().isLeftToRight()) {
                    mouseXOffset = p.x - resizingColumn.getWidth();
                } else {
                    mouseXOffset = p.x + resizingColumn.getWidth();
                }
            }
        }
    }

    private void swapCursor() {
        Cursor tmp = table.getCursor();
        table.setCursor(otherCursor);
        otherCursor = tmp;
    }

    public void mouseMoved(MouseEvent e) {
        if (canResize(getResizingColumn(e.getPoint())) != (table.getCursor() == resizeCursor)) {
            swapCursor();
        }
    }

    public void mouseDragged(MouseEvent e) {
        int mouseX = e.getX();

        TableColumn resizingColumn = table.getTableHeader().getResizingColumn();

        boolean headerLeftToRight = table.getTableHeader().getComponentOrientation().isLeftToRight();

        if (resizingColumn != null) {
            int oldWidth = resizingColumn.getWidth();
            int newWidth;
            if (headerLeftToRight) {
                newWidth = mouseX - mouseXOffset;
            } else {
                newWidth = mouseXOffset - mouseX;
            }
            resizingColumn.setWidth(newWidth);

            Container container;
            if ((table.getTableHeader().getParent() == null)
                    || ((container = table.getTableHeader().getParent().getParent()) == null)
                    || !(container instanceof JScrollPane)) {
                return;
            }

            if (!container.getComponentOrientation().isLeftToRight() && !headerLeftToRight) {

                JViewport viewport = ((JScrollPane) container).getViewport();
                int viewportWidth = viewport.getWidth();
                int diff = newWidth - oldWidth;
                int newHeaderWidth = table.getWidth() + diff;

                /* Resize a table */
                Dimension tableSize = table.getSize();
                tableSize.width += diff;
                table.setSize(tableSize);

                /*
                 * If this table is in AUTO_RESIZE_OFF mode and has a horizontal
                 * scrollbar, we need to update a view's position.
                 */
                if ((newHeaderWidth >= viewportWidth) && (table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF)) {
                    Point p = viewport.getViewPosition();
                    p.x = Math.max(0, Math.min(newHeaderWidth - viewportWidth, p.x + diff));
                    viewport.setViewPosition(p);

                /* Update the original X offset value. */
                    mouseXOffset += diff;
                }

            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        //setDraggedDistance(0, viewIndexForColumn(header.getDraggedColumn()));

        table.getTableHeader().setResizingColumn(null);
        table.getTableHeader().setDraggedColumn(null);
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}
