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
package org.jplot2d.swing.interaction;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jplot2d.element.MovableComponent;
import org.jplot2d.element.PComponent;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.interaction.InteractionHandler;
import org.jplot2d.interaction.InteractionModeHandler;
import org.jplot2d.interaction.MouseDragBehaviorHandler;

public class MouseMoveComponentHandler extends MouseDragBehaviorHandler<MouseMoveComponentBehavior>
		implements PropertyChangeListener {

	private MovableComponent comp;

	/**
	 * Indicate a buffered component has been moved by mouse dragging
	 */
	private boolean moved = false;

	private Point movingRefPoint;

	private Shape boundsShape;

	private Point startPoint;

	public MouseMoveComponentHandler(MouseMoveComponentBehavior behavior,
			InteractionModeHandler handler) {
		super(behavior, handler);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(InteractionHandler.ACTIVE_COMPONENT_KEY)) {
			PComponent acomp = (PComponent) evt.getNewValue();
			if (acomp instanceof MovableComponent && ((MovableComponent) acomp).isMovable()) {
				handler.putValue(InteractionHandler.ACTIVE_COMPONENT_MOVABLE_KEY, Boolean.TRUE);
			} else {
				handler.putValue(InteractionHandler.ACTIVE_COMPONENT_MOVABLE_KEY, null);
			}
		}
	}

	@Override
	public boolean canStartDargging(int x, int y) {
		Boolean movable = (Boolean) handler
				.getValue(InteractionHandler.ACTIVE_COMPONENT_MOVABLE_KEY);
		if (movable != null && movable) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void draggingStarted(int x, int y) {
		comp = (MovableComponent) handler.getValue(InteractionHandler.ACTIVE_COMPONENT_KEY);
		/* object selected start move operation */
		moved = false;
		startPoint = new Point(x, y);
		movingRefPoint = new Point(x, y);
		boundsShape = getDeviceBounds(comp);
	}

	@Override
	public void draggingTo(int x, int y) {
		Component ccomp = (Component) handler.getValue(InteractionHandler.COMPONENT_KEY);

		double xoff = x - movingRefPoint.x;
		double yoff = y - movingRefPoint.y;
		movingRefPoint.x = x;
		movingRefPoint.y = y;
		moved = true;

		Graphics2D g = (Graphics2D) ccomp.getGraphics();
		g.setColor(Color.red);
		g.setXORMode(ccomp.getBackground());
		g.draw(boundsShape);

		boundsShape = AffineTransform.getTranslateInstance(xoff, yoff).createTransformedShape(
				boundsShape);
		g.draw(boundsShape);
		g.setPaintMode();

		movingRefPoint = new Point(x, y);
	}

	@Override
	public void draggingFinished(int x, int y) {
		/* finish move */
		if (moved) {
			double xoff = x - startPoint.x;
			double yoff = y - startPoint.y;
			PhysicalTransform pxf = comp.getParent().getPhysicalTransform();
			Point2D dloc = pxf.getPtoD(comp.getLocation());
			dloc.setLocation(dloc.getX() + xoff, dloc.getY() + yoff);
			Point2D newLoc = pxf.getDtoP(dloc);
			comp.setLocation(newLoc);
			moved = false;
		}
	}

	@Override
	public void draggingCancelled() {

	}

	private static Shape getDeviceBounds(PComponent comp) {
		return comp.getPhysicalTransform().getPtoD(comp.getBounds());
	}

}