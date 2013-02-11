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
package org.jplot2d.env;

import static org.jplot2d.util.TestUtils.*;
import static org.junit.Assert.*;

import java.awt.Color;

import org.jplot2d.element.Axis;
import org.jplot2d.element.ElementFactory;
import org.jplot2d.element.Plot;
import org.jplot2d.element.Title;
import org.jplot2d.element.impl.PlotEx;
import org.jplot2d.util.Range;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test undo and redo of PlotEnvironment
 * 
 * @author Jingjing Li
 * 
 */
public class UndoRedoTest {

	private static ElementFactory ef = ElementFactory.getInstance();

	@BeforeClass
	public static void setUpBeforeClass() {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void undoPropertyTest() {
		Plot plot = ef.createPlot();
		PlotEx plotImpl0 = (PlotEx) ((ElementAddition) plot).getImpl();
		PlotEnvironment env = new PlotEnvironment(false);
		env.setPlot(plot);

		assertEquals(plot.getColor(), Color.BLACK);
		assertSame(env.plot, plot);
		assertSame(env.plotImpl, plotImpl0);
		assertEquals(env.copyMap.size(), 3);
		assertTrue(env.copyMap.containsKey(plotImpl0));
		assertTrue(env.copyMap.containsKey(plotImpl0.getMargin()));
		assertTrue(env.copyMap.containsKey(plotImpl0.getLegend()));
		PlotEx safeCopy0 = (PlotEx) env.copyMap.get(plotImpl0);
		assertFalse(env.canRedo());
		assertFalse(env.canUndo());

		// step 1 : change color to RED
		plot.setColor(Color.RED);
		assertEquals(plot.getColor(), Color.RED);
		assertTrue(env.copyMap.containsKey(plotImpl0));
		assertTrue(env.copyMap.containsKey(plotImpl0.getMargin()));
		assertTrue(env.copyMap.containsKey(plotImpl0.getLegend()));

		PlotEx safeCopy1 = (PlotEx) env.copyMap.get(plotImpl0);
		assertNotSame(safeCopy0, safeCopy1);
		assertNotSame(safeCopy0.getMargin(), safeCopy1.getMargin());
		assertNotSame(safeCopy0.getLegend(), safeCopy1.getLegend());

		assertFalse(env.canRedo());
		assertTrue(env.canUndo());

		// step 2 : undo
		env.undo();
		assertEquals(plot.getColor(), Color.BLACK);
		PlotEx plotImpl2 = (PlotEx) ((ElementAddition) plot).getImpl();
		assertNotSame(plotImpl0, plotImpl2);
		assertTrue(env.copyMap.containsKey(plotImpl2));
		assertTrue(env.copyMap.containsKey(plotImpl2.getMargin()));
		assertTrue(env.copyMap.containsKey(plotImpl2.getLegend()));

		PlotEx safeCopy2 = (PlotEx) env.copyMap.get(plotImpl2);
		assertSame(safeCopy2, safeCopy0);
		assertSame(safeCopy2.getMargin(), safeCopy0.getMargin());
		assertSame(safeCopy2.getLegend(), safeCopy0.getLegend());

		assertTrue(env.canRedo());
		assertFalse(env.canUndo());

		// step 3 : redo
		env.redo();
		assertEquals(plot.getColor(), Color.RED);
		PlotEx plotImpl3 = (PlotEx) ((ElementAddition) plot).getImpl();
		assertNotSame(plotImpl2, plotImpl3);
		assertTrue(env.copyMap.containsKey(plotImpl3));
		assertTrue(env.copyMap.containsKey(plotImpl3.getMargin()));
		assertTrue(env.copyMap.containsKey(plotImpl3.getLegend()));

		PlotEx safeCopy3 = (PlotEx) env.copyMap.get(plotImpl3);
		assertSame(safeCopy3, safeCopy1);
		assertSame(safeCopy3.getMargin(), safeCopy1.getMargin());
		assertSame(safeCopy3.getLegend(), safeCopy1.getLegend());

		assertFalse(env.canRedo());
		assertTrue(env.canUndo());

	}

	@Test
	public void undoAddTitleTest() {
		Plot plot = ef.createPlot();
		PlotEnvironment env = new PlotEnvironment(false);
		env.setPlot(plot);

		assertEquals(plot.getTitles().length, 0);
		assertFalse(env.canRedo());
		assertFalse(env.canUndo());

		Title title = ef.createTitle("title");
		plot.addTitle(title);
		assertEquals(plot.getTitles().length, 1);
		assertFalse(env.canRedo());
		assertTrue(env.canUndo());

		env.undo();
		assertEquals(plot.getTitles().length, 0);
		assertTrue(env.canRedo());
		assertFalse(env.canUndo());

		env.redo();
		assertEquals(plot.getTitles().length, 1);
		assertFalse(env.canRedo());
		assertTrue(env.canUndo());
	}

	@Test
	public void undoAddAxisTest() {
		Plot plot = ef.createPlot();
		PlotEnvironment env = new PlotEnvironment(false);
		env.setPlot(plot);

		assertEquals(plot.getXAxes().length, 0);
		assertEquals(plot.getYAxes().length, 0);
		assertFalse(env.canRedo());
		assertFalse(env.canUndo());

		Axis xaxis = ef.createAxis();
		xaxis.getTitle().setText("x axis");
		Axis yaxis = ef.createAxis();
		yaxis.getTitle().setText("y axis");
		plot.addXAxis(xaxis);
		plot.addYAxis(yaxis);
		assertEquals(plot.getXAxes().length, 1);
		assertEquals(plot.getYAxes().length, 1);
		assertFalse(env.canRedo());
		assertTrue(env.canUndo());

		env.undo();
		assertEquals(plot.getXAxes().length, 1);
		assertEquals(plot.getYAxes().length, 0);
		assertTrue(env.canRedo());
		assertTrue(env.canUndo());

		env.undo();
		assertEquals(plot.getXAxes().length, 0);
		assertEquals(plot.getYAxes().length, 0);
		assertTrue(env.canRedo());
		assertFalse(env.canUndo());

		env.redo();
		assertEquals(plot.getXAxes().length, 1);
		assertEquals(plot.getYAxes().length, 0);
		assertTrue(env.canRedo());
		assertTrue(env.canUndo());

		env.redo();
		assertEquals(plot.getXAxes().length, 1);
		assertEquals(plot.getYAxes().length, 1);
		assertFalse(env.canRedo());
		assertTrue(env.canUndo());
	}

	@Test
	public void undoAxisRangeTest() {
		Plot plot = ef.createPlot();
		PlotEx plotImpl0 = (PlotEx) ((ElementAddition) plot).getImpl();
		PlotEnvironment env = new PlotEnvironment(false);
		env.setPlot(plot);

		assertEquals(env.changeHistory.getCSN(), 1);

		Axis xaxis = ef.createAxis();
		xaxis.getTitle().setText("x axis");
		plot.addXAxis(xaxis);

		assertEquals(env.changeHistory.getCSN(), 2);
		PlotEx safeCopy2 = (PlotEx) env.copyMap.get(plotImpl0);
		checkRange(safeCopy2.getXAxis(0).getTickManager().getAxisTransform().getRange(), -1, 1);

		// set new range
		xaxis.getTickManager().getAxisTransform().setRange(new Range.Double(20, 30));

		assertEquals(env.changeHistory.getCSN(), 3);
		PlotEx safeCopy3 = (PlotEx) env.copyMap.get(plotImpl0);

		assertNotSame(safeCopy2, safeCopy3);
		assertNotSame(safeCopy2.getXAxis(0), safeCopy3.getXAxis(0));
		assertNotSame(safeCopy2.getXAxis(0).getTickManager(), safeCopy3.getXAxis(0).getTickManager());
		assertNotSame(safeCopy2.getXAxis(0).getTickManager().getAxisTransform(), safeCopy3.getXAxis(0).getTickManager()
				.getAxisTransform());

		checkRange(safeCopy2.getXAxis(0).getTickManager().getAxisTransform().getRange(), -1, 1);
		checkRange(safeCopy3.getXAxis(0).getTickManager().getAxisTransform().getRange(), 20, 30);

		env.undo();

		assertEquals(env.changeHistory.getCSN(), 2);
		checkRange(xaxis.getTickManager().getAxisTransform().getRange(), -1, 1);
	}
}
