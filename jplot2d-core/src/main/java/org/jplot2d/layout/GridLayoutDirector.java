/*
 * Copyright 2010-2016 Jingjing Li.
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
package org.jplot2d.layout;

import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.SparseDoubleArray;

import javax.annotation.Nonnull;
import java.awt.geom.Dimension2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Subplots are placed side-bye-side. The plot space are distributed to subplots according to ratio of their preferedContentSize.
 *
 * @author Jingjing Li
 */
public class GridLayoutDirector extends SimpleLayoutDirector {

    private final double hgap;

    private final double vgap;

    private final Map<PlotEx, GridCellGeom> plotsPreferredContentsGeomMap = new HashMap<>();

    private final Map<PlotEx, GridCellInsets> plotsMarginGeomMap = new HashMap<>();

    /**
     * Create a GridLayoutDirector with 0 horizontal gap and 0 vertical gap.
     */
    public GridLayoutDirector() {
        this(0, 0);
    }

    /**
     * Create a GridLayoutDirector with the given horizontal gap and vertical gap.
     *
     * @param hgap the horizontal gap
     * @param vgap the vertical gap
     */
    public GridLayoutDirector(double hgap, double vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
    }

    private static Map<PlotEx, AxesInPlot> getSubplotAxisMap(PlotEx[] subplots) {

        Map<PlotEx, AxesInPlot> glaMap = new HashMap<>();

		/*
         * Gather metrics for every axis. Iterate over grid to ensure the axes' z-order.
		 */
        for (PlotEx subplot : subplots) {
            AxesInPlot ais = getAllAxes(subplot);
            glaMap.put(subplot, ais);
        }

        return glaMap;
    }

    public double getHGap() {
        return hgap;
    }

    public double getVGap() {
        return vgap;
    }

    public void setConstraint(PlotEx plot, Object constraint) {
        if (constraint == null || constraint instanceof GridConstraint) {
            super.setConstraint(plot, constraint);
        } else {
            throw new IllegalArgumentException("cannot add to layout: constraint must be a GridConstraint (or null)");
        }
    }

    public void invalidateLayout(PlotEx plot) {
        plotsPreferredContentsGeomMap.remove(plot);
        plotsMarginGeomMap.remove(plot);
    }

    public void layout(PlotEx plot) {

        super.layout(plot);

        GridCellGeom contGeom = getSubplotsPreferredContentsGeom(plot);
        GridCellInsets marginGeom = getSubplotsMarginGeom(plot);

        // calculate contents size factor of subplots.
        Dimension2D scsFactor = calcSubplotContentFactor(plot, marginGeom);

        SparseDoubleArray cellWidthMap = new SparseDoubleArray();
        SparseDoubleArray cellHeightMap = new SparseDoubleArray();

        for (PlotEx sp : plot.getSubplots()) {
            GridConstraint grid = (GridConstraint) getConstraint(sp);
            int col = (grid == null) ? 0 : grid.getGridX();
            int row = (grid == null) ? 0 : grid.getGridY();

            double contWidth = scsFactor.getWidth() * contGeom.getWidth(col);
            double contHeight = scsFactor.getHeight() * contGeom.getHeight(row);

			/*
             * impose viewport constraint on its children.
			 */
            sp.setContentConstraint(new DoubleDimension2D(contWidth, contHeight));

            double cw = marginGeom.getLeft(col) + contWidth + marginGeom.getRight(col);
            double ch = marginGeom.getTop(row) + contHeight + marginGeom.getBottom(row);

            if (cellWidthMap.get(col, -1) < cw) {
                cellWidthMap.put(col, cw);
            }
            if (cellHeightMap.get(row, -1) < ch) {
                cellHeightMap.put(row, ch);
            }
        }

        GridCellGeom cellGeom = new GridCellGeom(cellWidthMap, cellHeightMap);

        for (PlotEx sp : plot.getSubplots()) {
            GridConstraint grid = (GridConstraint) getConstraint(sp);
            int col = (grid == null) ? 0 : grid.getGridX();
            int row = (grid == null) ? 0 : grid.getGridY();

            // the paper bottom-left of a grid in chart(root layer)
            double cws = cellGeom.getSumWidthLeft(col);
            double chs = cellGeom.getSumHeightBelow(row);
            double pX = cws + marginGeom.getLeft(col) + cellWidthMap.indexOfKey(col) * hgap;
            double pY = chs + marginGeom.getBottom(row) + cellHeightMap.indexOfKey(row) * vgap;

            sp.setLocation(pX, pY);
        }

    }

    /**
     * calculate the contents size factor for the given plot.
     *
     * @param plot    the plot
     * @param padding the margin of every subplot in the given plot
     * @return contents size factor
     */
    private Dimension2D calcSubplotContentFactor(PlotEx plot, GridCellInsets padding) {

        double ctsWidth = plot.getContentSize().getWidth();
        double ctsHeight = plot.getContentSize().getHeight();

        GridCellGeom contGeom = getSubplotsPreferredContentsGeom(plot);

        double sumWeightX = contGeom.getSumWidth();
        double sumWeightY = contGeom.getSumHeight();

        double xn = contGeom.getColNum();
        double yn = contGeom.getRowNum();

        double sumXpad = padding.getSumWidth();
        double sumYpad = padding.getSumHeight();

        double xfactor, yfactor;
        if (xn == 0 || yn == 0) {
            xfactor = ctsWidth;
            yfactor = ctsHeight;
        } else {
            xfactor = (ctsWidth - sumXpad - hgap * (xn - 1)) / sumWeightX;
            yfactor = (ctsHeight - sumYpad - vgap * (yn - 1)) / sumWeightY;
        }

        return new DoubleDimension2D(xfactor, yfactor);
    }

    @Nonnull
    public Dimension2D getPreferredContentSize(PlotEx plot) {
        GridCellGeom contentGeom = getSubplotsPreferredContentsGeom(plot);
        GridCellInsets marginGeom = getSubplotsMarginGeom(plot);

        SparseDoubleArray colWidthMap = new SparseDoubleArray();
        SparseDoubleArray rowHeightMap = new SparseDoubleArray();
        for (PlotEx sp : plot.getSubplots()) {
            GridConstraint grid = (GridConstraint) getConstraint(sp);
            int col = (grid == null) ? 0 : grid.getGridX();
            int row = (grid == null) ? 0 : grid.getGridY();
            double width = contentGeom.getWidth(col) + marginGeom.getLeft(col) + marginGeom.getRight(col);
            double Height = contentGeom.getHeight(row) + marginGeom.getTop(row) + marginGeom.getBottom(row);
            if (colWidthMap.get(col, -1) < width) {
                colWidthMap.put(col, width);
            }
            if (rowHeightMap.get(row, -1) < Height) {
                rowHeightMap.put(row, Height);
            }
        }

        GridCellGeom geom = new GridCellGeom(colWidthMap, rowHeightMap);
        double width = geom.getSumWidth() + hgap * (colWidthMap.size() - 1);
        double height = geom.getSumHeight() + vgap * (rowHeightMap.size() - 1);
        Dimension2D spcs = plot.getPreferredContentSize();
        if (width < spcs.getWidth()) {
            width = spcs.getWidth();
        }
        if (height < spcs.getHeight()) {
            height = spcs.getHeight();
        }
        return new DoubleDimension2D(width, height);
    }

    /**
     * Returns row width and column height of the subplots of the given plot.
     *
     * @param plot the plot
     * @return the geometry of every subplot of the given plot
     */
    private GridCellGeom getSubplotsPreferredContentsGeom(PlotEx plot) {
        if (plotsPreferredContentsGeomMap.containsKey(plot)) {
            return plotsPreferredContentsGeomMap.get(plot);
        }

        SparseDoubleArray colWidthMap = new SparseDoubleArray();
        SparseDoubleArray rowHeightMap = new SparseDoubleArray();
        for (PlotEx sp : plot.getSubplots()) {
            GridConstraint grid = (GridConstraint) getConstraint(sp);
            int col = (grid == null) ? 0 : grid.getGridX();
            int row = (grid == null) ? 0 : grid.getGridY();

            Dimension2D contentSize = sp.getLayoutDirector().getPreferredContentSize(sp);
            double width = contentSize.getWidth();
            double Height = contentSize.getHeight();

            if (colWidthMap.get(col, -1) < width) {
                colWidthMap.put(col, width);
            }
            if (rowHeightMap.get(row, -1) < Height) {
                rowHeightMap.put(row, Height);
            }
        }

        GridCellGeom geom = new GridCellGeom(colWidthMap, rowHeightMap);
        plotsPreferredContentsGeomMap.put(plot, geom);
        return geom;
    }

    /**
     * calculate the margin (from grid bounds to data area bounds) for every row and columns of subplots in the given plot.
     *
     * @return the margin
     */
    private GridCellInsets getSubplotsMarginGeom(PlotEx plot) {
        if (plotsMarginGeomMap.containsKey(plot)) {
            return plotsMarginGeomMap.get(plot);
        }

        Map<PlotEx, AxesInPlot> saMap = getSubplotAxisMap(plot.getSubplots());

        SparseDoubleArray topMargin = new SparseDoubleArray();
        SparseDoubleArray leftMargin = new SparseDoubleArray();
        SparseDoubleArray bottomMargin = new SparseDoubleArray();
        SparseDoubleArray rightMargin = new SparseDoubleArray();

        for (Map.Entry<PlotEx, AxesInPlot> me : saMap.entrySet()) {
            PlotEx sp = me.getKey();
            AxesInPlot ais = me.getValue();
            GridConstraint grid = (GridConstraint) getConstraint(sp);
            int col = (grid == null) ? 0 : grid.getGridX();
            int row = (grid == null) ? 0 : grid.getGridY();

            double mLeft = calcLeftMargin(sp, ais.leftAxes);
            double mRight = calcRightMargin(sp, ais.rightAxes);
            double mTop = calcTopMargin(sp, ais.topAxes);
            double mBottom = calcBottomMargin(sp, ais.bottomAxes);

            if (leftMargin.get(col, -1) < mLeft) {
                leftMargin.put(col, mLeft);
            }
            if (rightMargin.get(col, -1) < mRight) {
                rightMargin.put(col, mRight);
            }
            if (topMargin.get(row, -1) < mTop) {
                topMargin.put(row, mTop);
            }
            if (bottomMargin.get(row, -1) < mBottom) {
                bottomMargin.put(row, mBottom);
            }
        }

        GridCellInsets insets = new GridCellInsets(topMargin, leftMargin, bottomMargin, rightMargin);
        plotsMarginGeomMap.put(plot, insets);
        return insets;
    }

}
