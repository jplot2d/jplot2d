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
package org.jplot2d.swt.interaction;

import java.awt.Shape;
import java.awt.geom.PathIterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.jplot2d.env.PlotEnvironment;
import org.jplot2d.interaction.InteractiveComp;
import org.jplot2d.swt.JPlot2DComposite;

/**
 * @author Jingjing Li
 * 
 */
public class SwtInteractiveComp implements InteractiveComp {

	private final JPlot2DComposite comp;

	private CursorStyle cursorStyle;

	private final Cursor defaultCursor, moveCursor, crossCursor;

	private final Color plotBackground;

	private final Color tooltipBackground;

	private final Color tooltipForeground;

	public SwtInteractiveComp(JPlot2DComposite comp, PlotEnvironment env) {
		this.comp = comp;
		defaultCursor = new Cursor(comp.getDisplay(), SWT.CURSOR_ARROW);
		moveCursor = new Cursor(comp.getDisplay(), SWT.CURSOR_SIZEALL);
		crossCursor = new Cursor(comp.getDisplay(), SWT.CURSOR_CROSS);
		int r = comp.getPlotBackground().getRed();
		int g = comp.getPlotBackground().getGreen();
		int b = comp.getPlotBackground().getBlue();
		plotBackground = new Color(comp.getDisplay(), r, g, b);
		tooltipBackground = comp.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
		tooltipForeground = comp.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND);
	}

	public java.awt.Point getCursorLocation() {
		Point p = comp.toControl(Display.getCurrent().getCursorLocation());
		return new java.awt.Point(p.x - comp.getImageOffsetX(), p.y - comp.getImageOffsetY());
	}

	public void repaint() {
		comp.redraw();
	}

	public CursorStyle getCursor() {
		return cursorStyle;
	}

	public void setCursor(CursorStyle cursorStyle) {
		this.cursorStyle = cursorStyle;

		switch (cursorStyle) {
		case DEFAULT_CURSOR:
			comp.setCursor(defaultCursor);
			break;
		case MOVE_CURSOR:
			comp.setCursor(moveCursor);
			break;
		case CROSSHAIR_CURSOR:
			comp.setCursor(crossCursor);
			break;
		}
	}

	@SuppressWarnings("deprecation")
	public void drawLine(Object g, int rgb, int x1, int y1, int x2, int y2) {
		GC gc = (GC) g;
		int cr = (rgb & 0x00ff0000) >> 16;
		int cg = (rgb & 0x0000ff00) >> 8;
		int cb = (rgb & 0x000000ff);
		Color c = new Color(comp.getDisplay(), cr, cg, cb);

		gc.setForeground(c);
		gc.setBackground(plotBackground);
		gc.setXORMode(true);
		gc.drawLine(x1 + comp.getImageOffsetX(), y1 + comp.getImageOffsetY(),
				x2 + comp.getImageOffsetX(), y2 + comp.getImageOffsetY());
		gc.setXORMode(false);

		c.dispose();
	}

	@SuppressWarnings("deprecation")
	public void drawRectangle(Object g, int rgb, int x, int y, int width, int height) {
		GC gc = (GC) g;
		int cr = (rgb & 0x00ff0000) >> 16;
		int cg = (rgb & 0x0000ff00) >> 8;
		int cb = (rgb & 0x000000ff);
		Color c = new Color(comp.getDisplay(), cr, cg, cb);

		gc.setForeground(c);
		gc.setBackground(plotBackground);
		gc.setXORMode(true);
		gc.drawRectangle(x + comp.getImageOffsetX(), y + comp.getImageOffsetY(), width, height);
		gc.setXORMode(false);

		c.dispose();
	}

	@SuppressWarnings("deprecation")
	public void drawShape(Object g, int rgb, Shape shape) {
		Path path = toSwtPath(shape, comp.getImageOffsetX(), comp.getImageOffsetY());

		GC gc = (GC) g;
		int cr = (rgb & 0x00ff0000) >> 16;
		int cg = (rgb & 0x0000ff00) >> 8;
		int cb = (rgb & 0x000000ff);
		Color c = new Color(comp.getDisplay(), cr, cg, cb);

		gc.setForeground(c);
		gc.setBackground(plotBackground);
		gc.setXORMode(true);
		gc.drawPath(path);
		gc.setXORMode(false);

		c.dispose();
		path.dispose();
	}

	/**
	 * Converts an AWT <code>Shape</code> to a SWT <code>Path</code>.
	 * 
	 * @param shape
	 *            the shape to be converted.
	 * @param xoff
	 *            the x offset to append to the shape
	 * @param yoff
	 *            the y offset to append to the shape
	 * 
	 * @return The path
	 */
	private Path toSwtPath(Shape shape, int xoff, int yoff) {
		float[] coords = new float[6];
		Path path = new Path(comp.getDisplay());
		PathIterator pit = shape.getPathIterator(null);
		while (!pit.isDone()) {
			int type = pit.currentSegment(coords);
			switch (type) {
			case (PathIterator.SEG_MOVETO):
				path.moveTo(coords[0] + xoff, coords[1] + yoff);
				break;
			case (PathIterator.SEG_LINETO):
				path.lineTo(coords[0] + xoff, coords[1] + yoff);
				break;
			case (PathIterator.SEG_QUADTO):
				path.quadTo(coords[0] + xoff, coords[1] + yoff, coords[2] + xoff, coords[3] + yoff);
				break;
			case (PathIterator.SEG_CUBICTO):
				path.cubicTo(coords[0] + xoff, coords[1] + yoff, coords[2] + xoff,
						coords[3] + yoff, coords[4] + xoff, coords[5] + yoff);
				break;
			case (PathIterator.SEG_CLOSE):
				path.close();
				break;
			default:
				break;
			}
			pit.next();
		}
		return path;
	}

	public void drawTooltip(Object g, String s, int x, int y) {
		x += comp.getImageOffsetX() + 4;
		y += comp.getImageOffsetY() + 2;

		GC gc = (GC) g;
		gc.setForeground(tooltipForeground);
		gc.setBackground(tooltipBackground);

		int cridx = s.indexOf('\n');
		if (cridx == -1) {
			gc.drawString(s, x, y);
		} else {
			String sa = s.substring(0, cridx);
			gc.drawString(sa, x, y);

			y += gc.getFontMetrics().getHeight();
			String sb = s.substring(cridx + 1);
			gc.drawString(sb, x, y);
		}
	}
}
