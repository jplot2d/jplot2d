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
package org.jplot2d.renderer;

import java.awt.Dimension;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import org.apache.xmlgraphics.java2d.GraphicContext;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.PlotEx;

/**
 * Export plot image to a EPS stream or file.
 * 
 * @author Jingjing Li
 * 
 */
public class EpsExporter extends Renderer {

	private final OutputStream os;

	public EpsExporter(String pathname) throws FileNotFoundException {
		this(new File(pathname));
	}

	public EpsExporter(File file) throws FileNotFoundException {
		this(new FileOutputStream(file));
	}

	public EpsExporter(OutputStream out) {
		super();
		os = new BufferedOutputStream(out);
	}

	@Override
	public void render(PlotEx plot, Map<ComponentEx, ComponentEx> cacheableCompMap,
			Collection<ComponentEx> unmodifiedCacheableComps,
			Map<ComponentEx, ComponentEx[]> subcompsMap) {

		Dimension size = getDeviceBounds(plot).getSize();

		EPSDocumentGraphics2D g = new EPSDocumentGraphics2D(true);
		g.setGraphicContext(new GraphicContext());
		try {
			g.setupDocument(os, size.width, size.height);
		} catch (IOException e) {
			throw new RuntimeException("Error exporting EPS", e);
		}

		for (Map.Entry<ComponentEx, ComponentEx> me : cacheableCompMap.entrySet()) {
			ComponentEx ccopy = me.getValue();

			ComponentEx[] sublist = subcompsMap.get(ccopy);
			for (ComponentEx subcomp : sublist) {
				subcomp.draw(g);
			}
		}

		try {
			g.finish();
		} catch (IOException e) {
			throw new RuntimeException("Error exporting EPS", e);
		}
	}

}
