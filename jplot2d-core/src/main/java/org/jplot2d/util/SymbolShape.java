/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * $Id: SymbolShape.java,v 1.9 2010/08/17 07:55:40 jli Exp $
 */
package org.jplot2d.util;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
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
 * The instance is immutable! This class also mantain a internal collection to
 * keep all created symbol shapes. User can call the static method
 * {@link #getSymbolShapes()} to get them.
 * <p>
 * A symbol is paint in a square, with a base point. The data syntax (in EBNF):
 * 
 * <pre>
 *         syntax = &quot;{&quot; UOW &quot;,&quot; UOH &quot;,&quot; XOFF &quot;,&quot; YOFF SHAPE+ &quot;}&quot;
 *         UOW = NUM
 *         UOH = NUM
 *         XOFF = NUM
 *         YOFF = NUM
 *         SHAPE = FILLEDSHAPE | OUTLINESHAPE
 *         FILLEDSHAPE = &quot;F&quot; SHAPEDESC
 *         OUTLINESHAPE = SHAPEDESC
 *         SHAPEDESC = LINE | ARC | CIRCLE | ELLIPSE | RECTANGLE | POLYGON
 *         LINE = &quot;L&quot; LINE_DATA
 *         LINEDATA = POINT POINT
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
 * <dl>
 * <dt>UOW UOH</dt>
 * <dd>the number of unit in width and height.
 * <dd>
 * <dt>XOFF YOFF</dt>
 * <dd>the x and y offset of the base point by the center of the square.</dd>
 * <dt>RECTANGLE</dt>
 * <dd>is represented by a pair of end points of a diagonal or by x,y of bottom
 * left corner point and width and height.</dd>
 * <dt>ELLIPSE</dt>
 * <dd>is represented by a bounding RECTANGLE.</dd>
 * <dt>CIRLE</dt>
 * <dd>is represented by a center point and radius.</dd>
 * <dt>ARC</dt>
 * <dd>is represented by a ELLIPSE bounding rectangle and a ANGLERANGE, which is
 * defined by starting angle and angular extent in degrees.</dd>
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
	public static final SymbolShape DOT = new SymbolShape("DOT",
			"{2,2,0,0 L(0,0)(0,0)}");

	public static final SymbolShape VCROSS = new SymbolShape("VCROSS",
			"{2,2,0,0 L(-1,0)(1,0) L(0,-1)(0,1)}");

	public static final SymbolShape DCROSS = new SymbolShape("DCROSS",
			"{2,2,0,0 L(-1,-1)(1,1) L(-1,1)(1,-1)}");

	public static final SymbolShape VDCROSS = new SymbolShape("VDCROSS",
			"{2,2,0,0 L(-1,0)(1,0) L(0,-1)(0,1) L(-1,-1)(1,1) L(-1,1)(1,-1)}");

	public static final SymbolShape CIRCLE = new SymbolShape("CIRCLE",
			"{2,2,0,0 C(0,0,1)}");

	public static final SymbolShape TRIANGLE = new SymbolShape("TRIANGLE",
			"{2,2,0,0 P(-0.866,-0.5)(0.866,-0.5)(0,1)}");

	public static final SymbolShape UTRIANGLE = new SymbolShape("UTRIANGLE",
			"{2,2,0,0 P(-0.866,0.5)(0.866,0.5)(0,-1)}");

	public static final SymbolShape SQUARE = new SymbolShape("SQUARE",
			"{2,2,0,0 R(-1,-1,2,2)}");

	public static final SymbolShape SQUARE_CROSS = new SymbolShape(
			"SQUARE_CROSS",
			"{2,2,0,0 R(-1,-1,2,2) L(-1,-1)(1,1) L(-1,1)(1,-1)}");

	public static final SymbolShape DIAMOND = new SymbolShape("DIAMOND",
			"{2,2,0,0 P(-1,0)(0,-1)(1,0)(0,1)}");

	public static final SymbolShape DIAMOND_CROSS = new SymbolShape(
			"DIAMOND_CROSS",
			"{2,2,0,0 P(-1,0)(0,-1)(1,0)(0,1) L(-1,0)(1,0) L(0,-1)(0,1)}");

	public static final SymbolShape OCTAGON = new SymbolShape(
			"OCTAGON",
			"{2,2,0,0 P(-1,-0.4142)(-0.4142,-1)(0.4142,-1)(1,-0.4142)(1,0.4142)(0.4142,1)(-0.4142,1)(-1,0.4142)}");

	public static final SymbolShape STAR = new SymbolShape(
			"STAR",
			"{2,2,0,0 P(-0.9511,0.3090)(0.9511,0.3090)(-0.5878, -0.8090)(0,1)(0.5878,-0.8090)}");

	public static final SymbolShape FCIRCLE = new SymbolShape("FCIRCLE",
			"{2,2,0,0 FC(0,0,1)}");

	public static final SymbolShape FTRIANGLE = new SymbolShape("FTRIANGLE",
			"{2,2,0,0 FP(-0.866,-0.5)(0.866,-0.5)(0,1)}");

	public static final SymbolShape FSQUARE = new SymbolShape("FSQUARE",
			"{2,2,0,0 FR(-1,-1,2,2)}");

	public static final SymbolShape FDIAMOND = new SymbolShape("FDIAMOND",
			"{2,2,0,0 FP(-1,0)(0,-1)(1,0)(0,1)}");

	public static final SymbolShape FOCTAGON = new SymbolShape(
			"FOCTAGON",
			"{2,2,0,0 FP(-1,-0.4142)(-0.4142,-1)(0.4142,-1)(1,-0.4142)(1,0.4142)(0.4142,1)(-0.4142,1)(-1,0.4142)}");

	public static final SymbolShape UARROW = new SymbolShape("UARROW",
			"{2,2,0,0 L(0,-1)(0,0) L(0,0)(-0.5,-0.5) L(0,0)(0.5,-0.5)}");

	public static final SymbolShape DARROW = new SymbolShape("DARROW",
			"{2,2,0,0 L(0,1)(0,0) L(0,0)(-0.5,0.5) L(0,0)(0.5,0.5)}");

	public static final SymbolShape RARROW = new SymbolShape("RARROW",
			"{2,2,0,0 L(-1,0)(0,0) L(0,0)(-0.5,0.5) L(0,0)(-0.5,-0.5)}");

	public static final SymbolShape LARROW = new SymbolShape("LARROW",
			"{2,2,0,0 L(1,0)(0,0) L(0,0)(0.5,-0.5) L(0,0)(0.5,0.5)}");

	public static final SymbolShape DARROW_LARGE = new SymbolShape(
			"DARROW_LARGE", "{2,2,0,0 L(0,1)(0,-1) L(0,-1)(-1,0) L(0,-1)(1,0)}");

	public static final SymbolShape UARROW_TRIANGLE = new SymbolShape(
			"UARROW_TRIANGLE", "{2,2,0,0 L(0,-1)(0,1) P(-1,0)(1,0)(0,1)}");

	public static final SymbolShape DARROW_TRIANGLE = new SymbolShape(
			"DARROW_TRIANGLE", "{2,2,0,0 L(0,1)(0,-1) P(-1,0)(1,0)(0,-1)}");

	public static final SymbolShape UARROW_TAIL = new SymbolShape(
			"UARROW_TAIL",
			"{2,2,0,0 L(0,0)(0,1) L(0,1)(-0.5,0.5) L(0,1)(0.5,0.5)}");

	public static final SymbolShape DARROW_TAIL = new SymbolShape(
			"DARROW_TAIL",
			"{2,2,0,0 L(0,0)(0,-1) L(0,-1)(-0.5,-0.5) L(0,-1)(0.5,-0.5)}");

	public static final SymbolShape RARROW_TAIL = new SymbolShape(
			"RARROW_TAIL",
			"{2,2,0,0 L(0,0)(1,0) L(1,0)(0.5,0.5) L(1,0)(0.5,-0.5)}");

	public static final SymbolShape LARROW_TAIL = new SymbolShape(
			"LARROW_TAIL",
			"{2,2,0,0 L(0,0)(-1,0) L(-1,0)(-0.5,-0.5) L(-1,0)(-0.5,0.5)}");

	/**
	 * Returns all symbol shapes in a array.
	 * 
	 * @return a array contains all symbol shapes.
	 */
	public static SymbolShape[] getSymbolShapes() {
		synchronized (_symbolMap) {
			return _symbolMap.values().toArray(
					new SymbolShape[_symbolMap.size()]);
		}
	}

	// =========================================================

	private String _name;

	private String _data;

	/**
	 * units of width; unit of height. Normally, the 2 number is same. This 2
	 * variables define a rectangle. The rectangle will scale to 1x1 square when
	 * rendering.
	 */
	private double _uow, _uoh;

	private double _xoff, _yoff;

	private final List<Shape> _drawShapeList = new ArrayList<Shape>();

	private final List<Shape> _fillShapeList = new ArrayList<Shape>();

	/**
	 * Create a symbol with a name and a description string
	 * 
	 * @param name
	 * @param data
	 *            the shape description string
	 */
	public SymbolShape(String name, String data) {
		if (name.indexOf('{') != -1) {
			throw new IllegalArgumentException(
					"The char '{' is not allowed in name.");
		}
		_name = name;
		_data = data;
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
		return _name;
	}

	/**
	 * Returns the data string of this SymbolShape
	 * 
	 * @return the data string
	 */
	public String getData() {
		return _data;
	}

	/**
	 * Returns a AffineTransform to apply to Grapgics2D before painting the
	 * symbol.
	 * 
	 * @return a AffineTransform.
	 */
	public AffineTransform getSymbolTransform() {
		// the -1 convert coordinate from Cartesian to device
		AffineTransform tf = AffineTransform.getScaleInstance(1 / _uow, -1
				/ _uoh);
		tf.translate(-_xoff, -_yoff);
		return tf;
	}

	/**
	 * Returns a iterator over the draw shapes.
	 * 
	 * @return a iterator over the draw shapes.
	 */
	public Iterator<Shape> getDrawShapeIterator() {
		return this._drawShapeList.iterator();
	}

	/**
	 * Returns a iterator over the fill shapes.
	 * 
	 * @return a iterator over the fill shapes.
	 */
	public Iterator<Shape> getFillShapeIterator() {
		return this._fillShapeList.iterator();
	}

	private void parseData(String data) {
		if (data == null || data.length() == 0) {
			throw new IllegalArgumentException("data cannot be null or empty");
		}
		SymbolShapeParser ssp = parserTL.get();
		try {
			ssp.parse(data);
			_uow = ssp.getUOW();
			_uoh = ssp.getUOH();
			_xoff = ssp.getXoff();
			_yoff = ssp.getYoff();
			_drawShapeList.addAll(ssp.getDrawShapeList());
			_fillShapeList.addAll(ssp.getFillShapeList());
		} catch (ParseException e) {
			throw new IllegalArgumentException(
					"the syntax of data is not correct", e);
		} catch (TokenMgrError e) {
			throw new IllegalArgumentException(
					"the syntax of data is not correct", e);
		}
	}

	public boolean equals(Object obj) {
		return obj instanceof SymbolShape && _name == ((SymbolShape) obj)._name;
	}

	public int hashCode() {
		return _name.hashCode();
	}

	public String toString() {
		return _name;
	}

}
