/*
 * Copyright 2010-2012 Jingjing Li.
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
package org.jplot2d.swt.interaction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.jplot2d.env.RenderEnvironment;

import java.io.File;
import java.io.IOException;

/**
 * Handle actions of a popup menu
 *
 * @author Jingjing Li
 */
public class MenuHandler implements MenuListener, SelectionListener {

    protected final Control control;

    protected final Menu menu;

    protected final RenderEnvironment env;

    /**
     * Properties action command.
     */
    protected final MenuItem propertiesItem;

    /**
     * undo/redo
     */
    protected final MenuItem undoItem;

    protected final MenuItem redoItem;

    /**
     * Save action command.
     */
    protected final MenuItem exportItem;

    /**
     * Print action command.
     */
    protected final MenuItem printItem;

    protected final MenuItem printImageItem;

    /**
     * Menu item for resetting the zoom (both axes).
     */
    protected MenuItem autoRangeBothMenuItem;

    /**
     * Menu item for resetting the zoom (horizontal axis only).
     */
    protected MenuItem autoRangeHorizontalMenuItem;

    /**
     * Menu item for resetting the zoom (vertical axis only).
     */
    protected MenuItem autoRangeVerticalMenuItem;

    protected MenuItem pickCoordinateMenuItem;

    public MenuHandler(Control control, RenderEnvironment env) {
        this.control = control;
        this.env = env;
        menu = new Menu(control);
        menu.addMenuListener(this);

        propertiesItem = new MenuItem(menu, SWT.PUSH);
        propertiesItem.setText("Properties...");
        propertiesItem.addSelectionListener(this);

        //-------------------------------------------
        new MenuItem(menu, SWT.SEPARATOR);

        undoItem = new MenuItem(menu, SWT.PUSH);
        undoItem.setText("Undo");
        undoItem.addSelectionListener(this);
        redoItem = new MenuItem(menu, SWT.PUSH);
        redoItem.setText("Redo");
        redoItem.addSelectionListener(this);

        //-------------------------------------------
        new MenuItem(menu, SWT.SEPARATOR);

        exportItem = new MenuItem(menu, SWT.PUSH);
        exportItem.setText("Export...");
        exportItem.addSelectionListener(this);

        //-------------------------------------------
        new MenuItem(menu, SWT.SEPARATOR);

        printItem = new MenuItem(menu, SWT.PUSH);
        printItem.setText("Print ...");
        printItem.addSelectionListener(this);
        printImageItem = new MenuItem(menu, SWT.PUSH);
        printImageItem.setText("Print image ...");
        printImageItem.addSelectionListener(this);

    }

    public Menu getMenu() {
        return menu;
    }

    public void menuHidden(MenuEvent e) {

    }

    public void menuShown(MenuEvent e) {
        /*
         * Update the internal status according to the point where the menu popup.
		 */

        // undo/redo
        undoItem.setEnabled(env.canUndo());
        redoItem.setEnabled(env.canRedo());

    }

    public void widgetDefaultSelected(SelectionEvent e) {

    }

    public void widgetSelected(SelectionEvent event) {

        Object source = event.getSource();

        if (source == propertiesItem) {
            MessageBox msgbox = new MessageBox(control.getShell(), SWT.OK | SWT.ICON_INFORMATION
                    | SWT.APPLICATION_MODAL);
            msgbox.setMessage("Will be avaliable in next version.");
            msgbox.open();
            return;
        }
        if (source == undoItem) {
            env.undo();
            return;
        }
        if (source == redoItem) {
            env.redo();
            return;
        }
        if (source == exportItem) {
            try {
                doSaveAs();
            } catch (IOException e) {
                String msg = "I/O exception: " + e.getMessage();
                MessageBox msgbox = new MessageBox(control.getShell(), SWT.OK | SWT.ICON_ERROR | SWT.APPLICATION_MODAL);
                msgbox.setMessage(msg);
                msgbox.open();
            }
            return;
        }
        if (source == printItem) {
            // TODO: print the plot
            return;
        }
        if (source == printImageItem) {
            // TODO: print the plot image
            //noinspection UnnecessaryReturnStatement
            return;
        }

    }

    private static FileDialog _saveFileChooser;

    /**
     * Opens a file chooser and gives the user an opportunity to save the chart in PNG, JPG, EPS format.
     *
     * @throws IOException if there is an I/O error.
     */
    public void doSaveAs() throws IOException {

        if (_saveFileChooser == null) {
            initSaveFileChooser();
        }

        String filePath = _saveFileChooser.open();
        if (filePath != null) {
            File fFile = new File(filePath);
            String filename = fFile.getPath();
            String fileDesc = _saveFileChooser.getFilterNames()[_saveFileChooser.getFilterIndex()];
            if (fileDesc.contains("PNG")) {
                if (!filename.substring(filename.length() - 4).equalsIgnoreCase(".png")) {
                    filename = filename + ".png";
                }
            } else if (fileDesc.contains("JPEG")) {
                if (!filename.substring(filename.length() - 4).equalsIgnoreCase(".jpg")
                        && !filename.substring(filename.length() - 5).equalsIgnoreCase(".jpeg")) {
                    filename = filename + ".jpg";
                }
            } else if (fileDesc.contains("EPS")) {
                if (!filename.substring(filename.length() - 4).equalsIgnoreCase(".eps")) {
                    filename = filename + ".eps";
                }
            } else if (fileDesc.contains("PDF")) {
                if (!filename.substring(filename.length() - 4).equalsIgnoreCase(".pdf")) {
                    filename = filename + ".pdf";
                }
            }

            if (fileDesc.contains("PNG")) {
                env.exportToPNG(filename);
            } else if (fileDesc.contains("PDF")) {
                env.exportToPDF(filename);
            } else if (fileDesc.contains("EPS")) {
                env.exportToEPS(filename);
            }

        }

    }

    private void initSaveFileChooser() {
        _saveFileChooser = new FileDialog(control.getShell(), SWT.SAVE);
        _saveFileChooser.setFilterPath(RenderEnvironment.getDefaultExportDirectory());
        _saveFileChooser.setFilterNames(new String[]{"PNG file", "PDF file", "EPS file"});
        _saveFileChooser.setFilterExtensions(new String[]{"*.png", "*.pdf", "*.eps"});
        _saveFileChooser.setFilterIndex(0);
        _saveFileChooser.setOverwrite(true);
    }

}
