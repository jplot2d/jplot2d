/**
 * Copyright 2010-2013 Jingjing Li.
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
package org.jplot2d.env;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jplot2d.element.PComponent;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ElementEx;
import org.jplot2d.renderer.CacheableBlock;
import org.jplot2d.renderer.EpsExporter;
import org.jplot2d.renderer.PdfExporter;
import org.jplot2d.renderer.PngFileExporter;
import org.jplot2d.renderer.Renderer;

/**
 * This environment extends plot environment to add ability to render a plot.
 * 
 * @author Jingjing Li
 * 
 */
public class RenderEnvironment extends PlotEnvironment {

	private final List<Renderer> rendererList = Collections.synchronizedList(new ArrayList<Renderer>());

	protected static volatile File defaultExportDirectory;

	private volatile File exportDirectory;

	static {
		setDefaultExportDirectory(null);
	}

	/**
	 * Construct a environment to render the given plot.
	 * 
	 * @param threadSafe
	 *            if <code>false</code>, all plot properties can only be changed within a single thread, such as
	 *            servlet. if <code>true</code>, all plot properties can be safely changed by multiple threads.
	 */
	public RenderEnvironment(boolean threadSafe) {
		super(threadSafe);
	}

	public Renderer[] getRenderers() {
		return rendererList.toArray(new Renderer[0]);
	}

	public boolean addRenderer(Renderer renderer) {
		return rendererList.add(renderer);
	}

	public boolean removeRenderer(Renderer renderer) {
		return rendererList.remove(renderer);
	}

	@Override
	protected void render() {
		if (!plotImpl.isRerenderNeeded()) {
			return;
		}

		plotImpl.setRerenderNeeded(false);

		// clear redraw flag
		for (ElementEx element : proxyMap.keySet()) {
			if (element instanceof ComponentEx) {
				((ComponentEx) element).setRedrawNeeded(false);
			}
		}

		for (Renderer r : getRenderers()) {
			r.render(plotCopy, cacheBlockList);
		}
	}

	/**
	 * Export plot to the given renderer.
	 * 
	 * @param renderer
	 */
	public void exportPlot(Renderer renderer) {
		begin();

		try {
			renderer.render(plotCopy, cacheBlockList);
		} finally {
			end();
		}
	}

	/**
	 * Returns the default export directory.
	 */
	public static String getDefaultExportDirectory() {
		return defaultExportDirectory.getAbsolutePath();
	}

	/**
	 * Sets the default export directory. If the given directory is relative path, it's relative to user's home
	 * directory. The default export directory is persisted within a session.
	 */
	public static void setDefaultExportDirectory(String dir) {
		File dirFile;
		if (dir == null) {
			String home = System.getProperty("user.home");
			dirFile = new File(home);
		} else {
			dirFile = new File(dir);
			if (!dirFile.isDirectory()) {
				throw new IllegalArgumentException("The given dir " + dir + " is not a directory.");
			}
			if (!dirFile.isAbsolute()) {
				String home = System.getProperty("user.home");
				dirFile = new File(home, dir);
			}
		}
		defaultExportDirectory = dirFile;
	}

	/**
	 * Returns the export directory for this PlotXY. If this directory is null, the global default directory will be
	 * used when exporting a plot.
	 * 
	 * @see #getDefaultExportDirectory()
	 */
	public String getExportDirectory() {
		return (exportDirectory == null) ? null : exportDirectory.getAbsolutePath();
	}

	/**
	 * Sets the export directory for this PlotXY. If the directory is null, the global default directory will be used
	 * when exporting a plot. If the given directory is a relative path, it's relative to user's home directory.
	 * 
	 * @see #setDefaultExportDirectory(String)
	 */
	public void setExportDirectory(String dir) {
		File dirFile;
		if (dir == null) {
			dirFile = null;
		} else {
			dirFile = new File(dir);
			if (!dirFile.isDirectory()) {
				throw new IllegalArgumentException("The given dir " + dir + " is not a directory.");
			}
			if (!dirFile.isAbsolute()) {
				String home = System.getProperty("user.home");
				dirFile = new File(home, dir);
			}
		}
		exportDirectory = dirFile;
	}

	/**
	 * Returns the absolute pathname string for the given file name.
	 * 
	 * If the given file name is already absolute, then the pathname string is simply returned. Otherwise the default
	 * export directory is used as parent pathname.
	 */
	private File getExportFile(String filename) {
		File file = new File(filename);
		if (!file.isAbsolute()) {
			File dir = (exportDirectory == null) ? defaultExportDirectory : exportDirectory;
			file = new File(dir, filename);
		}
		return file;
	}

	/**
	 * Export the Plot to an EPS file.
	 * 
	 * @throws IOException
	 */
	public void exportToEPS(String filename) throws IOException {
		exportPlot(new EpsExporter(getExportFile(filename)));
	}

	/**
	 * Export the Plot to an PDF file.
	 * 
	 * @throws IOException
	 */
	public void exportToPDF(String filename) throws IOException {
		exportPlot(new PdfExporter(getExportFile(filename)));
	}

	/**
	 * Export the Plot to a PNG file.
	 * 
	 * @throws IOException
	 */
	public void exportToPNG(String filename) throws IOException {
		exportPlot(new PngFileExporter(getExportFile(filename)));
	}

}
