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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jplot2d.axtrans.NormalTransform;
import org.jplot2d.axtrans.TransformType;
import org.jplot2d.data.Graph;
import org.jplot2d.util.Range2D;
import org.jplot2d.util.RangeAdjustedToValueBoundsWarning;
import org.jplot2d.util.RangeSelectionWarning;

public class AxisRangeLockGroupImpl extends ElementImpl implements AxisRangeLockGroupEx {

	private static Range2D NORM_PHYSICAL_RANGE = new Range2D.Double(0.0, 1.0);

	private static Range2D INFINITY_PHYSICAL_RANGE = new Range2D.Double(Double.NEGATIVE_INFINITY,
			Double.POSITIVE_INFINITY);

	/**
	 * The unitive axis type. can be null is this lock group contains more than one axis type
	 */
	private TransformType type;

	private AxisRangeManagerEx prim;

	private boolean autoRange = true;

	private List<AxisRangeManagerEx> arms = new ArrayList<AxisRangeManagerEx>();

	private boolean autoRangeNeeded = true;

	public String getId() {
		return "AxisLockGroup@" + Integer.toHexString(System.identityHashCode(this));
	}

	public AxisRangeManagerEx getParent() {
		return (AxisRangeManagerEx) super.getParent();
	}

	public boolean isReferenced() {
		return arms.size() > 0;
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

	public int indexOfRangeManager(AxisRangeManagerEx rangeManager) {
		return arms.indexOf(rangeManager);
	}

	public AxisRangeManagerEx[] getRangeManagers() {
		return arms.toArray(new AxisRangeManagerEx[arms.size()]);
	}

	public void addRangeManager(AxisRangeManagerEx axis) {
		arms.add(axis);
		if (arms.size() == 1) {
			parent = axis;
			prim = axis;
			type = axis.getTransformType();
		} else {
			parent = null;
		}

		if (type != axis.getType()) {
			type = null;
		}

	}

	public void removeRangeManager(AxisRangeManagerEx axis) {
		arms.remove(axis);

		autoRange = false;

		if (arms.size() == 1) {
			parent = arms.get(0);
			prim = arms.get(0);
			type = prim.getTransformType();
		} else {
			parent = null;

			/* find a new primary axis */
			if (prim == axis) {
				for (AxisRangeManagerEx a : arms) {
					prim = a;
					break;
				}
			}

			// try to find unique type
			if (type == null) {
				TransformType utype = null;
				for (AxisRangeManagerEx ax : arms) {
					if (utype == null) {
						utype = ax.getTransformType();
					} else {
						if (utype != ax.getTransformType()) {
							utype = null;
							break;
						}
					}
				}
				type = utype;
			}
		}

	}

	public AxisRangeManagerEx getPrimaryAxis() {
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
	 * @throws WarningException
	 *             This WarningException can be a NegativeValueInLogException or multiple
	 *             NegativeValueInLogException in a MultiException.
	 */
	private void autoRange() {

		Map<AxisRangeManagerEx, NormalTransform> vtMap = AxisRangeUtils
				.createVirtualTransformMap(arms);

		RangeStatus<Boolean> pRange = calcNiceVirtualRange(vtMap);

		if (pRange == null) {
			/* all axis is LOG and contain no positive data, do nothing */
			warning(new RangeSelectionWarning("Axes contain no valide data, auto range ignored."));
			return;
		}

		if (pRange.getStatus()) {
			warning(new RangeAdjustedToValueBoundsWarning("AutoRange only on valid data of axes."));
			/* The detailed info can be gotten from pLogRange.getStatus() */
		}

		/*
		 * zoomPhysicalRange(pLo, pHi, axes) will choose a proper range when range == 0
		 */
		RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(pRange, vtMap);

		if (rs.getStatus() != null) {
			/* the PrecisionException should not be thrown from autoRange(). */
			warning(new RangeSelectionWarning(rs.getStatus().getMessage()));
		}

		/* extend master range to tick */
		AxisRangeManagerEx master = getPrimaryAxis();
		Range2D extRange;
		if (master.isAutoMargin()) {
			Range2D ur = vtMap.get(master).getTransU(rs);
			Range2D exur = master.expandRangeToTick(ur);
			extRange = vtMap.get(master).getTransP(exur);
		} else {
			extRange = rs;
		}

		RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(extRange, vtMap);
		if (xrs.getStatus() != null) {
			warning(new RangeSelectionWarning(xrs.getStatus().getMessage()));
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
	private RangeStatus<Boolean> calcNiceVirtualRange(Map<AxisRangeManagerEx, NormalTransform> vtMap) {

		/* find the physical intersected range of valid world range among layers */
		Range2D pbnds = new Range2D.Double(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		for (AxisRangeManagerEx ax : arms) {
			Range2D aprange = vtMap.get(ax).getTransP(
					ax.getType().getBoundary(ax.getTransformType()));
			pbnds = pbnds.intersect(aprange);
		}

		if (pbnds == null) {
			/* The given range on all layers are outside the boundary. */
			return null;
		}

		Range2D padRange = null;
		boolean dataOutsideBounds = false;
		for (AxisRangeManagerEx ax : arms) {
			for (LayerEx layer : ax.getLayers()) {
				Range2D urange = vtMap.get(ax).getTransU(pbnds);
				Range2D wDRange = new Range2D.Double();

				if (layer.getXRangeManager() == ax) {
					boolean yar = layer.getYRangeManager().getLockGroup().isAutoRange();
					Range2D yRange = (yar) ? layer.getYRangeManager().getType()
							.getBoundary(ax.getTransformType()) : layer.getYRangeManager()
							.getRange();
					for (GraphPlotterEx dp : layer.getGraphPlotters()) {
						Graph dataInBounds = dp.getGraph().setBoundary(urange, yRange);
						wDRange = dataInBounds.getXRange().union(wDRange);
						if (dataInBounds.hasPointOutsideXBounds()) {
							dataOutsideBounds = true;
						}
					}
				} else if (layer.getYRangeManager() == ax) {
					boolean xar = layer.getXRangeManager().getLockGroup().isAutoRange();
					Range2D xRange = (xar) ? layer.getXRangeManager().getType()
							.getBoundary(ax.getTransformType()) : layer.getXRangeManager()
							.getRange();
					for (GraphPlotterEx dp : layer.getGraphPlotters()) {
						Graph dataInBounds = dp.getGraph().setBoundary(xRange, urange);
						wDRange = dataInBounds.getYRange().union(wDRange);
						if (dataInBounds.hasPointOutsideYBounds()) {
							dataOutsideBounds = true;
						}
					}
				}

				if (!wDRange.isEmpty() ) {
					Range2D pDRange = vtMap.get(ax).getTransP(wDRange);
					/* expand range by margin factor */
					double pLo = pDRange.getMin();
					double pHi = pDRange.getMax();
					double span = pDRange.getSpan();
					pLo -= span * ax.getMarginFactor();
					pHi += span * ax.getMarginFactor();
					Range2D pXdRange = new Range2D.Double(pLo, pHi).intersect(pbnds);
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

	public void zoomVirtualRange(Range2D range, Map<AxisRangeManagerEx, NormalTransform> vtMap) {
		HashSet<AxisRangeLockGroupEx> orthset = new HashSet<AxisRangeLockGroupEx>();

		for (AxisRangeManagerEx arm : arms) {
			NormalTransform vt = vtMap.get(arm);
			vt.zoom(range);
			Range2D wrange = vt.getRangeW();
			arm.setNormalTransfrom(arm.getTransformType().createNormalTransform(wrange));

			for (LayerEx layer : arm.getLayers()) {
				if (layer.getXRangeManager() == arm) {
					orthset.add(layer.getYRangeManager().getLockGroup());
				} else if (layer.getXRangeManager() == arm) {
					orthset.add(layer.getXRangeManager().getLockGroup());
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
		Range2D range = new Range2D.Double(start, end);

		Range2D validRange = AxisRangeUtils.validateNormalRange(range, arms, false);
		if (!validRange.equals(range)) {
			warning(new RangeAdjustedToValueBoundsWarning(
					"Range exceed valid boundary, has been adjusted."));
		}

		RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(validRange, arms);
		if (rs.getStatus() != null) {
			warning(new RangeSelectionWarning(rs.getStatus().getMessage()));
		}
		RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(rs, arms);
		if (xrs.getStatus() != null) {
			warning(new RangeSelectionWarning(xrs.getStatus().getMessage()));
		}

		zoomNormalRange(xrs);
	}

	public void zoomNormalRange(Range2D npRange) {
		HashSet<AxisRangeLockGroupEx> orthset = new HashSet<AxisRangeLockGroupEx>();

		for (AxisRangeManagerEx axis : arms) {
			NormalTransform npt = axis.getNormalTransform();
			npt.zoom(npRange);
			Range2D wrange = npt.getRangeW();
			axis.setNormalTransfrom(axis.getTransformType().createNormalTransform(wrange));

			for (LayerEx layer : axis.getLayers()) {
				if (layer.getXRangeManager() == axis) {
					orthset.add(layer.getYRangeManager().getLockGroup());
				} else if (layer.getXRangeManager() == axis) {
					orthset.add(layer.getXRangeManager().getLockGroup());
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

		for (AxisRangeManagerEx ax : arms) {
			ax.changeTransformType(type);
		}

		validateAxesRange();
	}

	public void validateAxesRange() {
		/* find nice range */
		Range2D pRange = validateNormalRange(NORM_PHYSICAL_RANGE);
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
			for (AxisRangeManagerEx ax : arms) {
				Range2D defaultWRange = ax.getType().getDefaultWorldRange(ax.getTransformType());
				Range2D nwr = (ax.isInverted()) ? defaultWRange.invert() : defaultWRange;
				ax.setNormalTransfrom(ax.getTransformType().createNormalTransform(nwr));
			}

			warning(new RangeAdjustedToValueBoundsWarning(
					"All axes contain no valid data, range set to default range."));
			return;
		}

		if (pRange.getSpan() == 0) {
			/* if only one valid data point */
			for (AxisRangeManagerEx ax : arms) {
				Range2D defaultWRange = ax.getType().getDefaultWorldRange(ax.getTransformType());
				Range2D nwr = (ax.isInverted()) ? defaultWRange.invert() : defaultWRange;
				ax.setNormalTransfrom(ax.getTransformType().createNormalTransform(nwr));
			}

			Range2D pr = validateNormalRange(INFINITY_PHYSICAL_RANGE);
			RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(pr, arms);
			Range2D ur = prim.getNormalTransform().getTransU(rs);
			Range2D exur = prim.expandRangeToTick(ur);
			Range2D expr = prim.getNormalTransform().getTransP(exur);

			RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(expr, arms);
			zoomNormalRange(xrs);

			warning(new RangeSelectionWarning("The axis contains only one valid date."));
			return;
		}

		for (AxisRangeManagerEx ax : arms) {
			Range2D wr = ax.getNormalTransform().getTransU(pRange);
			// set the inverted nature
			if (ax.getType().getDefaultWorldRange(ax.getTransformType()).isInverted() != (wr
					.isInverted() ^ ax.isInverted())) {
				wr = wr.invert();
			}
			ax.setNormalTransfrom(ax.getTransformType().createNormalTransform(wr));
		}

		if (isAutoRange()) {
			reAutoRange();
		} else {
			AxisRangeManagerEx coreAxis = null;
			Range2D coreRange = null;
			for (AxisRangeManagerEx ax : arms) {
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
					warning(new RangeAdjustedToValueBoundsWarning(
							"Some axes contain invalid value, range adjusted."));
				}

				RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(
						NORM_PHYSICAL_RANGE, arms);
				if (rs.getStatus() != null) {
					warning(new RangeSelectionWarning(rs.getStatus().getMessage()));
				}
				RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(rs, arms);
				if (xrs.getStatus() != null) {
					warning(new RangeSelectionWarning(xrs.getStatus().getMessage()));
				}

				zoomNormalRange(xrs);
			}
		}

	}

	private Range2D validateNormalRange(Range2D range) {
		return AxisRangeUtils.validateNormalRange(range, arms, true);
	}

}
