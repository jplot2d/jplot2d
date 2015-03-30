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
package org.jplot2d.swing.components;

import org.jplot2d.element.Element;
import org.jplot2d.env.InterfaceInfo;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.swing.outline.PlotOutline;
import org.jplot2d.swing.proptable.PropertiesModel;
import org.jplot2d.swing.proptable.PropertyTable;
import org.jplot2d.swing.proptable.PropertyTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

/**
 * A properties frame, contains a plot tree to show all elements, and a properties table to show all
 * properties of selected tree node.
 *
 * @author Jingjing Li
 */
public class PlotPropertiesFrame extends JFrame implements ShowPropertiesAction {

    private static final long serialVersionUID = 1L;

    private final PlotOutline _manager;

    private JSplitPane splitPane;

    private PropertyTable _propertyTable;

    public PlotPropertiesFrame(PlotEnvironment env) {
        super("Properties Panel");
        _manager = new PlotOutline(env);
        _manager.setShowPropertiesAction(this);
        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().add(getTreeTablePanel(), BorderLayout.CENTER);

        pack();
        //setSize(getWidth(), 400);
    }

    private JSplitPane getTreeTablePanel() {
        if (splitPane == null) {
            splitPane = new JSplitPane();
            splitPane.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(Color.white, new Color(165, 163, 151)),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            JScrollPane scrollPaneL = new JScrollPane();
            JScrollPane scrollPaneR = new JScrollPane();
            splitPane.add(scrollPaneL, JSplitPane.LEFT);
            splitPane.add(scrollPaneR, JSplitPane.RIGHT);
            scrollPaneL.getViewport().add(getPlotTree());
            scrollPaneR.getViewport().add(getPropTable());
        }
        return splitPane;
    }

    private Component getPlotTree() {
        return _manager.getComponent();
    }

    private Component getPropTable() {
        if (_propertyTable == null) {
            _propertyTable = new PropertyTable();
        }
        return _propertyTable;
    }

    public void triggerShowProperties(Element element) {
        if (element == _propertyTable.getModel().getEngine()) {
            _propertyTable.getModel().refresh();
        } else if (element == null) {
            PropertyTableModel tableModel = new PropertyTableModel(null, null);
            _propertyTable.setModel(tableModel);
        } else {
            Class<?> cls = element.getClass().getInterfaces()[0];
            PropertiesModel model = new PropertiesModel(InterfaceInfo.loadInterfaceInfo(cls).getPropertyInfoGroupMap());
            PropertyTableModel tableModel = new PropertyTableModel(element, model);
            tableModel.setDialogParent(_propertyTable);
            _propertyTable.setModel(tableModel);
        }
    }

}
