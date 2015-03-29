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
package org.jplot2d.layout;

import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.Insets2D;

/**
 * This LayoutDirector overlay all plots over the top of each other. All subplots are put inside the
 * base subplot's content area. The content bounds of subplots are defined by
 * {@link BoundsConstraint} .
 *
 * @author Jingjing Li
 */
public class OverlayLayoutDirector extends SimpleLayoutDirector {

    public void layout(PlotEx plot) {

        super.layout(plot);

        // layout overlay subplots
        layoutSubplots(plot);

    }

    /**
     * layout overlay sub-plots over the base sub-plot.
     *
     * @param plot the plot
     */
    private void layoutSubplots(PlotEx plot) {
        if (plot.getSubplots().length == 0) {
            return;
        }

        double baseX = 0;
        double baseY = 0;
        double baseW = plot.getContentSize().getWidth();
        double baseH = plot.getContentSize().getHeight();

        for (PlotEx sp : plot.getSubplots()) {

            double spacingL = 0;
            double spacingR = 0;
            double spacingT = 0;
            double spacingB = 0;

            BoundsConstraint bc = (BoundsConstraint) getConstraint(sp);
            if (bc != null) {
                Insets2D fixed = bc.getFixedInsets();
                Insets2D elastic = bc.getElasticInsets();
                spacingL = fixed.getLeft() + elastic.getLeft() * baseW;
                spacingR = fixed.getRight() + elastic.getRight() * baseW;
                spacingT = fixed.getTop() + elastic.getTop() * baseH;
                spacingB = fixed.getBottom() + elastic.getBottom() * baseH;
            }

            double spbX = baseX + spacingL;
            double spbY = baseY + spacingB;
            double spbW = baseW - spacingL - spacingR;
            double spbH = baseH - spacingT - spacingB;

            // locate plot
            sp.setLocation(spbX, spbY);
            sp.setContentConstraint(new DoubleDimension2D(spbW, spbH));
        }

    }

}
