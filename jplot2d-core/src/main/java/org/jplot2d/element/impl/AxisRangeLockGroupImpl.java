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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jplot2d.data.Graph;
import org.jplot2d.notice.RangeAdjustedToValueBoundsNotice;
import org.jplot2d.notice.RangeSelectionNotice;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;

public class AxisRangeLockGroupImpl extends ElementImpl implements AxisRangeLockGroupEx {

	private static Range NORM_PHYSICAL_RANGE = new Range.Double(0.0, 1.0);

	private static Range INFINITY_PHYSICAL_RANGE = new Range.Double(Double.NEGATIVE_INFINITY,
			Double.POSITIVE_INFINITY);

	/**
	 * The unitive axis type. can be null is this lock group contains more than one axis type
	 */
	private TransformType type;

	private AxisTransformEx prim;

	private boolean autoRange = true;

	private List<AxisTransformEx> arms = new ArrayList<AxisTransformEx>();

	private boolean autoRangeNeeded = true;

	public String getId() {
		return "LockGroup@" + Integer.toHexString(System.identityHashCode(this));
	}

	public String getShortId() {
		return getFullId();
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

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		AxisRangeLockGroupImpl alg = (AxisRangeLockGroupImpl) src;
		this.type = alg.type;
		this.prim = alg.prim;
		this.autoRange = alg.autoRange;
		this.autoRangeNeeded = alg.autoRangeNeeded;
	}

	public int indexOfRangeManager(AxisTransformEx rangeManager) {
		return arms.indexOf(rangeManager);
	}

	public AxisTransformEx[] getRangeManagers() {
		return arms.toArray(new AxisTransformEx[arms.size()]);
	}

	public void addRangeManager(AxisTransformEx axis) {
		arms.add(axis);
		if (arms.size() == 1) {
			parent = axis;
			prim = axis;
			type = axis.getType();
		} else {
			parent = null;
		}

		if (type != axis.getType()) {
			type = null;
		}

	}

	public void removeRangeManager(AxisTransformEx axis) {
		arms.remove(axis);

		autoRange = false;

		if (arms.size() == 0) {
			parent = null;
			prim = null;
			type = null;
		} else if (arms.size() == 1) {
			parent = arms.get(0);
			prim = arms.get(0);
			type = prim.getType();
		} else {
			parent = null;

			/* find a new primary axis */
			if (prim == axis) {
				prim = arms.get(0);
			}

			// try to find unique type
			if (type == null) {
				TransformType utype = null;
				for (AxisTransformEx ax : arms) {
					if (utype == null) {
						utype = ax.getType();
					} else {
						if (utype != ax.getType()) {
							utype = null;
							break;
						}
					}
				}
				type = utype;
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
	}

	public void reAutoRange() {
		autoRange = true;
		autoRangeNeeded = true;
	}

	public void calcAutoRange() {
		if (autoRange && autoRangeNeeded) {
			autoRange();
			autoRangeNeeded = false;
		}
	}

	/**
	 * Execute a global autorange on the given axes locking group.
	 * 
	 * @param arms
	 *            the axes that global auto-range take place
	 */
	private void autoRange() {

		Map<AxisTransformEx, NormalTransform> vtMap = AxisRangeUtils
				.createVirtualTransformMap(arms);

		RangeStatus<Boolean> pRange = calcNiceVirtualRange(vtMap);

		if (pRange == null) {
			/* all axis is LOG and contain no positive data, do nothing */

			notify(new RangeSelectionNotice("Axes [" + getAxesShortId()
					+ "] contain no valide data, auto range ignored."));
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
			Range ur = vtMap.get(master).getTransU(rs);
			Range exur = master.expandRangeToTick(ur);
			extRange = vtMap.get(master).getTransP(exur);
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

	/**
	 * Find a nice norm-physical range to make sure all corresponding user range only contain valid
	 * values.
	 * <ul>
	 * <li>If any corresponding world range of layer span invalid value, the physical end of given
	 * range is adjusted inward to match the data that most close to boundary.</li>
	 * <li>If there is no valid data inside the given range, <code>null</code> will be returned.</li>
	 * <li>No margin added to the result.</li>
	 * </ul>
	 * 
	 * @return a RangeStatus contain the nice norm-physical range. The Boolean status to indicate if
	 *         there are some data points outside the value bounds
	 */
	private RangeStatus<Boolean> calcNiceVirtualRange(Map<AxisTransformEx, NormalTransform> vtMap) {

		/* find the physical intersected range of valid world range among layers */
		Range pbnds = new Range.Double(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		for (AxisTransformEx ax : arms) {
			Range aprange = vtMap.get(ax).getTransP(ax.getAxisType().getBoundary(ax.getType()));
			pbnds = pbnds.intersect(aprange);
		}

		if (pbnds == null) {
			/* The given range on all layers are outside the boundary. */
			return null;
		}

		Range padRange = null;
		boolean dataOutsideBounds = false;
		for (AxisTransformEx ax : arms) {
			for (LayerEx layer : ax.getLayers()) {
				Range urange = vtMap.get(ax).getTransU(pbnds);
				Range wDRange = new Range.Double();

				if (layer.getXAxisTransform() == ax) {
					boolean yar = layer.getYAxisTransform().getLockGroup().isAutoRange();
					Range yRange = (yar) ? layer.getYAxisTransform().getAxisType()
							.getBoundary(ax.getType()) : layer.getYAxisTransform().getRange();
					for (GraphPlotterEx dp : layer.getGraphPlotters()) {
						Graph dataInBounds = dp.getGraph().setBoundary(urange, yRange);
						wDRange = dataInBounds.getXRange().union(wDRange);
						if (dataInBounds.hasPointOutsideXBounds()) {
							dataOutsideBounds = true;
						}
					}
				} else if (layer.getYAxisTransform() == ax) {
					boolean xar = layer.getXAxisTransform().getLockGroup().isAutoRange();
					Range xRange = (xar) ? layer.getXAxisTransform().getAxisType()
							.getBoundary(ax.getType()) : layer.getXAxisTransform().getRange();
					for (GraphPlotterEx dp : layer.getGraphPlotters()) {
						Graph dataInBounds = dp.getGraph().setBoundary(xRange, urange);
						wDRange = dataInBounds.getYRange().union(wDRange);
						if (dataInBounds.hasPointOutsideYBounds()) {
							dataOutsideBounds = true;
						}
					}
				}

				if (!wDRange.isEmpty()) {
					Range pDRange = vtMap.get(ax).getTransP(wDRange);
					/* expand range by margin factor */
					double pLo = pDRange.getMin();
					double pHi = pDRange.getMax();
					double span = pDRange.getSpan();
					pLo -= span * ax.getMarginFactor();
					pHi += span * ax.getMarginFactor();
					Range pXdRange = new Range.Double(pLo, pHi).intersect(pbnds);
					if (padRange == null) {
						padRange = pXdRange;
					} else {
						padRange = padRange.union(pXdRange);
					}
				}
			}
		}

		if (padRange == null) {
			return null;
		}
		return new RangeStatus<Boolean>(padRange.getMin(), padRange.getMax(), dataOutsideBounds);

	}

	public void zoomVirtualRange(Range range, Map<AxisTransformEx, NormalTransform> vtMap) {
		HashSet<AxisRangeLockGroupEx> orthset = new HashSet<AxisRangeLockGroupEx>();

		for (AxisTransformEx arm : arms) {
			NormalTransform vt = vtMap.get(arm).zoom(range);
			Range wrange = vt.getRangeW();
			arm.setNormalTransfrom(arm.getType().createNormalTransform(wrange));

			for (LayerEx layer : arm.getLayers()) {
				if (layer.getXAxisTransform() == arm) {
					orthset.add(layer.getYAxisTransform().getLockGroup());
				} else if (layer.getXAxisTransform() == arm) {
					orthset.add(layer.getXAxisTransform().getLockGroup());
				}
			}
		}

		autoRange = false;

		for (AxisRangeLockGroupEx malg : orthset) {
			if (malg.isAutoRange()) {
				malg.reAutoRange();
			}
		}
	}

	public void zoomRange(double start, double end) {
		Range range = new Range.Double(start, end);

		Range validRange = AxisRangeUtils.validateNormalRange(range, arms, false);
		if (!validRange.equals(range)) {
			notify(new RangeAdjustedToValueBoundsNotice(
					"Range exceed valid boundary, has been adjusted."));
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
		HashSet<AxisRangeLockGroupEx> orthset = new HashSet<AxisRangeLockGroupEx>();

		for (AxisTransformEx axis : arms) {
			NormalTransform npt = axis.getNormalTransform().zoom(npRange);
			Range wrange = npt.getRangeW();
			axis.setNormalTransfrom(axis.getType().createNormalTransform(wrange));

			for (LayerEx layer : axis.getLayers()) {
				if (layer.getXAxisTransform() == axis) {
					orthset.add(layer.getYAxisTransform().getLockGroup());
				} else if (layer.getXAxisTransform() == axis) {
					orthset.add(layer.getXAxisTransform().getLockGroup());
				}
			}
		}

		autoRange = false;

		for (AxisRangeLockGroupEx malg : orthset) {
			if (malg.isAutoRange()) {
				malg.reAutoRange();
			}
		}
	}

	/**
	 * @return null if the group contains multiple types.
	 */
	public TransformType getType() {
		return type;
	}

	public void setType(TransformType type) {

		for (AxisTransformEx ax : arms) {
			ax.changeTransformType(type);
		}

		validateAxesRange();
	}

	public void validateAxesRange() {
		/* find nice range */
		Range pRange = validateNormalRange(NORM_PHYSICAL_RANGE);
		/*
		 * if the current physical range contains no valid data, find valid data at all range (like
		 * auto range)
		 */
		if (pRange == null) {
			pRange = validateNormalRange(INFINITY_PHYSICAL_RANGE);
		}

		if (pRange == null) {
			/*
			 * if no locked axes contains valid value, put them in the default world range
			 */
			for (AxisTransformEx ax : arms) {
				Range defaultWRange = ax.getAxisType().getDefaultWorldRange(ax.getType());
				Range nwr = (ax.isInverted()) ? defaultWRange.invert() : defaultWRange;
				ax.setNormalTransfrom(ax.getType().createNormalTransform(nwr));
			}

			notify(new RangeAdjustedToValueBoundsNotice(
					"All axes contain no valid data, range set to default range."));
			return;
		}

		if (pRange.getSpan() == 0) {
			/* if only one valid data point */
			for (AxisTransformEx ax : arms) {
				Range defaultWRange = ax.getAxisType().getDefaultWorldRange(ax.getType());
				Range nwr = (ax.isInverted()) ? defaultWRange.invert() : defaultWRange;
				ax.setNormalTransfrom(ax.getType().createNormalTransform(nwr));
			}

			Range pr = validateNormalRange(INFINITY_PHYSICAL_RANGE);
			RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(pr, arms);
			Range ur = prim.getNormalTransform().getTransU(rs);
			Range exur = prim.expandRangeToTick(ur);
			Range expr = prim.getNormalTransform().getTransP(exur);

			RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(expr, arms);
			zoomNormalRange(xrs);

			notify(new RangeSelectionNotice("The axis contains only one valid date."));
			return;
		}

		for (AxisTransformEx ax : arms) {
			Range wr = ax.getNormalTransform().getTransU(pRange);
			// set the inverted nature
			if (ax.getAxisType().getDefaultWorldRange(ax.getType()).isInverted() != (wr
					.isInverted() ^ ax.isInverted())) {
				wr = wr.invert();
			}
			ax.setNormalTransfrom(ax.getType().createNormalTransform(wr));
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
					notify(new RangeAdjustedToValueBoundsNotice(
							"Some axes contain invalid value, range adjusted."));
				}

				RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(
						NORM_PHYSICAL_RANGE, arms);
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

	private Range validateNormalRange(Range range) {
		return AxisRangeUtils.validateNormalRange(range, arms, true);
	}

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
