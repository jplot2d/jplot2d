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
package org.jplot2d.swing.proptable.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;

/**
 * This class is provided as a safe alternative to JDK's JColorChooser. <br>
 * JColorChooser can cause the EDT thread to sleep for an unknown time while in its constructor,
 * causing your application to freeze: see <a
 * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6419354">JDK Bug 6419354</a>.
 * <p>
 * <b> IMPORTANT: </b> JColorChooser should <b>not</b> be used, but this class. <br>
 * Note that this chooser does not have the <b>HSB tab</b>, if you need it you will have to build
 * your own, as it is the creation of this tab the one that can freeze the whole application.
 * <p>
 * Do not use the static factory method of JColorChooser
 * {@link JColorChooser#showDialog(Component, String, Color)} but the one available from this class
 * {@link ColorChooser#showDialog(Component, String, Color)} .
 */
public class ColorChooser extends JColorChooser {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a color chooser pane with an initial color of white.
	 */
	public ColorChooser() {
		super();
	}

	/**
	 * Creates a color chooser pane with the specified initial color.
	 * 
	 * @param initialColor
	 *            the initial color set in the chooser
	 */
	public ColorChooser(Color color) {
		super(color);
	}

	/**
	 * Creates a color chooser pane with the specified <code>ColorSelectionModel</code>.
	 * 
	 * @param model
	 *            the <code>ColorSelectionModel</code> to be used
	 */
	public ColorChooser(ColorSelectionModel model) {
		super(model);
	}

	@Override
	public void setChooserPanels(AbstractColorChooserPanel[] panels) {
		AbstractColorChooserPanel[] panels2 = panels;
		if (panels != null) {
			int numNeeded = 0;
			for (int i = 0; i < panels.length; i++) {
				if (!(panels[i].getClass().getName().contains("DefaultHSBChooserPanel")))
					numNeeded++;
			}
			if (numNeeded < panels.length) {
				panels2 = new AbstractColorChooserPanel[numNeeded];
				int j = 0;
				for (int i = 0; i < panels.length; i++) {
					if (!(panels[i].getClass().getName().contains("DefaultHSBChooserPanel")))
						panels2[j++] = panels[i];
				}
			}
		}
		super.setChooserPanels(panels2);
	}

	/**
	 * Shows a modal color-chooser dialog and blocks until the dialog is hidden. If the user presses
	 * the "OK" button, then this method hides/disposes the dialog and returns the selected color.
	 * If the user presses the "Cancel" button or closes the dialog without pressing "OK", then this
	 * method hides/disposes the dialog and returns <code>null</code>.<br>
	 * 
	 * This is an alternative to the static method in JColorChooser class, that creates a
	 * ColorChooser instead of a JColorChooser (forbidden to create).
	 * 
	 * @param component
	 *            the parent <code>Component</code> for the dialog
	 * @param title
	 *            the String containing the dialog's title
	 * @param initialColor
	 *            the initial Color set when the color-chooser is shown
	 * @return the selected color or <code>null</code> if the user opted out
	 * @exception HeadlessException
	 *                if GraphicsEnvironment.isHeadless() returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static Color showDialog(Component component, String title, Color initialColor)
			throws HeadlessException {

		final ColorChooser pane = new ColorChooser(initialColor != null ? initialColor
				: Color.WHITE);

		ColorTracker ok = new ColorTracker(pane);
		JDialog dialog = createDialog(component, title, true, pane, ok, null);

		dialog.setVisible(true); // blocks until user brings dialog down...

		return ok.getColor();
	}

	/**
	 * Creates and returns a new dialog containing the specified <code>ColorChooser</code> pane
	 * along with "OK", "Cancel", and "Reset" buttons. If the "OK" or "Cancel" buttons are pressed,
	 * the dialog is automatically hidden (but not disposed). If the "Reset" button is pressed, the
	 * color-chooser's color will be reset to the color which was set the last time
	 * <code>show</code> was invoked on the dialog and the dialog will remain showing.
	 * 
	 * This is a ColorChooser restricted forwarder to the static method provided by JColorChooser.
	 * 
	 * @param c
	 *            the parent component for the dialog
	 * @param title
	 *            the title for the dialog
	 * @param modal
	 *            a boolean. When true, the remainder of the program is inactive until the dialog is
	 *            closed.
	 * @param chooserPane
	 *            the color-chooser to be placed inside the dialog
	 * @param okListener
	 *            the ActionListener invoked when "OK" is pressed
	 * @param cancelListener
	 *            the ActionListener invoked when "Cancel" is pressed
	 * @return a new dialog containing the color-chooser pane
	 * @exception HeadlessException
	 *                if GraphicsEnvironment.isHeadless() returns true.
	 * @exception IllegalArgumentExcception
	 *                if invoked with a JColorChooser instead of a ColorChooser
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static JDialog createDialog(Component c, String title, boolean modal,
			JColorChooser chooserPane, ActionListener okListener, ActionListener cancelListener)
			throws HeadlessException {
		if (!(chooserPane instanceof ColorChooser)) {
			throw new IllegalArgumentException("Do not create a JColorChooser, use a ColorChooser!");
		}
		return JColorChooser.createDialog(c, title, modal, chooserPane, okListener, cancelListener);
	}

	static class ColorTracker implements ActionListener, Serializable {

		private static final long serialVersionUID = 1L;
		ColorChooser chooser;
		Color color;

		public ColorTracker(ColorChooser c) {
			chooser = c;
		}

		public void actionPerformed(ActionEvent e) {
			color = chooser.getColor();
		}

		public Color getColor() {
			return color;
		}
	}
}
