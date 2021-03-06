/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element.impl;

import org.jplot2d.env.PlotEnvironment;

import javax.annotation.Nullable;

/**
 * Class implements this interface means it maintains a intermediate cache.
 *
 * @author Jingjing Li
 */
public interface IntermediateCacheEx {

    /**
     * Create a cache holder to keep intermediate calculation result.
     * This method is called by {@link PlotEnvironment#commit()}, before create thread-safe copy for renderers.
     * The PlotEnvironment will keep hard reference to the returned Object.
     * Usually the returned object is used as key of WeakHashMap.
     *
     * @return an object to that PlotEnvironment should keep reference
     */
    @Nullable
    Object createCacheHolder();

}
