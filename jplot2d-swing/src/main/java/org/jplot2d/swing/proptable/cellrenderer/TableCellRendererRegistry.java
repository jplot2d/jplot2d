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
 * 
 */
package org.jplot2d.swing.proptable.cellrenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.TableCellRenderer;

import org.jplot2d.layout.LayoutDirector;
import org.jplot2d.swing.proptable.property.Property;
import org.jplot2d.tex.MathElement;
import org.jplot2d.transfrom.AxisTickTransform;
import org.jplot2d.util.Insets2D;
import org.jplot2d.util.Range;

/**
 * A registry to keep the map between property type and TableCellRenderer. Warning: the internal map
 * is not synchronized.
 * 
 * @author Jingjing Li
 * 
 */
public class TableCellRendererRegistry implements TableCellRendererFactory {

	private Map<Class<?>, TableCellRenderer> typeRendererMap;

	public TableCellRendererRegistry() {
		typeRendererMap = new HashMap<Class<?>, TableCellRenderer>();
		registerDefaults();
	}

	public void registerRenderer(Class<?> type, TableCellRenderer renderer) {
		typeRendererMap.put(type, renderer);
	}

	public void unregisterRenderer(Class<?> type) {
		typeRendererMap.remove(type);
	}

	public TableCellRenderer createTableCellRenderer(Property<?> property) {
		return typeRendererMap.get(property.getType());
	}

	/**
	 * Adds default renderers. This method is called by the constructor but may be called later to
	 * reset any customizations made through the <code>registerRenderer</code> methods.
	 * <p>
	 * Note: if overridden, <code>super.registerDefaults()</code> must be called before plugging
	 * custom defaults.
	 */
	public void registerDefaults() {
		typeRendererMap.clear();

		// geom renderer
		registerRenderer(Point.class, new PointCellRenderer());
		registerRenderer(Point2D.class, new Point2DCellRenderer());
		registerRenderer(Dimension.class, new DimensionCellRenderer());
		registerRenderer(Dimension2D.class, new Dimension2DCellRenderer());
		registerRenderer(Rectangle.class, new RectangleCellRenderer());
		registerRenderer(Rectangle2D.class, new Rectangle2DCellRenderer());
		registerRenderer(Insets.class, new InsetsCellRenderer());
		registerRenderer(Insets2D.class, new Insets2DCellRenderer());

		// color and font
		registerRenderer(Color.class, new ColorCellRenderer());
		registerRenderer(Font.class, new FontCellRenderer());

		// others
		registerRenderer(Range.class, new RangeCellRenderer());
		registerRenderer(MathElement.class, new MathCellRenderer());
		registerRenderer(LayoutDirector.class, new StringCellRenderer<Object>());
		registerRenderer(AxisTickTransform.class, new StringCellRenderer<Object>());
		registerRenderer(Stroke.class, new StringCellRenderer<Object>());
		registerRenderer(Paint.class, new StringCellRenderer<Object>());
	}

}
