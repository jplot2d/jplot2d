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
import org.jplot2d.element.Graph;
import org.jplot2d.element.HAlign;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.VAlign;
import org.jplot2d.element.XYGraph;
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
		uspx[0].getTickManager().getAxisTransform().setTransform(TransformType.LOGARITHMIC);
		uspx[1].setLabelVisible(false);

		Axis[] uspy = ef.createAxes(2);
		uspy[0].getTitle().setText("flux density [Jy]");
		uspy[0].getTitle().setFontSize(12);
		uspy[0].getTickManager().setRange(new Range.Double(0.05, 1200));
		uspy[0].getTickManager().getAxisTransform().setTransform(TransformType.LOGARITHMIC);
		uspy[0].getTickManager().setLabelFormat("%.0f");
		uspy[1].setLabelVisible(false);
		usp.addXAxes(uspx);
		usp.addYAxes(uspy);

		// lower subplot Axes
		Axis[] lspx = ef.createAxes(2);
		lspx[0].getTitle().setText("wavelength $\\mathrm{\\lambda}$ [$\\mathrm{\\micro}$m]");
		lspx[0].getTitle().setFontSize(10);
		lspx[0].getTickManager().setRange(new Range.Double(10, 1500));
		lspx[0].getTickManager().getAxisTransform().setTransform(TransformType.LOGARITHMIC);
		lspx[1].setLabelVisible(false);

		Axis[] lspy = ef.createAxes(2);
		lspy[0].getTitle().setText("residual [Jy]");
		lspy[0].getTitle().setFontSize(10);
		lspy[0].getTickManager().setRange(new Range.Double(-0.7, 0.7));
		lspy[0].getTickManager().setTicks(3);
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
		Graph sol = ef.createXYGraphPlotter(solx, soly);
		sol.setColor(Color.BLUE);
		sol.getLegendItem().setVisible(false);
		ulayer.addGraph(sol);

		// dashed line
		double[] dlx = { 10, 2000000 };
		double[] dly = { 0.1, 820 };
		XYGraph dl = ef.createXYGraphPlotter(dlx, dly);
		dl.setColor(Color.BLUE);
		dl.setLineStroke(ef.createStroke(1, new float[] { 1, 3 }));
		dl.getLegendItem().setVisible(false);
		ulayer.addGraph(dl);

		// ISO
		double[] xx = { 15 };
		double[] xy = { 0.1059 };
		double[] xye = { 0.0212 };
		XYGraph xl = ef.createXYGraphPlotter(xx, xy, null, null, xye, xye);
		xl.setColor(Color.GREEN);
		xl.setLineVisible(false);
		xl.setSymbolVisible(true);
		xl.setSymbolShape(SymbolShape.SQUARE);
		xl.getLegendItem().setText("Xilouris et al. 2004");
		ulayer.addGraph(xl);

		// IRAS
		double[] gx = { 24.9, 59.9, 99.8 };
		double[] gy = { 0.187, 0.546, 0.559 };
		double[] gye = { 0.0281, 0.0819, 0.0839 };
		XYGraph gl = ef.createXYGraphPlotter(gx, gy, null, null, gye, gye);
		gl.setColor(Color.GREEN);
		gl.setLineVisible(false);
		gl.setSymbolVisible(true);
		gl.setSymbolShape(SymbolShape.FTRIANGLE);
		gl.getLegendItem().setText("Golombek et al. 1988");
		ulayer.addGraph(gl);

		// MIPS
		double[] sx = { 23.67, 71.3, 156 };
		double[] sy = { 0.171, 0.455, 0.582 };
		double[] sye = { 0.013, 0.0092, 0.01 };
		XYGraph sl = ef.createXYGraphPlotter(sx, sy, null, null, sye, sye);
		sl.setColor(Color.GREEN);
		sl.setLineVisible(false);
		sl.setSymbolVisible(true);
		sl.setSymbolShape(SymbolShape.FDIAMOND);
		sl.getLegendItem().setText("Shi et al. 2007");
		ulayer.addGraph(sl);

		// SCUBA
		double[] hx = { 449, 848 };
		double[] hy = { 1.32, 2.48 };
		double[] hye = { 0.396, 0.496 };
		XYGraph hl = ef.createXYGraphPlotter(hx, hy, null, null, hye, hye);
		hl.setColor(Color.GREEN);
		hl.setLineVisible(false);
		hl.setSymbolVisible(true);
		hl.setSymbolShape(SymbolShape.TRIANGLE);
		hl.getLegendItem().setText("Haas et al. 2004");
		ulayer.addGraph(hl);

		// WMAP
		double[] wx = { 3180, 4910, 7300, 9070, 13000 };
		double[] wy = { 6.2, 9.7, 13.3, 15.5, 19.7 };
		double[] wye = { 0.4, 0.2, 0.1, 0.09, 0.06 };
		XYGraph wl = ef.createXYGraphPlotter(wx, wy, null, null, wye, wye);
		wl.setColor(Color.GREEN);
		wl.setLineVisible(false);
		wl.setSymbolVisible(true);
		wl.setSymbolShape(SymbolShape.STAR);
		wl.getLegendItem().setText("Wright et al. 2009");
		ulayer.addGraph(wl);

		// VLA
		double[] cx = { 20130, 36540, 61730, 180200, 908400 };
		double[] cy = { 26.4, 45.8, 70.1, 136.2, 327 };
		double[] cye = { 2.643, 3.66, 5.61, 10.89, 16.38 };
		XYGraph cl = ef.createXYGraphPlotter(cx, cy, null, null, cye, cye);
		cl.setColor(Color.GREEN);
		cl.setLineVisible(false);
		cl.setSymbolVisible(true);
		cl.setSymbolShape(SymbolShape.FOCTAGON);
		cl.getLegendItem().setText("Cotton et al. 2009");
		ulayer.addGraph(cl);

		// HERSCHEL
		double[] tx = { 100, 160, 250, 350, 500 };
		double[] ty = { 0.517, 0.673, 0.86, 1.074, 1.426 };
		double[] tye = { 0.129, 0.168, 0.215, 0.267, 0.375 };
		XYGraph tl = ef.createXYGraphPlotter(tx, ty, null, null, tye, tye);
		tl.setColor(Color.RED);
		tl.setLineVisible(false);
		tl.setSymbolVisible(true);
		tl.setSymbolShape(SymbolShape.FOCTAGON);
		tl.getLegendItem().setText("this paper");
		ulayer.addGraph(tl);

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
		XYGraph slrl = ef.createXYGraphPlotter(slrx, slry);
		slrl.setColor(Color.BLUE);
		slrl.getLegendItem().setVisible(false);
		llayer.addGraph(slrl);

		double[] xry = { -0.01 };
		XYGraph xrl = ef.createXYGraphPlotter(xx, xry, null, null, xye, xye);
		xrl.setColor(Color.GREEN);
		xrl.setLineVisible(false);
		xrl.setSymbolVisible(true);
		xrl.setSymbolShape(SymbolShape.SQUARE);
		xrl.getLegendItem().setVisible(false);
		llayer.addGraph(xrl);

		double[] gry = { 0.01, 0.2, 0.06 };
		XYGraph grl = ef.createXYGraphPlotter(gx, gry, null, null, gye, gye);
		grl.setColor(Color.GREEN);
		grl.setLineVisible(false);
		grl.setSymbolVisible(true);
		grl.setSymbolShape(SymbolShape.FTRIANGLE);
		grl.getLegendItem().setVisible(false);
		llayer.addGraph(grl);

		double[] sry = { 0.0, 0.07, -0.11 };
		XYGraph srl = ef.createXYGraphPlotter(sx, sry);
		srl.setColor(Color.GREEN);
		srl.setLineVisible(false);
		srl.setSymbolVisible(true);
		srl.setSymbolShape(SymbolShape.FDIAMOND);
		srl.getLegendItem().setVisible(false);
		llayer.addGraph(srl);

		double[] hry = { -0.23, -0.03 };
		XYGraph hrl = ef.createXYGraphPlotter(hx, hry, null, null, hye, hye);
		hrl.setColor(Color.GREEN);
		hrl.setLineVisible(false);
		hrl.setSymbolVisible(true);
		hrl.setSymbolShape(SymbolShape.TRIANGLE);
		hrl.getLegendItem().setVisible(false);
		llayer.addGraph(hrl);

		double[] trry = { 0.01, -0.03, -0.13, -0.21, -0.26 };
		XYGraph trl = ef.createXYGraphPlotter(tx, trry, null, null, tye, tye);
		trl.setColor(Color.RED);
		trl.setLineVisible(false);
		trl.setSymbolVisible(true);
		trl.setSymbolShape(SymbolShape.FOCTAGON);
		trl.getLegendItem().setVisible(false);
		llayer.addGraph(trl);

	}
}
