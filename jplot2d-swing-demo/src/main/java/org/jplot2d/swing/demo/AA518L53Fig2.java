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
package org.jplot2d.swing.demo;

import java.awt.Color;

import javax.swing.JFrame;

import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Axis;
import org.jplot2d.element.GraphPlotter;
import org.jplot2d.element.HAlign;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.VAlign;
import org.jplot2d.element.XYGraphPlotter;
import org.jplot2d.layout.GridConstraint;
import org.jplot2d.layout.GridLayoutDirector;
import org.jplot2d.sizing.AutoPackSizeMode;
import org.jplot2d.swing.JPlot2DFrame;
import org.jplot2d.transform.TransformType;
import org.jplot2d.util.Range;
import org.jplot2d.util.SymbolShape;

/**
 * @author Jingjing Li
 * 
 */
public class AA518L53Fig2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// ElementFactory
		ElementFactory ef = ElementFactory.getInstance();

		// Create plot
		Plot p = ef.createPlot();
		p.setSizeMode(new AutoPackSizeMode());
		JFrame frame = new JPlot2DFrame(p);
		frame.setSize(480, 480);
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
		Axis[] uspx = ef.createAxes(2);
		uspx[0].getTitle().setText("wavelength $\\mathrm{\\lambda}$ [$\\mathrm{\\micro}$m]");
		uspx[0].getTitle().setFontSize(10);
		uspx[0].getTickManager().setRange(new Range.Double(10, 2e6));
		uspx[0].getTickManager().getAxisTransform().setType(TransformType.LOGARITHMIC);
		uspx[1].setLabelVisible(false);

		Axis[] uspy = ef.createAxes(2);
		uspy[0].getTitle().setText("flux density [Jy]");
		uspy[0].getTitle().setFontSize(12);
		uspy[0].getTickManager().setRange(new Range.Double(0.05, 1200));
		uspy[0].getTickManager().getAxisTransform().setType(TransformType.LOGARITHMIC);
		uspy[0].getTickManager().setLabelFormat("%.0f");
		uspy[1].setLabelVisible(false);
		usp.addXAxes(uspx);
		usp.addYAxes(uspy);

		// lower subplot Axes
		Axis[] lspx = ef.createAxes(2);
		lspx[0].getTitle().setText("wavelength $\\mathrm{\\lambda}$ [$\\mathrm{\\micro}$m]");
		lspx[0].getTitle().setFontSize(10);
		lspx[0].getTickManager().setRange(new Range.Double(10, 1500));
		lspx[0].getTickManager().getAxisTransform().setType(TransformType.LOGARITHMIC);
		lspx[1].setLabelVisible(false);

		Axis[] lspy = ef.createAxes(2);
		lspy[0].getTitle().setText("residual [Jy]");
		lspy[0].getTitle().setFontSize(10);
		lspy[0].getTickManager().setRange(new Range.Double(-0.7, 0.7));
		lspy[0].getTickManager().setNumber(3);
		lspy[1].setLabelVisible(false);
		lsp.addXAxes(lspx);
		lsp.addYAxes(lspy);

		// Layer
		Layer ulayer = ef.createLayer();
		usp.addLayer(ulayer, uspx[0], uspy[0]);
		Layer llayer = ef.createLayer();
		lsp.addLayer(llayer, lspx[0], lspy[0]);

		// solid line
		double[] solx = { 10, 2000000 };
		double[] soly = { 0.09, 900 };
		GraphPlotter sol = ef.createXYGraphPlotter(solx, soly);
		sol.setColor(Color.BLUE);
		sol.getLegendItem().setVisible(false);
		ulayer.addGraphPlotter(sol);

		// dashed line
		double[] dlx = { 10, 2000000 };
		double[] dly = { 0.1, 820 };
		XYGraphPlotter dl = ef.createXYGraphPlotter(dlx, dly);
		dl.setColor(Color.BLUE);
		dl.setLineStroke(ef.createStroke(1, new float[] { 1, 3 }));
		dl.getLegendItem().setVisible(false);
		ulayer.addGraphPlotter(dl);

		// ISO
		double[] xx = { 15 };
		double[] xy = { 0.1059 };
		double[] xye = { 0.0212 };
		XYGraphPlotter xl = ef.createXYGraphPlotter(xx, xy, null, null, xye, xye);
		xl.setColor(Color.GREEN);
		xl.setLinesVisible(false);
		xl.setSymbolsVisible(true);
		xl.setSymbolShape(SymbolShape.SQUARE);
		xl.getLegendItem().setText("Xilouris et al. 2004");
		ulayer.addGraphPlotter(xl);

		// IRAS
		double[] gx = { 24.9, 59.9, 99.8 };
		double[] gy = { 0.187, 0.546, 0.559 };
		double[] gye = { 0.0281, 0.0819, 0.0839 };
		XYGraphPlotter gl = ef.createXYGraphPlotter(gx, gy, null, null, gye, gye);
		gl.setColor(Color.GREEN);
		gl.setLinesVisible(false);
		gl.setSymbolsVisible(true);
		gl.setSymbolShape(SymbolShape.FTRIANGLE);
		gl.getLegendItem().setText("Golombek et al. 1988");
		ulayer.addGraphPlotter(gl);

		// MIPS
		double[] sx = { 23.67, 71.3, 156 };
		double[] sy = { 0.171, 0.455, 0.582 };
		double[] sye = { 0.013, 0.0092, 0.01 };
		XYGraphPlotter sl = ef.createXYGraphPlotter(sx, sy, null, null, sye, sye);
		sl.setColor(Color.GREEN);
		sl.setLinesVisible(false);
		sl.setSymbolsVisible(true);
		sl.setSymbolShape(SymbolShape.FDIAMOND);
		sl.getLegendItem().setText("Shi et al. 2007");
		ulayer.addGraphPlotter(sl);

		// SCUBA
		double[] hx = { 449, 848 };
		double[] hy = { 1.32, 2.48 };
		double[] hye = { 0.396, 0.496 };
		XYGraphPlotter hl = ef.createXYGraphPlotter(hx, hy, null, null, hye, hye);
		hl.setColor(Color.GREEN);
		hl.setLinesVisible(false);
		hl.setSymbolsVisible(true);
		hl.setSymbolShape(SymbolShape.TRIANGLE);
		hl.getLegendItem().setText("Haas et al. 2004");
		ulayer.addGraphPlotter(hl);

		// WMAP
		double[] wx = { 3180, 4910, 7300, 9070, 13000 };
		double[] wy = { 6.2, 9.7, 13.3, 15.5, 19.7 };
		double[] wye = { 0.4, 0.2, 0.1, 0.09, 0.06 };
		XYGraphPlotter wl = ef.createXYGraphPlotter(wx, wy, null, null, wye, wye);
		wl.setColor(Color.GREEN);
		wl.setLinesVisible(false);
		wl.setSymbolsVisible(true);
		wl.setSymbolShape(SymbolShape.STAR);
		wl.getLegendItem().setText("Wright et al. 2009");
		ulayer.addGraphPlotter(wl);

		// VLA
		double[] cx = { 20130, 36540, 61730, 180200, 908400 };
		double[] cy = { 26.4, 45.8, 70.1, 136.2, 327 };
		double[] cye = { 2.643, 3.66, 5.61, 10.89, 16.38 };
		XYGraphPlotter cl = ef.createXYGraphPlotter(cx, cy, null, null, cye, cye);
		cl.setColor(Color.GREEN);
		cl.setLinesVisible(false);
		cl.setSymbolsVisible(true);
		cl.setSymbolShape(SymbolShape.FOCTAGON);
		cl.getLegendItem().setText("Cotton et al. 2009");
		ulayer.addGraphPlotter(cl);

		// HERSCHEL
		double[] tx = { 100, 160, 250, 350, 500 };
		double[] ty = { 0.517, 0.673, 0.86, 1.074, 1.426 };
		double[] tye = { 0.129, 0.168, 0.215, 0.267, 0.375 };
		XYGraphPlotter tl = ef.createXYGraphPlotter(tx, ty, null, null, tye, tye);
		tl.setColor(Color.RED);
		tl.setLinesVisible(false);
		tl.setSymbolsVisible(true);
		tl.setSymbolShape(SymbolShape.FOCTAGON);
		tl.getLegendItem().setText("this paper");
		ulayer.addGraphPlotter(tl);

		// legend
		usp.getLegend().setPosition(null);
		usp.getLegend().setColumns(1);
		usp.getLegend().setLocation(-10, 250);
		usp.getLegend().setHAlign(HAlign.LEFT);
		usp.getLegend().setVAlign(VAlign.TOP);
		usp.getLegend().setBorderVisible(false);
		usp.getLegend().setFontSize(12);

		// residual
		double[] slrx = { 10, 1000 };
		double[] slry = { 0, 0 };
		XYGraphPlotter slrl = ef.createXYGraphPlotter(slrx, slry);
		slrl.setColor(Color.BLUE);
		slrl.getLegendItem().setVisible(false);
		llayer.addGraphPlotter(slrl);

		double[] xry = { -0.01 };
		XYGraphPlotter xrl = ef.createXYGraphPlotter(xx, xry, null, null, xye, xye);
		xrl.setColor(Color.GREEN);
		xrl.setLinesVisible(false);
		xrl.setSymbolsVisible(true);
		xrl.setSymbolShape(SymbolShape.SQUARE);
		xrl.getLegendItem().setVisible(false);
		llayer.addGraphPlotter(xrl);

		double[] gry = { 0.01, 0.2, 0.06 };
		XYGraphPlotter grl = ef.createXYGraphPlotter(gx, gry, null, null, gye, gye);
		grl.setColor(Color.GREEN);
		grl.setLinesVisible(false);
		grl.setSymbolsVisible(true);
		grl.setSymbolShape(SymbolShape.FTRIANGLE);
		grl.getLegendItem().setVisible(false);
		llayer.addGraphPlotter(grl);

		double[] sry = { 0.0, 0.07, -0.11 };
		XYGraphPlotter srl = ef.createXYGraphPlotter(sx, sry);
		srl.setColor(Color.GREEN);
		srl.setLinesVisible(false);
		srl.setSymbolsVisible(true);
		srl.setSymbolShape(SymbolShape.FDIAMOND);
		srl.getLegendItem().setVisible(false);
		llayer.addGraphPlotter(srl);

		double[] hry = { -0.23, -0.03 };
		XYGraphPlotter hrl = ef.createXYGraphPlotter(hx, hry, null, null, hye, hye);
		hrl.setColor(Color.GREEN);
		hrl.setLinesVisible(false);
		hrl.setSymbolsVisible(true);
		hrl.setSymbolShape(SymbolShape.TRIANGLE);
		hrl.getLegendItem().setVisible(false);
		llayer.addGraphPlotter(hrl);

		double[] trry = { 0.01, -0.03, -0.13, -0.21, -0.26 };
		XYGraphPlotter trl = ef.createXYGraphPlotter(tx, trry, null, null, tye, tye);
		trl.setColor(Color.RED);
		trl.setLinesVisible(false);
		trl.setSymbolsVisible(true);
		trl.setSymbolShape(SymbolShape.FOCTAGON);
		trl.getLegendItem().setVisible(false);
		llayer.addGraphPlotter(trl);

	}
}