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
package org.jplot2d.element.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

import org.jplot2d.element.HAlign;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.element.VAlign;
import org.jplot2d.util.DoubleDimension2D;

/**
 * @author Jingjing Li
 * 
 */
public class LegendImpl extends ComponentImpl implements LegendEx {

	private static final double VERTICAL_BORDER = 2;

	private static final double ROW_SPACE = 2;

	private static final double HORIZONTAL_BORDER = 8;

	private static final double COLUMN_SPACE = 8;

	private static final Color BORDER_COLOR = Color.BLACK;

	private boolean enabled = true;

	private double locX, locY;

	private double width, height;

	private Position position = Position.BOTTOMCENTER;

	private HAlign halign;

	private VAlign valign;

	private Dimension2D maxItemSize;

	private int columns = 1, rows = 1;

	private double lengthConstraint = Double.NaN;

	private boolean sizeCalculationNeeded;

	private final Collection<LegendItemEx> items = new ArrayList<LegendItemEx>();

	private int visibleItemNum;

	private static LegendEx getEnabledLegend(PlotEx plot) {
		if (plot == null) {
			return null;
		}
		if (plot.getLegend().isEnabled()) {
			return plot.getLegend();
		} else {
			return getEnabledLegend(plot.getParent());
		}
	}

	public String getSelfId() {
		if (getParent() != null) {
			return "Legend";
		} else {
			return "Legend@" + Integer.toHexString(System.identityHashCode(this));
		}
	}

	public PlotEx getParent() {
		return (PlotEx) super.getParent();
	}

	/*
	 * Only contribute contents when it has visible items.
	 */
	public boolean canContribute() {
		return isVisible() && isEnabled() && visibleItemNum > 0;
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (isEnabled() && visibleItemNum > 0) {
			if (getParent() != null) {
				getParent().invalidate();
			}
		}
	}

	public void thisEffectiveColorChanged() {
		redraw();
	}

	public void thisEffectiveFontChanged() {
		sizeCalculationNeeded = true;
		redraw();
	}

	public Dimension2D getSize() {
		return new DoubleDimension2D(width, height);
	}

	public Rectangle2D getBounds() {
		double x, y;
		switch (getHAlign()) {
		case RIGHT:
			x = locX - width;
			break;
		case CENTER:
			x = locX - width / 2;
			break;
		default:
			x = locX;
		}
		switch (getVAlign()) {
		case TOP:
			y = locY - height;
			break;
		case MIDDLE:
			y = locY - height / 2;
			break;
		default:
			y = locY;
		}
		return new Rectangle2D.Double(x, y, width, height);
	}

	public Point2D getLocation() {
		return new Point2D.Double(locX, locY);
	}

	public void setLocation(Point2D loc) {
		setLocation(loc.getX(), loc.getY());
	}

	public void setLocation(double locX, double locY) {
		if (getLocation().getX() != locX || getLocation().getY() != locY) {
			this.locX = locX;
			this.locY = locY;
			redraw();
		}
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
		if (canContribute()) {
			invalidatePlotIfVisible();
		}
	}

	public HAlign getHAlign() {
		return halign;
	}

	public void setHAlign(HAlign halign) {
		this.halign = halign;
	}

	public VAlign getVAlign() {
		return valign;
	}

	public void setVAlign(VAlign valign) {
		this.valign = valign;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		if (columns < 1) {
			throw new IllegalArgumentException("Columns number must great than 0.");
		}
		this.columns = columns;
	}

	public void addLegendItem(LegendItemEx item) {
		items.add(item);
		if (isEnabled()) {
			item.setLegend(this);
			if (item.isVisible()) {
				visibleItemNum++;
				maxItemSize = null;
				sizeCalculationNeeded = true;
				if (isVisible()) {
					redraw();
				}
			}
		} else {
			LegendEx enabledLegend = getEnabledLegend(getParent());
			if (enabledLegend != null) {
				enabledLegend.addLegendItem(item);
			}
		}
	}

	public void removeLegendItem(LegendItemEx item) {
		items.remove(item);
		if (isEnabled()) {
			item.setLegend(null);
			if (item.isVisible()) {
				visibleItemNum--;
				if (item.getSize().equals(maxItemSize)) {
					maxItemSize = null;
				}
				sizeCalculationNeeded = true;
				redraw();
			}
		} else {
			LegendEx enabledLegend = getEnabledLegend(getParent());
			if (enabledLegend != null) {
				enabledLegend.removeLegendItem(item);
			}
		}
	}

	private void setLengthConstraint(double length) {
		if (lengthConstraint != length) {
			lengthConstraint = length;
			sizeCalculationNeeded = true;
		}
	}

	public void itemSizeChanged(LegendItemEx item) {
		if (item.getSize().equals(maxItemSize)) {
			maxItemSize = null;
			sizeCalculationNeeded = true;
			redraw();
		}
	}

	public void itemVisibleChanged(LegendItemImpl item) {
		if (item.getSize().equals(maxItemSize)) {
			maxItemSize = null;
		}
		if (item.isVisible()) {
			visibleItemNum++;
		} else {
			visibleItemNum--;
		}
		sizeCalculationNeeded = true;
		redraw();
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		LegendImpl legend = (LegendImpl) src;
		enabled = legend.enabled;
		locX = legend.locX;
		locY = legend.locY;
		width = legend.width;
		height = legend.height;
		position = legend.position;
		halign = legend.halign;
		valign = legend.valign;
		maxItemSize = legend.maxItemSize;
		rows = legend.rows;
		columns = legend.columns;
		lengthConstraint = legend.lengthConstraint;
		sizeCalculationNeeded = legend.sizeCalculationNeeded;
	}

	/**
	 * Invalid plot if this legend is visible
	 */
	private void invalidatePlotIfVisible() {
		if (isVisible() && getParent() != null) {
			getParent().invalidate();
		}
	}

	public double getThickness() {
		switch (getPosition()) {
		case TOPLEFT:
		case TOPCENTER:
		case TOPRIGHT:
		case BOTTOMLEFT:
		case BOTTOMCENTER:
		case BOTTOMRIGHT:
			return getSize().getHeight();
		case LEFTTOP:
		case LEFTMIDDLE:
		case LEFTBOTTOM:
		case RIGHTTOP:
		case RIGHTMIDDLE:
		case RIGHTBOTTOM:
			return getSize().getWidth();
		default:
			return 0;
		}
	}

	public void calcSize() {

		PlotEx plot = getParent();
		switch (getPosition()) {
		case TOPLEFT:
		case TOPCENTER:
		case TOPRIGHT:
		case BOTTOMLEFT:
		case BOTTOMCENTER:
		case BOTTOMRIGHT: {
			double legendWidth = plot.getSize().getWidth() - plot.getMargin().getExtraLeft()
					- plot.getMargin().getExtraRight();
			setLengthConstraint(legendWidth);
			break;
		}
		case LEFTTOP:
		case LEFTMIDDLE:
		case LEFTBOTTOM:
		case RIGHTTOP:
		case RIGHTMIDDLE:
		case RIGHTBOTTOM: {
			double contentHeight = plot.getContainerSize().getHeight();
			setLengthConstraint(contentHeight);
			break;
		}
		}

		if (!sizeCalculationNeeded) {
			return;
		}
		sizeCalculationNeeded = false;

		switch (getPosition()) {
		case TOPLEFT:
		case TOPCENTER:
		case TOPRIGHT:
		case BOTTOMLEFT:
		case BOTTOMCENTER:
		case BOTTOMRIGHT: {
			fitColumnsToWidth(lengthConstraint);
			width = (maxItemSize.getWidth() + COLUMN_SPACE) * columns - COLUMN_SPACE + 2
					* HORIZONTAL_BORDER;
			height = (maxItemSize.getHeight() + ROW_SPACE) * rows - ROW_SPACE + 2 * VERTICAL_BORDER;
			break;
		}
		case LEFTTOP:
		case LEFTMIDDLE:
		case LEFTBOTTOM:
		case RIGHTTOP:
		case RIGHTMIDDLE:
		case RIGHTBOTTOM: {
			fitRowsToHeight(lengthConstraint);
			width = (maxItemSize.getWidth() + COLUMN_SPACE) * columns - COLUMN_SPACE + 2
					* HORIZONTAL_BORDER;
			height = (maxItemSize.getHeight() + ROW_SPACE) * rows - ROW_SPACE + 2 * VERTICAL_BORDER;
			break;
		}
		}

		locateLegendItems();
	}

	/**
	 * Calculate the proper column number to fit the given width.
	 * 
	 * @param width
	 *            the width to fit for
	 */
	private void fitColumnsToWidth(double width) {
		Dimension2D lisize = getMaxItemSize();

		// the max possible columns
		double maxColumns = (width - 2 * HORIZONTAL_BORDER + COLUMN_SPACE)
				/ (lisize.getWidth() + COLUMN_SPACE);
		int ncol = (int) maxColumns;
		if (ncol < 1) {
			ncol = 1;
		} else if (ncol > visibleItemNum) {
			ncol = visibleItemNum;
		}
		int nrow = visibleItemNum / ncol;
		if (visibleItemNum % ncol > 0) {
			nrow++;
		}

		columns = ncol;
		rows = nrow;
	}

	/**
	 * Calculate the proper column number to fit the given height.
	 * 
	 * @param height
	 *            the height to fit for
	 */
	private void fitRowsToHeight(double height) {
		Dimension2D lisize = getMaxItemSize();

		double maxRows = (height - 2 * VERTICAL_BORDER + ROW_SPACE)
				/ (lisize.getHeight() + ROW_SPACE);
		int nrow = (int) maxRows;
		if (nrow < 1) {
			nrow = 1;
		} else if (nrow > visibleItemNum) {
			nrow = visibleItemNum;
		}
		int ncol = visibleItemNum / nrow;
		if (visibleItemNum % nrow > 0) {
			ncol++;
		}

		columns = ncol;
		rows = nrow;
	}

	/**
	 * Locate all legend items
	 */
	private void locateLegendItems() {
		if ((visibleItemNum == 0) || !isVisible()) {
			return;
		}

		double[] lipx = new double[columns];
		double[] lipy = new double[rows];
		for (int i = 0; i < columns; i++) {
			lipx[i] = HORIZONTAL_BORDER + i * maxItemSize.getWidth()
					+ ((i > 0) ? i * COLUMN_SPACE : 0);
		}
		for (int i = 0; i < rows; i++) {
			lipy[i] = VERTICAL_BORDER + (rows - i - 0.5) * maxItemSize.getHeight()
					+ ((rows - 1 - i > 0) ? (rows - 1 - i) * ROW_SPACE : 0);
		}

		int col = 0, row = 0;
		for (LegendItemEx item : items) {
			if (item.isVisible()) {
				item.setLocation(lipx[col], lipy[row]);
				col++;
				if (col >= columns) {
					col = 0;
					row++;
				}
			}
		}
	}

	/**
	 * find largest legend item size
	 */
	private Dimension2D getMaxItemSize() {
		if (maxItemSize != null) {
			return maxItemSize;
		}

		double maxWidth = 0;
		double maxHeight = 0;
		for (LegendItemEx item : items) {
			if (item.isVisible()) {
				Dimension2D psize = item.getSize();
				double pwidth = psize.getWidth();
				double pheight = psize.getHeight();
				if (maxWidth < pwidth) {
					maxWidth = pwidth;
				}
				if (maxHeight < pheight) {
					maxHeight = pheight;
				}
			}
		}

		maxItemSize = new DoubleDimension2D(maxWidth, maxHeight);
		return maxItemSize;
	}

	public void draw(Graphics2D graphics) {
		if (!canContribute()) {
			return;
		}

		// drawBounds(graphics);

		// transform to relative paper space
		Graphics2D g = (Graphics2D) graphics.create();
		g.transform(getPhysicalTransform().getTransform());
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(BORDER_COLOR);
		g.draw(new Rectangle2D.Double(0, 0, width, height));

		for (LegendItemEx item : items) {
			if (item.isVisible()) {
				item.draw(g);
			}
		}
	}

}
