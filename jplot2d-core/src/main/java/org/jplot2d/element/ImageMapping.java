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
package org.jplot2d.element;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;
import org.jplot2d.image.ColorMap;
import org.jplot2d.image.IntensityTransform;
import org.jplot2d.image.LimitsAlgorithm;
import org.jplot2d.image.MinMaxAlgorithm;
import org.jplot2d.util.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class defines how to transform a number array to a pseudo-color or grayscale image. The transformation take
 * these steps:
 * <ol>
 * <li>Apply limit algorithm</li>
 * <li>Apply intensity transform</li>
 * <li>Apply bias/gain. method of Schlick{@link <a href=http://dept-info.labri.fr/~schlick/DOC/gem2.ps.gz>(C. Schlick,
 * Fast Alternatives to Perlin's Bias and Gain Functions)</a>}</li>
 * <li>Zoom to the correct size for display</li>
 * <li>Apply color map to produce a pseudo-color image</li>
 * </ol>
 *
 * @author Jingjing Li
 */
@SuppressWarnings("unused")
@PropertyGroup("Image Mapping")
public interface ImageMapping extends Element {

    /**
     * Returns all ImageGraph whose mapping are controlled by this ImageMapping.
     *
     * @return all ImageGraph whose mapping are controlled by this ImageMapping
     */
    @Hierarchy(HierarchyOp.GETARRAY)
    ImageGraph[] getGraphs();

    /**
     * Return the cut limits calculated by LimitsAlgorithm.
     * Maybe <code>null</code> if all images doesn't contain valid data.
     *
     * @return the cut limits
     */
    @Property(order = 0)
    @Nullable
    Range getLimits();

    /**
     * Returns the LimitsAlgorithm.
     *
     * @return the LimitsAlgorithm
     */
    @Nonnull
    @Property(order = 1)
    LimitsAlgorithm getLimitsAlgorithm();

    /**
     * Sets the LimitsAlgorithm to calculate cutting limits. The default algorithm is {@link MinMaxAlgorithm}.
     *
     * @param algo the LimitsAlgorithm
     */
    void setLimitsAlgorithm(@Nonnull LimitsAlgorithm algo);

    /**
     * Returns the IntensityTransform used to enhance images before applying bias/gain.
     *
     * @return the IntensityTransform
     */
    @Nullable
    @Property(order = 2)
    IntensityTransform getIntensityTransform();

    /**
     * Apply the given IntensityTransform to enhance images before applying bias/gain.
     *
     * @param it the IntensityTransform to be applied
     */
    void setIntensityTransform(@Nullable IntensityTransform it);

    /**
     * Returns the bias value. The default value is 0.5.
     *
     * @return the bias value
     */
    @Property(order = 3)
    double getBias();

    /**
     * Sets the bias value. The valid range is [0,1] and the default value is 0.5.
     *
     * @param bias the bias value
     * @see <a href=http://dept-info.labri.fr/~schlick/DOC/gem2.ps.gz>C. Schlick, Fast Alternatives to Perlin's Bias and Gain Functions</a>
     */
    void setBias(double bias);

    /**
     * Returns the gain value. The default value is 0.5.
     *
     * @return the gain value
     */
    @Property(order = 4)
    double getGain();

    /**
     * Sets the gain value. The valid range is [0,1] and the default value is 0.5.
     *
     * @param gain the gain value
     * @see <a href=http://dept-info.labri.fr/~schlick/DOC/gem2.ps.gz>C. Schlick, Fast Alternatives to Perlin's Bias and Gain Functions</a>
     */
    void setGain(double gain);

    /**
     * Returns the ColorMap which convert intensity rasters to displayable images.
     *
     * @return the ColorMap
     */
    @Property(order = 5)
    @Nullable
    ColorMap getColorMap();

    /**
     * Sets a ColorMap to convert intensity rasters to displayable images.
     * The default value is <code>null</code>, to generate grayscale images in sRGB color space.
     *
     * @param colorMap the ColorMap
     */
    void setColorMap(@Nullable ColorMap colorMap);

}
