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

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.jplot2d.element.AxisLabelSide;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.AxisPosition;
import org.jplot2d.element.AxisTickSide;
import org.jplot2d.element.HAlign;
import org.jplot2d.element.TextComponent;
import org.jplot2d.element.VAlign;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.MathElement;
import org.jplot2d.util.Range2D;

/**
 * @author Jingjing Li
 * 
 */
public class AxisImpl extends ComponentImpl implements AxisEx {

	/* the gap on the other side of ticks */
	private static final double TIC_GAP = 0.05;

	/* the tick height + gap */
	private static final double TIC_RATIO = 1.3;

	/* the label height + gap */
	private static final double LABEL_RATIO = 1.1;

	private double length;

	private double _asc, _desc;

	private double _labelOffset;

	private HAlign _labelHAlign;

	private VAlign _labelVAlign;

	private double _titleOffset;

	private VAlign _titleVAlign;

	public String getSelfId() {
		if (getParent() != null) {
			int xidx = getParent().indexOfXAxis(this);
			if (xidx != -1) {
				return "XAxis" + xidx;
			}
			int yidx = getParent().indexOfYAxis(this);
			if (yidx != -1) {
				return "YAxis" + yidx;
			}
			return null;
		} else {
			return "Axis@" + System.identityHashCode(this);
		}
	}

	public SubplotEx getParent() {
		return (SubplotEx) super.getParent();
	}

	public AxisOrientation getOrientation() {
		// TODO Auto-generated method stub
		return null;
	}

	public AxisPosition getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	public AxisTickImpl getTick() {
		// TODO Auto-generated method stub
		return null;
	}

	public TextComponent getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPosition(AxisPosition position) {
		// TODO Auto-generated method stub

	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getThickness() {
		return getAsc() + getDesc();
	}

	public double getAsc() {
		return _asc;
	}

	public double getDesc() {
		return _desc;
	}

	/**
	 * The <code>validate</code> method is used to cause this axis to lay out
	 * its ticks and labels again. It should be invoked when its layout-related
	 * information changed.
	 */
	public void updateThickness() {

		getTick().calcTicks();

		if (isValid()) {
			return;
		}

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

		if (getTitle().isVisible() && getTitle().getModel() != MathElement.NULL) {
			double titleHeight = getTitle().getPhysicalBounds().getHeight();
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
		if (Math.abs(_labelOffset - labelOffset) > Math.abs(_labelOffset) * 1e-12
				|| _labelHAlign != labelHAlign || _labelVAlign != labelVAlign) {
			_labelOffset = labelOffset;
			_labelHAlign = labelHAlign;
			_labelVAlign = labelVAlign;
			changed = true;
		}
		if (Math.abs(_titleOffset - titleOffset) > Math.abs(_titleOffset) * 1e-12
				|| _titleVAlign != titleVAlign) {
			_titleOffset = titleOffset;
			_titleVAlign = titleVAlign;
			changed = true;
		}
		if (Math.abs(_asc - ba) > Math.abs(_asc) * 1e-12
				|| Math.abs(_desc - bd) > Math.abs(_desc) * 1e-12) {
			_asc = ba;
			_desc = bd;
			changed = true;
		}

		if (changed) {
			invalidate();
		}
		super.validate();
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
		Dimension2D[] labelsSize = getLabelsPhySize(getTick()
				.getInRangeLabelModels(), getTick().getActualLabelFont());
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

	public AxisEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap) {
		AxisImpl result = new AxisImpl();
		result.copyFrom(this);
		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}
		return result;
	}

	private void copyFrom(AxisImpl src) {
		super.copyFrom(src);
	}

}
