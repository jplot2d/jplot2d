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
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jplot2d.axtick.LinearTickAlgorithm;
import org.jplot2d.element.AxisLabelSide;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.AxisPosition;
import org.jplot2d.element.AxisTickSide;
import org.jplot2d.element.AxisTickTransform;
import org.jplot2d.element.HAlign;
import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.element.VAlign;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.MathElement;
import org.jplot2d.util.Range2D;

/**
 * @author Jingjing Li
 * 
 */
public class AxisImpl extends ComponentImpl implements AxisEx {

	/**
	 * the default physical width, 0.5 pt.
	 */
	private static final double DEFAULT_AXISLINE_WIDTH = 0.5 / 72;

	/* the gap on the other side of ticks */
	private static final double TIC_GAP = 0.05;

	/* the tick height + gap */
	private static final double TIC_RATIO = 1.3;

	/* the label height + gap */
	private static final double LABEL_RATIO = 1.1;

	private AxisTickImpl tick;

	private final TextComponentImpl title;

	private AxisPosition position;

	private double asc, desc;

	private double labelOffset;

	private HAlign labelHAlign;

	private VAlign labelVAlign;

	private double titleOffset;

	private VAlign titleVAlign;

	private boolean thicknessCalculationNeeded = true;

	private AxisTickTransform tickTransform;

	public AxisImpl() {
		tick = new AxisTickImpl();
		tick.setParent(this);
		tick.setTickAlgorithm(LinearTickAlgorithm.getInstance());
		title = new TextComponentImpl();
	}

	public String getSelfId() {
		if (getParent() != null) {
			int xidx = getParent().indexOfAxis(this);
			return "Axis" + xidx;
		} else {
			return "Axis@" + System.identityHashCode(this);
		}
	}

	public ViewportAxisEx getParent() {
		return (ViewportAxisEx) super.getParent();
	}

	public Dimension2D getSize() {
		if (getParent() == null) {
			return null;
		} else {
			switch (getParent().getOrientation()) {
			case HORIZONTAL:
				return new DoubleDimension2D(getParent().getLength(),
						getThickness());
			case VERTICAL:
				return new DoubleDimension2D(getThickness(), getParent()
						.getLength());
			default:
				return null;
			}
		}
	}

	public Rectangle2D getBounds() {
		if (getParent() == null) {
			return null;
		} else {
			switch (getParent().getOrientation()) {
			case HORIZONTAL:
				return new Rectangle2D.Double(getLocation().getX(),
						getLocation().getY() + desc, getParent().getLength(),
						getThickness());
			case VERTICAL:
				return new Rectangle2D.Double(getLocation().getX() - asc,
						getLocation().getY(), getParent().getLength(),
						getThickness());
			default:
				return null;
			}
		}
	}

	public void calcTicks() {
		tick.calcTicks();
	}

	public AxisTickTransform getTickTransform() {
		return tickTransform;
	}

	public void setTickTransform(AxisTickTransform transform) {
		this.tickTransform = transform;
		tick.axisOrTickTransformChanged();
	}

	public void axisTransformChanged() {
		tick.axisOrTickTransformChanged();
	}

	public void axisTypeChanged() {
		tick.axisTypeChanged();
	}

	public Range2D getRange() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRange(Range2D range) {
		// TODO Auto-generated method stub

	}

	public void setRange(double low, double high) {
		// TODO Auto-generated method stub

	}

	public double getCanonicalValue(double d) {
		return d % getParent().getType().getCircle();
	}

	public long getCanonicalValue(long d) {
		return (long) (d % getParent().getType().getCircle());
	}

	public AxisTickEx getTick() {
		return tick;
	}

	public TextComponentEx getTitle() {
		return title;
	}

	public AxisPosition getPosition() {
		return position;
	}

	public void setPosition(AxisPosition position) {
		this.position = position;
	}

	public double getThickness() {
		calcThickness();
		return getAsc() + getDesc();
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
	void invalidateThickness() {
		thicknessCalculationNeeded = true;
	}

	/**
	 * The <code>validate</code> method is used to cause this axis to lay out
	 * its ticks and labels again. It should be invoked when its layout-related
	 * information changed.
	 */
	public void calcThickness() {

		// we don't calc ticks here. getTick().calcTicks();

		if (!thicknessCalculationNeeded) {
			return;
		}
		thicknessCalculationNeeded = false;

		double ba = 0;
		double bd = 0;
		double baGap, bdGap;
		double labelOffset = 0;
		VAlign labelVAlign = null;
		HAlign labelHAlign = null;
		double titleOffset = 0;
		VAlign titleVAlign = null;

		bdGap = TIC_GAP;
		baGap = TIC_GAP;
		if (getTick().getVisible()) {
			if (isTickBothSide() || isTickPositiveSide()) {
				ba += getTick().getTickHeight();
				baGap = (TIC_RATIO - 1) * getTick().getTickHeight();
			}
			if (isTickBothSide() || !isTickPositiveSide()) {
				bd -= getTick().getTickHeight();
				bdGap = (TIC_RATIO - 1) * getTick().getTickHeight();
			}
		}

		if (getTick().isLabelVisible()) {
			double labelHeight = getLabelHeight();
			if (isLabelPositiveSide()) {
				labelOffset = ba + baGap;
				if (getOrientation() == getTick().getLabelOrientation()) {
					ba = labelOffset + labelHeight;
					baGap = (LABEL_RATIO - 1) * labelHeight;
					labelVAlign = (getOrientation() == AxisOrientation.HORIZONTAL) ? VAlign.BOTTOM
							: VAlign.TOP;
					labelHAlign = HAlign.CENTER;
				} else {
					ba = labelOffset + getLabelsMaxNormalPhysicalWidth();
					baGap = TIC_GAP;
					labelVAlign = VAlign.MIDDLE;
					labelHAlign = HAlign.LEFT;
				}
			} else {
				labelOffset = bd - bdGap;
				if (getOrientation() == getTick().getLabelOrientation()) {
					bd = labelOffset - labelHeight;
					bdGap = (LABEL_RATIO - 1) * labelHeight;
					labelVAlign = (getOrientation() == AxisOrientation.HORIZONTAL) ? VAlign.TOP
							: VAlign.BOTTOM;
					labelHAlign = HAlign.CENTER;
				} else {
					bd = labelOffset - getLabelsMaxNormalPhysicalWidth();
					bdGap = TIC_GAP;
					labelVAlign = VAlign.MIDDLE;
					labelHAlign = HAlign.RIGHT;
				}
			}
		}

		if (getTitle().isVisible()
				&& getTitle().getTextModel() != MathElement.NULL) {
			double titleHeight = getTitle().getBounds().getHeight();
			if (isTitlePositiveSide()) {
				titleOffset = ba + baGap;
				titleVAlign = (getOrientation() == AxisOrientation.HORIZONTAL) ? VAlign.BOTTOM
						: VAlign.TOP;
				ba = titleOffset + titleHeight;
			} else {
				titleOffset = bd - bdGap;
				titleVAlign = (getOrientation() == AxisOrientation.HORIZONTAL) ? VAlign.TOP
						: VAlign.BOTTOM;
				bd = titleOffset - titleHeight;
			}
		}

		boolean changed = false;
		if (Math.abs(this.labelOffset - labelOffset) > Math
				.abs(this.labelOffset) * 1e-12
				|| this.labelHAlign != labelHAlign
				|| this.labelVAlign != labelVAlign) {
			this.labelOffset = labelOffset;
			this.labelHAlign = labelHAlign;
			this.labelVAlign = labelVAlign;
			changed = true;
		}
		if (Math.abs(this.titleOffset - titleOffset) > Math
				.abs(this.titleOffset) * 1e-12
				|| this.titleVAlign != titleVAlign) {
			this.titleOffset = titleOffset;
			this.titleVAlign = titleVAlign;
			changed = true;
		}
		if (Math.abs(asc - ba) > Math.abs(asc) * 1e-12
				|| Math.abs(desc - bd) > Math.abs(desc) * 1e-12) {
			asc = ba;
			desc = bd;
			changed = true;
		}

		if (changed) {
			invalidate();
		}

	}

	private AxisOrientation getOrientation() {
		return getParent().getOrientation();
	}

	private boolean isTitlePositiveSide() {
		boolean axisPositiveSide = (getPosition() == AxisPosition.POSITIVE_SIDE);
		return axisPositiveSide;
	}

	private boolean isTickBothSide() {
		return getTick().getSide() == AxisTickSide.BOTH;
	}

	private boolean isTickPositiveSide() {
		boolean axisPositiveSide = (getPosition() == AxisPosition.POSITIVE_SIDE);
		return axisPositiveSide == (getTick().getSide() == AxisTickSide.OUTWARD);
	}

	private boolean isLabelPositiveSide() {
		boolean axisPositiveSide = (getPosition() == AxisPosition.POSITIVE_SIDE);
		return axisPositiveSide == (getTick().getLabelSide() == AxisLabelSide.OUTWARD);
	}

	/**
	 * @return the physical label height for proposed label font.
	 */
	private double getLabelHeight() {
		FontRenderContext frc = new FontRenderContext(null, false, true);
		LineMetrics lm = getTick().getLabelFont().getLineMetrics(
				"Can be any string", frc);
		return (lm.getAscent() + lm.getDescent()) / 72;
	}

	/**
	 * Traverse all labels to find the Maximum Normal Width.
	 * 
	 * @return the maximum normal width.
	 */
	private double getLabelsMaxNormalPhysicalWidth() {
		Dimension2D[] labelsSize = getLabelsPhySize(
				tick.getInRangeLabelModels(), tick.getActualLabelFont());
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
	static Dimension2D[] getLabelsPhySize(MathElement[] labels, Font labelFont) {
		Dimension2D[] ss = new Dimension2D[labels.length];

		MathLabel labelDrawer = new MathLabel(labelFont, new Point2D.Double(0,
				0), VAlign.MIDDLE, HAlign.CENTER);

		for (int i = 0; i < labels.length; i++) {
			labelDrawer.setModel(labels[i]);
			Rectangle2D rect = labelDrawer.getBoundsP();
			ss[i] = new DoubleDimension2D(rect.getWidth(), rect.getHeight());
		}

		return ss;
	}

	public void copyFrom(ComponentEx src, Map<ElementEx, ElementEx> orig2copyMap) {
		super.copyFrom(src, orig2copyMap);

		AxisImpl axis = (AxisImpl) src;

		this.position = axis.position;
		this.asc = axis.asc;
		this.desc = axis.desc;
		this.labelOffset = axis.labelOffset;
		this.labelHAlign = axis.labelHAlign;
		this.labelVAlign = axis.labelVAlign;
		this.titleOffset = axis.titleOffset;
		this.titleVAlign = axis.titleVAlign;

		tick = axis.tick.copy();
		tick.setParent(this);
		title.copyFrom(src, orig2copyMap);
	}

	public void draw(Graphics2D g) {

		g.setColor(getColor());

		Stroke oldStroke = g.getStroke();
		/*
		 * Workaround for a bug of java2d (Sun Bug ID 6635297), pixel rounding
		 * behave different between solid line and dash line. This cause grid
		 * lines not coinciding with major ticks. Turning ANTIALIAS ON can avoid
		 * it.
		 */
		RenderingHints oldRenderingHints = g.getRenderingHints();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		PhysicalTransform _lxf = getParent().getPhysicalTransform();

		g.setStroke(new BasicStroke(
				(float) (_lxf.getScale() * DEFAULT_AXISLINE_WIDTH)));
		drawAxisLine(g);
		drawTicks(g);

		double p;
		if (getOrientation() == AxisOrientation.HORIZONTAL) {
			p = getLocation().getY();
		} else {
			p = getLocation().getX();
		}

		if (getTick().isLabelVisible()) {
			double labelLoc = p + labelOffset;
			VAlign vertalign = labelVAlign;
			HAlign horzalign = labelHAlign;
			drawLabels(g, labelLoc, vertalign, horzalign);
		}

		if (getTitle().isVisible()
				&& getTitle().getTextModel() != MathElement.NULL) {
			double titleLoc = p + titleOffset;
			VAlign valign = titleVAlign;
			drawTitle(g, titleLoc, valign);
		}

		g.setRenderingHints(oldRenderingHints);
		g.setStroke(oldStroke);

	}

	private void drawAxisLine(Graphics2D g2) {
		PhysicalTransform pxf = getParent().getPhysicalTransform();
		Point2D dloc = pxf.getPtoD(getLocation());
		Shape s;
		if (getOrientation() == AxisOrientation.HORIZONTAL) {
			s = new Line2D.Double(dloc.getX(), dloc.getY(), dloc.getX()
					+ getParent().getLength() * pxf.getScale(), dloc.getY());
		} else {
			s = new Line2D.Double(dloc.getX(), dloc.getY(), dloc.getX(),
					dloc.getY() - getParent().getLength() * pxf.getScale());
		}
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
		if (getTick().isGridLines()) {
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

			Rectangle2D plotRect = getParent().getParent().getViewportBounds();
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

		if (getTick().getVisible()) {
			if (getOrientation() == AxisOrientation.HORIZONTAL) {
				double yp = getLocation().getY();
				for (int i = 0; i < tvslen; i++) {
					double xp = transTickToPaper(Array.getDouble(tvs, i));
					drawXTic(g, xp, yp, getTick().getTickHeight());
				}
				for (int i = 0; i < mvslen; i++) {
					double xp = transTickToPaper(Array.getDouble(mvs, i));
					drawXTic(g, xp, yp, getTick().getMinorHeight());
				}
			} else {
				double xp = getLocation().getX();
				for (int i = 0; i < tvslen; i++) {
					double yp = transTickToPaper(Array.getDouble(tvs, i));
					drawYTic(g, xp, yp, getTick().getTickHeight());
				}
				for (int i = 0; i < mvslen; i++) {
					double yp = transTickToPaper(Array.getDouble(mvs, i));
					drawYTic(g, xp, yp, getTick().getMinorHeight());
				}
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
	private void drawLabels(Graphics2D g, double yt, VAlign vertalign,
			HAlign horzalign) {

		PhysicalTransform pxf = getParent().getPhysicalTransform();

		Object tvs = getTick().getValues();
		MathElement[] labels = tick.getInRangeLabelModels();

		for (int i = 0; i < labels.length; i++) {
			double x = Array.getDouble(tvs, i * getTick().getLabelInterval());
			double xt = transTickToPaper(x);
			Point2D point = (getOrientation() == AxisOrientation.HORIZONTAL) ? new Point2D.Double(
					xt, yt) : new Point2D.Double(yt, xt);

			MathLabel label = new MathLabel(labels[i],
					tick.getActualLabelFont(), point);
			label.setPhysicalTransform(pxf);
			label.setAlign(vertalign, horzalign);
			if (getTick().getLabelOrientation() == AxisOrientation.HORIZONTAL) {
				label.setAngle(0);
			} else if (getTick().getLabelOrientation() == AxisOrientation.VERTICAL) {
				label.setAngle(90);
			}
			label.setColor(getTick().getLabelColor());

			label.draw(g);
		}
	}

	private void drawTitle(Graphics2D g, double vloc, VAlign vertalign) {
		double xt, yt;

		TextComponentEx title = getTitle();

		xt = getLocation().getX() + getParent().getLength() * 0.5;
		if (getOrientation() == AxisOrientation.HORIZONTAL) {
			xt = getLocation().getX() + getParent().getLength() * 0.5;
			yt = vloc;
		} else {
			yt = getLocation().getY() + getParent().getLength() * 0.5;
			xt = vloc;
		}
		title.setLocation(new Point2D.Double(xt, yt));
		title.setHAlign(HAlign.CENTER);
		title.setVAlign(vertalign);
		if (getOrientation() == AxisOrientation.HORIZONTAL) {
			title.setAngle(0);
		} else if (getOrientation() == AxisOrientation.VERTICAL) {
			title.setAngle(90);
		}

		title.draw(g);

	}

	private void drawXTic(Graphics2D g, double xp, double yp, double ticHeight) {

		PhysicalTransform _lxf = getParent().getPhysicalTransform();

		double x0, y0, y1;
		double yp0, yp1;

		if (getTick().getSide() == AxisTickSide.BOTH) {
			yp0 = yp + ticHeight;
			yp1 = yp - ticHeight;
		} else if ((getPosition() == AxisPosition.POSITIVE_SIDE) == (getTick()
				.getSide() == AxisTickSide.OUTWARD)) {
			yp0 = yp + ticHeight;
			yp1 = yp;
		} else {
			yp0 = yp;
			yp1 = yp - ticHeight;
		}

		x0 = _lxf.getXPtoD(xp);
		y0 = _lxf.getYPtoD(yp0);
		y1 = _lxf.getYPtoD(yp1);
		Shape line = new Line2D.Double(x0, y0, x0, y1);
		g.draw(line);
	}

	private void drawYTic(Graphics2D g, double xp, double yp, double ticHeight) {
		PhysicalTransform _lxf = getParent().getPhysicalTransform();

		double x0, x1, y0;
		double xp0, xp1;

		if (getTick().getSide() == AxisTickSide.BOTH) {
			xp0 = xp + ticHeight;
			xp1 = xp - ticHeight;
		} else if ((getPosition() == AxisPosition.POSITIVE_SIDE) == (getTick()
				.getSide() == AxisTickSide.OUTWARD)) {
			xp0 = xp + ticHeight;
			xp1 = xp;
		} else {
			xp0 = xp;
			xp1 = xp - ticHeight;
		}

		y0 = _lxf.getYPtoD(yp);
		x0 = _lxf.getXPtoD(xp0);
		x1 = _lxf.getXPtoD(xp1);
		Shape line = new Line2D.Double(x0, y0, x1, y0);
		g.draw(line);
	}

	/**
	 * Draw vertical grid line on X axis
	 * 
	 * @param g
	 * @param xp
	 *            perpendicularity
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
	 *            perpendicularity
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

	/**
	 * Transform world coordinate to physical coordinate on this axis.
	 * 
	 * @param uvalue
	 * @return
	 */
	double transTickToPaper(double tickValue) {
		double uv;
		if (tickTransform != null) {
			uv = tickTransform.transformTick2User(tickValue);
		} else {
			uv = tickValue;
		}
		return getParent().getAxisTransform().getTransP(uv);
	}

	private List<AxisEx> getOrthoAxes() {
		List<AxisEx> axes = new ArrayList<AxisEx>();
		if (getOrientation() == AxisOrientation.HORIZONTAL) {
			for (ViewportAxisEx ag : getParent().getParent().getYViewportAxes()) {
				for (AxisEx a : ag.getAxes()) {
					axes.add(a);
				}
			}
		} else {
			for (ViewportAxisEx ag : getParent().getParent().getXViewportAxes()) {
				for (AxisEx a : ag.getAxes()) {
					axes.add(a);
				}
			}
		}
		return axes;
	}

}
