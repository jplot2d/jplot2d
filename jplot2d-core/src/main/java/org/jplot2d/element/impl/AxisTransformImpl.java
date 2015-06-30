/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element.impl;

import org.jplot2d.axtype.AxisType;
import org.jplot2d.element.AxisRangeLockGroup;
import org.jplot2d.notice.Notice;
import org.jplot2d.notice.RangeAdjustedToValueBoundsNotice;
import org.jplot2d.notice.RangeSelectionNotice;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AxisTransformImpl extends ElementImpl implements AxisTransformEx {

    /**
     * The default margin factor (used for both lower and upper margins)
     */
    public static final double DEFAULT_MARGIN_FACTOR = 1.0 / 32; // 0.03125

    protected final List<AxisTickManagerEx> tickManagers = new ArrayList<>();
    private final List<LayerEx> layers = new ArrayList<>();
    @Nonnull
    private AxisType type;
    @Nonnull
    private TransformType txfType;
    private boolean autoMargin = true;
    private double marginFactor = DEFAULT_MARGIN_FACTOR;
    @Nullable
    private Range coreRange;
    @Nonnull
    private NormalTransform ntf;
    @Nullable
    private AxisRangeLockGroupEx group;

    public AxisTransformImpl() {
        super();

        type = AxisType.NUMBER;
        txfType = type.getDefaultTransformType();
        ntf = txfType.createNormalTransform(type.getDefaultWorldRange(txfType));
    }

    public String getId() {
        StringBuilder sb = new StringBuilder();
        sb.append("AxisTransform");
        for (AxisTickManagerEx tick : tickManagers) {
            sb.append("(");
            for (AxisEx axis : tick.getAxes()) {
                sb.append(axis.getShortId()).append(',');
            }
            sb.replace(sb.length() - 1, sb.length(), ")");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")(");
        for (LayerEx layer : layers) {
            sb.append(layer.getShortId()).append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    public String getFullId() {
        if (group != null) {
            int xidx = group.indexOfAxisTransform(this);
            return "AxisTransform" + xidx + "." + group.getFullId();
        } else {
            return "AxisTransform@" + Integer.toHexString(System.identityHashCode(this));
        }
    }

    public InvokeStep getInvokeStepFormParent() {
        if (tickManagers.size() == 0) {
            return null;
        }

        Method method;
        try {
            method = AxisTickManagerEx.class.getMethod("getAxisTransform");
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
        return new InvokeStep(method);
    }

    @Nullable
    public AxisTickManagerEx getParent() {
        return (AxisTickManagerEx) super.getParent();
    }

    public ElementEx getPrim() {
        if (tickManagers.size() == 0) {
            return null;
        } else {
            return tickManagers.get(0);
        }
    }

    public void notify(Notice msg) {
        if (getPrim() != null) {
            getPrim().notify(msg);
        }
    }

    public boolean isInverted() {
        return ntf.isInverted();
    }

    public void setInverted(boolean flag) {
        if (ntf.isInverted() != flag) {
            setNormalTransform(ntf.invert());
        }
    }

    public boolean isAutoMargin() {
        return autoMargin;
    }

    public void setAutoMargin(boolean autoMargin) {
        this.autoMargin = autoMargin;

        if (group != null && group.isAutoRange() && group.getPrimaryAxis() == this) {
            group.reAutoRange();
        } else if (coreRange != null) {
            Range irng = coreRange.copy();
            setRange(coreRange, true);
            coreRange = irng;
        }
    }

    public double getMarginFactor() {
        return marginFactor;
    }

    public void setMarginFactor(double factor) {
        marginFactor = factor;

        if (group != null && group.isAutoRange() && group.getPrimaryAxis() == this) {
            group.reAutoRange();
        } else if (coreRange != null) {
            Range irng = coreRange.copy();
            setRange(coreRange, true);
            coreRange = irng;
        }
    }

    @Nonnull
    public AxisType getType() {
        return type;
    }

    public void setType(@Nonnull AxisType type) {
        if (group != null && group.getAxisTransforms().length > 1) {
            throw new IllegalStateException(
                    "The axis type can only be changed when the axis doses not lock with other axes.");
        }

        this.type = type;
        if (!type.canSupport(txfType)) {
            txfType = type.getDefaultTransformType();
        }

        for (AxisTickManagerEx atm : tickManagers) {
            atm.transformTypeChanged();
        }

        if (group != null) {
            group.validateAxesRange();
        }
    }

    @Nonnull
    public TransformType getTransform() {
        return txfType;
    }

    public void setTransform(@Nonnull TransformType txfType) {
        if (group != null && group.getAxisTransforms().length > 1) {
            throw new IllegalStateException(
                    "The axis type can only be changed when the axis doses not lock with other axes.");
        }

        this.txfType = txfType;

        for (AxisTickManagerEx atm : tickManagers) {
            atm.transformTypeChanged();
        }

        if (group != null) {
            group.validateAxesRange();
        }
    }

    @Nullable
    public AxisRangeLockGroupEx getLockGroup() {
        return group;
    }

    public void setLockGroup(@Nullable AxisRangeLockGroup group) {
        if (this.group != null) {
            this.group.removeAxisTransform(this);
        }
        this.group = (AxisRangeLockGroupEx) group;
        if (this.group != null) {
            this.group.addAxisTransform(this);
        }
    }

    @Nonnull
    public NormalTransform getNormalTransform() {
        return ntf;
    }

    public void setNormalTransform(@Nonnull NormalTransform ntf) {
        if (this.ntf.equals(ntf)) {
            return;
        }

        boolean sameRange = this.ntf.equals(ntf.invert());

        this.ntf = ntf;

        if (!sameRange) {
            for (LayerEx layer : layers) {
                if (layer.getXAxisTransform() == this) {
                    if (layer.getYAxisTransform() != null) {
                        AxisRangeLockGroupEx orth = layer.getYAxisTransform().getLockGroup();
                        if (orth != null && orth.isAutoRange()) {
                            orth.reAutoRange();
                        }
                    }
                } else if (layer.getYAxisTransform() == this) {
                    if (layer.getXAxisTransform() != null) {
                        AxisRangeLockGroupEx orth = layer.getXAxisTransform().getLockGroup();
                        if (orth != null && orth.isAutoRange()) {
                            orth.reAutoRange();
                        }
                    }
                }
            }
        }

        for (LayerEx layer : layers) {
            layer.transformChanged();
        }
    }

    @Nonnull
    public AxisTickManagerEx[] getTickManagers() {
        return tickManagers.toArray(new AxisTickManagerEx[tickManagers.size()]);
    }

    public void addTickManager(AxisTickManagerEx tickManager) {
        tickManagers.add(tickManager);
        if (tickManagers.size() == 1) {
            parent = tickManagers.get(0);
        } else {
            parent = null;
        }
    }

    public void removeTickManager(AxisTickManagerEx tickManager) {
        tickManagers.remove(tickManager);
        if (tickManagers.size() == 1) {
            parent = tickManagers.get(0);
        } else {
            parent = null;
        }
    }

    public int indexOfTickManager(AxisTickManagerEx tickManager) {
        return tickManagers.indexOf(tickManager);
    }

    @Nonnull
    public LayerEx[] getLayers() {
        return layers.toArray(new LayerEx[layers.size()]);
    }

    public void addLayer(LayerEx layer) {
        layers.add(layer);
        if (group != null && group.isAutoRange()) {
            for (GraphEx graph : layer.getGraphs()) {
                if (graph.isVisible()) {
                    group.reAutoRange();
                    break;
                }
            }
        }
    }

    public void removeLayer(LayerEx layer) {
        layers.remove(layer);
        if (group != null && group.isAutoRange()) {
            for (GraphEx graph : layer.getGraphs()) {
                if (graph.isVisible()) {
                    group.reAutoRange();
                    break;
                }
            }
        }
    }

    public void linkLayer(LayerEx layer) {
        layers.add(layer);
    }

    @Nullable
    public Range getCoreRange() {
        return coreRange;
    }

    public void setCoreRange(@Nullable Range crange) {
        if (crange == null) {
            coreRange = null;
        } else {
            if (crange.isInverted() != ntf.isInverted()) {
                crange = crange.invert();
            }
            if (!crange.equals(coreRange)) {
                coreRange = crange;
                setRange(coreRange, true);
            }
        }
    }

    @Nonnull
    public Range getRange() {
        return ntf.getValueRange();
    }

    public void setRange(@Nullable Range urange) {
        if (urange == null) {
            throw new IllegalArgumentException("Range cannot be null.");
        }
        if (urange.isInverted() != ntf.isInverted()) {
            urange = urange.invert();
        }
        if (!urange.equals(getRange())) {
            setRange(urange, false);
        }
    }

    /**
     * Notice after this method, the _coreRange will be reset to null
     *
     * @param urange       the range to be set
     * @param appendMargin true to indicate a margin should be appended to the given range, to derive a actual range.
     */
    private void setRange(@Nonnull Range urange, boolean appendMargin) {

        if (Double.isNaN(urange.getStart()) || Double.isNaN(urange.getEnd())) {
            throw new IllegalArgumentException("Range cannot start or end at NaN.");
        }

        AxisTransformEx[] axfs;
        if (group != null) {
            axfs = group.getAxisTransforms();
        } else {
            axfs = new AxisTransformEx[]{this};
        }
        Map<AxisTransformEx, NormalTransform> vtMap = AxisRangeUtils.createVirtualTransformMap(Arrays.asList(axfs));

        NormalTransform vnt = vtMap.get(this);
        Range pr = vnt.convToNR(urange);

        if (appendMargin) {
            double span = pr.getSpan();
            double mpStart = pr.getStart() - span * getMarginFactor();
            double mpEnd = pr.getEnd() + span * getMarginFactor();
            pr = new Range.Double(mpStart, mpEnd);
        }
        Range pRange = AxisRangeUtils.validateNormalRange(pr, vtMap, false);
        if (pRange == null) {
            // no intersect at all
            throw new IllegalArgumentException(getFullId() + ": The given range is not valid.");
        }

        double pLo = pRange.getMin();
        double pHi = pRange.getMax();

        if (!pRange.equals(pr)) {
            notify(new RangeAdjustedToValueBoundsNotice(getFullId()
                    + ": the given range contains invalid value, range adjusted to [" + ntf.convFromNR(pLo) + ", "
                    + ntf.convFromNR(pHi) + "]"));
        }

		/* ensurePrecision */
        RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(pRange, vtMap);
        if (rs.getStatus() != null) {
            notify(new RangeSelectionNotice(rs.getStatus().getMessage()));
        }

		/* extend range to tick */
        Range extRange;
        if (appendMargin && isAutoMargin()) {
            Range ur = vnt.convFromNR(rs);
            Range exur = expandRangeToTick(ur);
            extRange = vnt.convToNR(exur);
        } else {
            extRange = rs;
        }

        RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(extRange, vtMap);
        if (xrs.getStatus() != null) {
            notify(new RangeSelectionNotice(xrs.getStatus().getMessage()));
        }

        if (group != null) {
            group.zoomVirtualRange(xrs, vtMap);
        } else {
            NormalTransform vt = vtMap.get(this).zoom(xrs);
            Range wrange = vt.getValueRange();
            if (!getRange().equals(wrange)) {
                setNormalTransform(getTransform().createNormalTransform(wrange));
            }
        }

    }

    public Range expandRangeToTick(Range ur) {
        if (tickManagers.size() > 0) {
            return tickManagers.get(0).expandRangeToTick(getTransform(), ur);
        }
        return null;
    }

    @Override
    public AxisTransformEx copyStructure(@Nonnull Map<ElementEx, ElementEx> orig2copyMap) {
        AxisTransformImpl result = new AxisTransformImpl();

        orig2copyMap.put(this, result);

        // copy or link lock group
        if (group != null) {
            AxisRangeLockGroupEx algCopy = (AxisRangeLockGroupEx) orig2copyMap.get(group);
            if (algCopy == null) {
                algCopy = (AxisRangeLockGroupEx) group.copyStructure(orig2copyMap);
            }
            result.group = algCopy;
            algCopy.addAxisTransform(result);
        }

        return result;
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        AxisTransformImpl arm = (AxisTransformImpl) src;
        this.type = arm.type;
        this.txfType = arm.txfType;
        this.autoMargin = arm.autoMargin;
        this.marginFactor = arm.marginFactor;
        this.coreRange = arm.coreRange;
        this.ntf = arm.ntf;
    }

}
