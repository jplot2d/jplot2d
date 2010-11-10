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
import java.awt.geom.Dimension2D;
import java.lang.reflect.Array;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jplot2d.axtick.TickAlgorithm;
import org.jplot2d.axtick.TickCalculator;
import org.jplot2d.axtick.TickUtils;
import org.jplot2d.element.AxisLabelSide;
import org.jplot2d.element.AxisOrientation;
import org.jplot2d.element.AxisTickSide;
import org.jplot2d.util.MathElement;
import org.jplot2d.util.NumberArrayUtils;
import org.jplot2d.util.TeXMathUtils;

/**
 * @author Jingjing Li
 * 
 */
public class AxisTickImpl extends ElementImpl implements AxisTickEx {

	public static final int AUTO_TICKS_MIN = 4;

	private boolean visible = true;

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

	private int actualMinorNumber;

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
	 * labels before fixed label override.
	 */
	private MathElement[] autoLabels;

	/**
	 * labels in visible range
	 */
	private MathElement[] labels = new MathElement[0];

	private TickAlgorithm tickAlgorithm;

	private TickCalculator tickCalculator;

	private Font actualLabelFont;

	/* =========== xxx Changed =========== */

	private boolean autoAdjustNumberChanged;

	private boolean autoIntervalChanged;

	private boolean autoValuesChanged;

	private boolean autoLabelFormatChanged;

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

	private boolean _axisTypeChanged;

	private boolean _trfChanged;

	public AxisImpl getParent() {
		return (AxisImpl) parent;
	}

	private AxisImpl getAxis() {
		return (AxisImpl) parent;
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

	public Font getActualLabelFont() {
		return actualLabelFont;
	}

	public MathElement[] getInRangeLabelModels() {
		return labels;
	}

	/**
	 * notified by main axis type changed. This mainly affect the label
	 * calculation by value circle.
	 */
	public void axisTypeChanged() {
		_axisTypeChanged = true;
	}

	public void setTickAlgorithm(TickAlgorithm algorithm) {
		if (tickAlgorithm != algorithm) {
			tickAlgorithm = algorithm;
			tickCalculator = tickAlgorithm.createCalculator();
			_tickAlgorithmChanged = true;
		}
	}

	public String[] getLabelStrings() {
		// TODO Auto-generated method stub
		return null;
	}

	public void calcTicks() {

		tickCalculator.setRange(getAxis().getRange());

		if (autoValues && autoInterval && autoAdjustNumber) {
			calcAutoTicks();
			return;
		}

		int[] valueIdxes = null, mvalueIdxes;
		Object values, mvalues;
		if (autoValues) {
			if (autoInterval) {
				if (autoValuesChanged || autoIntervalChanged
						|| autoAdjustNumberChanged || _propNumberChanged
						|| _minorNumberChanged || _tickAlgorithmChanged
						|| _axisTypeChanged || _rangeChanged) {
					tickCalculator.calcValuesByTickNumber(tickNumber,
							minorNumber);
					interval = tickCalculator.getInterval();
					actualMinorNumber = tickCalculator.getMinorNumber();
				}
			} else {
				if (autoValuesChanged || _intervalChanged
						|| _minorNumberChanged || _tickAlgorithmChanged
						|| _axisTypeChanged || _rangeChanged) {
					tickCalculator.calcValuesByTickInterval(interval, offset,
							minorNumber);
					actualMinorNumber = tickCalculator.getMinorNumber();
				}
			}
			values = tickCalculator.getValues();
			mvalues = tickCalculator.getMinorValues();
		} else {
			// filter off the ticks out of range
			valueIdxes = tickCalculator.getInRangeValuesIdx(fixedValues);
			mvalueIdxes = tickCalculator.getInRangeValuesIdx(fixedMinorValues);
			values = NumberArrayUtils.subArray(fixedValues, valueIdxes);
			mvalues = NumberArrayUtils.subArray(fixedMinorValues, mvalueIdxes);
		}

		if (!NumberArrayUtils.equals(this.values, values)) {
			this.values = values;
			_valuesChanged = true;
		}
		if (!NumberArrayUtils.equals(this.minorValues, mvalues)) {
			this.minorValues = mvalues;
			_minorValuesChanged = true;
		}

		autoIntervalChanged = autoAdjustNumberChanged = false;
		_propNumberChanged = _minorNumberChanged = false;
		_intervalChanged = false;
		autoValuesChanged = _rangeChanged = _tickAlgorithmChanged = false;

		/* calculate auto format & labels form values */
		if (autoLabelFormat && (_valuesChanged || autoLabelFormatChanged)) {
			Format atf = tickCalculator
					.calcAutoLabelTextFormat(getCanonicalValues(values));
			String alf = tickCalculator
					.calcAutoLabelFormat(getCanonicalValues(values));
			changeLabelFormat(atf, alf);
		}
		autoLabelFormatChanged = false;

		if (_valuesChanged || _labelFormatChanged) {
			autoLabels = calcAutoLabels(getCanonicalValues(values),
					labelTextFormat, labelFormat);
		}
		_labelFormatChanged = false;

		/* apply fixed labels */
		MathElement[] fixedLabelsInRange;
		if (autoValues || fixedLabels == null) {
			fixedLabelsInRange = fixedLabels;
		} else {
			List<MathElement> ul = new ArrayList<MathElement>();
			for (int i : valueIdxes) {
				if (i < fixedLabels.length) {
					ul.add(fixedLabels[i]);
				}
			}
			fixedLabelsInRange = ul.toArray(new MathElement[ul.size()]);
		}
		MathElement[] labels = calcLabels(fixedLabelsInRange, autoLabels,
				labelInterval);
		boolean labelsChanged = false;
		if (!Arrays.equals(this.labels, labels)) {
			this.labels = labels;
			labelsChanged = true;
		}

		/* shrink labels */
		boolean actualLabelFontChanged = false;
		if (_valuesChanged || labelsChanged || _labelIntervalChanged
				|| _propLabelFontChanged || _labelOrientationChanged
				|| _axisTypeChanged || _trfChanged) {
			Font newFont = shrinkLabelFont();
			if (!actualLabelFont.equals(newFont)) {
				actualLabelFont = newFont;
				actualLabelFontChanged = true;
			}
		}
		_propLabelFontChanged = _trfChanged = _axisTypeChanged = false;

		if (labelsChanged || _labelIntervalChanged || _labelOrientationChanged
				|| actualLabelFontChanged) {
			getAxis().invalidateThickness();
			getAxis().redraw();
			_labelIntervalChanged = _labelOrientationChanged = false;
		}

		if (_valuesChanged || _minorValuesChanged) {
			getAxis().redraw();
			_valuesChanged = _minorValuesChanged = _trfChanged = false;
		}

	}

	/**
	 * calculate interval from given range, tick number, label height and label
	 * format. This will set the interval, the values and the label density.
	 */
	private void calcAutoTicks() {

		/* Nothing changed */
		if (!(autoValuesChanged || autoIntervalChanged
				|| autoAdjustNumberChanged || _propNumberChanged
				|| _minorNumberChanged || autoLabelFormatChanged
				|| _labelFormatChanged || _labelIntervalChanged
				|| _propLabelFontChanged || _labelOrientationChanged
				|| _tickAlgorithmChanged || _trfChanged || _rangeChanged)) {
			return;
		}

		autoValuesChanged = autoIntervalChanged = autoAdjustNumberChanged = false;
		_propNumberChanged = _minorNumberChanged = false;
		autoLabelFormatChanged = _labelFormatChanged = false;
		_labelIntervalChanged = false;
		_propLabelFontChanged = _labelOrientationChanged = false;
		_tickAlgorithmChanged = _trfChanged = false;
		_rangeChanged = false;

		int tickNumber = this.tickNumber;
		Object values;
		Format labelTextFormat = this.labelTextFormat;
		String labelFormat = this.labelFormat;
		MathElement[] autoLabels;
		MathElement[] labels;
		while (true) {
			tickCalculator.calcValuesByTickNumber(tickNumber, minorNumber);
			values = tickCalculator.getValues();
			if (autoLabelFormat) {
				labelTextFormat = tickCalculator
						.calcAutoLabelTextFormat(getCanonicalValues(values));
				labelFormat = tickCalculator
						.calcAutoLabelFormat(getCanonicalValues(values));
			}
			autoLabels = calcAutoLabels(getCanonicalValues(values),
					labelTextFormat, labelFormat);
			labels = calcLabels(fixedLabels, autoLabels, labelInterval);

			double density = getLabelsDensity(values, labels, getLabelFont(),
					labelInterval);
			if (density <= 1) {
				break;
			}
			/* fast approximating, the next tick we should try */
			tickNumber = Math.min(tickNumber, Array.getLength(values)) - 1;
			if (tickNumber < AUTO_TICKS_MIN) {
				break;
			}
		}

		if (!NumberArrayUtils.equals(this.values, values)) {
			this.values = values;
			_valuesChanged = true;
		}
		if (!NumberArrayUtils.equals(minorValues, tickCalculator
				.getMinorValues())) {
			this.minorValues = tickCalculator.getMinorValues();
			this.actualMinorNumber = tickCalculator.getMinorNumber();
			_minorValuesChanged = true;
		}
		boolean labelsChanged = false;
		if (!Arrays.equals(this.labels, labels)) {
			this.labels = labels;
			labelsChanged = true;
		}
		interval = tickCalculator.getInterval();
		changeLabelFormat(labelTextFormat, labelFormat);
		this.autoLabels = autoLabels;

		Font newFont;
		if (tickNumber < AUTO_TICKS_MIN) {
			newFont = shrinkLabelFont();
		} else {
			newFont = getLabelFont();
		}
		boolean actualLabelFontChanged = false;
		if (!actualLabelFont.equals(newFont)) {
			actualLabelFont = newFont;
			actualLabelFontChanged = true;
		}

		if (labelsChanged || _labelIntervalChanged || _labelOrientationChanged
				|| actualLabelFontChanged) {
			getAxis().invalidateThickness();
			getAxis().redraw();
			_labelIntervalChanged = _labelOrientationChanged = false;
		}

		if (_valuesChanged || _minorValuesChanged) {
			getAxis().redraw();
			_valuesChanged = _minorValuesChanged = _trfChanged = false;
		}

	}

	/**
	 * Combine the user assigned labels with auto labels.
	 * 
	 * @param fixedLabels
	 *            user labels for every major tick, no label interval
	 *            considered.
	 * @param autoLabels
	 *            labels for every major tick, no label interval considered.
	 * @param labelInterval
	 * @return
	 */
	private static MathElement[] calcLabels(MathElement[] fixedLabels,
			MathElement[] autoLabels, int labelInterval) {
		int fixedLabelsLength = (fixedLabels == null) ? 0 : fixedLabels.length;
		int autoLabelsLength = (autoLabels == null) ? 0 : autoLabels.length;
		int length = (int) Math.floor((double) (autoLabelsLength - 1)
				/ labelInterval) + 1;
		MathElement[] result = new MathElement[length];
		int j = 0;
		for (int i = 0; i < autoLabelsLength; i = i + labelInterval) {
			if (i < fixedLabelsLength && fixedLabels[i] != null) {
				result[j] = fixedLabels[i];
			} else {
				result[j] = autoLabels[i];
			}
			j++;
		}
		return result;
	}

	/**
	 * Calculate label from values and format.
	 */
	private static MathElement[] calcAutoLabels(Object values,
			Format textFormat, String format) {
		MathElement[] labels = new MathElement[Array.getLength(values)];
		if (textFormat == null) {
			for (int i = 0; i < labels.length; i++) {
				labels[i] = TickUtils.format(format, Array.get(values, i));
			}
		} else {
			for (int i = 0; i < labels.length; i++) {
				labels[i] = TickUtils.format(textFormat, Array.get(values, i));
			}
		}
		return labels;
	}

	/**
	 * @param format
	 *            must not be null
	 */
	private void changeLabelFormat(Format textFormat, String format) {
		if (labelTextFormat != textFormat
				&& (labelTextFormat == null || !labelTextFormat
						.equals(textFormat))) {
			labelTextFormat = textFormat;
			_labelFormatChanged = true;
		}
		if (labelFormat != format
				&& (labelFormat == null || !labelFormat.equals(format))) {
			labelFormat = format;
			_labelFormatChanged = true;
		}
	}

	private Font shrinkLabelFont() {
		double density = getLabelsDensity(values, labels, getLabelFont(),
				labelInterval);
		if (Double.isInfinite(density)) { // when device length is zero
			return getLabelFont();
		}

		Font font = getLabelFont();
		double oldDensity = density;
		int count = 0;
		while (density > 1) {
			font = font
					.deriveFont((float) (font.getSize2D() / ((density > 1.1) ? density
							: 1.1)));
			density = getLabelsDensity(values, labels, font, labelInterval);

			if (oldDensity / density >= 1.1) {
				oldDensity = density;
				count = 0;
			} else {
				if (count++ > 10) {
					break;
				}
			}
		}
		return font;
	}

	/**
	 * density > 1 means label overlapped.
	 * 
	 * @return the label density.
	 */
	private double getLabelsDensity(Object tickValues, MathElement[] labels,
			Font labelFont, int labelInterval) {

		if (Array.getLength(tickValues) == 0) {
			return 0;
		}

		Dimension2D[] labelsSize = AxisImpl.getLabelsPhySize(labels, labelFont);
		/* the space between 2 neighbor labels */
		double blankWidth = labelsSize[0].getHeight() / 2;

		/* Note: when scale is very small, deltaD will be zero */
		double maxDesity = 0;
		double ticA = getAxis()
				.transTickToPaper(Array.getDouble(tickValues, 0));
		if (labelOrientation == getAxis().getOrientation()) {
			for (int i = 1; i < labelsSize.length; i++) {
				double ticB = getAxis().transTickToPaper(
						Array.getDouble(tickValues, i * labelInterval));
				double deltaD = Math.abs(ticB - ticA);
				double desity = (labelsSize[i - 1].getWidth() / 2
						+ labelsSize[i].getWidth() / 2 + blankWidth)
						/ deltaD;
				if (maxDesity < desity) {
					maxDesity = desity;
				}
				ticA = ticB;
			}
		} else {
			for (int i = 1; i < labelsSize.length; i++) {
				double ticB = getAxis().transTickToPaper(
						Array.getDouble(tickValues, i * labelInterval));
				double deltaD = Math.abs(ticB - ticA);
				double desity = (labelsSize[i - 1].getHeight() / 2
						+ labelsSize[i].getHeight() / 2 + blankWidth)
						/ deltaD;
				if (maxDesity < desity) {
					maxDesity = desity;
				}
				ticA = ticB;
			}
		}

		return maxDesity;

	}

	/**
	 * Calculate the canonical values of the given values. The canonical values
	 * will be used to produce labels.
	 * 
	 * @param values
	 * @param mod
	 * @return the canonical values
	 */
	private Object getCanonicalValues(Object values) {
		if (values instanceof double[]) {
			double[] result = new double[Array.getLength(values)];
			double[] array = (double[]) values;
			for (int i = 0; i < array.length; i++) {
				result[i] = getAxis().getCanonicalValue(array[i]);
			}
			return result;
		}
		if (values instanceof long[]) {
			long[] result = new long[Array.getLength(values)];
			long[] array = (long[]) values;
			for (int i = 0; i < array.length; i++) {
				result[i] = getAxis().getCanonicalValue(array[i]);
			}
			return result;
		}
		throw new Error();
	}

}
