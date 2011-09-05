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


import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.jplot2d.interaction.InteractionHandler;
import org.jplot2d.interaction.InteractionModeHandler;
import org.jplot2d.interaction.MouseDragBehavior;
import org.jplot2d.interaction.MouseDragBehaviorHandler;

public abstract class MouseMarqueeHandler<B extends MouseDragBehavior> extends
		MouseDragBehaviorHandler<B> {

	private Point marqueeStart;

	private Rectangle marqueeRect = new Rectangle(0, 0, 0, 0);

	private Component comp;

	public MouseMarqueeHandler(B behavior, InteractionModeHandler handler) {
		super(behavior, handler);
		comp = (Component) handler.getValue(InteractionHandler.COMPONENT_KEY);
	}

	@Override
	public void draggingStarted(int x, int y) {
		marqueeStart = new Point(x, y);
		marqueeRect.x = x;
		marqueeRect.y = y;
		marqueeRect.width = 0;
		marqueeRect.height = 0;
		Graphics2D g = (Graphics2D) comp.getGraphics();
		g.setColor(comp.getForeground());
		g.setXORMode(comp.getBackground());
		g.draw(marqueeRect);
		g.setPaintMode();
	}

	@Override
	public void draggingTo(int x, int y) {
		Graphics2D g = (Graphics2D) comp.getGraphics();
		g.setColor(comp.getForeground());
		g.setXORMode(comp.getBackground());
		g.draw(marqueeRect);
		marqueeRect.setFrameFromDiagonal(marqueeStart.x, marqueeStart.y, x, y);
		g.draw(marqueeRect);
		g.setPaintMode();
	}

	@Override
	public void draggingFinished(int x, int y) {
		Graphics2D g = (Graphics2D) comp.getGraphics();
		g.setColor(comp.getForeground());
		g.setXORMode(comp.getBackground());
		g.draw(marqueeRect);
		if (marqueeRect.width > 1 || marqueeRect.height > 1) {
			handleMarquee(marqueeStart, new Point(x, y));
		}
	}

	@Override
	public void draggingCancelled() {
		Graphics2D g = (Graphics2D) comp.getGraphics();
		g.setColor(comp.getForeground());
		g.setXORMode(comp.getBackground());
		g.draw(marqueeRect);
	}

	/**
	 * Perform an action when mouse up event ends marquee.
	 * 
	 * @param startPoint
	 *            the mouse point that the marquee starts
	 * @param endPoint
	 *            the mouse point that the marquee ends
	 */
	protected abstract void handleMarquee(Point startPoint, Point endPoint);

}