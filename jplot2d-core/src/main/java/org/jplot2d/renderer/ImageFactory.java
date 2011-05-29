package org.jplot2d.renderer;

import java.awt.Color;
import java.awt.image.BufferedImage;

public interface ImageFactory {

	public static Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);

	/**
	 * Create a transparent buffered image. The image is used by component
	 * renderer.
	 * 
	 * @return a BufferedImage
	 */
	public BufferedImage createTransparentImage(int width, int height);

	/**
	 * Create a buffered image. The image is the final image to draw everything.
	 * 
	 * @return a BufferedImage
	 */
	public BufferedImage createImage(int width, int height);

}