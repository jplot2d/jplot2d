/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package org.jplot2d.element.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jplot2d.axtrans.NormalTransform;
import org.jplot2d.util.Range2D;

/**
 * @author Jingjing Li
 * 
 */
class AxisRangeUtils {

	/**
	 * The depth of zoom-in is controlled by this constant. The precision limit
	 * of double is 0x1.0p-52. 65536x to ensure the ticks look like
	 * even-distributed.
	 */
	protected static final double PRECISIONLIMIT = 0x1.0p-36;

	/**
	 * The default precision limit when range == 0. This make the range is large
	 * enough to produce tick labels within 4 digits.
	 */
	protected static final double DIGI4LIMIT = 0.004;

	private AxisRangeUtils() {

	}

	/**
	 * find the virtual normal intersected range of valid world range
	 * 
	 * @param axes
	 *            locked axes
	 * @return
	 */
	private static Range2D getBounds(Map<ViewportAxisEx, NormalTransform> vtMap) {
		Range2D pbnds = new Range2D.Double(Double.NEGATIVE_INFINITY,
				Double.POSITIVE_INFINITY);
		for (Map.Entry<ViewportAxisEx, NormalTransform> me : vtMap.entrySet()) {
			ViewportAxisEx ax = me.getKey();
			Range2D aprange = me.getValue().getTransP(
					ax.getType().getBoundary(ax.getTransformType()));
			pbnds = pbnds.intersect(aprange);
		}
		return pbnds;
	}

	/**
	 * Create a set of virtual NormalTransform correspond to NPTs of the given
	 * axes. The virtual NormalTransform will try to remove the offset to
	 * minimize the double calculation round error.
	 * 
	 * @param axes
	 * @return
	 */
	static Map<ViewportAxisEx, NormalTransform> createVirtualTransformMap(
			Collection<ViewportAxisEx> axes) {

		if (axes.size() == 0) {
			return Collections.emptyMap();
		}

		Map<ViewportAxisEx, NormalTransform> result = new HashMap<ViewportAxisEx, NormalTransform>();

		boolean match = true;
		double offset = Double.NaN;
		for (ViewportAxisEx axis : axes) {
			if (!match) {
				offset = axis.getNormalTransform().getOffset();
				match = true;
			} else if (axis.getNormalTransform().getOffset() != offset) {
				match = false;
				break;
			}
		}
		if (match) {
			for (ViewportAxisEx axis : axes) {
				NormalTransform nx = axis.getNormalTransform().deriveNoOffset();
				result.put(axis, nx);
			}
		} else {
			for (ViewportAxisEx axis : axes) {
				NormalTransform nx = axis.getNormalTransform();
				result.put(axis, nx);
			}
		}

		return result;
	}

	private static Map<ViewportAxisEx, NormalTransform> createNormalTransformMap(
			Collection<ViewportAxisEx> axes) {
		Map<ViewportAxisEx, NormalTransform> vtMap = new LinkedHashMap<ViewportAxisEx, NormalTransform>();
		for (ViewportAxisEx axis : axes) {
			vtMap.put(axis, axis.getNormalTransform());
		}
		return vtMap;
	}

	/**
	 * Adjust the given range and to make sure all corresponding user range only
	 * contain valid values.
	 * <ul>
	 * <li>If there is no intersection among the given range and valid
	 * boundaries, null is returned.</li>
	 * <li>If all corresponding world range of layers are valid, the given norm
	 * range is returned without any adjustment.</li>
	 * <li>If any corresponding world range of layers spans over invalid value,
	 * the end of given range is adjusted inward to the valid boundary.</li>
	 * <li>If the given range start/end at INFINITY, then "nearest data" well be
	 * found instead of the valid boundary.
	 * <ul>
	 * <li>If there is no valid data inside the valid range, <code>null</code>
	 * will be returned.</li>
	 * <li>A margin is appended on the "nearest data" end.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param range
	 *            The caller should guarantee pLo < pHi
	 * @return a RangeStatus contain the adjusted norm-physical range. The
	 *         RangeAdjustTag status indicate if the given range is adjusted.
	 */
	static Range2D validateNormalRange(Range2D range,
			Collection<ViewportAxisEx> axes, boolean findNearsetData) {
		Map<ViewportAxisEx, NormalTransform> axisMap = createNormalTransformMap(axes);
		return validateNormalRange(range, axisMap, findNearsetData);
	}

	/**
	 * Find a nice norm range inside the given range and to make sure all
	 * corresponding user range only contain valid values.
	 * <ul>
	 * <li>If there is no intersection among the given range and valid
	 * boundaries, null is returned.</li>
	 * <li>If all corresponding world range of layers are valid, the given norm
	 * range is returned without any adjustment.</li>
	 * <li>If any corresponding world range of layers spans over invalid value,
	 * the end of given range is adjusted inward to the data that most close to
	 * boundary.
	 * <ul>
	 * <li>If there is no valid data inside the valid range, <code>null</code>
	 * will be returned.</li>
	 * <li>A margin is appended on the "nearest data" end.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param range
	 *            The caller should guarantee pLo < pHi
	 * @return a RangeStatus contain the nice norm-physical range. The
	 *         RangeAdjustTag status indicate if the given range is adjusted.
	 */
	static Range2D validateNormalRange(Range2D range,
			Map<ViewportAxisEx, NormalTransform> axisMap,
			boolean findNearsetData) {

		if (range.isInverted()) {
			throw new IllegalArgumentException();
		}

		/* find the physical intersected range of valid world range among layers */
		Range2D pbnds = new Range2D.Double(range);
		for (Map.Entry<ViewportAxisEx, NormalTransform> me : axisMap.entrySet()) {
			ViewportAxisEx ax = me.getKey();
			// virtual normal range of the axis type boundary
			Range2D aprange = me.getValue().getTransP(
					me.getKey().getType().getBoundary(ax.getTransformType()));
			pbnds = pbnds.intersect(aprange);
		}

		/* There is no intersect among boundaries */
		if (pbnds == null) {
			return null;
		}
		/*
		 * The pbnds may be not null if a round error cause an intersection. In
		 * this case, the pbnds.span() is 0.
		 */
		for (Map.Entry<ViewportAxisEx, NormalTransform> me : axisMap.entrySet()) {
			Range2D urange = me.getValue().getTransU(pbnds);
			ViewportAxisEx ax = me.getKey();
			urange = urange.intersect(ax.getType().getBoundary(
					ax.getTransformType()));
			if (urange == null) {
				return null;
			}
		}

		boolean findLoData = false;
		boolean findHiData = false;

		if (findNearsetData) {
			for (Map.Entry<ViewportAxisEx, NormalTransform> me : axisMap
					.entrySet()) {
				Range2D urange = me.getValue().getTransU(range);
				ViewportAxisEx ax = me.getKey();
				if (urange.getStart() < ax.getType()
						.getBoundary(ax.getTransformType()).getStart()) {
					findLoData = true;
				}
				if (urange.getEnd() > ax.getType()
						.getBoundary(ax.getTransformType()).getEnd()) {
					findHiData = true;
				}
			}
		} else {
			findLoData = range.getStart() == Double.NEGATIVE_INFINITY;
			findHiData = range.getStart() == Double.POSITIVE_INFINITY;
		}

		/* should find the value that most close to pLo/pHi */
		if (findLoData || findHiData) {
			pbnds = findDataRange(pbnds, axisMap, findLoData, findHiData);
		}
		if (pbnds == null) {
			return null;
		}

		return pbnds;
	}

	/**
	 * If there is no data point in the given range, null is returned. The range
	 * on the end of found data point will expand by a margin factor.
	 * 
	 * @param pbnds
	 *            The caller guarantee this is valid on all axes.
	 * @param axes
	 * @param findLoData
	 *            indicate the low data should be found
	 * @param findHiData
	 *            indicate the high data should be found
	 * @return
	 */
	private static Range2D findDataRange(Range2D pbnds,
			Map<ViewportAxisEx, NormalTransform> axisMap, boolean findLoData,
			boolean findHiData) {

		// the physical adjusted range that find the data edge
		Range2D padRange = null;

		for (Map.Entry<ViewportAxisEx, NormalTransform> me : axisMap.entrySet()) {
			double pLo = Double.POSITIVE_INFINITY;
			double pHi = Double.NEGATIVE_INFINITY;

			ViewportAxisEx ax = (ViewportAxisEx) me.getKey();
			NormalTransform vnt = me.getValue();
			Range2D urange = vnt.getTransU(pbnds);
			// in case of the round error exceed valid bounds
			urange = urange.intersect(ax.getType().getBoundary(
					ax.getTransformType()));
			for (LayerEx layer : ax.getLayers()) {
				Range2D wDRange = null;
				switch (ax.getOrientation()) {
				case HORIZONTAL:
					for (GraphPlotterEx dp : layer.getGraphPlotters()) {
						wDRange = dp.getGraph().setXBoundary(urange)
								.getXRange().union(wDRange);
					}
					break;
				case VERTICAL:
					for (GraphPlotterEx dp : layer.getGraphPlotters()) {
						wDRange = dp.getGraph().setYBoundary(urange)
								.getYRange().union(wDRange);
					}
					break;
				}
				if (wDRange != null) {
					Range2D pDRange = vnt.getTransP(wDRange);
					if (pLo > pDRange.getMin()) {
						pLo = pDRange.getMin();
					}
					if (pHi < pDRange.getMax()) {
						pHi = pDRange.getMax();
					}
				}
			}

			if (!findLoData) {
				pLo = pbnds.getStart();
			}
			if (!findHiData) {
				pHi = pbnds.getEnd();
			}

			/* expand range by margin factor */
			// physical extended range
			Range2D pXdRange = null;
			if (pLo == pHi) {
				pXdRange = new Range2D.Double(pLo, pHi);
			} else if (pLo < pHi) {
				if (ax.getType().getDefaultTransformType() == vnt.getType()) {
					double span = pHi - pLo;
					pLo -= span * ax.getMarginFactor();
					pHi += span * ax.getMarginFactor();
					pXdRange = new Range2D.Double(pLo, pHi);
				} else {
					Range2D wr = vnt.getTransU(new Range2D.Double(pLo, pHi));
					NormalTransform npt = ax.getType()
							.getDefaultTransformType()
							.createNormalTransform(wr);
					Range2D exnpr = new Range2D.Double(-ax.getMarginFactor(),
							1 + ax.getMarginFactor());
					Range2D exwr = npt.getTransU(exnpr);
					pXdRange = vnt.getTransP(exwr);
				}
			}

			if (pXdRange != null) {
				// to prevent expand over bounds
				pXdRange = pXdRange.intersect(pbnds);
				if (padRange == null) {
					padRange = pXdRange;
				} else {
					padRange = padRange.union(pXdRange);
				}
			}
		}

		if (padRange == null) {
			return null;
		}

		return new Range2D.Double((findLoData) ? padRange.getMin()
				: pbnds.getStart(), (findHiData) ? padRange.getMax()
				: pbnds.getEnd());

	}

	/**
	 * The actually range maybe different from the given range to satisfy the
	 * precision limit.
	 * 
	 * @param pLo
	 *            the norm-physical range low value
	 * @param pHi
	 *            the norm-physical range high value
	 * @param lockedAxes
	 * @return the new range that satisfy the precision limit. The status object
	 *         is PrecisionException if the range is adjusted.
	 */
	static RangeStatus<PrecisionState> ensurePrecision(Range2D prange,
			Collection<ViewportAxisEx> axes) {
		Map<ViewportAxisEx, NormalTransform> axisMap = createNormalTransformMap(axes);
		return ensurePrecision(prange, axisMap);
	}

	static RangeStatus<PrecisionState> ensurePrecision(Range2D prange,
			Map<ViewportAxisEx, NormalTransform> vtMap) {

		double pLo = prange.getStart();
		double pHi = prange.getEnd();

		if (pLo > pHi) {
			throw new IllegalArgumentException("pLo > pHi is not allowed.");
		}

		double precisionLimit = (pHi == pLo) ? DIGI4LIMIT : PRECISIONLIMIT;
		PrecisionState ex = null;
		/* the minimal virtual range to satisfy the precision limit */
		double minRange = 0;
		/* the axis id that require the maximal physical range expansion */
		String minRangeId = null;

		// find new range to avoid PrecisionException
		for (Map.Entry<ViewportAxisEx, NormalTransform> me : vtMap.entrySet()) {
			ViewportAxisEx axis = me.getKey();
			NormalTransform vt = me.getValue();

			/*
			 * the required physical range to satisfy the precision limit for
			 * the axis
			 */
			double r = vt.getMinPSpan4PrecisionLimit(pLo, pHi, precisionLimit);
			if (minRange < r) {
				minRange = r;
				minRangeId = axis.getId();
			}
		}

		/* adjust range */
		if (pHi == pLo && minRange == 0) {
			/* when auto range and range == 0 */
			/* only when all axes are linear and contain constant 0 values */
			double minScale = Double.POSITIVE_INFINITY;
			for (Map.Entry<ViewportAxisEx, NormalTransform> me : vtMap
					.entrySet()) {
				NormalTransform vt = me.getValue();

				// np to world scale
				double ascale = Math.abs(vt.getScale());
				if (minScale > ascale) {
					minScale = ascale;
				}
			}
			minRange = DIGI4LIMIT / minScale;
		}

		double span = Double.NaN;
		if (minRange > pHi - pLo) {
			span = minRange;
			double avg = (pLo + pHi) / 2;
			pLo = avg - minRange / 2;
			pHi = avg + minRange / 2;
			ex = new PrecisionState(
					"The range has been adjusted to satisfy the precision limit for axis "
							+ minRangeId);
		}

		if (ex != null) {
			Range2D bnds = getBounds(vtMap);
			if (bnds.getSpan() < span) {
				pLo = bnds.getStart();
				pHi = bnds.getEnd();
			} else if (!bnds.contains(pLo)) {
				pLo = bnds.getStart();
				pHi = pLo + span;
			} else if (!bnds.contains(pHi)) {
				pHi = bnds.getEnd();
				pLo = pHi - span;
			}
		}

		RangeStatus<PrecisionState> result = new RangeStatus<PrecisionState>(
				pLo, pHi, ex);

		return result;
	}

	static RangeStatus<PrecisionState> ensureCircleSpan(Range2D prange,
			Collection<ViewportAxisEx> axes) {
		Map<ViewportAxisEx, NormalTransform> vtMap = createNormalTransformMap(axes);
		return ensureCircleSpan(prange, vtMap);
	}

	static RangeStatus<PrecisionState> ensureCircleSpan(Range2D prange,
			Map<ViewportAxisEx, NormalTransform> vtMap) {

		double pLo = prange.getStart();
		double pHi = prange.getEnd();

		if (pLo > pHi) {
			throw new IllegalArgumentException("pLo > pHi is not allowed.");
		}

		/* the maximum physical range to satisfy the circle */
		double maxRange = Double.POSITIVE_INFINITY;
		String maxRangeId = null;
		for (Map.Entry<ViewportAxisEx, NormalTransform> me : vtMap.entrySet()) {
			ViewportAxisEx axis = me.getKey();
			NormalTransform vt = me.getValue();

			if (axis.getType().getCircularRange() != null) {
				double ascale = Math.abs(vt.getScale());
				double pcircle = axis.getType().getCircularRange().getSpan()
						/ ascale;
				if (maxRange > pcircle) {
					maxRange = pcircle;
					maxRangeId = axis.getId();
				}
			}
		}

		PrecisionState ex = null;
		if (maxRange < pHi - pLo) {
			double avg = (pLo + pHi) / 2;
			pLo = avg - maxRange / 2;
			pHi = avg + maxRange / 2;
			ex = new PrecisionState(
					"The range has been adjusted to satisfy the circle limit for axis "
							+ maxRangeId);
		}

		return new RangeStatus<PrecisionState>(pLo, pHi, ex);

	}

}