/**
 * Copyright 2010-2012 Jingjing Li.
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

import org.jplot2d.element.AxisTransform;
import org.jplot2d.transform.NormalTransform;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;

/**
 * @author Jingjing Li
 */
public interface AxisTransformEx extends AxisTransform, ElementEx, Joinable {

    public AxisTickManagerEx getParent();

    public AxisTickManagerEx[] getTickManagers();

    public AxisRangeLockGroupEx getLockGroup();

    public int indexOfTickManager(AxisTickManagerEx tickManager);

    /**
     * Sets the normal transform of this axis
     *
     * @param ntf the normal transform
     */
    public void setNormalTransform(NormalTransform ntf);

    public void addTickManager(AxisTickManagerEx tickManager);

    public void removeTickManager(AxisTickManagerEx tickManager);

    public LayerEx[] getLayers();

    /**
     * Called when a layer attach to this viewport axis
     *
     * @param layer the layer
     */
    public void addLayer(LayerEx layer);

    /**
     * Called when a layer detach from this viewport axis
     *
     * @param layer the layer
     */
    public void removeLayer(LayerEx layer);

    public void linkLayer(LayerEx layer);

    public Range expandRangeToTick(Range ur);

    public void changeTransformType(TransformType txfType);

}
