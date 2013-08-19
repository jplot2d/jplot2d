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
package org.jplot2d.interaction;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

public abstract class MouseMarqueeHandler<B extends MouseDragBehavior> extends MouseDragBehaviorHandler<B> implements
		VisualFeedbackDrawer {

	private Point marqueeStart;

	private Point marqueeEnd;

	private InteractiveComp icomp;

	public MouseMarqueeHandler(B behavior, InteractionModeHandler handler) {
		super(behavior, handler);
		icomp = (InteractiveComp) handler.getValue(PlotInteractionManager.INTERACTIVE_COMP_KEY);
	}

	@Override
	public void draggingStarted(int x, int y) {
		marqueeStart = new Point(x, y);
	}

	@Override
	public void draggingTo(int x, int y) {
		marqueeEnd = new Point(x, y);
		icomp.repaint();
	}

	@Override
	public void draggingFinished(int x, int y) {
		Rectangle marqueeRect = new Rectangle(0, 0, 0, 0);
		marqueeRect.setFrameFromDiagonal(marqueeStart.x, marqueeStart.y, x, y);

		if (marqueeRect.width > 1 || marqueeRect.height > 1) {
			handleMarquee(marqueeStart, new Point(x, y));
		}

		marqueeStart = null;
		icomp.repaint();
	}

	@Override
	public void draggingCancelled() {
		marqueeStart = null;
		icomp.repaint();
	}

	public void draw(Object g) {
		if (marqueeStart == null) {
			return;
		}

		Rectangle marqueeRect = new Rectangle(0, 0, 0, 0);
		marqueeRect.setFrameFromDiagonal(marqueeStart, marqueeEnd);

		icomp.drawRectangle(g, Color.GRAY.getRGB(), marqueeRect.x, marqueeRect.y, marqueeRect.width, marqueeRect.height);
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