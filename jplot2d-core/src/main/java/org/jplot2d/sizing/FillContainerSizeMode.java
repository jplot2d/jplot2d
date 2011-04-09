package org.jplot2d.sizing;

import java.awt.geom.Dimension2D;

public class FillContainerSizeMode extends AbstractSizeMode {

	private final Dimension2D targetSize;

	/**
	 * The scale is kept. The chart size will automatically fit the container
	 * size. The plot size is changed as well. The laying out is working on
	 * outer-to-inner mode.
	 */
	public FillContainerSizeMode(double scale) {
		targetSize = null;
		this.scale = scale;
	}

	/**
	 * The scale is calculated toward target size. The chart aspect ratio will
	 * automatically fit the container. The plot size is changed as well. The
	 * laying out is working on outer-to-inner mode.
	 */
	public FillContainerSizeMode(Dimension2D targetSize) {
		this.targetSize = targetSize;
	}

	public void update() {
		Dimension2D containerSize = plot.getContainerSize();

		if (targetSize != null) {
			Dimension2D tcSize = targetSize;

			double scaleX = containerSize.getWidth() / tcSize.getWidth();
			double scaleY = containerSize.getHeight() / tcSize.getHeight();
			double scale = (scaleX < scaleY) ? scaleX : scaleY;

			double width = containerSize.getWidth() / scale;
			double height = containerSize.getHeight() / scale;

			if (this.width != width || this.height != height
					|| this.scale != scale) {
				this.width = width;
				this.height = height;
				this.scale = scale;
				updatePxf();
			}
		} else {
			double width = containerSize.getWidth() / scale;
			double height = containerSize.getHeight() / scale;

			if (this.width != width || this.height != height) {
				this.width = width;
				this.height = height;
				updatePxf();
			}
		}

	}

}