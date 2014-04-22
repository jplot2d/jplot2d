/**
 * 
 */
package org.jplot2d.swing;

import org.jplot2d.renderer.ImageRenderer;

/**
 * A factory to create ImageRenderer objects.
 * 
 */
public interface ImageRendererFactory {

	/**
	 * Creates an ImageRenderer object.
	 * 
	 * @return created ImageRenderer object
	 */
	public ImageRenderer createImageRenderer();

}
