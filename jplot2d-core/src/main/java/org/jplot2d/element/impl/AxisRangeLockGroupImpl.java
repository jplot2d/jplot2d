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

import org.jplot2d.notice.Notice;
import org.jplot2d.notice.RangeAdjustedToValueBoundsNotice;
import org.jplot2d.notice.RangeSelectionNotice;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.util.Range;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AxisRangeLockGroupImpl extends ElementImpl implements AxisRangeLockGroupEx {

    private static final Range NORM_PHYSICAL_RANGE = new Range.Double(0.0, 1.0);

    private static final Range INFINITY_PHYSICAL_RANGE = new Range.Double(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    private AxisTransformEx prim;

    private boolean autoRange = true;

    private boolean zoomable = true;

    private final List<AxisTransformEx> arms = new ArrayList<>();

    private boolean autoRangeNeeded = true;

    public String getId() {
        return "LockGroup@" + Integer.toHexString(System.identityHashCode(this));
    }

    public String getFullId() {
        return "AxisRangeLockGroup@" + Integer.toHexString(System.identityHashCode(this));
    }

    public InvokeStep getInvokeStepFormParent() {
        if (arms.size() == 0) {
            return null;
        }

        Method method;
        try {
            method = AxisTransformEx.class.getMethod("getLockGroup");
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
        return new InvokeStep(method);
    }

    public AxisTransformEx getParent() {
        return (AxisTransformEx) super.getParent();
    }

    public ElementEx getPrim() {
        return prim;
    }

    public void notify(Notice msg) {
        if (getPrim() != null) {
            getPrim().notify(msg);
        }
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        AxisRangeLockGroupImpl alg = (AxisRangeLockGroupImpl) src;
        this.autoRange = alg.autoRange;
        this.zoomable = alg.zoomable;
        this.autoRangeNeeded = alg.autoRangeNeeded;
    }

    public int indexOfAxisTransform(AxisTransformEx axisTransform) {
        return arms.indexOf(axisTransform);
    }

    @Nonnull
    public AxisTransformEx[] getAxisTransforms() {
        return arms.toArray(new AxisTransformEx[arms.size()]);
    }

    public void addAxisTransform(AxisTransformEx axis) {
        arms.add(axis);

        if (arms.size() == 1) {
            parent = axis;
            prim = axis;
        } else {
            parent = null;
            autoRange = false;
        }
    }

    public void removeAxisTransform(AxisTransformEx axis) {
        arms.remove(axis);

        autoRange = false;

        if (arms.size() == 0) {
            parent = null;
            prim = null;
        } else if (arms.size() == 1) {
            parent = arms.get(0);
            prim = arms.get(0);
        } else {
            parent = null;

			/* find a new primary axis */
            if (prim == axis) {
                prim = arms.get(0);
            }
        }

    }

    public AxisTransformEx getPrimaryAxis() {
        return prim;
    }

    public boolean isAutoRange() {
        return autoRange;
    }

    public void setAutoRange(boolean autoRange) {
        this.autoRange = autoRange;
        if (autoRange) {
            autoRangeNeeded = true;
        }
    }

    public boolean isZoomable() {
        return this.zoomable;
    }

    public void setZoomable(boolean zoomable) {
        this.zoomable = zoomable;
    }

    public void reAutoRange() {
        autoRange = true;
        autoRangeNeeded = true;
    }

    public boolean calcAutoRange() {
        if (autoRange && autoRangeNeeded) {
            autoRange();
            autoRangeNeeded = false;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Execute a global auto-range on the given axes locking group.
     */
    private void autoRange() {

        Map<AxisTransformEx, NormalTransform> vtMap = AxisRangeUtils.createVirtualTransformMap(arms);

        RangeStatus<Boolean> pRange = AxisRangeUtils.calcNiceVirtualRange(vtMap);

        if (pRange == null) {
            /* all axis is LOG and contain no positive data, do nothing */

            notify(new RangeSelectionNotice("Axes [" + getAxesShortId()
                    + "] contain no valid data, auto range ignored."));
            return;
        }

        if (pRange.getStatus()) {
            notify(new RangeAdjustedToValueBoundsNotice("AutoRange only on valid data of axes."));
            /* The detailed info can be gotten from pLogRange.getStatus() */
        }

		/*
         * zoomPhysicalRange(pLo, pHi, axes) will choose a proper range when range == 0
		 */
        RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(pRange, vtMap);

        if (rs.getStatus() != null) {
			/* the PrecisionException should not be thrown from autoRange(). */
            notify(new RangeSelectionNotice(rs.getStatus().getMessage()));
        }

		/* extend master range to tick */
        AxisTransformEx master = getPrimaryAxis();
        Range extRange;
        if (master.isAutoMargin()) {
            Range ur = vtMap.get(master).convFromNR(rs);
            Range exur = master.expandRangeToTick(ur);
            extRange = vtMap.get(master).convToNR(exur);
        } else {
            extRange = rs;
        }

        RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(extRange, vtMap);
        if (xrs.getStatus() != null) {
            notify(new RangeSelectionNotice(xrs.getStatus().getMessage()));
        }
        zoomVirtualRange(xrs, vtMap);

		/*
		 * After zoomPhysicalRange(), autoRange is set to false, re-set to true.
		 */
        autoRange = true;

    }

    public void zoomVirtualRange(Range range, Map<AxisTransformEx, NormalTransform> vtMap) {
        for (AxisTransformEx arm : arms) {
            NormalTransform vt = vtMap.get(arm).zoom(range);
            Range wrange = vt.getValueRange();
            if (!arm.getRange().equals(wrange)) {
                arm.setNormalTransform(arm.getTransform().createNormalTransform(wrange));
            }
        }

        autoRange = false;
    }

    public void zoomRange(double start, double end) {
        Range range = new Range.Double(start, end);

        Range validRange = AxisRangeUtils.validateNormalRange(range, arms, false);
        if (!validRange.equals(range)) {
            notify(new RangeAdjustedToValueBoundsNotice("Range exceed valid boundary, has been adjusted."));
        }

        RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(validRange, arms);
        if (rs.getStatus() != null) {
            notify(new RangeSelectionNotice(rs.getStatus().getMessage()));
        }
        RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(rs, arms);
        if (xrs.getStatus() != null) {
            notify(new RangeSelectionNotice(xrs.getStatus().getMessage()));
        }

        zoomNormalRange(xrs);
    }

    public void zoomNormalRange(Range npRange) {
        for (AxisTransformEx axis : arms) {
            NormalTransform npt = axis.getNormalTransform().zoom(npRange);
            axis.setNormalTransform(npt);
        }

        autoRange = false;
    }

    public void validateAxesRange() {
		/* find nice range */
        Range pRange = AxisRangeUtils.validateNormalRange(NORM_PHYSICAL_RANGE, arms, true);
		/*
		 * if the current normalized range contains no valid data, find valid data at all range (like auto range)
		 */
        if (pRange == null) {
            pRange = AxisRangeUtils.validateNormalRange(INFINITY_PHYSICAL_RANGE, arms, true);
        }

        if (pRange == null) {
			/*
			 * if no locked axes contains valid value, put them in the default world range
			 */
            for (AxisTransformEx ax : arms) {
                Range defaultWRange = ax.getType().getDefaultWorldRange(ax.getTransform());
                Range nwr = (ax.isInverted()) ? defaultWRange.invert() : defaultWRange;
                ax.setNormalTransform(ax.getTransform().createNormalTransform(nwr));
            }

            notify(new RangeAdjustedToValueBoundsNotice("All axes contain no valid data, range set to default range."));
            return;
        }

        if (pRange.getSpan() == 0) {
			/* if only one valid data point */
            for (AxisTransformEx ax : arms) {
                Range defaultWRange = ax.getType().getDefaultWorldRange(ax.getTransform());
                Range nwr = (ax.isInverted()) ? defaultWRange.invert() : defaultWRange;
                ax.setNormalTransform(ax.getTransform().createNormalTransform(nwr));
            }

            Range pr = AxisRangeUtils.validateNormalRange(INFINITY_PHYSICAL_RANGE, arms, true);
            RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(pr, arms);
            Range ur = prim.getNormalTransform().convFromNR(rs);
            Range exur = prim.expandRangeToTick(ur);
            Range expr = prim.getNormalTransform().convToNR(exur);

            RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(expr, arms);
            zoomNormalRange(xrs);

            notify(new RangeSelectionNotice("The axis contains only one valid date."));
            return;
        }

        for (AxisTransformEx ax : arms) {
            Range wr = ax.getNormalTransform().convFromNR(pRange);
            ax.setNormalTransform(ax.getTransform().createNormalTransform(wr));
        }

        if (isAutoRange()) {
            reAutoRange();
        } else {
            AxisTransformEx coreAxis = null;
            Range coreRange = null;
            for (AxisTransformEx ax : arms) {
                if (ax.getCoreRange() != null) {
                    coreAxis = ax;
                    coreRange = ax.getCoreRange();
                    break;
                }
            }
            if (coreAxis != null) {
                coreAxis.setCoreRange(coreRange);
            } else {
                if (!pRange.equals(NORM_PHYSICAL_RANGE)) {
                    notify(new RangeAdjustedToValueBoundsNotice("Some axes contain invalid value, range adjusted."));
                }

                RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(NORM_PHYSICAL_RANGE, arms);
                if (rs.getStatus() != null) {
                    notify(new RangeSelectionNotice(rs.getStatus().getMessage()));
                }
                RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(rs, arms);
                if (xrs.getStatus() != null) {
                    notify(new RangeSelectionNotice(xrs.getStatus().getMessage()));
                }

                zoomNormalRange(xrs);
            }
        }

    }

    /**
     * Returns short id of axes managed by this lock group.
     */
    private String getAxesShortId() {
        StringBuilder sb = new StringBuilder();
        for (AxisTransformEx rm : arms) {
            for (AxisTickManagerEx tm : rm.getTickManagers()) {
                for (AxisEx axis : tm.getAxes()) {
                    sb.append(axis.getShortId());
                    sb.append(", ");
                }
            }
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }
}
