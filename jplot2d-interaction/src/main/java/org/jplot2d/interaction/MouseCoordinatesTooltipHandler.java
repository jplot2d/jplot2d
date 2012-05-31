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
package org.jplot2d.interaction;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.jplot2d.element.Axis;
import org.jplot2d.element.AxisTransform;
import org.jplot2d.element.Plot;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.InteractiveComp.CursorStyle;
import org.jplot2d.util.NumberUtils;

/**
 * This handler shows a tooltip to display the coordinates on where the cursor is.
 * 
 * @author Jingjing Li
 * 
 */
public class MouseCoordinatesTooltipHandler extends
		MouseMoveBehaviorHandler<MouseCoordinatesTooltipBehavior> implements VisualFeedbackDrawer {

	private final InteractiveComp icomp;

	private boolean show;

	public MouseCoordinatesTooltipHandler(MouseCoordinatesTooltipBehavior behavior,
			InteractionModeHandler handler) {
		super(behavior, handler);
		icomp = handler.getInteractiveComp();
	}

	@Override
	public boolean enterModifiersKey() {
		show = true;
		icomp.setCursor(CursorStyle.CROSSHAIR_CURSOR);
		// repaint to show the tooltip
		icomp.repaint();
		return true;
	}

	@Override
	public boolean exitModifiersKey() {
		show = false;
		icomp.setCursor(CursorStyle.DEFAULT_CURSOR);
		// repaint to hide the tooltip
		icomp.repaint();
		return true;
	}

	@Override
	public boolean behaviorPerformed(int x, int y) {
		// repaint to update the tooltip
		icomp.repaint();
		return true;
	}

	public void draw(Object g) {

		if (!show) {
			return;
		}

		/*
		 * Draw tooltip for the current coordinate
		 */
		InteractiveComp icomp = handler.getInteractiveComp();
		Point p = icomp.getCursorLocation();

		// the selectable component that contains the specified point.
		PlotEnvironment env = (PlotEnvironment) handler.getValue(InteractionHandler.PLOT_ENV_KEY);
		Plot plot = env.getPlotAt(p);

		if (plot == null) {
			return;
		}

		double nx = plot.getPaperTransform().getXDtoP(p.x) / plot.getContentSize().getWidth();
		double nxL = plot.getPaperTransform().getXDtoP(p.x - 1) / plot.getContentSize().getWidth();
		double nxH = plot.getPaperTransform().getXDtoP(p.x + 1) / plot.getContentSize().getWidth();

		double ny = plot.getPaperTransform().getYDtoP(p.y) / plot.getContentSize().getHeight();
		double nyL = plot.getPaperTransform().getYDtoP(p.y - 1) / plot.getContentSize().getHeight();
		double nyH = plot.getPaperTransform().getYDtoP(p.y + 1) / plot.getContentSize().getHeight();

		if (nx < 0 || nx > 1) {
			return;
		}
		if (ny < 0 || ny > 1) {
			return;
		}

		Set<AxisTransform> xats = new HashSet<AxisTransform>();
		for (Axis axis : plot.getXAxes()) {
			xats.add(axis.getTickManager().getAxisTransform());
		}
		Set<AxisTransform> yats = new HashSet<AxisTransform>();
		for (Axis axis : plot.getYAxes()) {
			yats.add(axis.getTickManager().getAxisTransform());
		}

		StringBuilder sb = new StringBuilder();
		if (xats.size() > 1 || yats.size() > 1) {
			sb.append("X=");
			for (AxisTransform xat : xats) {
				double v = xat.getNormalTransform().convFromNR(nx);
				double deltaL = Math.abs(xat.getNormalTransform().convFromNR(nxL) - v);
				double deltaH = Math.abs(xat.getNormalTransform().convFromNR(nxH) - v);
				String format = NumberUtils.calcDeltaFormatStr(v, Math.min(deltaL, deltaH) / 2);
				sb.append(String.format((Locale) null, format, v)).append("; ");
			}
			sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1);
			sb.append("\n");
			sb.append("Y=");
			for (AxisTransform yat : yats) {
				double v = yat.getNormalTransform().convFromNR(ny);
				double deltaL = Math.abs(yat.getNormalTransform().convFromNR(nyL) - v);
				double deltaH = Math.abs(yat.getNormalTransform().convFromNR(nyH) - v);
				String format = NumberUtils.calcDeltaFormatStr(v, Math.min(deltaL, deltaH) / 2);
				sb.append(String.format((Locale) null, format, v)).append("; ");
			}
			sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1);
		} else {
			for (AxisTransform xat : xats) {
				double v = xat.getNormalTransform().convFromNR(nx);
				double deltaL = Math.abs(xat.getNormalTransform().convFromNR(nxL) - v);
				double deltaH = Math.abs(xat.getNormalTransform().convFromNR(nxH) - v);
				String format = NumberUtils.calcDeltaFormatStr(v, Math.min(deltaL, deltaH) / 2);
				sb.append(String.format((Locale) null, format, v));
			}
			sb.append(", ");
			for (AxisTransform yat : yats) {
				double v = yat.getNormalTransform().convFromNR(ny);
				double deltaL = Math.abs(yat.getNormalTransform().convFromNR(nyL) - v);
				double deltaH = Math.abs(yat.getNormalTransform().convFromNR(nyH) - v);
				String format = NumberUtils.calcDeltaFormatStr(v, Math.min(deltaL, deltaH) / 2);
				sb.append(String.format((Locale) null, format, v));
			}
		}

		Dimension2D csize = plot.getContentSize();
		Rectangle2D cbnds = new Rectangle2D.Double(0, 0, csize.getWidth(), csize.getHeight());
		Rectangle2D plotRect = plot.getPaperTransform().getPtoD(cbnds).getBounds2D();

		icomp.drawLine(g, Color.GRAY.getRGB(), (int) plotRect.getX(), p.y,
				(int) plotRect.getMaxX(), p.y);
		icomp.drawLine(g, Color.GRAY.getRGB(), p.x, (int) plotRect.getY(), p.x,
				(int) plotRect.getMaxY());
		icomp.drawTooltip(g, sb.toString(), p.x, p.y);

	}

}