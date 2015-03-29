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
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;
import org.jplot2d.image.IntensityTransform;
import org.jplot2d.image.LimitsAlgorithm;

/**
 * This class defines how to transform a band of image. The transformation takes these steps:
 * <ol>
 * <li>Apply limit algorithm</li>
 * <li>Apply intensity transform</li>
 * <li>Apply bias/gain. method of Schlick{@link <a href=http://dept-info.labri.fr/~schlick/DOC/gem2.ps.gz>
 * (C. Schlick, Fast Alternatives to Perlin's Bias and Gain Functions)</a>}</li>
 * </ol>
 *
 * @author Jingjing Li
 */
@PropertyGroup("Image Band Transform")
public interface ImageBandTransform extends Element {

    @Hierarchy(HierarchyOp.GET)
    public RGBImageMapping getParent();

    /**
     * Returns the LimitsAlgorithm
     *
     * @return the LimitsAlgorithm
     */
    @Property(order = 0)
    public LimitsAlgorithm getLimitsAlgorithm();

    /**
     * Sets the LimitsAlgorithm
     *
     * @param algo the LimitsAlgorithm
     */
    public void setLimitsAlgorithm(LimitsAlgorithm algo);

    @Property(order = 1)
    public IntensityTransform getIntensityTransform();

    public void setIntensityTransform(IntensityTransform it);

    @Property(order = 2)
    public double getBias();

    public void setBias(double bias);

    @Property(order = 3)
    public double getGain();

    public void setGain(double gain);

}
