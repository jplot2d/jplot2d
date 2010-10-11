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

import java.awt.Color;
import java.awt.Font;
import java.text.Format;

import org.jplot2d.element.AxisLabelSide;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.AxisTick;
import org.jplot2d.element.AxisTickSide;
import org.jplot2d.tick.TickAlgorithm;
import org.jplot2d.tick.TickCalculator;
import org.jplot2d.util.MathElement;
import org.jplot2d.util.TeXMathUtils;

/**
 * @author Jingjing Li
 * 
 */
public class AxisTickImpl implements AxisTick {

	private AxisImpl axis;

	private boolean visible = true;

	private boolean tickCalcNeeded;

	private boolean autoAdjustNumber;

	private int tickNumber;

	private boolean autoInterval;

	private double interval;

	private double offset;

	private boolean autoValues;

	private Object fixedValues;

	private Object fixedMinorValues;

	private boolean showGridLines;

	private double tickHeight;

	private AxisTickSide tickSide;

	private boolean autoMinorNumber;

	private int minorNumber;

	private double minorHeight;

	private boolean labelVisible;

	private boolean autoLabelFormat;

	private Format labelTextFormat;

	private String labelFormat;

	/**
	 * fixed labels correspondent to fixed ticks
	 */
	private MathElement[] fixedLabels;

	private Color labelColor;

	private String fontName;

	private int fontStyle = -1;

	private float fontSize = Float.NaN;

	private AxisLabelSide labelSide;

	private int labelInterval;

	private AxisOrientation labelOrientation;

	/**
	 * values in visible range
	 */
	private Object values = new double[0];

	private Object minorValues = new double[0];

	/**
	 * labels in visible range
	 */
	private MathElement[] labels = new MathElement[0];

	private TickAlgorithm tickAlgorithm;

	private TickCalculator tickCalculator;

	private Font actualLabelFont;

	/* =========== xxx Changed =========== */

	private boolean _isAutoAdjustNumberChanged;

	private boolean _isAutoIntervalChanged;

	private boolean _isAutoValuesChanged;

	private boolean _isAutoLabelFormatChanged;

	private boolean _propNumberChanged;

	/**
	 * interval or offset changed
	 */
	private boolean _intervalChanged;

	private boolean _valuesChanged;

	private boolean _minorNumberChanged;

	private boolean _minorValuesChanged;

	private boolean _labelFormatChanged;

	private boolean _labelIntervalChanged;

	private boolean _propLabelFontChanged;

	private boolean _labelOrientationChanged;

	private boolean _rangeChanged;

	private boolean _tickAlgorithmChanged;

	private boolean _trfScaleChanged;

	private boolean _axisTypeChanged;

	private boolean _trfChanged;

	public AxisImpl getParent() {
		return axis;
	}

	public boolean isTickCalcNeeded() {
		return tickCalcNeeded;
	}

	public void calcTicks() {

		tickCalculator.setRange(axis.getRange());

		if (autoValues && autoInterval && autoAdjustNumber) {
			// return calcAutoTicks();
		}

		tickCalcNeeded = false;
	}

	public boolean getVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean getAutoAdjustNumber() {
		return autoAdjustNumber;
	}

	public void setAutoAdjustNumber(boolean flag) {
		this.autoAdjustNumber = flag;
	}

	public int getNumber() {
		return tickNumber;
	}

	public void setNumber(int tickNumber) {
		this.tickNumber = tickNumber;
	}

	public boolean getAutoInterval() {
		return autoInterval;
	}

	public void setAutoInterval(boolean ati) {
		this.autoInterval = ati;
	}

	public double getInterval() {
		return interval;
	}

	public double getOffset() {
		return offset;
	}

	public void setInterval(double interval) {
		this.interval = interval;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public boolean isAutoValues() {
		return autoValues;
	}

	public void setAutoValues(boolean atv) {
		this.autoValues = atv;
	}

	public Object getFixedValues() {
		return fixedValues;
	}

	public void setFixedValues(Object values) {
		this.fixedValues = values;
	}

	public Object getFixedMinorValues() {
		return fixedMinorValues;
	}

	public void setFixedMinorValues(Object minorValues) {
		this.fixedMinorValues = minorValues;
	}

	public boolean isGridLines() {
		return showGridLines;
	}

	public void setGridLines(boolean showGridLines) {
		this.showGridLines = showGridLines;
	}

	public double getTickHeight() {
		return tickHeight;
	}

	public void setTickHeight(double height) {
		this.tickHeight = height;
	}

	public AxisTickSide getSide() {
		return tickSide;
	}

	public void setSide(AxisTickSide side) {
		this.tickSide = side;
	}

	public boolean isAutoMinorNumber() {
		return autoMinorNumber;
	}

	public void setAutoMinorNumber(boolean flag) {
		this.autoMinorNumber = flag;
	}

	public int getMinorNumber() {
		return minorNumber;
	}

	public void setMinorNumber(int minors) {
		this.minorNumber = minors;
	}

	public double getMinorHeight() {
		return minorHeight;
	}

	public void setMinorHeight(double height) {
		this.minorHeight = height;
	}

	/* =========================== Labels ============================= */

	public boolean isLabelVisible() {
		return labelVisible;
	}

	public void setLabelVisible(boolean visible) {
		this.labelVisible = visible;
	}

	public boolean isAutoLabelFormat() {
		return autoLabelFormat;
	}

	public void setAutoLabelFormat(boolean alf) {
		this.autoLabelFormat = alf;
	}

	public Format getLabelTextFormat() {
		return labelTextFormat;
	}

	public void setLabelTextFormat(Format format) {
		this.labelTextFormat = format;
	}

	public String getLabelFormat() {
		return labelFormat;
	}

	public void setLabelFormat(String format) {
		this.labelFormat = format;
	}

	public String[] getFixedLabelStrings() {
		if (fixedLabels == null) {
			return new String[0];
		}
		String[] result = new String[fixedLabels.length];
		for (int i = 0; i < fixedLabels.length; i++) {
			result[i] = TeXMathUtils.toString(fixedLabels[i]);
		}
		return result;
	}

	public void setFixedLabelStrings(String[] labels) {
		MathElement[] mes = new MathElement[labels.length];
		for (int i = 0; i < mes.length; i++) {
			mes[i] = TeXMathUtils.parseText(labels[i]);
		}
		fixedLabels = mes;
		calcTicks();

	}

	public Color getLabelColor() {
		return labelColor;
	}

	public void setLabelColor(Color color) {
		this.labelColor = color;
	}

	public Font getLabelFont() {
		return new Font(fontName, fontStyle, (int) fontSize)
				.deriveFont(fontSize);
	}

	public void setLabelFont(Font font) {
		fontName = font.getName();
		fontStyle = font.getStyle();
		fontSize = font.getSize2D();

		actualLabelFont = font;
		_propLabelFontChanged = true;
	}

	public String getLabelFontName() {
		return fontName;
	}

	public void setLabelFontName(String name) {
		fontName = name;
	}

	public int getLabelFontStyle() {
		return fontStyle;
	}

	public void setLabelFontStyle(int style) {
		fontStyle = style;
	}

	public float getLabelFontSize() {
		return fontSize;
	}

	public void setLabelFontSize(float size) {
		fontSize = size;
	}

	public AxisLabelSide getLabelSide() {
		return labelSide;
	}

	public void setLabelSide(AxisLabelSide side) {
		this.labelSide = side;
	}

	public int getLabelInterval() {
		return labelInterval;
	}

	public void setLabelInterval(int n) {
		this.labelInterval = n;
	}

	public AxisOrientation getLabelOrientation() {
		return labelOrientation;
	}

	public void setLabelOrientation(AxisOrientation orientation) {
		this.labelOrientation = orientation;
	}

	public Object getValues() {
		return values;
	}

	public Object getMinorValues() {
		return minorValues;
	}

	Font getActualLabelFont() {
		return actualLabelFont;
	}

	MathElement[] getInRangeLabelModels() {
		return labels;
	}

	/* ========================== Packages ============================== */

	void setTickAlgorithm(TickAlgorithm strategy) {
		if (tickAlgorithm != strategy) {
			tickAlgorithm = strategy;
			tickCalculator = tickAlgorithm.createCalculator();
			_tickAlgorithmChanged = true;
		}
	}

	public String[] getLabelStrings() {
		// TODO Auto-generated method stub
		return null;
	}

}
