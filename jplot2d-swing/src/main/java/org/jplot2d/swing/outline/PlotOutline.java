/**
 * Copyright 2010-2013 Jingjing Li.
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
package org.jplot2d.swing.outline;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.jplot2d.element.Element;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.env.ElementChangeEvent;
import org.jplot2d.env.ElementChangeListener;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.swing.components.ShowPropertiesAction;

/**
 * The PlotOutlineManager manage a plot tree. It register itself to environment as an ElementChangeListener. Whenever a
 * plot property changed, this PlotOutline will notify the registered ShowPropertiesAction to refresh its contents. Also
 * when user choose a new tree node, this PlotOutline will notify the registered ShowPropertiesAction to show the
 * properties.
 * 
 * @author Jingjing Li
 * 
 */
public class PlotOutline implements ElementChangeListener {

	private final Plot _plot;

	private final PlotTreeModel _treeModel;

	private final PlotTree _plotTree;

	private boolean _modified;

	private ShowPropertiesAction _showPropertiesAction;

	public PlotOutline(PlotEnvironment env) {
		_plot = env.getPlot();
		_treeModel = new PlotTreeModel(_plot);

		_plotTree = new PlotTree(_treeModel);
		_plotTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreePath newPath = e.getNewLeadSelectionPath();
				triggerShowPanel(newPath);
			}
		});
		_plotTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showPopupMenu(e.getPoint());
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showPopupMenu(e.getPoint());
				}
			}
		});

		_plotTree.expandAll(new TreePath(_treeModel.getRoot()), Plot.class);

		env.addElementChangeListener(this);
	}

	public JComponent getComponent() {
		return _plotTree;
	}

	public void componentAdded(final ElementChangeEvent evt) {
		_modified = true;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				_treeModel.fireTreeStructureChanged(evt.getElement());
			}
		});
	}

	public void componentRemoving(ElementChangeEvent evt) {

	}

	public void componentRemoved(final ElementChangeEvent evt) {
		_modified = true;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				_treeModel.fireTreeStructureChanged(evt.getElement());
			}
		});
	}

	public void propertiesChanged(ElementChangeEvent evt) {
		_modified = true;
	}

	public void propertyChangesProcessed(ElementChangeEvent evt) {
		if (_modified) {
			_modified = false;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					TreePath path = _plotTree.getSelectionPath();
					// if a tree node has been selected
					if (path != null) {
						triggerShowPanel(path);
					}
				}
			});
		}
	}

	private void showPopupMenu(Point point) {
		final TreePath path = _plotTree.getSelectionPath();
		if (path == null) {
			return;
		}

		JPopupMenu popup = new JPopupMenu();

		Object pe = path.getLastPathComponent();
		if (pe instanceof Layer) {
			JMenuItem itemRemove = new JMenuItem("Remove");
			itemRemove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					removeLayer(path);
				}
			});
			popup.add(itemRemove);
		}

		popup.show(_plotTree, point.x, point.y);
	}

	private void removeLayer(TreePath path) {
		Plot plot = (Plot) path.getParentPath();
		Layer layer = (Layer) path.getLastPathComponent();
		plot.removeLayer(layer);
	}

	/**
	 * notify the properties panel need to update
	 * 
	 * @param path
	 */
	private void triggerShowPanel(TreePath path) {
		Element newNode = (path == null) ? null : (Element) path.getLastPathComponent();
		_showPropertiesAction.triggerShowProperties(newNode);
	}

	public void setShowPropertiesAction(ShowPropertiesAction action) {
		_showPropertiesAction = action;
	}

	public void showCurrentProperties() {
		TreePath path = _plotTree.getSelectionPath();
		triggerShowPanel(path);
	}

}
