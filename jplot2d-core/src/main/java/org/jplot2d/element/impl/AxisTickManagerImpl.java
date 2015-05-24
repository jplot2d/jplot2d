/**
 * Copyright 2010-2014 Jingjing Li.
 * <p/>
 * This file is part of jplot2d.
 * <p/>
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element.impl;

import org.jplot2d.axtick.RangeAdvisor;
import org.jplot2d.axtick.TickAlgorithm;
import org.jplot2d.axtick.TickCalculator;
import org.jplot2d.element.AxisTransform;
import org.jplot2d.notice.Notice;
import org.jplot2d.tex.MathElement;
import org.jplot2d.tex.TeXMathUtils;
import org.jplot2d.transform.AxisTickTransform;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.NumberArrayUtils;
import org.jplot2d.util.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.Dimension2D;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.Format;
import java.util.*;
import java.util.List;

/**
 * Manages tick values and labels for axes.
 *
 * @author Jingjing Li
 */
public class AxisTickManagerImpl extends ElementImpl implements AxisTickManagerEx, Cloneable {

    /*
     * Implementation Notes:
     * This class has some internal cache for properties of its axes, and trace the values valid conditions.
     * The calcTicks() can detect if ticks need to be recalculated.
     */

    public static final int AUTO_TICKS_MIN = 4;
    private AxisTransformEx axisTransform;
    private List<AxisEx> axes = new ArrayList<>();
    private boolean autoAdjustNumber = true;
    private int tickNumber = DEFAULT_TICKS_NUMBER;
    private boolean autoInterval = true;
    private double interval;
    private double offset;
    private boolean autoValues = true;
    private Object fixedValues;
    private Object fixedMinorValues;
    private boolean autoMinorNumber = true;
    private int actualMinorNumber;
    private int minorNumber;
    private boolean autoLabelFormat = true;
    private Format labelTextFormat;
    private String labelFormat;
    /**
     * fixed labels correspondent to fixed ticks
     */
    @Nonnull
    private MathElement[] fixedLabels = new MathElement[0];
    private int labelInterval = 1;
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
    private boolean autoAdjustNumberChanged;

    /* =========== xxx Changed =========== */
    private boolean autoIntervalChanged;
    private boolean autoValuesChanged;
    private boolean autoLabelFormatChanged;
    private boolean propNumberChanged;
    /**
     * interval or offset changed
     */
    private boolean intervalChanged;
    /**
     * The minorNumber changed or autoMinorNumber changed
     */
    private boolean minorNumberChanged;
    private boolean labelFormatChanged;
    /**
     * The label font cache for detect font changes
     */
    private Map<AxisEx, AxisStatus> axisStatusMap = new HashMap<>();
    private boolean _labelMaxDensityChanged;
    private boolean _tickAlgorithmChanged;
    private boolean _axisCircularRangeChanged;
    private boolean _trfChanged;

    /**
     * The tick range for calculation
     */
    private Range range;
    private AxisTickTransform tickTransform;
    private NormalTransform axisNormalTransform;
    private Range circularRange;

    public AxisTickManagerImpl() {

    }

    /**
     * Combine the user assigned labels with auto labels.
     *
     * @param fixedLabels   user labels for every major tick, no label interval considered.
     * @param autoLabels    labels for every major tick, no label interval considered.
     * @param labelInterval the label interval
     * @return the final labels
     */
    private static MathElement[] calcLabels(@Nullable MathElement[] fixedLabels, MathElement[] autoLabels, int labelInterval) {
        int fixedLabelsLength = (fixedLabels == null) ? 0 : fixedLabels.length;
        int autoLabelsLength = (autoLabels == null) ? 0 : autoLabels.length;
        int length = (int) Math.floor((double) (autoLabelsLength - 1) / labelInterval) + 1;
        MathElement[] result = new MathElement[length];
        int j = 0;
        for (int i = 0; i < autoLabelsLength; i = i + labelInterval) {
            if (i < fixedLabelsLength && fixedLabels != null && fixedLabels[i] != null) {
                result[j] = fixedLabels[i];
            } else {
                result[j] = autoLabels[i];
            }
            j++;
        }
        return result;
    }

    public String getId() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tick(");
        for (AxisEx axis : axes) {
            sb.append(axis.getShortId()).append(',');
        }
        sb.replace(sb.length() - 1, sb.length(), ")");
        return sb.toString();
    }

    public String getFullId() {
        if (axisTransform != null) {
            int xidx = axisTransform.indexOfTickManager(this);
            return "Tick" + xidx + "." + axisTransform.getFullId();
        } else {
            return "AxisTickManager@" + Integer.toHexString(System.identityHashCode(this));
        }
    }

    public InvokeStep getInvokeStepFormParent() {
        if (axes.size() == 0) {
            return null;
        }

        Method method;
        try {
            method = AxisEx.class.getMethod("getTickManager");
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
        return new InvokeStep(method);
    }

    public AxisEx getParent() {
        return (AxisImpl) parent;
    }

    public ElementEx getPrim() {
        if (axes.size() == 0) {
            return null;
        } else {
            return axes.get(0);
        }
    }

    public void notify(Notice msg) {
        if (getPrim() != null) {
            getPrim().notify(msg);
        }
    }

    public AxisTransformEx getAxisTransform() {
        return axisTransform;
    }

    public void setAxisTransform(AxisTransform rangeManager) {
        if (this.axisTransform != null) {
            this.axisTransform.removeTickManager(this);
        }
        this.axisTransform = (AxisTransformEx) rangeManager;
        if (this.axisTransform != null) {
            this.axisTransform.addTickManager(this);
            updateTickAlgorithm();
        }
    }

    public void transformTypeChanged() {
        updateTickAlgorithm();
    }

    /**
     * update tick algorithm when axis type or tick transform changed.
     */
    private void updateTickAlgorithm() {
        TickAlgorithm ta = axisTransform.getType().getTickAlgorithm(axisTransform.getTransform(), getTickTransform());
        if (ta == null) {
            ta = axisTransform.getType().getTickAlgorithm(axisTransform.getTransform(), null);
        }
        setTickAlgorithm(ta);
    }

    public AxisEx[] getAxes() {
        return axes.toArray(new AxisEx[axes.size()]);
    }

    public void addAxis(AxisEx axis) {
        axes.add(axis);
        if (axes.size() == 1) {
            parent = axes.get(0);
        } else {
            parent = null;
        }
        /*
         * Note: The axis cache info will be put when calcTicks() is called
		 */
        _labelMaxDensityChanged = true;
    }

    public void removeAxis(AxisEx axis) {
        axes.remove(axis);
        if (axes.size() == 1) {
            parent = axes.get(0);
        } else {
            parent = null;
        }
        axisStatusMap.remove(axis);
        _labelMaxDensityChanged = true;
    }

    public AxisTickTransform getTickTransform() {
        return tickTransform;
    }

    public void setTickTransform(AxisTickTransform transform) {
        this.tickTransform = transform;
        _trfChanged = true;
        updateTickAlgorithm();
    }

    public Range getRange() {
        Range range = getAxisTransform().getRange();
        if (tickTransform == null) {
            return range;
        } else {
            double start = tickTransform.transformUser2Tick(range.getStart());
            double end = tickTransform.transformUser2Tick(range.getEnd());
            return new Range.Double(start, end);
        }
    }

    public void setRange(Range range) {
        if (tickTransform == null) {
            getAxisTransform().setRange(range);
        } else {
            double ustart = tickTransform.transformTick2User(range.getStart());
            double uend = tickTransform.transformTick2User(range.getEnd());
            getAxisTransform().setRange(new Range.Double(ustart, uend));
        }
    }

    public boolean isAutoReduceTickNumber() {
        return autoAdjustNumber;
    }

    public void setAutoReduceTickNumber(boolean flag) {
        this.autoAdjustNumber = flag;
        autoAdjustNumberChanged = true;
    }

    public int getTickNumber() {
        return tickNumber;
    }

    public void setTickNumber(int tickNumber) {
        this.tickNumber = tickNumber;
        propNumberChanged = true;
    }

    public boolean isAutoTickInterval() {
        return autoInterval;
    }

    public void setAutoTickInterval(boolean autoInterval) {
        if (this.autoInterval != autoInterval) {
            this.autoInterval = autoInterval;
            autoIntervalChanged = true;
        }
    }

    public double getTickInterval() {
        return interval;
    }

    public void setTickInterval(double interval) {
        if (Double.isNaN(interval) || Double.isInfinite(interval)) {
            throw new IllegalArgumentException("tick interval can not be NaN or Infinite.");
        }
        if (this.interval != interval) {
            this.interval = interval;
            intervalChanged = true;
            setAutoTickInterval(false);
        }

    }

    public double getTickOffset() {
        return offset;
    }

    public void setTickOffset(double offset) {
        if (Double.isNaN(interval) || Double.isInfinite(interval)) {
            throw new IllegalArgumentException("tick offset can not be NaN or Infinite.");
        }
        if (this.offset != offset) {
            this.offset = offset;
            intervalChanged = true;
            setAutoTickInterval(false);
        }
    }

    public boolean isAutoTickValues() {
        return autoValues;
    }

    public void setAutoTickValues(boolean atv) {
        if (this.autoValues != atv) {
            this.autoValues = atv;
            this.autoValuesChanged = true;
        }
    }

    public Object getFixedTickValues() {
        return fixedValues;
    }

    public void setFixedTickValues(Object values) {
        this.fixedValues = values;
        setAutoTickValues(false);
    }

    public Object getFixedMinorTickValues() {
        return fixedMinorValues;
    }

    public void setFixedMinorTickValues(Object minorValues) {
        this.fixedMinorValues = minorValues;
    }

    public boolean isAutoMinorTicks() {
        return autoMinorNumber;
    }

    public void setAutoMinorTicks(boolean flag) {
        if (this.autoMinorNumber != flag) {
            this.autoMinorNumber = flag;
            minorNumberChanged = true;
        }
    }

    public int getMinorTickNumber() {
        return minorNumber;
    }

    public void setMinorTickNumber(int minors) {
        if (this.minorNumber != minors) {
            this.minorNumber = minors;
            minorNumberChanged = true;
            setAutoMinorTicks(false);
        }
    }

    public int getActualMinorTickNumber() {
        return actualMinorNumber;
    }

	/* =========================== Labels ============================= */

    public boolean isAutoLabelFormat() {
        return autoLabelFormat;
    }

    public void setAutoLabelFormat(boolean alf) {
        if (this.autoLabelFormat != alf) {
            this.autoLabelFormat = alf;
            autoLabelFormatChanged = true;
        }
    }

    public Format getLabelTextFormat() {
        return labelTextFormat;
    }

    public void setLabelTextFormat(Format format) {
        this.labelTextFormat = format;
        labelFormatChanged = true;
        setAutoLabelFormat(false);
    }

    public String getLabelFormat() {
        return labelFormat;
    }

    public void setLabelFormat(String format) {
        if (tickCalculator.isValidFormat(format)) {
            this.labelFormat = format;
            labelFormatChanged = true;
            setAutoLabelFormat(false);
        } else {
            throw new IllegalArgumentException("The label format is invalid");
        }
    }

    public String[] getFixedLabelStrings() {
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
    }

    public int getLabelInterval() {
        return labelInterval;
    }

    public void setLabelInterval(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Label interval cannot be 0 or negative.");
        }
        this.labelInterval = n;
    }

    public Object getTickValues() {
        return values;
    }

    public Object getMinorTickValues() {
        return minorValues;
    }

    public MathElement[] getLabelModels() {
        return labels;
    }

    public TickAlgorithm getTickAlgorithm() {
        return tickAlgorithm;
    }

    public void setTickAlgorithm(TickAlgorithm algorithm) {
        if (tickAlgorithm != algorithm) {
            tickAlgorithm = algorithm;
            tickCalculator = tickAlgorithm.createCalculator();
            _tickAlgorithmChanged = true;
        }
    }

    public String[] getLabelStrings() {
        String[] result = new String[labels.length];
        for (int i = 0; i < labels.length; i++) {
            result[i] = TeXMathUtils.toString(labels[i]);
        }
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    public void calcTicks() {

        // detect changes of range
        boolean _rangeChanged = false;
        if (!getRange().equals(this.range)) {
            _rangeChanged = true;
            this.range = getRange();
        }
        if (_tickAlgorithmChanged || _rangeChanged) {
            tickCalculator.setRange(range);
        }

        AxisTransformEx va = getAxisTransform();
        if (!va.getNormalTransform().equals(this.axisNormalTransform)) {
            _trfChanged = true;
            this.axisNormalTransform = va.getNormalTransform();
        }
        if (va.getType().getCircularRange() != this.circularRange) {
            _axisCircularRangeChanged = true;
            this.circularRange = va.getType().getCircularRange();
        }

        for (AxisEx axis : axes) {
            AxisStatus axisStatus = axisStatusMap.get(axis);
            if (axisStatus == null) { // new axis
                axisStatus = new AxisStatus(axis.getLength(), isLabelSameOrientation(axis), axis.getEffectiveFont());
                axisStatusMap.put(axis, axisStatus);
            } else { // re-create axisStatus if changed
                boolean labelSameOrientation = isLabelSameOrientation(axis);
                boolean newStatus = false;
                if (axis.getLength() != axisStatus.axisLength) {
                    _labelMaxDensityChanged = true;
                    newStatus = true;
                }
                if (labelSameOrientation != axisStatus.labelSameOrientation) {
                    _labelMaxDensityChanged = true;
                    newStatus = true;
                }
                if (!axis.getEffectiveFont().equals(axisStatus.labelFont)) {
                    _labelMaxDensityChanged = true;
                    newStatus = true;
                }
                if (newStatus) {
                    axisStatus = new AxisStatus(axis.getLength(), labelSameOrientation, axis.getEffectiveFont());
                    axisStatusMap.put(axis, axisStatus);
                }
            }
        }

        if (autoValues && autoInterval && autoAdjustNumber) {
            if (autoValuesChanged || autoIntervalChanged || autoAdjustNumberChanged || propNumberChanged
                    || minorNumberChanged || autoLabelFormatChanged || labelFormatChanged || _labelMaxDensityChanged
                    || _tickAlgorithmChanged || _trfChanged || _rangeChanged) {
                calcAutoTicks();
            }

            return;
        }

        int[] valueIdxes = null;
        Object values; // only available when (fixedValues != null)
        int[] mvalueIdxes;
        Object mvalues; // only available when (fixedMinorValues != null)

        if (autoValues) {
            if (autoInterval) {
                if (autoValuesChanged || autoIntervalChanged || autoAdjustNumberChanged || propNumberChanged
                        || minorNumberChanged || _tickAlgorithmChanged || _axisCircularRangeChanged || _rangeChanged) {
                    tickCalculator.calcValuesByTickNumber(tickNumber, autoMinorNumber ? -1 : minorNumber);
                    interval = tickCalculator.getInterval();
                    actualMinorNumber = tickCalculator.getMinorNumber();
                }
            } else {
                if (autoValuesChanged || intervalChanged || minorNumberChanged || _tickAlgorithmChanged
                        || _axisCircularRangeChanged || _rangeChanged) {
                    tickCalculator.calcValuesByTickInterval(interval, offset, autoMinorNumber ? -1 : minorNumber);
                    actualMinorNumber = tickCalculator.getMinorNumber();
                }
            }
            values = tickCalculator.getValues();
            mvalues = tickCalculator.getMinorValues();
        } else {
            // filter off the ticks out of range
            if (fixedValues != null) {
                valueIdxes = tickCalculator.getInRangeValuesIdx(fixedValues);
                values = NumberArrayUtils.subArray(fixedValues, valueIdxes);
            } else {
                values = new double[0];
            }
            if (fixedMinorValues != null) {
                mvalueIdxes = tickCalculator.getInRangeValuesIdx(fixedMinorValues);
                mvalues = NumberArrayUtils.subArray(fixedMinorValues, mvalueIdxes);
            } else {
                mvalues = new double[0];
            }
        }

        boolean _valuesChanged = false;
        boolean _minorValuesChanged = false;
        if (!NumberArrayUtils.equals(this.values, values)) {
            this.values = values;
            _valuesChanged = true;
        }
        if (!NumberArrayUtils.equals(this.minorValues, mvalues)) {
            this.minorValues = mvalues;
            _minorValuesChanged = true;
        }

        autoIntervalChanged = autoAdjustNumberChanged = false;
        propNumberChanged = minorNumberChanged = false;
        intervalChanged = false;
        autoValuesChanged = _tickAlgorithmChanged = false;

		/* calculate auto format & labels form values */
        if (autoLabelFormat && (_valuesChanged || autoLabelFormatChanged)) {
            Format atf = tickCalculator.calcLabelTextFormat(getCanonicalValues(values));
            String alf = tickCalculator.calcLabelFormatString(getCanonicalValues(values));
            changeLabelFormat(atf, alf);
        }
        autoLabelFormatChanged = false;

        if (_valuesChanged || labelFormatChanged) {
            autoLabels = calcAutoLabels(getCanonicalValues(values), labelTextFormat, labelFormat);
        }
        labelFormatChanged = false;

		/* apply fixed labels */
        MathElement[] fixedLabelsInRange;
        if (autoValues) {
            fixedLabelsInRange = fixedLabels;
        } else {
            List<MathElement> ul = new ArrayList<>();
            if (valueIdxes != null) {
                for (int i : valueIdxes) {
                    if (i < fixedLabels.length) {
                        ul.add(fixedLabels[i]);
                    }
                }
            }
            fixedLabelsInRange = ul.toArray(new MathElement[ul.size()]);
        }
        MathElement[] labels = calcLabels(fixedLabelsInRange, autoLabels, labelInterval);
        boolean labelsChanged = false;
        if (!Arrays.equals(this.labels, labels)) {
            this.labels = labels;
            labelsChanged = true;
        }

		/* shrink labels */
        if (_valuesChanged || labelsChanged || _labelMaxDensityChanged || _axisCircularRangeChanged || _trfChanged) {
            shrinkLabelFonts();
        }
        _labelMaxDensityChanged = _trfChanged = _axisCircularRangeChanged = false;

        // notify axis redraw since they get tick values and label text from this tick manager
        if (_valuesChanged || _minorValuesChanged || labelsChanged) {
            for (AxisEx axis : axes) {
                ComponentImpl.redraw(axis);
            }
        }
    }

    /**
     * calculate interval from given range, tick number, label height and label format. This will set the interval, the
     * values and the label density.
     */
    private void calcAutoTicks() {

        autoValuesChanged = autoIntervalChanged = autoAdjustNumberChanged = false;
        propNumberChanged = minorNumberChanged = false;
        autoLabelFormatChanged = labelFormatChanged = false;
        _labelMaxDensityChanged = false;
        _tickAlgorithmChanged = _trfChanged = false;

        int tickNumber = this.tickNumber;
        Object values;
        Format labelTextFormat = this.labelTextFormat;
        String labelFormat = this.labelFormat;
        MathElement[] autoLabels;
        MathElement[] labels;
        while (true) {
            tickCalculator.calcValuesByTickNumber(tickNumber, autoMinorNumber ? -1 : minorNumber);
            values = tickCalculator.getValues();
            if (autoLabelFormat) {
                labelTextFormat = tickCalculator.calcLabelTextFormat(getCanonicalValues(values));
                labelFormat = tickCalculator.calcLabelFormatString(getCanonicalValues(values));
            }
            autoLabels = calcAutoLabels(getCanonicalValues(values), labelTextFormat, labelFormat);
            labels = calcLabels(fixedLabels, autoLabels, labelInterval);

            double density = getMaxLabelsDensity(axisNormalTransform, values, labels);
            if (density <= 1) {
                break;
            }
            /* fast approximating, the next tick we should try */
            tickNumber = Math.min(tickNumber, Array.getLength(values)) - 1;
            if (tickNumber < AUTO_TICKS_MIN) {
                break;
            }
        }

        boolean _valuesChanged = false;
        boolean _minorValuesChanged = false;
        if (!NumberArrayUtils.equals(this.values, values)) {
            this.values = values;
            _valuesChanged = true;
        }
        if (!NumberArrayUtils.equals(minorValues, tickCalculator.getMinorValues())) {
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

        if (tickNumber < AUTO_TICKS_MIN) {
            shrinkLabelFonts();
        } else {
            // reset actual label font on all axes
            for (AxisEx axis : axes) {
                axis.setActualFont(axis.getEffectiveFont());
            }
        }

        // notify axis redraw since they get tick values and label text from this tick manager
        if (_valuesChanged || _minorValuesChanged || labelsChanged) {
            for (AxisEx axis : axes) {
                ComponentImpl.redraw(axis);
            }
        }
    }

    private boolean isLabelSameOrientation(AxisEx axis) {
        return (axis.getOrientation() == axis.getLabelOrientation());
    }

    /**
     * Calculate label from values and format.
     */
    private MathElement[] calcAutoLabels(Object values, Format textFormat, String format) {
        if (textFormat == null) {
            return tickCalculator.formatValues(format, values);
        } else {
            return tickCalculator.formatValues(textFormat, values);
        }
    }

    /**
     * Change the label format by the given format.
     *
     * @param textFormat the text format
     * @param format     if the textFormat is <code>null</code>, this parameter must not be null
     */
    private void changeLabelFormat(Format textFormat, String format) {
        if (labelTextFormat != textFormat && (labelTextFormat == null || !labelTextFormat.equals(textFormat))) {
            labelTextFormat = textFormat;
            labelFormatChanged = true;
        }
        //noinspection StringEquality
        if (labelFormat != format && (labelFormat == null || !labelFormat.equals(format))) {
            labelFormat = format;
            labelFormatChanged = true;
        }
    }

    /**
     * Shrink label font to satisfy density on all axes.
     */
    private void shrinkLabelFonts() {
        for (AxisEx axis : axes) {
            AxisStatus axisStatus = axisStatusMap.get(axis);
            Font sf = shrinkLabelFont(axisStatus);
            axis.setActualFont(sf);
        }
    }

    private Font shrinkLabelFont(AxisStatus axisStatus) {
        Font font = axisStatus.labelFont;

        double density = getLabelsDensity(axisNormalTransform, axisStatus.axisLength, values, labels, font, axisStatus.labelSameOrientation);
        if (Double.isInfinite(density)) { // when axis length is zero
            return font;
        }

        double oldDensity = density;
        int count = 0;
        while (density > 1) {
            font = font.deriveFont((float) (font.getSize2D() / ((density > 1.1) ? density : 1.1)));
            density = getLabelsDensity(axisNormalTransform, axisStatus.axisLength, values, labels, font, axisStatus.labelSameOrientation);

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
    private double getMaxLabelsDensity(NormalTransform nxf, Object tickValues, MathElement[] labels) {
        double maxDensity = 0;
        for (AxisEx axis : axes) {
            double density = getLabelsDensity(nxf, axis.getLength(), tickValues, labels, axis.getEffectiveFont(),
                    isLabelSameOrientation(axis));
            if (maxDensity < density) {
                maxDensity = density;
            }
        }
        return maxDensity;
    }

    /**
     * Calculate label density by the given values. density > 1 means labels overlapped.
     *
     * @param nxf                  the transform of AxisTransform
     * @param axisLength           the paper length
     * @param tickValues           the tick values
     * @param labels               the labels
     * @param labelFont            the label font
     * @param labelSameOrientation <code>true</code> if the label orientation is same as axis
     * @return the label density
     */
    private double getLabelsDensity(NormalTransform nxf, double axisLength, Object tickValues, MathElement[] labels,
                                    Font labelFont, boolean labelSameOrientation) {

        if (Array.getLength(tickValues) == 0) {
            return 0;
        }

        Dimension2D[] labelsSize = AxisImpl.getLabelsPaperSize(labels, labelFont);
        /* the space between 2 neighbor labels */
        double blankWidth = labelsSize[0].getHeight() / 2;

		/* Note: when scale is very small, deltaD will be zero */
        double maxDensity = 0;
        double ticA = transTickToPaper(nxf, axisLength, Array.getDouble(tickValues, 0));
        if (labelSameOrientation) {
            for (int i = 1; i < labelsSize.length; i++) {
                double ticB = transTickToPaper(nxf, axisLength, Array.getDouble(tickValues, i * labelInterval));
                double deltaD = Math.abs(ticB - ticA);
                double density = (labelsSize[i - 1].getWidth() / 2 + labelsSize[i].getWidth() / 2 + blankWidth) / deltaD;
                if (maxDensity < density) {
                    maxDensity = density;
                }
                ticA = ticB;
            }
        } else {
            for (int i = 1; i < labelsSize.length; i++) {
                double ticB = transTickToPaper(nxf, axisLength, Array.getDouble(tickValues, i * labelInterval));
                double deltaD = Math.abs(ticB - ticA);
                double desity = (labelsSize[i - 1].getHeight() / 2 + labelsSize[i].getHeight() / 2 + blankWidth)
                        / deltaD;
                if (maxDensity < desity) {
                    maxDensity = desity;
                }
                ticA = ticB;
            }
        }

        return maxDensity;

    }

    /**
     * Transform tick value to paper value on this axis.
     *
     * @param nxf        the NormalTransform of this axis tick manager
     * @param axisLength tick value
     * @param tickValue  the tick value
     * @return paper value
     */
    private double transTickToPaper(NormalTransform nxf, double axisLength, double tickValue) {
        double uv;
        if (tickTransform != null) {
            uv = tickTransform.transformTick2User(tickValue);
        } else {
            uv = tickValue;
        }
        return nxf.convToNR(uv) * axisLength;
    }

    /**
     * Calculate the canonical values of the given values. The canonical values will be used to produce labels.
     *
     * @param values the tick values
     * @return the canonical values
     */
    private Object getCanonicalValues(Object values) {
        if (values instanceof double[]) {
            double[] result = new double[Array.getLength(values)];
            double[] array = (double[]) values;
            for (int i = 0; i < array.length; i++) {
                result[i] = getCanonicalValue(array[i]);
            }
            return result;
        }
        if (values instanceof long[]) {
            long[] result = new long[Array.getLength(values)];
            long[] array = (long[]) values;
            for (int i = 0; i < array.length; i++) {
                result[i] = getCanonicalValue(array[i]);
            }
            return result;
        }
        return null;
    }

    private double getCanonicalValue(double d) {
        if (circularRange == null) {
            return d;
        }

        double uv;
        if (tickTransform == null) {
            uv = d;
        } else {
            uv = tickTransform.transformTick2User(d);
        }
        double canon = uv % circularRange.getSpan() + circularRange.getMin();
        if (tickTransform == null) {
            return canon;
        } else {
            return tickTransform.transformUser2Tick(canon);
        }
    }

    private long getCanonicalValue(long d) {
        if (circularRange == null) {
            return d;
        }

        double uv;
        if (tickTransform == null) {
            uv = d;
        } else {
            uv = tickTransform.transformTick2User(d);
        }
        double canon = uv % circularRange.getSpan() + circularRange.getMin();
        if (tickTransform == null) {
            return (long) canon;
        } else {
            return (long) tickTransform.transformUser2Tick(canon);
        }
    }

    public Range expandRangeToTick(TransformType txfType, Range range) {
        RangeAdvisor rav = (RangeAdvisor) tickCalculator;
        if (autoValues) {
            if (autoInterval) {
                if (autoAdjustNumber) {
                    return expandRangeToAutoAdjustedTick(txfType, range);
                } else {
                    rav.setRange(range);
                    rav.expandRangeByTickNumber(tickNumber);
                    return rav.getRange();
                }
            } else {
                rav.setRange(range);
                rav.expandRangeByTickInterval(interval);
                return rav.getRange();
            }
        }
        return range;
    }

    /**
     * expand range on autoAdjustNumber axis.
     */
    private Range expandRangeToAutoAdjustedTick(TransformType txfType, Range range) {

        RangeAdvisor rav = (RangeAdvisor) tickCalculator;
        int tickNumber = this.tickNumber;
        while (true) {
            rav.setRange(range);
            rav.expandRangeByTickNumber(tickNumber);
            Range r = rav.getRange();

            tickCalculator.calcValuesByTickInterval(rav.getInterval(), 0, 0);
            Object values = tickCalculator.getValues();
            Format labelTextFormat;
            String labelFormat;
            if (autoLabelFormat) {
                labelTextFormat = tickCalculator.calcLabelTextFormat(getCanonicalValues(values));
                labelFormat = tickCalculator.calcLabelFormatString(getCanonicalValues(values));
            } else {
                labelTextFormat = this.labelTextFormat;
                labelFormat = this.labelFormat;
            }
            MathElement[] autoLabels = calcAutoLabels(getCanonicalValues(values), labelTextFormat, labelFormat);
            MathElement[] labels = calcLabels(null, autoLabels, labelInterval);

            NormalTransform trf = txfType.createNormalTransform(r);

            // calculate max desity
            double maxDensity = getMaxLabelsDensity(trf, values, labels);
            if (maxDensity > 1) {
                /* fast approximating, the next tick we should try */
                tickNumber = Math.min(tickNumber, Array.getLength(values)) - 1;
                if (tickNumber >= AUTO_TICKS_MIN) {
                    continue;
                }
            }
            return r;
        }
    }

    @Override
    public AxisTickManagerImpl copyStructure(@Nonnull Map<ElementEx, ElementEx> orig2copyMap) {
        AxisTickManagerImpl result = null;
        try {
            result = (AxisTickManagerImpl) this.clone();
        } catch (CloneNotSupportedException e) {
            // impossible
        }
        assert result != null;
        result.axes = new ArrayList<>();
        result.axisStatusMap = new HashMap<>();

        orig2copyMap.put(this, result);

        // copy or link range manager
        AxisTransformEx axisTransformCopy = (AxisTransformEx) orig2copyMap.get(axisTransform);
        if (axisTransformCopy == null) {
            axisTransformCopy = (AxisTransformEx) axisTransform.copyStructure(orig2copyMap);
        }
        result.axisTransform = axisTransformCopy;
        axisTransformCopy.addTickManager(result);

        return result;
    }

    /**
     * An internal cache for status of an axis managed by this tick manager
     */
    private static class AxisStatus {
        private final double axisLength;
        private final boolean labelSameOrientation;
        private final Font labelFont;

        public AxisStatus(double length, boolean labelSameOrientation, Font font) {
            this.axisLength = length;
            this.labelSameOrientation = labelSameOrientation;
            this.labelFont = font;
        }

    }

}
