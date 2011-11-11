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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jplot2d.axtype.AxisType;
import org.jplot2d.element.AxisRangeLockGroup;
import org.jplot2d.notice.RangeAdjustedToValueBoundsNotice;
import org.jplot2d.notice.RangeSelectionNotice;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;

public class AxisTransformImpl extends ElementImpl implements AxisTransformEx {

	/** The default margin factor (used for both lower and upper margins) */
	public static final double DEFAULT_MARGIN_FACTOR = 1.0 / 32; // 0.03125

	private AxisType type;

	private TransformType txfType;

	private boolean autoMargin = true;

	private double marginFactor = DEFAULT_MARGIN_FACTOR;

	private Range coreRange;

	private NormalTransform ntf;

	private AxisRangeLockGroupEx group;

	private final List<AxisTickManagerEx> tickManagers = new ArrayList<AxisTickManagerEx>();

	private final List<LayerEx> layers = new ArrayList<LayerEx>();

	public AxisTransformImpl() {
		super();

		type = AxisType.NUMBER;
		txfType = type.getDefaultTransformType();
		ntf = txfType.createNormalTransform(type.getDefaultWorldRange(txfType));
	}

	public String getId() {
		StringBuilder sb = new StringBuilder();
		sb.append("Transform(");
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
		sb.append("))");
		return sb.toString();
	}

	public String getShortId() {
		return getFullId();
	}

	public String getFullId() {
		if (group != null) {
			int xidx = group.indexOfRangeManager(this);
			return "Range" + xidx + "." + group.getFullId();
		} else {
			return super.getId();
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

	public boolean isInverted() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setInverted(boolean flag) {
		// TODO Auto-generated method stub

	}

	public boolean isAutoMargin() {
		return autoMargin;
	}

	public void setAutoMargin(boolean autoMargin) {
		this.autoMargin = autoMargin;

		if (group.isAutoRange() && group.getPrimaryAxis() == this) {
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

		if (group.isAutoRange() && group.getPrimaryAxis() == this) {
			group.reAutoRange();
		} else if (coreRange != null) {
			Range irng = coreRange.copy();
			setRange(coreRange, true);
			coreRange = irng;
		}
	}

	public AxisType getAxisType() {
		return type;
	}

	public void setAxisType(AxisType type) {
		if (group.getRangeManagers().length > 1) {
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

		group.validateAxesRange();
	}

	public TransformType getType() {
		return txfType;
	}

	public void setType(TransformType txfType) {
		if (group.getRangeManagers().length > 1) {
			throw new IllegalStateException(
					"The axis type can only be changed when the axis doses not lock with other axes.");
		}

		this.txfType = txfType;

		for (AxisTickManagerEx atm : tickManagers) {
			atm.transformTypeChanged();
		}

		group.validateAxesRange();
	}

	public void changeTransformType(TransformType txfType) {
		this.txfType = txfType;

		for (AxisTickManagerEx atm : tickManagers) {
			atm.transformTypeChanged();
		}
	}

	public AxisRangeLockGroupEx getLockGroup() {
		return group;
	}

	public void setLockGroup(AxisRangeLockGroup group) {
		if (this.group != null) {
			this.group.removeRangeManager(this);
		}
		this.group = (AxisRangeLockGroupEx) group;
		if (this.group != null) {
			this.group.addRangeManager(this);
		}
	}

	public NormalTransform getNormalTransform() {
		return ntf;
	}

	public void setNormalTransfrom(NormalTransform ntf) {
		this.ntf = ntf;

		for (AxisTickManagerEx atm : tickManagers) {
			for (AxisEx axis : atm.getAxes()) {
				axis.redraw();
			}
		}
		for (LayerEx layer : layers) {
			layer.transformChanged();
		}
	}

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

	public LayerEx[] getLayers() {
		return layers.toArray(new LayerEx[layers.size()]);
	}

	public void addLayer(LayerEx layer) {
		layers.add(layer);
		if (group.isAutoRange()) {
			for (GraphPlotterEx plotter : ((LayerEx) layer).getGraphPlotters()) {
				if (plotter.isVisible()) {
					group.reAutoRange();
					break;
				}
			}
		}
	}

	public void removeLayer(LayerEx layer) {
		layers.remove(layer);
		if (group.isAutoRange()) {
			for (GraphPlotterEx plotter : ((LayerEx) layer).getGraphPlotters()) {
				if (plotter.isVisible()) {
					group.reAutoRange();
					break;
				}
			}
		}
	}

	public Range getCoreRange() {
		return coreRange;
	}

	public void setCoreRange(Range range) {
		if (range == null) {

		} else {
			setRange(range, true);
		}
		coreRange = range;
	}

	public Range getRange() {
		return ntf.getRangeW();
	}

	public void setRange(Range urange) {
		if (urange.isInverted() != ntf.getRangeW().isInverted()) {
			urange = urange.invert();
		}

		setRange(urange, false);
	}

	/**
	 * Notice after this method, the _coreRange will be reset to null
	 * 
	 * @param urange
	 *            the range to be set
	 * @param appendMargin
	 *            true to indicate a margin should be appended to the given range, to derive a
	 *            actual range.
	 */
	private void setRange(Range urange, boolean appendMargin) {

		if (Double.isNaN(urange.getStart()) || Double.isNaN(urange.getEnd())) {
			throw new IllegalArgumentException("Range cannot start or end at NaN.");
		}

		Map<AxisTransformEx, NormalTransform> vtMap = AxisRangeUtils
				.createVirtualTransformMap(Arrays.asList(group.getRangeManagers()));

		NormalTransform vnt = vtMap.get(this);
		Range pr = vnt.getTransP(urange);

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
					+ ": the given range contains invalid value, range adjusted to ["
					+ ntf.getTransU(pLo) + ", " + ntf.getTransU(pHi) + "]"));
		}

		/* ensurePrecision */
		RangeStatus<PrecisionState> rs = AxisRangeUtils.ensurePrecision(pRange, vtMap);
		if (rs.getStatus() != null) {
			notify(new RangeSelectionNotice(rs.getStatus().getMessage()));
		}

		/* extend range to tick */
		Range extRange;
		if (appendMargin && isAutoMargin()) {
			Range ur = vnt.getTransU(rs);
			Range exur = expandRangeToTick(ur);
			extRange = vnt.getTransP(exur);
		} else {
			extRange = rs;
		}

		RangeStatus<PrecisionState> xrs = AxisRangeUtils.ensureCircleSpan(extRange, vtMap);
		if (xrs.getStatus() != null) {
			notify(new RangeSelectionNotice(xrs.getStatus().getMessage()));
		}
		group.zoomVirtualRange(xrs, vtMap);

	}

	public Range expandRangeToTick(Range ur) {
		if (tickManagers.size() > 0) {
			return tickManagers.get(0).expandRangeToTick(getType(), ur);
		}
		return null;
	}

	@Override
	public AxisTransformEx copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		AxisTransformImpl result = new AxisTransformImpl();

		if (orig2copyMap != null) {
			orig2copyMap.put(this, result);
		}

		// copy or link lock group
		AxisRangeLockGroupEx algCopy = (AxisRangeLockGroupEx) orig2copyMap.get(group);
		if (algCopy == null) {
			algCopy = (AxisRangeLockGroupEx) group.copyStructure(orig2copyMap);
		}
		result.group = algCopy;
		algCopy.addRangeManager(result);

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
