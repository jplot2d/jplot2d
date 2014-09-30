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
package org.jplot2d.util;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jplot2d.javacc.ParseException;
import org.jplot2d.javacc.SymbolShapeParser;
import org.jplot2d.javacc.TokenMgrError;

/**
 * SymbolShape has an unique name and data to direct how to paint it.
 * <p>
 * The instance is immutable! This class also maintains a internal collection to keep all created symbol shapes. User
 * can call the static method {@link #getSymbolShapes()} to get them.
 * <p>
 * A symbol is painted in an 1-unit circle boundary. The data syntax (in EBNF):
 * 
 * <pre>
 *         syntax = &quot;{&quot; SHAPE+ &quot;}&quot;
 *         SHAPE = FILLEDSHAPE | OUTLINESHAPE
 *         FILLEDSHAPE = &quot;F&quot; SHAPEDESC
 *         OUTLINESHAPE = SHAPEDESC
 *         SHAPEDESC = LINE | ARC | CIRCLE | ELLIPSE | RECTANGLE | POLYGON
 *         LINE = &quot;L&quot; LINE_DATA
 *         LINE_DATA = POINT POINT
 *         ARC = OPENARC | CHORDARC | PIEARC
 *         OPENARC = &quot;A&quot; ARC_DARTA
 *         CHORDARC = &quot;AC&quot; ARC_DARTA
 *         PIEARC = &quot;AP&quot; ARC_DARTA
 *         ARC_DARTA = RECTANGLE_DATA ANGLERANGE
 *         ANGLERANGE = &quot;(&quot; NUM &quot;,&quot; NUM &quot;)&quot;
 *         CIRCLE = &quot;C&quot; CIRCLE_DATA
 *         CIRCLE_DATA = &quot;(&quot; NUM &quot;,&quot; NUM &quot;,&quot; NUM &quot;)&quot;
 *         ELLIPSE = &quot;E&quot; RECTANGLE_DATA
 *         RECTANGLE = &quot;R&quot; RECTANGLE_DATA
 *         RECTANGLE_DATA = DIAGONAL_DATA | XYWH_DATA
 *         DIAGONAL_DATA = LINE_DATA
 *         XYWH_DATA = &quot;(&quot; NUM &quot;,&quot; NUM &quot;,&quot; NUM &quot;,&quot; NUM &quot;)&quot;
 *         POLYGON = &quot;P&quot; POINT POINT POINT+
 *         POINT = &quot;(&quot; NUM &quot;,&quot; NUM &quot;)&quot;
 *         NUM = (&quot;+&quot;|&quot;-&quot;)? ([&quot;0&quot;-&quot;9&quot;])+ (&quot;.&quot; ([&quot;0&quot;-&quot;9&quot;])*)? | &quot;.&quot; ([&quot;0&quot;-&quot;9&quot;])*
 * </pre>
 * 
 * <p>
 * <dt>RECTANGLE</dt>
 * <dd>is represented by a pair of end points of a diagonal or by x,y of bottom left corner point and width and height.</dd>
 * <dt>ELLIPSE</dt>
 * <dd>is represented by a bounding RECTANGLE.</dd>
 * <dt>CIRLE</dt>
 * <dd>is represented by a center point and radius.</dd>
 * <dt>ARC</dt>
 * <dd>is represented by a ELLIPSE bounding rectangle and a ANGLERANGE, which is defined by starting angle and angular
 * extent in degrees.</dd>
 * </dl>
 * </p>
 * 
 * @author Jingjing Li
 * 
 */
public class SymbolShape {

	private static final Map<String, SymbolShape> _symbolMap = new LinkedHashMap<String, SymbolShape>();

	private static final ThreadLocal<SymbolShapeParser> parserTL = new ThreadLocal<SymbolShapeParser>() {
		@Override
		protected SymbolShapeParser initialValue() {
			return new SymbolShapeParser(new StringReader(""));
		}
	};

	public static final SymbolShape VCROSS = createSymbolShape("VCROSS", "{L(-0.5,0)(0.5,0) L(0,-0.5)(0,0.5)}");

	public static final SymbolShape DCROSS = createSymbolShape("DCROSS",
			"{L(-0.707,-0.707)(0.707,0.707) L(-0.707,0.707)(0.707,-0.707)}");

	public static final SymbolShape VDCROSS = createSymbolShape("VDCROSS",
			"{L(-0.5,0)(0.5,0) L(0,-0.5)(0,0.5) L(-0.707,-0.707)(0.707,0.707) L(-0.707,0.707)(0.707,-0.707)}");

	public static final SymbolShape CIRCLE = createSymbolShape("CIRCLE", "{C(0,0,0.5)}");

	public static final SymbolShape FCIRCLE = createSymbolShape("FCIRCLE", "{FC(0,0,0.5)}");

	public static final SymbolShape TRIANGLE = createSymbolShape("TRIANGLE", "{P(-0.433,-0.25)(0.433,-0.25)(0,0.5)}");

	public static final SymbolShape FTRIANGLE = createSymbolShape("FTRIANGLE", "{FP(-0.433,-0.25)(0.433,-0.25)(0,0.5)}");

	public static final SymbolShape UTRIANGLE = createSymbolShape("UTRIANGLE", "{P(-0.433,0.25)(0.433,0.25)(0,-0.5)}");

	public static final SymbolShape SQUARE = createSymbolShape("SQUARE", "{R(-0.354,-0.354)(0.354,0.354)}");

	public static final SymbolShape FSQUARE = createSymbolShape("FSQUARE", "{FR(-0.354,-0.354)(0.354,0.354)}");

	public static final SymbolShape SQUARE_CROSS = createSymbolShape("SQUARE_CROSS",
			"{R(-0.354,-0.354)(0.354,0.354) L(-0.354,-0.354)(0.354,0.354) L(-0.354,0.354)(0.354,-0.354)}");

	public static final SymbolShape DIAMOND = createSymbolShape("DIAMOND", "{P(-0.5,0)(0,-0.5)(0.5,0)(0,0.5)}");

	public static final SymbolShape FDIAMOND = createSymbolShape("FDIAMOND", "{FP(-0.5,0)(0,-0.5)(0.5,0)(0,0.5)}");

	public static final SymbolShape DIAMOND_CROSS = createSymbolShape("DIAMOND_CROSS",
			"{P(-0.5,0)(0,-0.5)(0.5,0)(0,0.5) L(-0.5,0)(0.5,0) L(0,-0.5)(0,0.5)}");

	public static final SymbolShape OCTAGON = createSymbolShape(
			"OCTAGON",
			"{P(-0.4619,-0.1913)(-0.1913,-0.4619)(0.1913,-0.4619)(0.4619,-0.1913)(0.4619,0.1913)(0.1913,0.4619)(-0.1913,0.4619)(-0.4619,0.1913)}");

	public static final SymbolShape FOCTAGON = createSymbolShape(
			"FOCTAGON",
			"{FP(-0.4619,-0.1913)(-0.1913,-0.4619)(0.1913,-0.4619)(0.4619,-0.1913)(0.4619,0.1913)(0.1913,0.4619)(-0.1913,0.4619)(-0.4619,0.1913)}");

	public static final SymbolShape STAR = createSymbolShape("STAR",
			"{P(-0.4755,0.1545)(0.4755,0.1545)(-0.2939,-0.4045)(0,0.5)(0.2939,-0.4045)}");

	public static final SymbolShape UARROW = createSymbolShape("UARROW",
			"{L(0,-0.5)(0,0) L(0,0)(-0.25,-0.25) L(0,0)(0.25,-0.25)}");

	public static final SymbolShape DARROW = createSymbolShape("DARROW",
			"{L(0,0.5)(0,0) L(0,0)(-0.25,0.25) L(0,0)(0.25,0.25)}");

	public static final SymbolShape RARROW = createSymbolShape("RARROW",
			"{L(-0.5,0)(0,0) L(0,0)(-0.25,0.25) L(0,0)(-0.25,-0.25)}");

	public static final SymbolShape LARROW = createSymbolShape("LARROW",
			"{L(0.5,0)(0,0) L(0,0)(0.25,-0.25) L(0,0)(0.25,0.25)}");

	public static final SymbolShape UARROW_TAIL = createSymbolShape("UARROW_TAIL",
			"{L(0,0)(0,0.5) L(0,0.5)(-0.25,0.25) L(0,0.5)(0.25,0.25)}");

	public static final SymbolShape DARROW_TAIL = createSymbolShape("DARROW_TAIL",
			"{L(0,0)(0,-0.5) L(0,-0.5)(-0.25,-0.25) L(0,-0.5)(0.25,-0.25)}");

	public static final SymbolShape RARROW_TAIL = createSymbolShape("RARROW_TAIL",
			"{L(0,0)(0.5,0) L(0.5,0)(0.25,0.25) L(0.5,0)(0.25,-0.25)}");

	public static final SymbolShape LARROW_TAIL = createSymbolShape("LARROW_TAIL",
			"{L(0,0)(-0.5,0) L(-0.5,0)(-0.25,-0.25) L(-0.5,0)(-0.25,0.25)}");

	/**
	 * Returns all symbol shapes in a array.
	 * 
	 * @return a array contains all symbol shapes.
	 */
	public static SymbolShape[] getSymbolShapes() {
		synchronized (_symbolMap) {
			return _symbolMap.values().toArray(new SymbolShape[_symbolMap.size()]);
		}
	}

	// =========================================================

	private String name;

	private String data;

	protected final List<Shape> drawShapeList = new ArrayList<Shape>();

	protected final List<Shape> fillShapeList = new ArrayList<Shape>();

	/**
	 * Create a symbol with a name and a description string
	 * 
	 * @param name
	 * @param data
	 *            the shape description string
	 */
	public static SymbolShape createSymbolShape(String name, String data) {
		SymbolShape ss = new SymbolShape(name, data);
		synchronized (_symbolMap) {
			_symbolMap.put(name, ss);
		}
		return ss;
	}

	/**
	 * Create a symbol with a name and a description string
	 * 
	 * @param name
	 * @param data
	 *            the shape description string
	 */
	protected SymbolShape(String name, String data) {
		if (name.indexOf('{') != -1) {
			throw new IllegalArgumentException("The char '{' is not allowed in name.");
		}
		this.name = name;
		this.data = data;
		this.parseData(data);
	}

	/**
	 * Returns the symbol name
	 * 
	 * @return the symbol name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the data string of this SymbolShape
	 * 
	 * @return the data string
	 */
	public String getData() {
		return data;
	}

	/**
	 * Transform this shape and drawing it to the given Graphics2D.
	 * 
	 * @param g
	 *            the graphics context to use for drawing
	 * @param maf
	 *            the AffineTransform that transform this shape before drawing
	 */
	public void draw(Graphics2D g, AffineTransform maf) {
		Iterator<Shape> dit = drawShapeList.iterator();
		while (dit.hasNext()) {
			Shape s = maf.createTransformedShape(dit.next());
			g.draw(s);
		}
		Iterator<Shape> fit = fillShapeList.iterator();
		while (fit.hasNext()) {
			Shape s = maf.createTransformedShape(fit.next());
			g.fill(s);
		}
	}

	protected void parseData(String data) {
		if (data == null || data.length() == 0) {
			throw new IllegalArgumentException("data cannot be null or empty");
		}
		SymbolShapeParser ssp = parserTL.get();
		try {
			ssp.parse(data);
			drawShapeList.addAll(ssp.getDrawShapeList());
			fillShapeList.addAll(ssp.getFillShapeList());
		} catch (ParseException e) {
			throw new IllegalArgumentException("the syntax of data is not correct", e);
		} catch (TokenMgrError e) {
			throw new IllegalArgumentException("the syntax of data is not correct", e);
		}
	}

	public boolean equals(Object obj) {
		return obj instanceof SymbolShape && name == ((SymbolShape) obj).name;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public String toString() {
		return name;
	}

}
