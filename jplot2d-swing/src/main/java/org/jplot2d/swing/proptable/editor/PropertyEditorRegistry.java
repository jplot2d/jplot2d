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
package org.jplot2d.swing.proptable.editor;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

import org.jplot2d.swing.proptable.property.Property;

/**
 * A registry to keep the map between property type and PropertyEditor. Warning: the internal map is
 * not synchronized.
 * 
 * @author Jingjing Li
 * 
 */
public class PropertyEditorRegistry implements PropertyEditorFactory {

	private Map<Class<?>, PropertyEditor> typeEditorMap;

	public PropertyEditorRegistry() {
		typeEditorMap = new HashMap<Class<?>, PropertyEditor>();
		registerDefaults();
	}

	public void registerEditor(Class<?> type, PropertyEditor renderer) {
		typeEditorMap.put(type, renderer);
	}

	public void unregisterEditor(Class<?> type) {
		typeEditorMap.remove(type);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PropertyEditor createPropertyEditor(Property<?> property) {

		Object[] avs = property.getAvailableValues();
		if (avs != null) {
			return new ComboBoxPropertyEditor(avs);
		}

		Class<?> propType = property.getType();
		if (Enum.class.isAssignableFrom(propType)) {
			return new EnumPropertyEditor((Class<? extends Enum>) propType);
		}
		return typeEditorMap.get(propType);
	}

	/**
	 * Adds default renderers. This method is called by the constructor but may be called later to
	 * reset any customizations made through the <code>registerRenderer</code> methods.
	 * <p>
	 * Note: if overridden, <code>super.registerDefaults()</code> must be called before plugging
	 * custom defaults.
	 */
	public void registerDefaults() {
		typeEditorMap.clear();

		PropertyEditor doubleEditor = new DoubleEditor();
		registerEditor(double.class, doubleEditor);
		registerEditor(Double.class, doubleEditor);

		PropertyEditor floatEditor = new FloatEditor();
		registerEditor(float.class, floatEditor);
		registerEditor(Float.class, floatEditor);

		PropertyEditor longEditor = new LongEditor();
		registerEditor(long.class, longEditor);
		registerEditor(Long.class, longEditor);

		PropertyEditor integerEditor = new IntegerEditor();
		registerEditor(int.class, integerEditor);
		registerEditor(Integer.class, integerEditor);

		PropertyEditor booleanEditor = new BooleanPropertyEditor();
		registerEditor(boolean.class, booleanEditor);
		registerEditor(Boolean.class, booleanEditor);

		registerEditor(float[].class, new FloatArrayEditor());
		registerEditor(double[].class, new DoubleArrayEditor());

		// string
		registerEditor(String.class, new StringPropertyEditor());

		// color and font
		registerEditor(Color.class, new ColorPropertyEditor());
		registerEditor(Font.class, new FontPropertyEditor());

	}

}
