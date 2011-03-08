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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jplot2d.element.PhysicalTransform;
import org.jplot2d.element.PlotSizeMode;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.WarningMessage;
import org.jplot2d.util.WarningReceiver;

/**
 * @author Jingjing Li
 * 
 */
public class PlotImpl extends SubplotImpl implements PlotEx {

	private boolean rerenderNeeded = true;

	private WarningReceiver warningReceiver;

	private double scale = 1;

	private PlotSizeMode sizeMode = PlotSizeMode.FIT_CONTAINER_WITH_TARGET_SIZE;

	private boolean preferredSizeChanged;

	private Dimension containerSize = new Dimension(640, 480);

	private Dimension2D targetSize = new DoubleDimension2D(640, 480);

	public PlotImpl() {
		super.setColor(Color.BLACK);
		super.setFontName("Serif");
		super.setFontStyle(Font.PLAIN);
		super.setFontSize(12.0f);

		getMargin().setExtraTop(12);
		getMargin().setExtraLeft(12);
		getMargin().setExtraBottom(12);
		getMargin().setExtraRight(12);

		super.setCacheable(true);
		super.width = containerSize.getWidth();
		super.height = containerSize.getHeight();
	}

	public String getSelfId() {
		return "Plot@" + Integer.toHexString(System.identityHashCode(this));
	}

	public void setCacheable(boolean cacheMode) {
		// ignore setting cacheable
	}

	public boolean isRerenderNeeded() {
		return rerenderNeeded;
	}

	public void rerender() {
		rerenderNeeded = true;
	}

	public void clearRerenderNeeded() {
		rerenderNeeded = false;
	}

	public void setWarningReceiver(WarningReceiver warningReceiver) {
		this.warningReceiver = warningReceiver;
	}

	public void warning(WarningMessage msg) {
		if ((warningReceiver != null)) {
			warningReceiver.warning(msg);
		}
	}

	public PhysicalTransform getPhysicalTransform() {
		if (pxf == null) {
			pxf = new PhysicalTransform(0.0, getSize().getHeight(), scale);
		}
		return pxf;
	}

	public void childPreferredContentSizeChanged() {
		preferredSizeChanged = true;
	}

	public void validate() {
		if (getLayoutDirector() != null
				&& getSizeMode() == PlotSizeMode.FIT_CONTENTS) {
			if (!isValid() || preferredSizeChanged) {
				Dimension2D prefSize = getLayoutDirector().getPreferredSize(
						this);
				if (this.width != prefSize.getWidth()
						|| this.height != prefSize.getHeight()) {
					this.setSize(prefSize);
				}
				preferredSizeChanged = false;

			}
		}

		super.validate();
	}

	/**
	 * set this plot a new physical transform. this will trigger a redraw
	 * notification.
	 */
	private void updatePxf() {
		pxf = null;
		redraw();

		// notify all layers
		for (LayerEx layer : layers) {
			layer.parentPhysicalTransformChanged();
		}
		// notify all subplots
		for (SubplotEx sp : subplots) {
			sp.parentPhysicalTransformChanged();
		}
	}

	public PlotSizeMode getSizeMode() {
		return sizeMode;
	}

	public void setSizeMode(PlotSizeMode sizeMode) {
		this.sizeMode = sizeMode;

		switch (getSizeMode()) {
		case FIT_CONTAINER_WITH_TARGET_SIZE: {
			Dimension2D tcSize = getTargetPaperSize();

			double scaleX = containerSize.width / tcSize.getWidth();
			double scaleY = containerSize.height / tcSize.getHeight();
			double scale = (scaleX < scaleY) ? scaleX : scaleY;

			double width = containerSize.width / scale;
			double height = containerSize.height / scale;

			if (this.width != width || this.height != height
					|| this.scale != scale) {
				super.setSize(width, height);
				this.scale = scale;
				updatePxf();
			}
			break;
		}
		case FIT_CONTAINER_SIZE: {
			double width = containerSize.width / scale;
			double height = containerSize.height / scale;

			if (this.width != width || this.height != height) {
				super.setSize(width, height);
				updatePxf();
			}
			break;
		}
		case FIXED_SIZE:
			break;
		case FIT_CONTENTS: {
			/*
			 * Calculate scale based on container size and physical size.
			 */
			childPreferredContentSizeChanged();
			break;
		}
		}
	}

	/* This method should only be call from user, not internal method */
	public void setSize(double width, double height) {
		super.setSize(width, height);
		/*
		 * Calculate scale based on container size and physical size.
		 */
		double scaleX = containerSize.width / width;
		double scaleY = containerSize.height / height;
		scale = (scaleX < scaleY) ? scaleX : scaleY;

		updatePxf();
		invalidate();
	}

	public Dimension getContainerSize() {
		return containerSize;
	}

	public void setContainerSize(Dimension size) {
		this.containerSize = size;

		switch (getSizeMode()) {
		case FIT_CONTAINER_WITH_TARGET_SIZE: {
			Dimension2D tcSize = getTargetPaperSize();

			double scaleX = containerSize.width / tcSize.getWidth();
			double scaleY = containerSize.height / tcSize.getHeight();
			double scale = (scaleX < scaleY) ? scaleX : scaleY;

			double width = containerSize.width / scale;
			double height = containerSize.height / scale;

			if (this.width != width || this.height != height
					|| this.scale != scale) {
				super.setSize(width, height);
				this.scale = scale;
				updatePxf();
			}
			break;
		}
		case FIT_CONTAINER_SIZE: {
			double width = containerSize.width / scale;
			double height = containerSize.height / scale;

			if (this.width != width || this.height != height) {
				super.setSize(width, height);
				updatePxf();
			}
			break;
		}
		case FIXED_SIZE:
		case FIT_CONTENTS: {
			/*
			 * Calculate scale based on container size and physical size.
			 */
			Dimension2D physize = getSize();
			double scaleX = containerSize.width / physize.getWidth();
			double scaleY = containerSize.height / physize.getHeight();
			double scale = (scaleX < scaleY) ? scaleX : scaleY;

			if (this.scale != scale) {
				this.scale = scale;
				updatePxf();
			}
			break;
		}
		}
	}

	public Dimension2D getTargetPaperSize() {
		return targetSize;
	}

	public void setTargetPaperSize(Dimension2D paperSize) {
		targetSize = paperSize;
	}

	@Override
	public PlotImpl copyStructure(Map<ElementEx, ElementEx> orig2copyMap) {
		PlotImpl result = (PlotImpl) super.copyStructure(orig2copyMap);

		// link layer and range manager
		linkLayerAndRangeManager(this, orig2copyMap);

		return result;
	}

	@Override
	public void copyFrom(ElementEx src) {
		super.copyFrom(src);

		PlotImpl plot = (PlotImpl) src;
		sizeMode = plot.sizeMode;
		containerSize = (Dimension) plot.containerSize.clone();
		targetSize = (Dimension2D) plot.targetSize.clone();
		scale = plot.scale;
	}

	public void commit() {

		/*
		 * Axis a special component. Its length can be set by layout manager,
		 * but its thick depends on its internal status, such as tick height,
		 * labels. The auto range must be re-calculated after all axes length
		 * are set. So we cannot use deep-first validate tree. we must layout
		 * all subplot, then calculate auto range, then calculate thickness of
		 * all axes.
		 */

		/*
		 * The initial axis has 0 length and no label. The initial legend size
		 * as it contains 1 item. In most case, this assumption is correct.
		 */

		while (true) {

			/*
			 * Laying out axes may register some axis that ticks need be
			 * re-calculated
			 */
			this.validate();

			/*
			 * Auto range axes MUST be executed after they are laid out. <br>
			 * Auto range axes may register some axis that ticks need be
			 * re-calculated
			 */
			calcPendingLockGroupAutoRange();

			/*
			 * Calculating axes tick may invalidate some axis. Their metrics
			 * need be re-calculated
			 */
			calcAxesTick(this);

			/* thickness changes may invalidate the plot */
			calcAxesThickness(this);
			/* length constraint changes may invalidate the plot */
			calcLegendSize(this);

			if (this.isValid()) {
				break;
			}
		}
	}

	/**
	 * Re-autorange on all AxisLockGroups whoes autorange are true.
	 */
	private void calcPendingLockGroupAutoRange() {
		Set<AxisLockGroupEx> algs = new HashSet<AxisLockGroupEx>();
		fillLockGroups(this, algs);

		for (AxisLockGroupEx alg : algs) {
			alg.calcAutoRange();
		}
	}

	/**
	 * fill all AxisLockGroups in the given subplot to the set.
	 */
	private void fillLockGroups(SubplotEx subplot, Set<AxisLockGroupEx> algs) {
		for (AxisEx axis : subplot.getXAxes()) {
			AxisLockGroupEx alg = axis.getRangeManager().getLockGroup();
			algs.add(alg);
		}
		for (AxisEx axis : subplot.getYAxes()) {
			AxisLockGroupEx alg = axis.getRangeManager().getLockGroup();
			algs.add(alg);
		}
		for (SubplotEx sp : subplot.getSubplots()) {
			fillLockGroups(sp, algs);
		}
	}

	/**
	 * Calculate axis thickness according to its tick height, label font and
	 * label orientation.
	 */
	private void calcAxesThickness(SubplotEx subplot) {
		for (AxisEx axis : subplot.getXAxes()) {
			if (axis.isVisible()) {
				axis.calcThickness();
			}
		}
		for (AxisEx axis : subplot.getYAxes()) {
			if (axis.isVisible()) {
				axis.calcThickness();
			}
		}
		for (SubplotEx sp : subplot.getSubplots()) {
			calcAxesThickness(sp);
		}
	}

	/**
	 * Calculate axis ticks according to its length, range and tick properties.
	 */
	private void calcAxesTick(SubplotEx subplot) {
		for (AxisEx axis : subplot.getXAxes()) {
			axis.calcTicks();
		}
		for (AxisEx axis : subplot.getYAxes()) {
			axis.calcTicks();
		}
		for (SubplotEx sp : subplot.getSubplots()) {
			calcAxesTick(sp);
		}
	}

	/**
	 * Calculate legend size according to its length constraint, items and item
	 * font.
	 */
	private void calcLegendSize(SubplotEx subplot) {
		if (subplot.getLegend().isVisible()) {
			subplot.getLegend().calcSize();
		}
		for (SubplotEx sp : subplot.getSubplots()) {
			calcLegendSize(sp);
		}
	}

}
