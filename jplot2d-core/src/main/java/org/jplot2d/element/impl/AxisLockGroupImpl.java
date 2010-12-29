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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.jplot2d.axtrans.NormalTransform;
import org.jplot2d.axtrans.TransformType;
import org.jplot2d.data.Graph;
import org.jplot2d.util.Range2D;
import org.jplot2d.util.RangeAdjustedToValueBoundsWarning;
import org.jplot2d.util.RangeSelectionWarning;

public class AxisLockGroupImpl extends ElementImpl implements AxisLockGroupEx {

	private static Range2D NORM_PHYSICAL_RANGE = new Range2D.Double(0.0, 1.0);

	private static Range2D INFINITY_PHYSICAL_RANGE = new Range2D.Double(
			Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

	/**
	 * The unitive axis type. can be null is this lock group contains more than
	 * one axis type
	 */
	private TransformType _type;

	private ViewportAxisEx _prim;

	private boolean autoRange = true;

	private Collection<ViewportAxisEx> axes = new ArrayList<ViewportAxisEx>();

	private boolean autoRangeNeeded = true;

	public ViewportAxisEx[] getParents() {
		return getViewportAxes();
	}

	public ElementEx deepCopy(Map<ElementEx, ElementEx> orig2copyMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public ViewportAxisEx[] getViewportAxes() {
		return axes.toArray(new ViewportAxisEx[axes.size()]);
	}

	public void addViewportAxis(ViewportAxisEx axis) {
		axes.add(axis);
		if (axes.size() == 1) {
			parent = axis;
			_prim = axis;
			_type = axis.getTransformType();
		} else {
			parent = null;
		}

		if (_type != axis.getType()) {
			_type = null;
		}

	}

	public void removeViewportAxis(ViewportAxisEx axis) {
		axes.remove(axis);

		autoRange = false;

		if (axes.size() == 1) {
			parent = axis;
		} else {
			parent = null;
		}

		/* find a new primary axis */
		if (_prim == axis) {
			for (ViewportAxisEx a : axes) {
				_prim = a;
				break;
			}
		}

		// try to find unique type
		if (_type == null) {
			TransformType utype = null;
			for (ViewportAxisEx ax : axes) {
				if (utype == null) {
					utype = ax.getTransformType();
				} else {
					if (utype != ax.getTransformType()) {
						utype = null;
						break;
					}
				}
			}
			_type = utype;
		}
	}

	public ViewportAxisEx getPrimaryAxis() {
		return _prim;
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
	 * @param axes
	 *            the axes that global auto-range take place
	 * @throws WarningException
	 *             This WarningException can be a NegativeValueInLogException or
	 *             multiple NegativeValueInLogException in a MultiException.
	 */
	private void autoRange() {

		Map<ViewportAxisEx, NormalTransform> vtMap = AxisRangeUtils
				.createVirtualTransformMap(axes);

		RangeStatus<Boolean> pRange = calcNiceVirtualRange(vtMap);

		if (pRange == null) {
			/* all axis is LOG and contain no positive data, do nothing */
			warning(new RangeSelectionWarning(
					"Axes contain no valide data, auto range ignored."));
			return;
		}

		if (pRange.getStatus()) {
			warning(new RangeAdjustedToValueBoundsWarning(
					"AutoRange only on valid data of axes."));
			/* The detailed info can be gotten from pLogRange.getStatus() */
		}

		/*
		 * zoomPhysicalRange(pLo, pHi, axes) will choose a proper range when
		 * range == 0
		 */
		RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(pRange,
				vtMap);

		if (rs.getStatus() != null) {
			/* the PrecisionException should not be thrown from autoRange(). */
			warning(new RangeSelectionWarning(rs.getStatus().getMessage()));
		}

		/* extend master range to tick */
		ViewportAxisEx master = getPrimaryAxis();
		Range2D extRange;
		if (master.isAutoMargin()) {
			Range2D ur = vtMap.get(master).getTransU(rs);
			Range2D exur = master.expandRangeToTick(ur);
			extRange = vtMap.get(master).getTransP(exur);
		} else {
			extRange = rs;
		}

		RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(
				extRange, vtMap);
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
	 * Find a nice norm-physical range to make sure all corresponding user range
	 * only contain valid values.
	 * <ul>
	 * <li>If any corresponding world range of layer span invalid value, the
	 * physical end of given range is adjusted inward to match the data that
	 * most close to boundary.</li>
	 * <li>If there is no valid data inside the given range, <code>null</code>
	 * will be returned.</li>
	 * <li>No margin added to the result.</li>
	 * </ul>
	 * 
	 * @return a RangeStatus contain the nice norm-physical range. The Boolean
	 *         status to indicate if there are some data points outside the
	 *         value bounds
	 */
	private RangeStatus<Boolean> calcNiceVirtualRange(
			Map<ViewportAxisEx, NormalTransform> vtMap) {

		/* find the physical intersected range of valid world range among layers */
		Range2D pbnds = new Range2D.Double(Double.NEGATIVE_INFINITY,
				Double.POSITIVE_INFINITY);
		for (ViewportAxisEx ax : axes) {
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
		for (ViewportAxisEx ax : axes) {
			for (LayerEx layer : ax.getLayers()) {
				Range2D urange = vtMap.get(ax).getTransU(pbnds);
				Range2D wDRange = new Range2D.Double();

				switch (ax.getOrientation()) {
				case HORIZONTAL:
					boolean yar = layer.getYViewportAxis().getLockGroup()
							.isAutoRange();
					Range2D yRange = (yar) ? layer.getYViewportAxis().getType()
							.getBoundary(ax.getTransformType()) : layer
							.getYViewportAxis().getRange();
					for (GraphPlotterEx dp : layer.getGraphPlotters()) {
						Graph dataInBounds = dp.getGraph().setBoundary(urange,
								yRange);
						wDRange = dataInBounds.getXRange().union(wDRange);
						if (dataInBounds.hasPointOutsideXBounds()) {
							dataOutsideBounds = true;
						}
					}
					break;
				case VERTICAL:
					boolean xar = layer.getXViewportAxis().getLockGroup()
							.isAutoRange();
					Range2D xRange = (xar) ? layer.getXViewportAxis().getType()
							.getBoundary(ax.getTransformType()) : layer
							.getXViewportAxis().getRange();
					for (GraphPlotterEx dp : layer.getGraphPlotters()) {
						Graph dataInBounds = dp.getGraph().setBoundary(xRange,
								urange);
						wDRange = dataInBounds.getYRange().union(wDRange);
						if (dataInBounds.hasPointOutsideYBounds()) {
							dataOutsideBounds = true;
						}
					}
					break;
				}

				if (wDRange != null) {
					Range2D pDRange = vtMap.get(ax).getTransP(wDRange);
					/* expand range by margin factor */
					double pLo = pDRange.getMin();
					double pHi = pDRange.getMax();
					double span = pDRange.getSpan();
					pLo -= span * ax.getMarginFactor();
					pHi += span * ax.getMarginFactor();
					Range2D pXdRange = new Range2D.Double(pLo, pHi)
							.intersect(pbnds);
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
		return new RangeStatus<Boolean>(padRange.getMin(), padRange.getMax(),
				dataOutsideBounds);

	}

	public void zoomVirtualRange(Range2D range,
			Map<ViewportAxisEx, NormalTransform> vtMap) {
		HashSet<AxisLockGroupEx> orthset = new HashSet<AxisLockGroupEx>();

		for (ViewportAxisEx axis : axes) {
			NormalTransform vt = vtMap.get(axis);
			vt.zoom(range);
			Range2D wrange = vt.getRangeW();
			axis.setNormalTransfrom(axis.getTransformType()
					.createNormalTransform(wrange));

			switch (axis.getOrientation()) {
			case HORIZONTAL:
				for (LayerEx layer : axis.getLayers()) {
					orthset.add(layer.getYViewportAxis().getLockGroup());
				}
				break;
			case VERTICAL:
				for (LayerEx layer : axis.getLayers()) {
					orthset.add(layer.getXViewportAxis().getLockGroup());
				}
				break;
			}
		}

		autoRange = false;

		for (AxisLockGroupEx malg : orthset) {
			if (malg.isAutoRange()) {
				malg.reAutoRange();
			}
		}
	}

	public void zoomRange(double start, double end) {
		Range2D range = new Range2D.Double(start, end);

		Range2D validRange = AxisRangeUtils.validateNormalRange(range, axes,
				false);
		if (!validRange.equals(range)) {
			warning(new RangeAdjustedToValueBoundsWarning(
					"Range exceed valid boundary, has been adjusted."));
		}

		RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(
				validRange, axes);
		if (rs.getStatus() != null) {
			warning(new RangeSelectionWarning(rs.getStatus().getMessage()));
		}
		RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(rs,
				axes);
		if (xrs.getStatus() != null) {
			warning(new RangeSelectionWarning(xrs.getStatus().getMessage()));
		}

		zoomNormalRange(xrs);

	}

	/**
	 * Zoom the given normalized physical range to entire axis. All axes in this
	 * lock group are changed. If the orthogonal axes are autoRange, they need
	 * to be re-autoRange.
	 * 
	 * @param pRange
	 */
	public void zoomNormalRange(Range2D npRange) {
		HashSet<AxisLockGroupEx> orthset = new HashSet<AxisLockGroupEx>();

		for (ViewportAxisEx axis : axes) {
			NormalTransform npt = axis.getNormalTransform();
			npt.zoom(npRange);
			Range2D wrange = npt.getRangeW();
			axis.setNormalTransfrom(axis.getTransformType()
					.createNormalTransform(wrange));

			switch (axis.getOrientation()) {
			case HORIZONTAL:
				for (LayerEx layer : axis.getLayers()) {
					orthset.add(layer.getYViewportAxis().getLockGroup());
				}
				break;
			case VERTICAL:
				for (LayerEx layer : axis.getLayers()) {
					orthset.add(layer.getXViewportAxis().getLockGroup());
				}
				break;
			}
		}

		autoRange = false;

		for (AxisLockGroupEx malg : orthset) {
			if (malg.isAutoRange()) {
				malg.reAutoRange();
			}
		}
	}

	/**
	 * @return null if the group contains multiple types.
	 */
	public TransformType getType() {
		return _type;
	}

	public void setType(TransformType type) {

		for (ViewportAxisEx ax : axes) {
			ax.changeTransformType(type);
		}

		validateAxesRange();
	}

	public void validateAxesRange() {
		/* find nice range */
		Range2D pRange = validateNormalRange(NORM_PHYSICAL_RANGE);
		/*
		 * if the current physical range contains no valid data, find valid data
		 * at all range (like auto range)
		 */
		if (pRange == null) {
			pRange = validateNormalRange(INFINITY_PHYSICAL_RANGE);
		}

		if (pRange == null) {
			/*
			 * if no locked axes contains valid value, put them in the default
			 * world range
			 */
			for (ViewportAxisEx ax : axes) {
				Range2D defaultWRange = ax.getType().getDefaultWorldRange(
						ax.getTransformType());
				Range2D nwr = (ax.isInverted()) ? defaultWRange.invert()
						: defaultWRange;
				ax.setNormalTransfrom(ax.getTransformType()
						.createNormalTransform(nwr));
			}

			warning(new RangeAdjustedToValueBoundsWarning(
					"All axes contain no valid data, range set to default range."));
			return;
		}

		if (pRange.getSpan() == 0) {
			/* if only one valid data point */
			for (ViewportAxisEx ax : axes) {
				Range2D defaultWRange = ax.getType().getDefaultWorldRange(
						ax.getTransformType());
				Range2D nwr = (ax.isInverted()) ? defaultWRange.invert()
						: defaultWRange;
				ax.setNormalTransfrom(ax.getTransformType()
						.createNormalTransform(nwr));
			}

			Range2D pr = validateNormalRange(INFINITY_PHYSICAL_RANGE);
			RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(pr,
					axes);
			Range2D ur = _prim.getNormalTransform().getTransU(rs);
			Range2D exur = _prim.expandRangeToTick(ur);
			Range2D expr = _prim.getNormalTransform().getTransP(exur);

			RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(
					expr, axes);
			zoomNormalRange(xrs);

			warning(new RangeSelectionWarning(
					"The axis contains only one valid date."));
			return;
		}

		for (ViewportAxisEx ax : axes) {
			Range2D wr = ax.getNormalTransform().getTransU(pRange);
			// set the inverted nature
			if (ax.getType().getDefaultWorldRange(ax.getTransformType())
					.isInverted() != (wr.isInverted() ^ ax.isInverted())) {
				wr = wr.invert();
			}
			ax.setNormalTransfrom(ax.getTransformType().createNormalTransform(
					wr));
		}

		if (isAutoRange()) {
			reAutoRange();
		} else {
			ViewportAxisEx coreAxis = null;
			Range2D coreRange = null;
			for (ViewportAxisEx ax : axes) {
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

				RangeStatus<PrecisionState> rs = AxisRangeUtils
						.ensurePrecision(NORM_PHYSICAL_RANGE, axes);
				if (rs.getStatus() != null) {
					warning(new RangeSelectionWarning(rs.getStatus()
							.getMessage()));
				}
				RangeStatus<PrecisionState> xrs = AxisRangeUtils
						.ensureCircleSpan(rs, axes);
				if (xrs.getStatus() != null) {
					warning(new RangeSelectionWarning(xrs.getStatus()
							.getMessage()));
				}

				zoomNormalRange(xrs);
			}
		}

	}

	private Range2D validateNormalRange(Range2D range) {
		return AxisRangeUtils.validateNormalRange(range, axes, true);
	}

}
