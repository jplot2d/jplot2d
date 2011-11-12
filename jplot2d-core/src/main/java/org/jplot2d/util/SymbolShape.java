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
package org.jplot2d.util;

import java.awt.Shape;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jplot2d.javacc.ParseException;
import org.jplot2d.javacc.SymbolShapeParser;
import org.jplot2d.javacc.TokenMgrError;

/**
 * SymbolShape has a unique id, a name and data to direct how to paint it.
 * <p>
 * The instance is immutable! This class also maintains a internal collection to keep all created
 * symbol shapes. User can call the static method {@link #getSymbolShapes()} to get them.
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
 * <dd>is represented by a pair of end points of a diagonal or by x,y of bottom left corner point
 * and width and height.</dd>
 * <dt>ELLIPSE</dt>
 * <dd>is represented by a bounding RECTANGLE.</dd>
 * <dt>CIRLE</dt>
 * <dd>is represented by a center point and radius.</dd>
 * <dt>ARC</dt>
 * <dd>is represented by a ELLIPSE bounding rectangle and a ANGLERANGE, which is defined by starting
 * angle and angular extent in degrees.</dd>
 * </dl>
 * </p>
 * 
 * @author Jingjing Li
 * 
 */
public final class SymbolShape implements Serializable {

	private static final long serialVersionUID = -4709755495282488510L;

	private static final Map<String, SymbolShape> _symbolMap = new HashMap<String, SymbolShape>();

	private static final ThreadLocal<SymbolShapeParser> parserTL = new ThreadLocal<SymbolShapeParser>() {
		@Override
		protected SymbolShapeParser initialValue() {
			return new SymbolShapeParser(new StringReader(""));
		}
	};

	/**
	 * DOT is a 0-length line
	 */
	public static final SymbolShape DOT = new SymbolShape("DOT", "{L(0,0)(0,0)}");

	public static final SymbolShape VCROSS = new SymbolShape("VCROSS",
			"{L(-0.5,0)(0.5,0) L(0,-0.5)(0,0.5)}");

	public static final SymbolShape DCROSS = new SymbolShape("DCROSS",
			"{L(-0.707,-0.707)(0.707,0.707) L(-0.707,0.707)(0.707,-0.707)}");

	public static final SymbolShape VDCROSS = new SymbolShape("VDCROSS",
			"{L(-0.5,0)(0.5,0) L(0,-0.5)(0,0.5) L(-0.707,-0.707)(0.707,0.707) L(-0.707,0.707)(0.707,-0.707)}");

	public static final SymbolShape CIRCLE = new SymbolShape("CIRCLE", "{C(0,0,0.5)}");

	public static final SymbolShape FCIRCLE = new SymbolShape("FCIRCLE", "{FC(0,0,0.5)}");

	public static final SymbolShape TRIANGLE = new SymbolShape("TRIANGLE",
			"{P(-0.433,-0.25)(0.433,-0.25)(0,0.5)}");

	public static final SymbolShape FTRIANGLE = new SymbolShape("FTRIANGLE",
			"{FP(-0.433,-0.25)(0.433,-0.25)(0,0.5)}");

	public static final SymbolShape UTRIANGLE = new SymbolShape("UTRIANGLE",
			"{P(-0.433,0.25)(0.433,0.25)(0,-0.5)}");

	public static final SymbolShape SQUARE = new SymbolShape("SQUARE",
			"{R(-0.354,-0.354)(0.354,0.354)}");

	public static final SymbolShape FSQUARE = new SymbolShape("FSQUARE",
			"{FR(-0.354,-0.354)(0.354,0.354)}");

	public static final SymbolShape SQUARE_CROSS = new SymbolShape("SQUARE_CROSS",
			"{R(-0.354,-0.354)(0.354,0.354) L(-0.354,-0.354)(0.354,0.354) L(-0.354,0.354)(0.354,-0.354)}");

	public static final SymbolShape DIAMOND = new SymbolShape("DIAMOND",
			"{P(-0.5,0)(0,-0.5)(0.5,0)(0,0.5)}");

	public static final SymbolShape FDIAMOND = new SymbolShape("FDIAMOND",
			"{FP(-0.5,0)(0,-0.5)(0.5,0)(0,0.5)}");

	public static final SymbolShape DIAMOND_CROSS = new SymbolShape("DIAMOND_CROSS",
			"{P(-0.5,0)(0,-0.5)(0.5,0)(0,0.5) L(-0.5,0)(0.5,0) L(0,-0.5)(0,0.5)}");

	public static final SymbolShape OCTAGON = new SymbolShape(
			"OCTAGON",
			"{P(-0.4619,-0.1913)(-0.1913,-0.4619)(0.1913,-0.4619)(0.4619,-0.1913)(0.4619,0.1913)(0.1913,0.4619)(-0.1913,0.4619)(-0.4619,0.1913)}");

	public static final SymbolShape FOCTAGON = new SymbolShape(
			"FOCTAGON",
			"{FP(-0.4619,-0.1913)(-0.1913,-0.4619)(0.1913,-0.4619)(0.4619,-0.1913)(0.4619,0.1913)(0.1913,0.4619)(-0.1913,0.4619)(-0.4619,0.1913)}");

	public static final SymbolShape STAR = new SymbolShape("STAR",
			"{P(-0.4755,0.1545)(0.4755,0.1545)(-0.2939,-0.4045)(0,0.5)(0.2939,-0.4045)}");

	public static final SymbolShape UARROW = new SymbolShape("UARROW",
			"{L(0,-0.5)(0,0) L(0,0)(-0.25,-0.25) L(0,0)(0.25,-0.25)}");

	public static final SymbolShape DARROW = new SymbolShape("DARROW",
			"{L(0,0.5)(0,0) L(0,0)(-0.25,0.25) L(0,0)(0.25,0.25)}");

	public static final SymbolShape RARROW = new SymbolShape("RARROW",
			"{L(-0.5,0)(0,0) L(0,0)(-0.25,0.25) L(0,0)(-0.25,-0.25)}");

	public static final SymbolShape LARROW = new SymbolShape("LARROW",
			"{L(0.5,0)(0,0) L(0,0)(0.25,-0.25) L(0,0)(0.25,0.25)}");

	public static final SymbolShape UARROW_TAIL = new SymbolShape("UARROW_TAIL",
			"{L(0,0)(0,0.5) L(0,0.5)(-0.25,0.25) L(0,0.5)(0.25,0.25)}");

	public static final SymbolShape DARROW_TAIL = new SymbolShape("DARROW_TAIL",
			"{L(0,0)(0,-0.5) L(0,-0.5)(-0.25,-0.25) L(0,-0.5)(0.25,-0.25)}");

	public static final SymbolShape RARROW_TAIL = new SymbolShape("RARROW_TAIL",
			"{L(0,0)(0.5,0) L(0.5,0)(0.25,0.25) L(0.5,0)(0.25,-0.25)}");

	public static final SymbolShape LARROW_TAIL = new SymbolShape("LARROW_TAIL",
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

	private final List<Shape> drawShapeList = new ArrayList<Shape>();

	private final List<Shape> fillShapeList = new ArrayList<Shape>();

	/**
	 * Create a symbol with a name and a description string
	 * 
	 * @param name
	 * @param data
	 *            the shape description string
	 */
	public SymbolShape(String name, String data) {
		if (name.indexOf('{') != -1) {
			throw new IllegalArgumentException("The char '{' is not allowed in name.");
		}
		this.name = name;
		this.data = data;
		this.parseData(data);
		synchronized (_symbolMap) {
			_symbolMap.put(name, this);
		}
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
	 * Returns a iterator over the draw shapes.
	 * 
	 * @return a iterator over the draw shapes.
	 */
	public Iterator<Shape> getDrawShapeIterator() {
		return this.drawShapeList.iterator();
	}

	/**
	 * Returns a iterator over the fill shapes.
	 * 
	 * @return a iterator over the fill shapes.
	 */
	public Iterator<Shape> getFillShapeIterator() {
		return this.fillShapeList.iterator();
	}

	private void parseData(String data) {
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
