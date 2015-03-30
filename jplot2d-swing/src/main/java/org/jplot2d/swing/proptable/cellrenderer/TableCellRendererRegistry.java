/**
 * Copyright 2010-2014 Jingjing Li.
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
package org.jplot2d.swing.proptable.cellrenderer;

import org.jplot2d.swing.proptable.property.Property;
import org.jplot2d.util.Insets2D;
import org.jplot2d.util.Range;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.TableCellRenderer;

/**
 * A registry to keep the map between property type and TableCellRenderer. Warning: the internal map is not
 * synchronized.
 *
 * @author Jingjing Li
 */
public class TableCellRendererRegistry implements TableCellRendererFactory {

    private final Map<Class<?>, TableCellRenderer> typeRendererMap;

    private final TableCellRenderer defaultCellRenderer = new StringCellRenderer<>();

    public TableCellRendererRegistry() {
        typeRendererMap = new HashMap<>();
        registerDefaults();
    }

    public void registerRenderer(Class<?> type, TableCellRenderer renderer) {
        typeRendererMap.put(type, renderer);
    }

    public void unregisterRenderer(Class<?> type) {
        typeRendererMap.remove(type);
    }

    /*
     * Sometimes the declare type (property.getType()) is Object, its value is float[] or double[], so we need a
     * ObjectCellRenderer, to display values according to its value type.
     */
    public TableCellRenderer createTableCellRenderer(Property<?> property) {
        TableCellRenderer result = typeRendererMap.get(property.getType());
        if (result == null && property.getType().isInterface()) {
            result = defaultCellRenderer;
        }
        if (result instanceof DigitsLimitableCellRenderer) {
            if (property.getDisplayDigits() != 0) {
                ((DigitsLimitableCellRenderer<?>) result).setDigitsLimit(property.getDisplayDigits());
            }
        }
        return result;
    }

    /**
     * Adds default renderers. This method is called by the constructor but may be called later to reset any
     * customizations made through the <code>registerRenderer</code> methods.
     * <p/>
     * Note: if overridden, <code>super.registerDefaults()</code> must be called before plugging custom defaults.
     */
    public void registerDefaults() {
        typeRendererMap.clear();

        // number renderer
        registerRenderer(float.class, new FloatCellRenderer());
        registerRenderer(Float.class, new FloatCellRenderer());
        registerRenderer(double.class, new DoubleCellRenderer());
        registerRenderer(Double.class, new DoubleCellRenderer());

        // geometry renderer
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
        registerRenderer(BasicStroke.class, new BasicStrokeCellRenderer());

        // Object renderer
        registerRenderer(Object.class, new ObjectCellRenderer());
        // number array renderer
        registerRenderer(float[].class, new FloatArrayCellRenderer());
        registerRenderer(double[].class, new DoubleArrayCellRenderer());
        // String array renderer
        registerRenderer(String[].class, new StringArrayCellRenderer());

    }

}
