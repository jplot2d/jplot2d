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
package org.jplot2d.swing.demo;

import org.jplot2d.element.*;
import org.jplot2d.layout.GridConstraint;
import org.jplot2d.layout.GridLayoutDirector;
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.swing.JPlot2DFrame;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;
import org.jplot2d.util.SymbolShape;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jingjing Li
 */
public class AA518L53Fig2 {

    public static void main(String[] args) {

        // ElementFactory
        ElementFactory ef = ElementFactory.getInstance();

        // Create plot
        Plot p = ef.createPlot();
        p.setSizeMode(new AutoPackSizeMode());
        JFrame frame = new JPlot2DFrame(p);
        frame.setSize(480, 600);
        frame.setVisible(true);

        // create subplots
        Plot usp = ef.createSubplot();
        Plot lsp = ef.createSubplot();
        usp.setPreferredContentSize(380, 260);
        lsp.setPreferredContentSize(380, 160);
        lsp.getMargin().setExtraTop(10);
        p.setLayoutDirector(new GridLayoutDirector());
        p.addSubplot(usp, new GridConstraint(0, 1));
        p.addSubplot(lsp, new GridConstraint(0, 0));

        // upper subplot Axes
        PlotAxis[] uspx = ef.createAxes(2);
        uspx[0].getTitle().setText("wavelength $\\mathrm{\\lambda}$ [$\\mathrm{\\micro}$m]");
        uspx[0].getTitle().setFontSize(10);
        uspx[0].getTickManager().setRange(new Range.Double(10, 2e6));
        uspx[0].getTickManager().getAxisTransform().setTransform(TransformType.LOGARITHMIC);
        uspx[1].setLabelVisible(false);

        PlotAxis[] uspy = ef.createAxes(2);
        uspy[0].getTitle().setText("flux density [Jy]");
        uspy[0].getTitle().setFontSize(12);
        uspy[0].getTickManager().setRange(new Range.Double(0.05, 1200));
        uspy[0].getTickManager().getAxisTransform().setTransform(TransformType.LOGARITHMIC);
        uspy[0].getTickManager().setLabelFormat("%.0f");
        uspy[1].setLabelVisible(false);
        usp.addXAxes(uspx);
        usp.addYAxes(uspy);

        // lower subplot Axes
        PlotAxis[] lspx = ef.createAxes(2);
        lspx[0].getTitle().setText("wavelength $\\mathrm{\\lambda}$ [$\\mathrm{\\micro}$m]");
        lspx[0].getTitle().setFontSize(10);
        lspx[0].getTickManager().setRange(new Range.Double(10, 1500));
        lspx[0].getTickManager().getAxisTransform().setTransform(TransformType.LOGARITHMIC);
        lspx[1].setLabelVisible(false);

        PlotAxis[] lspy = ef.createAxes(2);
        lspy[0].getTitle().setText("residual [Jy]");
        lspy[0].getTitle().setFontSize(10);
        lspy[0].getTickManager().setRange(new Range.Double(-0.7, 0.7));
        lspy[0].getTickManager().setTickNumber(3);
        lspy[1].setLabelVisible(false);
        lsp.addXAxes(lspx);
        lsp.addYAxes(lspy);

        // Layer
        Layer ulayer = ef.createLayer();
        usp.addLayer(ulayer, uspx[0], uspy[0]);
        Layer llayer = ef.createLayer();
        lsp.addLayer(llayer, lspx[0], lspy[0]);

        // solid line
        double[] solx = {10, 2000000};
        double[] soly = {0.09, 900};
        XYGraph sol = ef.createXYGraph(solx, soly);
        sol.setColor(Color.BLUE);
        sol.getLegendItem().setVisible(false);
        ulayer.addGraph(sol);

        // dashed line
        double[] dlx = {10, 2000000};
        double[] dly = {0.1, 820};
        XYGraph dl = ef.createXYGraph(dlx, dly);
        dl.setColor(Color.BLUE);
        dl.setLineStroke(ef.createStroke(1, new float[]{1, 3}));
        dl.getLegendItem().setVisible(false);
        ulayer.addGraph(dl);

        // ISO
        double[] xx = {15};
        double[] xy = {0.1059};
        double[] xye = {0.0212};
        XYGraph xl = ef.createXYGraph(xx, xy, null, null, xye, xye, "Xilouris et al. 2004");
        xl.setColor(Color.GREEN);
        xl.setLineVisible(false);
        xl.setSymbolVisible(true);
        xl.setSymbolShape(SymbolShape.SQUARE);
        ulayer.addGraph(xl);

        // IRAS
        double[] gx = {24.9, 59.9, 99.8};
        double[] gy = {0.187, 0.546, 0.559};
        double[] gye = {0.0281, 0.0819, 0.0839};
        XYGraph gl = ef.createXYGraph(gx, gy, null, null, gye, gye, "Golombek et al. 1988");
        gl.setColor(Color.GREEN);
        gl.setLineVisible(false);
        gl.setSymbolVisible(true);
        gl.setSymbolShape(SymbolShape.FTRIANGLE);
        ulayer.addGraph(gl);

        // MIPS
        double[] sx = {23.67, 71.3, 156};
        double[] sy = {0.171, 0.455, 0.582};
        double[] sye = {0.013, 0.0092, 0.01};
        XYGraph sl = ef.createXYGraph(sx, sy, null, null, sye, sye, "Shi et al. 2007");
        sl.setColor(Color.GREEN);
        sl.setLineVisible(false);
        sl.setSymbolVisible(true);
        sl.setSymbolShape(SymbolShape.FDIAMOND);
        ulayer.addGraph(sl);

        // SCUBA
        double[] hx = {449, 848};
        double[] hy = {1.32, 2.48};
        double[] hye = {0.396, 0.496};
        XYGraph hl = ef.createXYGraph(hx, hy, null, null, hye, hye, "Haas et al. 2004");
        hl.setColor(Color.GREEN);
        hl.setLineVisible(false);
        hl.setSymbolVisible(true);
        hl.setSymbolShape(SymbolShape.TRIANGLE);
        ulayer.addGraph(hl);

        // WMAP
        double[] wx = {3180, 4910, 7300, 9070, 13000};
        double[] wy = {6.2, 9.7, 13.3, 15.5, 19.7};
        double[] wye = {0.4, 0.2, 0.1, 0.09, 0.06};
        XYGraph wl = ef.createXYGraph(wx, wy, null, null, wye, wye, "Wright et al. 2009");
        wl.setColor(Color.GREEN);
        wl.setLineVisible(false);
        wl.setSymbolVisible(true);
        wl.setSymbolShape(SymbolShape.STAR);
        ulayer.addGraph(wl);

        // VLA
        double[] cx = {20130, 36540, 61730, 180200, 908400};
        double[] cy = {26.4, 45.8, 70.1, 136.2, 327};
        double[] cye = {2.643, 3.66, 5.61, 10.89, 16.38};
        XYGraph cl = ef.createXYGraph(cx, cy, null, null, cye, cye, "Cotton et al. 2009");
        cl.setColor(Color.GREEN);
        cl.setLineVisible(false);
        cl.setSymbolVisible(true);
        cl.setSymbolShape(SymbolShape.FOCTAGON);
        ulayer.addGraph(cl);

        // HERSCHEL
        double[] tx = {100, 160, 250, 350, 500};
        double[] ty = {0.517, 0.673, 0.86, 1.074, 1.426};
        double[] tye = {0.129, 0.168, 0.215, 0.267, 0.375};
        XYGraph tl = ef.createXYGraph(tx, ty, null, null, tye, tye, "this paper");
        tl.setColor(Color.RED);
        tl.setLineVisible(false);
        tl.setSymbolVisible(true);
        tl.setSymbolShape(SymbolShape.FOCTAGON);
        ulayer.addGraph(tl);

        // legend
        usp.getLegend().setPosition(LegendPosition.FREE);
        usp.getLegend().setColumns(1);
        usp.getLegend().setLocation(-10, 250);
        usp.getLegend().setHAlign(HAlign.LEFT);
        usp.getLegend().setVAlign(VAlign.TOP);
        usp.getLegend().setBorderVisible(false);
        usp.getLegend().setFontSize(12);

        // residual
        double[] slrx = {10, 1000};
        double[] slry = {0, 0};
        XYGraph slrl = ef.createXYGraph(slrx, slry);
        slrl.setColor(Color.BLUE);
        slrl.getLegendItem().setVisible(false);
        llayer.addGraph(slrl);

        double[] xry = {-0.01};
        XYGraph xrl = ef.createXYGraph(xx, xry, null, null, xye, xye);
        xrl.setColor(Color.GREEN);
        xrl.setLineVisible(false);
        xrl.setSymbolVisible(true);
        xrl.setSymbolShape(SymbolShape.SQUARE);
        xrl.getLegendItem().setVisible(false);
        llayer.addGraph(xrl);

        double[] gry = {0.01, 0.2, 0.06};
        XYGraph grl = ef.createXYGraph(gx, gry, null, null, gye, gye);
        grl.setColor(Color.GREEN);
        grl.setLineVisible(false);
        grl.setSymbolVisible(true);
        grl.setSymbolShape(SymbolShape.FTRIANGLE);
        grl.getLegendItem().setVisible(false);
        llayer.addGraph(grl);

        double[] sry = {0.0, 0.07, -0.11};
        XYGraph srl = ef.createXYGraph(sx, sry);
        srl.setColor(Color.GREEN);
        srl.setLineVisible(false);
        srl.setSymbolVisible(true);
        srl.setSymbolShape(SymbolShape.FDIAMOND);
        srl.getLegendItem().setVisible(false);
        llayer.addGraph(srl);

        double[] hry = {-0.23, -0.03};
        XYGraph hrl = ef.createXYGraph(hx, hry, null, null, hye, hye);
        hrl.setColor(Color.GREEN);
        hrl.setLineVisible(false);
        hrl.setSymbolVisible(true);
        hrl.setSymbolShape(SymbolShape.TRIANGLE);
        hrl.getLegendItem().setVisible(false);
        llayer.addGraph(hrl);

        double[] trry = {0.01, -0.03, -0.13, -0.21, -0.26};
        XYGraph trl = ef.createXYGraph(tx, trry, null, null, tye, tye);
        trl.setColor(Color.RED);
        trl.setLineVisible(false);
        trl.setSymbolVisible(true);
        trl.setSymbolShape(SymbolShape.FOCTAGON);
        trl.getLegendItem().setVisible(false);
        llayer.addGraph(trl);

    }
}
