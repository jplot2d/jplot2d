package org.jplot2d.sizing;

import java.awt.geom.Dimension2D;

import org.jplot2d.element.impl.PlotEx;

/**
 * The scale is kept. The plot size will automatically fit the container size. The plot content size
 * is changed as well.
 */
public class FillContainerSizeMode extends SizeMode {

	private final Dimension2D targetSize;

	private final double scale;

	public FillContainerSizeMode(double scale) {
		super(false);
		this.targetSize = null;
		this.scale = scale;
	}

	/**
	 * The scale is calculated toward target size. The chart aspect ratio will automatically fit the
	 * container. The plot size is changed as well. The laying out is working on outer-to-inner
	 * mode.
	 */
	public FillContainerSizeMode(Dimension2D targetSize) {
		super(false);
		this.targetSize = targetSize;
		this.scale = 1;
	}

	public Result update(PlotEx plot) {
		Dimension2D containerSize = plot.getContainerSize();

		if (targetSize != null) {
			Dimension2D tcSize = targetSize;

			double scaleX = containerSize.getWidth() / tcSize.getWidth();
			double scaleY = containerSize.getHeight() / tcSize.getHeight();
			double scale = (scaleX < scaleY) ? scaleX : scaleY;

			double width = containerSize.getWidth() / scale;
			double height = containerSize.getHeight() / scale;

			return new Result(width, height, scale);

		} else {
			double width = containerSize.getWidth() / scale;
			double height = containerSize.getHeight() / scale;

			return new Result(width, height, scale);
		}

	}

	public String toString() {
		if (targetSize == null) {
			return "Fill container with scale " + scale;
		} else {
			return "Fill container with target size " + targetSize.getWidth() + "x"
					+ targetSize.getHeight();
		}
	}

}