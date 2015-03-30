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
package org.jplot2d.swing.outline;

import org.jplot2d.element.Element;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Override the convertValueToText method to provide an unique display string for every tree node,
 * not rely on the toString method of tree node.
 *
 * @author Jingjing Li
 */
public class PlotTree extends JTree {

    private static final long serialVersionUID = 1L;

    public PlotTree(PlotTreeModel model) {
        super(model);
    }

    public String convertValueToText(Object value, boolean selected, boolean expanded,
                                     boolean leaf, int row, boolean hasFocus) {

        if (value instanceof Element) {
            return ((Element) value).getId();
        } else {
            return "Unknown";
        }

    }

    public void expandAll(TreePath path) {
        expandAll(path, -1);
    }

    /**
     * Expand the tree node to the given depth.
     *
     * @param path  the node to expand
     * @param depth if depth=0, collapse the given path. if depth==-1, expand to leaf.
     */
    public void expandAll(TreePath path, int depth) {
        if (depth == 0) {
            collapsePath(path);
            return;
        }
        Object parent = path.getLastPathComponent();
        if (getModel().isLeaf(parent)) {
            return;
        }

        expandPath(path);

        depth--;
        int size = getModel().getChildCount(parent);
        for (int i = 0; i < size; i++) {
            Object child = getModel().getChild(parent, i);
            expandAll(path.pathByAddingChild(child), depth);
        }
    }

    /**
     * Expand the tree node if it's instance of the given class.
     *
     * @param path  the node to expand
     * @param clazz the class
     */
    public void expandAll(TreePath path, Class<?> clazz) {
        Object parent = path.getLastPathComponent();

        if (clazz.isAssignableFrom(parent.getClass())) {
            expandPath(path);
        }

        int size = getModel().getChildCount(parent);
        for (int i = 0; i < size; i++) {
            Object child = getModel().getChild(parent, i);
            expandAll(path.pathByAddingChild(child), clazz);
        }
    }

}
