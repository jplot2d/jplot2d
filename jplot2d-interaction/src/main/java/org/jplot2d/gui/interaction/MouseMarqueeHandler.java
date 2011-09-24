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
package org.jplot2d.gui.interaction;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.jplot2d.interaction.InteractionModeHandler;
import org.jplot2d.interaction.InteractiveComp;
import org.jplot2d.interaction.MouseDragBehavior;
import org.jplot2d.interaction.MouseDragBehaviorHandler;
import org.jplot2d.interaction.PlotPaintEvent;
import org.jplot2d.interaction.PlotPaintListener;

public abstract class MouseMarqueeHandler<B extends MouseDragBehavior> extends
		MouseDragBehaviorHandler<B> implements PlotPaintListener {

	private Point marqueeStart;

	private Point marqueeEnd;

	private InteractiveComp icomp;

	public MouseMarqueeHandler(B behavior, InteractionModeHandler handler) {
		super(behavior, handler);
		icomp = handler.getInteractiveComp();
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

	public void plotPainted(PlotPaintEvent evt) {
		if (marqueeStart == null) {
			return;
		}

		Rectangle marqueeRect = new Rectangle(0, 0, 0, 0);
		marqueeRect.setFrameFromDiagonal(marqueeStart, marqueeEnd);

		Graphics2D g = evt.getGraphics();
		g.setColor(icomp.getForeground());
		g.setXORMode(icomp.getBackground());
		g.draw(marqueeRect);
		g.setPaintMode();
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