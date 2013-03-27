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
package org.jplot2d.env;

import java.awt.Color;
import java.awt.Paint;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;

import org.jplot2d.element.Element;

/**
 * Profile can persist in java Preferences.
 * 
 * @author Jingjing Li
 * 
 */
public class PrefsProfile implements Profile {

	private static Object IGNORE = new Object();

	private Preferences pref;

	/**
	 * Create a profile who store data in Preferences node "org/jplot2d/profile/default".
	 */
	public PrefsProfile() {
		pref = Preferences.userRoot().node("org/jplot2d/profile/default");
	}

	/**
	 * Create a profile who store data in Preferences node in the given path.
	 * 
	 * @param profileName
	 */
	public PrefsProfile(String pathName) {
		pref = Preferences.userRoot().node(pathName);
	}

	/**
	 * Apply profile to the given element
	 * 
	 * @param element
	 */
	public void applyTo(Element element) {
		Class<?> eif = getElementInterface(element.getClass());

		if (eif == null) {
			return;
		}

		Preferences node = pref.node(eif.getSimpleName());

		InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(eif);
		for (PropertyInfo[] pinfos : iinfo.getPropertyInfoGroupMap().values()) {
			for (PropertyInfo pinfo : pinfos) {
				String pname = pinfo.getName();
				Method reader = pinfo.getReadMethod();

				if (pinfo.getWriteMethod() != null) {
					try {
						Object oldValue = reader.invoke(element);
						String pvalue = node.get(pname, null);
						if (pvalue == null) {
							// populate the preference
							node.put(pname, toString(oldValue));
						} else if (!toString(oldValue).equals(pvalue)) {
							// apply the preference value
							Method writter = pinfo.getWriteMethod();
							Class<?> ptype = pinfo.getPropertyType();
							Object v = parse(pvalue, ptype);
							if (v != IGNORE) {
								writter.invoke(element, v);
							}
						}
					} catch (IllegalArgumentException e) {

					} catch (IllegalAccessException e) {

					} catch (InvocationTargetException e) {

					}
				}
			}
		}

	}

	private String toString(Object obj) {
		if (obj instanceof Color) {
			return "0x" + Integer.toHexString(((Color) obj).getRGB());
		}
		return String.valueOf(obj);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object parse(String s, Class ptype) {
		if (s.equals("null")) {
			return null;
		} else if (ptype == String.class) {
			return s;
		}

		try {
			if (ptype == Boolean.TYPE) {
				return Boolean.parseBoolean(s);
			} else if (ptype == Byte.TYPE) {
				return Byte.parseByte(s);
			} else if (ptype == Short.TYPE) {
				return Short.parseShort(s);
			} else if (ptype == Integer.TYPE) {
				return Integer.parseInt(s);
			} else if (ptype == Long.TYPE) {
				return Long.parseLong(s);
			} else if (ptype == Float.TYPE) {
				return Float.parseFloat(s);
			} else if (ptype == Double.TYPE) {
				return Double.parseDouble(s);
			} else if (ptype.isEnum()) {
				return Enum.valueOf(ptype, s);
			}

			if (ptype == Paint.class) {
				return Color.decode(s);
			}
		} catch (Exception e) {

		}

		return IGNORE;
	}

	/**
	 * Returns the parent interface which is in org.jplot2d.element package
	 * 
	 * @param element
	 * @return
	 */
	protected static Class<?> getElementInterface(Class<?> element) {
		if (element.getPackage().getName().equals("org.jplot2d.element")) {
			return element;
		}
		Class<?>[] interfaces = element.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			Class<?> eif = getElementInterface(interfaces[i]);
			if (eif != null) {
				return eif;
			}
		}
		return null;
	}

	/**
	 * Returns a element instance who can proxy get/set values from/to this profile.
	 * 
	 * @param elementInterface
	 *            the element interface
	 * @return
	 */
	public <T extends Element> T getProxyBean(Class<T> elementInterface) {
		return null;
	}

}
