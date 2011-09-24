/**
 * Copyright 2010, 2011 Jingjing Li.
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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.jplot2d.env.RenderEnvironment;

/**
 * Handle actions of a popup menu
 * 
 * @author Jingjing Li
 */
public class MenuHandler implements SelectionListener {

	protected final Control control;

	protected final Menu menu;

	protected final RenderEnvironment env;

	/** Properties action command. */
	protected MenuItem propertiesItem;

	/** undo/redo */
	protected MenuItem undoItem;

	protected MenuItem redoItem;

	/** Save action command. */
	protected MenuItem saveItem;

	/** Print action command. */
	protected MenuItem printItem;

	protected MenuItem printImageItem;

	/** Menu item for resetting the zoom (both axes). */
	protected MenuItem autoRangeBothMenuItem;

	/** Menu item for resetting the zoom (horizontal axis only). */
	protected MenuItem autoRangeHorizontalMenuItem;

	/** Menu item for resetting the zoom (vertical axis only). */
	protected MenuItem autoRangeVerticalMenuItem;

	protected MenuItem pickCoordinateMenuItem;

	public MenuHandler(Control control, RenderEnvironment env) {
		this.control = control;
		this.env = env;
		menu = new Menu(control);

		boolean separator = false;

		propertiesItem = new MenuItem(menu, SWT.PUSH);
		propertiesItem.setText("Properties...");
		propertiesItem.addSelectionListener(this);
		separator = true;

		if (separator) {
			new MenuItem(menu, SWT.SEPARATOR);
			separator = false;
		}

		{
			undoItem = new MenuItem(menu, SWT.PUSH);
			undoItem.setText("Undo");
			undoItem.addSelectionListener(this);
			redoItem = new MenuItem(menu, SWT.PUSH);
			redoItem.setText("Redo");
			redoItem.addSelectionListener(this);
			separator = true;
		}

		if (separator) {
			new MenuItem(menu, SWT.SEPARATOR);
			separator = false;
		}

		saveItem = new MenuItem(menu, SWT.PUSH);
		saveItem.setText("Save as...");
		saveItem.addSelectionListener(this);
		separator = true;

		if (separator) {
			new MenuItem(menu, SWT.SEPARATOR);
			separator = false;
		}

		printItem = new MenuItem(menu, SWT.PUSH);
		printItem.setText("Print ...");
		printItem.addSelectionListener(this);
		printImageItem = new MenuItem(menu, SWT.PUSH);
		printImageItem.setText("Print image ...");
		printImageItem.addSelectionListener(this);
		separator = true;

	}

	public Menu getMenu() {
		return menu;
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public void widgetSelected(SelectionEvent event) {

		Object source = event.getSource();

		if (source == propertiesItem) {
			// TODO: show properties panel
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
		if (source == saveItem) {
			try {
				doSaveAs();
			} catch (IOException e) {
				String msg = "SgtPlotXY doSaveAs: i/o exception = " + e.getMessage();
				MessageBox msgbox = new MessageBox(control.getShell(), SWT.OK | SWT.ICON_ERROR
						| SWT.APPLICATION_MODAL);
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
			return;
		}

	}

	/**
	 * Update the internal status according to the point where the menu popup.
	 * 
	 * @param x
	 * @param y
	 */
	public void updateStatus(int x, int y) {
		/* undo/redo */
		undoItem.setEnabled(env.canUndo());
		redoItem.setEnabled(env.canRedo());
	}

	private static FileDialog _saveFileChooser;

	/**
	 * Opens a file chooser and gives the user an opportunity to save the chart in PNG, JPG, EPS
	 * format.
	 * 
	 * @throws IOException
	 *             if there is an I/O error.
	 */
	public void doSaveAs() throws IOException {

		final AtomicReference<IOException> ar = new AtomicReference<IOException>();

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

			try {
				if (fileDesc.contains("PNG")) {
					env.saveAsPNG(filename);
				} else if (fileDesc.contains("PDF")) {
					env.saveAsPDF(filename);
				}
			} catch (IOException e) {
				ar.set(e);
			}

		}

		if (ar.get() != null) {
			throw ar.get();
		}
	}

	private void initSaveFileChooser() {
		_saveFileChooser = new FileDialog(control.getShell(), SWT.SAVE);
		_saveFileChooser.setFilterPath(RenderEnvironment.getDefaultExportDirectory());
		_saveFileChooser.setFilterNames(new String[] { "PNG file", "PDF file" });
		_saveFileChooser.setFilterExtensions(new String[] { "*.png", "*.pdf" });
		_saveFileChooser.setFilterIndex(0);
		_saveFileChooser.setOverwrite(true);
	}
}
