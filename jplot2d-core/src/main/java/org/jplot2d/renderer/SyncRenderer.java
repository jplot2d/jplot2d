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
package org.jplot2d.renderer;

import java.awt.Dimension;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;

import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.PlotEx;

/**
 * @author Jingjing Li
 * 
 */
public class SyncRenderer<T> extends Renderer<T> {

	private int fsn;

	public SyncRenderer(Assembler<T> assembler) {
		super(assembler);
	}

	SyncRenderer(Assembler<T> assembler, Executor executor) {
		super(assembler, executor);
	}

	@Override
	public final void render(PlotEx plot,
			Map<ComponentEx, ComponentEx> cacheableCompMap,
			Collection<ComponentEx> unmodifiedCacheableComps,
			Map<ComponentEx, ComponentEx[]> subcompsMap) {
		AssemblyInfo<T> ainfo = runCompRender(cacheableCompMap,
				unmodifiedCacheableComps, subcompsMap);

		Dimension size = plot.getBounds().getBounds().getSize();
		T result = assembler.assembleResult(size, ainfo);
		fireRenderingFinished(fsn++, result);
	}

}
