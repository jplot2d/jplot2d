/**
 * 
 */
package org.jplot2d.env;

import java.util.EventObject;

import org.jplot2d.element.Element;

/**
 * Encapsulates information describing changes to a plot structure, and used to
 * notify plot structure listeners of the change.
 * 
 * @author Jingjing Li
 * 
 */
public class JPlot2DChangeEvent extends EventObject {

	private static final long serialVersionUID = 2635718155892621255L;

	private Element[] _children;

	/**
	 * create an event when plot structure have been changed. The children plot
	 * elements are the created component or removed component.
	 * 
	 * @param epath
	 */
	public JPlot2DChangeEvent(Environment source, Element[] children) {
		super(source);
		_children = children;
	}

	public Element[] getChildren() {
		if (_children != null) {
			int cCount = _children.length;
			Element[] retChildren = new Element[cCount];

			System.arraycopy(_children, 0, retChildren, 0, cCount);
			return retChildren;
		}
		return null;
	}

}
