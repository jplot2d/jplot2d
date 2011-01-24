/**
 * Copyright 2010 Jingjing Li.
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jplot2d.axtick.LinearTickAlgorithm;
import org.jplot2d.element.AxisLabelSide;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.AxisPosition;
import org.jplot2d.element.AxisTickSide;
import org.jplot2d.element.AxisTickTransform;
import org.jplot2d.element.Element;
import org.jplot2d.element.HAlign;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.element.VAlign;
import org.jplot2d.element.AxisRangeManager;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.MathElement;
import org.jplot2d.util.MathLabel;
import org.jplot2d.util.Range2D;

/**
 * @author Jingjing Li
 * 
 */
public class AxisImpl extends ContainerImpl implements AxisEx {

	/**
	 * the default physical width, 0.5 pt.
	 */
	private static final float DEFAULT_AXISLINE_WIDTH = 0.5f;

	/* the gap between label and tick */
	private static final double LABEL_GAP_RATIO = 1.0 / 4;

	/* the gap between title and label */
	private static final double TITLE_GAP_RATIO = 1.0 / 4;

	private final AxisTickEx tick;

	private final TextComponentEx title;

	private AxisOrientation orientation;

	private AxisRangeManagerEx rangeManager;

	private double offset;

	private double length;

	private AxisPosition position;

	private AxisTickTransform tickTransform;

	private boolean showGridLines;

	private boolean tickVisible = true;

	private AxisTickSide tickSide = AxisTickSide.OUTWARD;

	private double tickHeight = 8.0;

	private double minorHeight = 4.0;

	private boolean labelVisible = true;

	private AxisOrientation labelOrientation = AxisOrientation.HORIZONTAL;

	private AxisLabelSide labelSide = AxisLabelSide.OUTWARD;

	private Color labelColor;

	/* thickness */

	private double asc, desc;

	private double labelOffset;

	private HAlign labelHAlign;

	private VAlign labelVAlign;

	private double titleOffset;

	private VAlign titleVAlign;

	private boolean thicknessCalculationNeeded = true;

	public AxisImpl() {
		tick = new AxisTickImpl();
		tick.setParent(this);
		tick.setTickAlgorithm(LinearTickAlgorithm.getInstance());

		title = new TextComponentImpl();
		title.setParent(this);
	}

	protected AxisImpl(AxisTickEx tick, TextComponentEx title) {
		this.tick = tick;
		this.title = title;
	}

	protected String getSelfId() {
		if (getParent() != null) {
			switch (getOrientation()) {
			case HORIZONTAL:
				int xidx = getParent().indexOfXAxis(this);
				return "X" + xidx;
			case VERTICAL:
				int yidx = getParent().indexOfYAxis(this);
				return "Y" + yidx;
			}
		}
		return "Axis@" + Integer.toHexString(System.identityHashCode(this));
	}

	public SubplotEx getParent() {
		return (SubplotEx) super.getParent();
	}

	public Map<Element, Element> getMooringMap() {
		Map<Element, Element> result = new HashMap<Element, Element>();

		if (rangeManager.getParent() == this) {
			for (LayerEx layer : rangeManager.getLayers()) {
				result.put(rangeManager, layer);
			}
		}

		return result;
	}

	public AxisOrientation getOrientation() {
		return orientation;
	}

	public void setOrientation(AxisOrientation orientation) {
		this.orientation = orientation;
	}

	public Dimension2D getSize() {
		if (getOrientation() == null) {
			return null;
		} else {
			switch (getOrientation()) {
			case HORIZONTAL:
				return new DoubleDimension2D(getLength(), getThickness());
			case VERTICAL:
				return new DoubleDimension2D(getThickness(), getLength());
			default:
				return null;
			}
		}
	}

	public Rectangle2D getBounds() {
		if (getOrientation() == null) {
			return null;
		} else {
			switch (getOrientation()) {
			case HORIZONTAL:
				return new Rectangle2D.Double(getLocation().getX(),
						getLocation().getY() - desc, getLength(),
						getThickness());
			case VERTICAL:
				return new Rectangle2D.Double(getLocation().getX() - asc,
						getLocation().getY(), getThickness(), getLength());
			default:
				return null;
			}
		}
	}

	public PhysicalTransform getPhysicalTransform() {
		if (getParent() == null) {
			return null;
		} else {
			PhysicalTransform pxf = getParent().getPhysicalTransform()
					.translate(getLocation().getX(), getLocation().getY());
			if (getOrientation() == AxisOrientation.VERTICAL) {
				pxf = pxf.rotate(Math.PI / 2);
			}
			return pxf;
		}
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		invalidate();
	}

	/**
	 * Invalidate the subplot.
	 */
	private void invalidate() {
		if (getParent() != null && getParent().getParent() != null) {
			getParent().getParent().invalidate();
		}
	}

	public AxisRangeManagerEx getRangeManager() {
		return rangeManager;
	}

	public void setRangeManager(AxisRangeManager rangeManager) {
		if (this.rangeManager != null) {
			this.rangeManager.removeAxis(this);
		}
		this.rangeManager = (AxisRangeManagerEx) rangeManager;
		if (this.rangeManager != null) {
			this.rangeManager.addAxis(this);
		}
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		if (this.offset != offset) {
			this.offset = offset;
		}
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		if (this.length != length) {
			this.length = length;
		}
	}

	public AxisPosition getPosition() {
		return position;
	}

	public void setPosition(AxisPosition position) {
		this.position = position;
		invalidateThickness();
		invalidate();
	}

	public void calcTicks() {
		boolean tickChanged = tick.calcTicks();
		if (tickChanged) {
			invalidateThickness();
		}
	}

	public AxisTickTransform getTickTransform() {
		return tickTransform;
	}

	public void setTickTransform(AxisTickTransform transform) {
		this.tickTransform = transform;
	}

	public Range2D getRange() {
		Range2D range = rangeManager.getRange();
		if (tickTransform != null) {
			double start = tickTransform.transformUser2Tick(range.getStart());
			double end = tickTransform.transformUser2Tick(range.getEnd());
			return new Range2D.Double(start, end);
		} else {
			return range;
		}
	}

	public void setRange(Range2D range) {
		double ustart = tickTransform.transformTick2User(range.getStart());
		double uend = tickTransform.transformTick2User(range.getEnd());
		rangeManager.setRange(new Range2D.Double(ustart, uend));
	}

	public boolean isGridLines() {
		return showGridLines;
	}

	public void setGridLines(boolean showGridLines) {
		this.showGridLines = showGridLines;
		redraw();
	}

	public boolean isTickVisible() {
		return tickVisible;
	}

	public void setTickVisible(boolean visible) {
		this.tickVisible = visible;
		invalidateThickness();
		redraw();
	}

	public AxisTickSide getTickSide() {
		return tickSide;
	}

	public void setTickSide(AxisTickSide side) {
		this.tickSide = side;
		invalidateThickness();
		redraw();
	}

	public double getTickHeight() {
		return tickHeight;
	}

	public void setTickHeight(double height) {
		this.tickHeight = height;
		invalidateThickness();
		redraw();
	}

	public double getMinorTickHeight() {
		return minorHeight;
	}

	public void setMinorTickHeight(double height) {
		this.minorHeight = height;
	}

	public boolean isLabelVisible() {
		return labelVisible;
	}

	public void setLabelVisible(boolean visible) {
		this.labelVisible = visible;
		invalidateThickness();
		redraw();
	}

	public AxisOrientation getLabelOrientation() {
		return labelOrientation;
	}

	public void setLabelOrientation(AxisOrientation orientation) {
		this.labelOrientation = orientation;
		invalidateThickness();
		redraw();
	}

	protected boolean isLabelSameOrientation() {
		return getOrientation() == getLabelOrientation();
	}

	public AxisLabelSide getLabelSide() {
		return labelSide;
	}

	public void setLabelSide(AxisLabelSide side) {
		this.labelSide = side;
		invalidateThickness();
		redraw();
	}

	public Color getLabelColor() {
		return labelColor;
	}

	public void setLabelColor(Color color) {
		this.labelColor = color;
		redraw();
	}

	public AxisTickEx getTick() {
		return tick;
	}

	public TextComponentEx getTitle() {
		return title;
	}

	public double getThickness() {
		calcThickness();
		return asc + desc;
	}

	public double getAsc() {
		calcThickness();
		return asc;
	}

	/**
	 * always negative
	 */
	public double getDesc() {
		calcThickness();
		return desc;
	}

	/**
	 * Mark the thickness is invalid. Changing tick height, label strings label
	 * font or orientation will call this method
	 */
	protected void invalidateThickness() {
		thicknessCalculationNeeded = true;
	}

	public void calcThickness() {

		if (!thicknessCalculationNeeded) {
			return;
		}
		thicknessCalculationNeeded = false;

		double ba = 0;
		double bd = 0;
		double labelOffset = 0;
		VAlign labelVAlign = null;
		HAlign labelHAlign = null;
		double titleOffset = 0;
		VAlign titleVAlign = null;

		if (isTickVisible()) {
			if (isTickBothSide() || isTickAscSide()) {
				ba += getTickHeight();
			}
			if (isTickBothSide() || !isTickAscSide()) {
				bd += getTickHeight();
			}
		}

		if (isLabelVisible()) {
			double labelHeight = getLabelHeight();
			if (isLabelAscSide()) {
				labelOffset = ba + labelHeight * LABEL_GAP_RATIO;
				if (isLabelSameOrientation()) {
					ba = labelOffset + labelHeight;
					labelVAlign = VAlign.BOTTOM;
					labelHAlign = HAlign.CENTER;
				} else {
					ba = labelOffset + getLabelsMaxNormalPhysicalWidth();
					labelVAlign = VAlign.MIDDLE;
					labelHAlign = HAlign.RIGHT;
				}
			} else {
				labelOffset = -bd - labelHeight * LABEL_GAP_RATIO;
				if (isLabelSameOrientation()) {
					bd = -labelOffset + labelHeight;
					labelVAlign = VAlign.TOP;
					labelHAlign = HAlign.CENTER;
				} else {
					bd = -labelOffset + getLabelsMaxNormalPhysicalWidth();
					labelVAlign = VAlign.MIDDLE;
					labelHAlign = HAlign.LEFT;
				}
			}
		}

		if (getTitle().isVisible() && getTitle().getTextModel() != null) {
			double titleHeight = getTitle().getBounds().getHeight();
			if (isTitleAscSide()) {
				titleOffset = ba + titleHeight * TITLE_GAP_RATIO;
				titleVAlign = VAlign.BOTTOM;
				ba = titleOffset + titleHeight;
			} else {
				titleOffset = -bd - titleHeight * TITLE_GAP_RATIO;
				titleVAlign = VAlign.TOP;
				bd = -titleOffset + titleHeight;
			}
		}

		this.labelOffset = labelOffset;
		this.labelHAlign = labelHAlign;
		this.labelVAlign = labelVAlign;
		this.titleOffset = titleOffset;
		this.titleVAlign = titleVAlign;

		if (Math.abs(asc - ba) > Math.abs(asc) * 1e-12
				|| Math.abs(desc - bd) > Math.abs(desc) * 1e-12) {
			asc = ba;
			desc = bd;
			invalidate();
		}

	}

	private boolean isTitleAscSide() {
		boolean axisHorizontal = (getOrientation() == AxisOrientation.HORIZONTAL);
		boolean axisPositiveSide = (getPosition() == AxisPosition.POSITIVE_SIDE);
		return axisHorizontal == axisPositiveSide;
	}

	private boolean isTickBothSide() {
		return getTickSide() == AxisTickSide.BOTH;
	}

	private boolean isTickAscSide() {
		boolean axisHorizontal = (getOrientation() == AxisOrientation.HORIZONTAL);
		boolean axisPositiveSide = (getPosition() == AxisPosition.POSITIVE_SIDE);
		boolean outwardSide = (getTickSide() == AxisTickSide.OUTWARD);
		return (axisHorizontal == axisPositiveSide) == outwardSide;
	}

	private boolean isLabelAscSide() {
		boolean axisHorizontal = (getOrientation() == AxisOrientation.HORIZONTAL);
		boolean axisPositiveSide = (getPosition() == AxisPosition.POSITIVE_SIDE);
		boolean outwardSide = (getLabelSide() == AxisLabelSide.OUTWARD);
		return (axisHorizontal == axisPositiveSide) == outwardSide;
	}

	/**
	 * @return the physical label height for proposed label font.
	 */
	private double getLabelHeight() {
		FontRenderContext frc = new FontRenderContext(null, false, true);
		LineMetrics lm = getEffectiveFont().getLineMetrics("Can be any string",
				frc);
		return (lm.getAscent() + lm.getDescent());
	}

	/**
	 * Traverse all labels to find the Maximum Normal Width.
	 * 
	 * @return the maximum normal width.
	 */
	private double getLabelsMaxNormalPhysicalWidth() {
		Dimension2D[] labelsSize = getLabelsPhySize(getTick().getLabelModels(),
				getTick().getActualLabelFont());
		double maxWidth = 0;
		double maxHeight = 0;
		for (int i = 0; i < labelsSize.length; i++) {
			if (maxWidth < labelsSize[i].getWidth()) {
				maxWidth = labelsSize[i].getWidth();
			}
			if (maxHeight < labelsSize[i].getHeight()) {
				maxHeight = labelsSize[i].getHeight();
			}
		}
		return maxWidth;
	}

	/**
	 * Returns the labels normal size.
	 * 
	 * @return the labels normal size.
	 */
	public static Dimension2D[] getLabelsPhySize(MathElement[] labels,
			Font labelFont) {
		Dimension2D[] ss = new Dimension2D[labels.length];

		for (int i = 0; i < labels.length; i++) {
			MathLabel labelDrawer = new MathLabel(labels[i], labelFont,
					VAlign.MIDDLE, HAlign.CENTER);
			Rectangle2D rect = labelDrawer.getBounds();
			ss[i] = new DoubleDimension2D(rect.getWidth(), rect.getHeight());
		}

		return ss;
	}

	public ComponentEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap) {
		AxisImpl result = new AxisImpl(tick.deepCopy(orig2copyMap),
				title.deepCopy(orig2copyMap));
		result.tick.setParent(result);
		result.title.setParent(result);
		result.copyFrom(this, orig2copyMap);

		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}

		return result;

	}

	public void copyFrom(ComponentEx src, Map<ElementEx, ElementEx> orig2copyMap) {
		super.copyFrom(src, orig2copyMap);

		AxisImpl axis = (AxisImpl) src;

		this.orientation = axis.orientation;
		this.offset = axis.offset;
		this.length = axis.length;
		this.position = axis.position;
		this.asc = axis.asc;
		this.desc = axis.desc;
		this.labelOffset = axis.labelOffset;
		this.labelHAlign = axis.labelHAlign;
		this.labelVAlign = axis.labelVAlign;
		this.titleOffset = axis.titleOffset;
		this.titleVAlign = axis.titleVAlign;

	}

	public void draw(Graphics2D graphics) {

		Graphics2D g = (Graphics2D) graphics.create();

		g.transform(getPhysicalTransform().getTransform());

		g.setColor(getEffectiveColor());
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setStroke(new BasicStroke(DEFAULT_AXISLINE_WIDTH));
		drawAxisLine(g);
		drawTicks(g);

		if (isLabelVisible()) {
			drawLabels(g, labelVAlign, labelHAlign);
		}

		if (getTitle().isVisible() && getTitle().getTextModel() != null) {
			drawTitle(graphics);
		}

	}

	private void drawAxisLine(Graphics2D g2) {
		Shape s = new Line2D.Double(0, 0, getLength(), 0);
		g2.draw(s);
	}

	/**
	 * Draw ticks and grid lines.
	 * 
	 * @param g
	 *            Graphics
	 */
	private void drawTicks(Graphics2D g) {

		Object tvs = getTick().getValues();
		Object mvs = getTick().getMinorValues();
		int tvslen = Array.getLength(tvs);
		int mvslen = Array.getLength(mvs);

		/*
		 * implement notes: grid lines must be drawn before ticks, otherwise the
		 * tick may be overlapped.
		 */
		if (isGridLines()) {
			Stroke oldStroke = g.getStroke();
			Color oldColor = g.getColor();

			Stroke gridLineStroke = new BasicStroke(
					((BasicStroke) oldStroke).getLineWidth() / 2,
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f,
					new float[] { 2.0f, 2.0f }, 0.0f);
			g.setStroke(gridLineStroke);

			Color c = getColor();
			g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), c
					.getAlpha() / 2));

			Rectangle2D plotRect = getParent().getContentBounds();
			if (getOrientation() == AxisOrientation.HORIZONTAL) {
				for (int i = 0; i < tvslen; i++) {
					double xp = transTickToPaper(Array.getDouble(tvs, i));
					drawXGridLine(g, xp, plotRect.getMinY(), plotRect.getMaxY());
				}
			} else {
				for (int i = 0; i < tvslen; i++) {
					double yp = transTickToPaper(Array.getDouble(tvs, i));
					drawYGridLine(g, yp, plotRect.getMinX(), plotRect.getMaxX());
				}
			}

			g.setStroke(oldStroke);
			g.setColor(oldColor);
		}

		if (isTickVisible()) {
			for (int i = 0; i < tvslen; i++) {
				double xp = transTickToPaper(Array.getDouble(tvs, i));
				drawXTic(g, xp, getTickHeight());
			}
			for (int i = 0; i < mvslen; i++) {
				double xp = transTickToPaper(Array.getDouble(mvs, i));
				drawXTic(g, xp, getMinorTickHeight());
			}
		}
	}

	/**
	 * Draw all labels of this axis.
	 * 
	 * @param g
	 * @param yt
	 *            the y value in physical coordinate system.
	 * @param vertalign
	 * @param horzalign
	 */
	private void drawLabels(Graphics2D g, VAlign vertalign, HAlign horzalign) {

		Object tvs = getTick().getValues();
		MathElement[] labels = getTick().getLabelModels();

		AffineTransform oldTransform = g.getTransform();

		for (int i = 0; i < labels.length; i++) {
			double x = Array.getDouble(tvs, i * getTick().getLabelInterval());
			double xt = transTickToPaper(x);

			MathLabel label = new MathLabel(labels[i], getTick()
					.getActualLabelFont(), vertalign, horzalign);

			Color color = getLabelColor();

			g.translate(xt, labelOffset);
			g.scale(1, -1);
			if (!isLabelSameOrientation()) {
				g.rotate(Math.PI / 2.0);
			}
			g.setColor(color);

			label.draw(g);

			g.setTransform(oldTransform);
		}

	}

	private void drawTitle(Graphics2D g) {
		TextComponentEx title = getTitle();

		double xt = getLength() * 0.5;
		title.setLocation(new Point2D.Double(xt, titleOffset));
		title.setHAlign(HAlign.CENTER);
		title.setVAlign(titleVAlign);

		title.draw(g);

	}

	private void drawXTic(Graphics2D g, double xp, double ticHeight) {

		double yp0, yp1;

		if (isTickBothSide()) {
			yp0 = +ticHeight;
			yp1 = -ticHeight;
		} else if (isTickAscSide()) {
			yp0 = +ticHeight;
			yp1 = 0;
		} else {
			yp0 = 0;
			yp1 = -ticHeight;
		}

		Shape line = new Line2D.Double(xp, yp0, xp, yp1);
		g.draw(line);
	}

	/**
	 * Draw vertical grid line on X axis
	 * 
	 * @param g
	 * @param xp
	 *            x location
	 * @param sp
	 *            start p
	 * @param ep
	 *            end p
	 */
	private void drawXGridLine(Graphics2D g, double xp, double sp, double ep) {

		/* avoid overlapping on the other Y axes */
		for (AxisEx axis : this.getOrthoAxes()) {
			if (Math.abs(axis.getLocation().getX() - xp) < DEFAULT_AXISLINE_WIDTH) {
				return;
			}
		}

		PhysicalTransform _lxf = getParent().getPhysicalTransform();
		double x0 = _lxf.getXPtoD(xp);
		double y0 = _lxf.getYPtoD(sp);
		double y1 = _lxf.getYPtoD(ep);

		Shape line = new Line2D.Double(x0, y0, x0, y1);
		g.draw(line);

	}

	/**
	 * Draw horizontal grid line on Y axis
	 * 
	 * @param g
	 * @param yp
	 *            y location
	 * @param sp
	 *            start p
	 * @param ep
	 *            end p
	 */
	private void drawYGridLine(Graphics2D g, double yp, double sp, double ep) {

		/* avoid overlapping on the other X axes */
		for (AxisEx axis : getOrthoAxes()) {
			if (Math.abs(axis.getLocation().getY() - yp) < DEFAULT_AXISLINE_WIDTH) {
				return;
			}
		}

		PhysicalTransform _lxf = getParent().getPhysicalTransform();
		double y0 = _lxf.getYPtoD(yp);
		double x0 = _lxf.getXPtoD(sp);
		double x1 = _lxf.getXPtoD(ep);

		Shape line = new Line2D.Double(x0, y0, x1, y0);
		g.draw(line);

	}

	private List<AxisEx> getOrthoAxes() {
		List<AxisEx> axes = new ArrayList<AxisEx>();
		if (getOrientation() == AxisOrientation.HORIZONTAL) {
			for (AxisEx a : getParent().getYAxes()) {
				axes.add(a);
			}
		} else {
			for (AxisEx a : getParent().getXAxes()) {
				axes.add(a);
			}
		}
		return axes;
	}

	/**
	 * Transform tick value to paper value on this axis.
	 * 
	 * @param the
	 *            tick value
	 * @return paper value
	 */
	private double transTickToPaper(double tickValue) {
		double uv;
		if (tickTransform != null) {
			uv = tickTransform.transformTick2User(tickValue);
		} else {
			uv = tickValue;
		}
		return rangeManager.getNormalTransform().getTransP(uv) * length;
	}

}
