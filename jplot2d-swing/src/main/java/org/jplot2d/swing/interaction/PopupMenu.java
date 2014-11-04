/**
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
package org.jplot2d.swing.interaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.swing.components.PlotPropertiesFrame;
import org.jplot2d.swing.print.PrintRenderer;

/**
 * The popup menu support for a plot.
 * 
 * @author Jingjing Li
 */
public class PopupMenu extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = 1L;

	protected RenderEnvironment env;

	/** Properties action command. */
	protected final String PROPERTIES_ACTION_COMMAND = "PROPERTIES";

	/** undo/redo */
	protected final String UNDO_ACTION_COMMAND = "UNDO";

	protected final String REDO_ACTION_COMMAND = "REDO";

	/** Save action command. */
	protected final String EXPORT_ACTION_COMMAND = "EXPORT";

	/** Print action command. */
	protected final String PRINT_PAGE_SETUP_COMMAND = "PAGE_SETUP";

	protected final String PRINT_ACTION_COMMAND = "PRINT";

	protected JMenuItem undoItem;

	protected JMenuItem redoItem;

	/** Menu item for resetting the zoom (both axes). */
	protected JMenuItem autoRangeBothMenuItem;

	/** Menu item for resetting the zoom (horizontal axis only). */
	protected JMenuItem autoRangeHorizontalMenuItem;

	/** Menu item for resetting the zoom (vertical axis only). */
	protected JMenuItem autoRangeVerticalMenuItem;

	protected JMenuItem pickCoordinateMenuItem;

	public PopupMenu(RenderEnvironment env) {
		super("jplot2d");
		this.env = env;

		boolean separator = false;

		JMenuItem propertiesItem = new JMenuItem("Properties...");
		propertiesItem.setActionCommand(PROPERTIES_ACTION_COMMAND);
		propertiesItem.addActionListener(this);
		super.add(propertiesItem);
		separator = true;

		if (separator) {
			super.addSeparator();
			separator = false;
		}

		{
			undoItem = new JMenuItem("Undo");
			undoItem.setActionCommand(UNDO_ACTION_COMMAND);
			undoItem.addActionListener(this);
			super.add(undoItem);
			redoItem = new JMenuItem("Redo");
			redoItem.setActionCommand(REDO_ACTION_COMMAND);
			redoItem.addActionListener(this);
			super.add(redoItem);
			separator = true;
		}

		if (separator) {
			super.addSeparator();
			separator = false;
		}

		JMenuItem saveItem = new JMenuItem("Export...");
		saveItem.setActionCommand(EXPORT_ACTION_COMMAND);
		saveItem.addActionListener(this);
		super.add(saveItem);
		separator = true;

		if (separator) {
			super.addSeparator();
			separator = false;
		}

		JMenuItem pageSetupItem = new JMenuItem("Page setup ...");
		pageSetupItem.setActionCommand(PRINT_PAGE_SETUP_COMMAND);
		pageSetupItem.addActionListener(this);
		super.add(pageSetupItem);
		JMenuItem printItem = new JMenuItem("Print...");
		printItem.setActionCommand(PRINT_ACTION_COMMAND);
		printItem.addActionListener(this);
		super.add(printItem);
		separator = true;

	}

	public void actionPerformed(ActionEvent event) {

		String command = event.getActionCommand();

		if (command.equals(PROPERTIES_ACTION_COMMAND)) {
			showProperties();
			return;
		}
		if (command.equals(UNDO_ACTION_COMMAND)) {
			env.undo();
			return;
		}
		if (command.equals(REDO_ACTION_COMMAND)) {
			env.redo();
			return;
		}
		if (command.equals(EXPORT_ACTION_COMMAND)) {
			try {
				doSaveAs();
			} catch (IOException e) {
				String msg = "I/O exception: " + e.getMessage();
				JOptionPane.showMessageDialog(getInvoker(), msg);
			}
			return;
		}
		if (command.equals(PRINT_PAGE_SETUP_COMMAND)) {
			PrintRenderer.pageDialog();
			return;
		}
		if (command.equals(PRINT_ACTION_COMMAND)) {
			try {
				PrintRenderer.printDialog(env);
			} catch (PrinterException e) {
				JOptionPane.showMessageDialog(getInvoker(), e.getMessage());
			}
			return;
		}

	}

	/**
	 * Create and show the properties frame.
	 */
	protected void showProperties() {
		JFrame frame = new PlotPropertiesFrame(env);
		frame.setVisible(true);
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

	private static JFileChooser _saveFileChooser;

	/**
	 * Opens a file chooser and gives the user an opportunity to save the chart in PNG, JPG, EPS format.
	 * 
	 * @throws IOException
	 *             if there is an I/O error.
	 */
	public void doSaveAs() throws IOException {

		final AtomicReference<IOException> ar = new AtomicReference<IOException>();

		if (_saveFileChooser == null) {
			initSaveFileChooser();
		}

		int option = _saveFileChooser.showSaveDialog(getInvoker());
		if (option == JFileChooser.APPROVE_OPTION) {
			File fFile = _saveFileChooser.getSelectedFile();
			String filename = fFile.getPath();
			String fileDesc = _saveFileChooser.getFileFilter().getDescription();
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

			if (confirmOverwrite(filename)) {
				try {
					if (fileDesc.contains("PNG")) {
						env.exportToPNG(filename);
					} else if (fileDesc.contains("PDF")) {
						env.exportToPDF(filename);
					} else if (fileDesc.contains("EPS")) {
						env.exportToEPS(filename);
					}
				} catch (IOException e) {
					ar.set(e);
				}
			}

		}

		if (ar.get() != null) {
			throw ar.get();
		}
	}

	private boolean confirmOverwrite(String filename) {
		File fFile = new File(filename);
		if (fFile.exists()) {
			int response = JOptionPane.showConfirmDialog(getInvoker(), "Overwrite existing file?", "Confirm Overwrite",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}
		return true;
	}

	private static void initSaveFileChooser() {
		_saveFileChooser = new JFileChooser(RenderEnvironment.getDefaultExportDirectory());
		_saveFileChooser.setAcceptAllFileFilterUsed(false);

		FileNameExtensionFilter PNG_FILE_FILTER = new FileNameExtensionFilter("PNG file", "png");
		FileNameExtensionFilter PDF_FILE_FILTER = new FileNameExtensionFilter("PDF file", "pdf");
		FileNameExtensionFilter EPS_FILE_FILTER = new FileNameExtensionFilter("EPS file", "eps");
		List<FileNameExtensionFilter> EXPORT_FILE_FILTERS = new ArrayList<FileNameExtensionFilter>(2);
		EXPORT_FILE_FILTERS.add(PNG_FILE_FILTER);
		EXPORT_FILE_FILTERS.add(PDF_FILE_FILTER);
		EXPORT_FILE_FILTERS.add(EPS_FILE_FILTER);

		for (FileNameExtensionFilter fnef : EXPORT_FILE_FILTERS) {
			_saveFileChooser.addChoosableFileFilter(fnef);
		}
		_saveFileChooser.setFileFilter(PNG_FILE_FILTER);
	}

}
