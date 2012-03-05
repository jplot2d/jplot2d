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

import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

/**
 * This class extract properties from method declaration, rather than using java introspector. The
 * property annotation is defined on getter. if a sub-interface override a getter, the overriding
 * method will take effect. The setter method type must match the getter return type. setter methods
 * with same name but different argument type are not allowed.
 * 
 * @author Jingjing Li
 * 
 */
public class InterfaceInfo {

	private static final String IS_PREFIX = "is";
	private static final String GET_PREFIX = "get";
	private static final String SET_PREFIX = "set";

	private static Map<Class<?>, InterfaceInfo> interfaceInfoCache = new HashMap<Class<?>, InterfaceInfo>();

	private final Map<String, PropertyInfo> piMap;

	private final Map<Method, Method> propWriteReadMap = new HashMap<Method, Method>();

	private final Collection<Method> propReadMethods = new HashSet<Method>();

	/** listener get add remove Methods */
	private final Collection<Method> listenerGarMethods = new HashSet<Method>();

	private final Map<Method, HierarchyOp> hierachyMethodMap = new HashMap<Method, HierarchyOp>();

	private Map<String, PropertyInfo[]> pisGroupMap = new LinkedHashMap<String, PropertyInfo[]>();

	/**
	 * Load interface info for the given interface and its parents.The subclass may hide the info of
	 * its superclass. For example, if a sub-interface only redeclare a getter, the info will report
	 * the property is read-only.
	 * 
	 * @param interfaceClass
	 */
	public static InterfaceInfo loadInterfaceInfo(Class<?> interfaceClass) {
		InterfaceInfo iinfo = interfaceInfoCache.get(interfaceClass);
		if (iinfo == null) {
			iinfo = new InterfaceInfo(interfaceClass);
			interfaceInfoCache.put(interfaceClass, iinfo);
		}
		return iinfo;
	}

	private InterfaceInfo(Class<?> interfaceClass) {
		// get an array of all the public methods at all level
		Method methods[] = interfaceClass.getMethods();

		try {
			piMap = getPropertyInfo(methods);
		} catch (IntrospectionException e) {
			throw new Error(e);
		}

		loadGroup(interfaceClass);
		load(interfaceClass);

		for (Method method : methods) {
			Hierarchy hierAnn = method.getAnnotation(Hierarchy.class);
			if (hierAnn != null) {
				hierachyMethodMap.put(method, hierAnn.value());
			}
		}

	}

	private void loadGroup(Class<?> interfaceClass) {
		// load parents
		for (Class<?> superClass : interfaceClass.getInterfaces()) {
			loadGroup(superClass);
		}
		// load this interface
		PropertyGroup pg = interfaceClass.getAnnotation(PropertyGroup.class);
		if (pg != null) {
			pisGroupMap.put(pg.value(), null);
		}
	}

	private void load(Class<?> beanClass) {

		Map<String, List<PropertyInfo>> pilGroupMap = new HashMap<String, List<PropertyInfo>>();

		for (PropertyInfo p : piMap.values()) {
			if (p.getPropertyType() != null) {
				Method readMethod = p.getReadMethod();
				Method writeMethod = p.getWriteMethod();
				if (readMethod != null) {
					Property pann = readMethod.getAnnotation(Property.class);
					if (pann != null) {
						if (pann.displayName().length() > 0) {
							p.setDisplayName(pann.displayName());
						}
						if (pann.description().length() > 0) {
							p.setShortDescription(pann.description());
						}
						p.setOrder(pann.order());
						PropertyGroup pg = readMethod.getDeclaringClass().getAnnotation(
								PropertyGroup.class);
						if (pg != null) {
							List<PropertyInfo> pis = pilGroupMap.get(pg.value());
							if (pis == null) {
								pis = new ArrayList<PropertyInfo>();
								pilGroupMap.put(pg.value(), pis);
							}
							pis.add(p);
						}
					}

					propReadMethods.add(readMethod);
					if (writeMethod != null) {
						propWriteReadMap.put(writeMethod, readMethod);
					}
				}
			}
		}

		// sort value array of pisGroupMap
		Iterator<Map.Entry<String, PropertyInfo[]>> itr = pisGroupMap.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, PropertyInfo[]> me = itr.next();
			List<PropertyInfo> gpdList = pilGroupMap.get(me.getKey());
			if (gpdList != null) {
				PropertyInfo[] gpds = gpdList.toArray(new PropertyInfo[gpdList.size()]);
				Arrays.sort(gpds);
				me.setValue(gpds);
			} else {
				itr.remove();
			}
		}

	}

	public Map<String, PropertyInfo[]> getPropertyInfoGroupMap() {
		return pisGroupMap;
	}

	public Class<?> getPropWriteMethodType(String propName) {
		if (piMap.containsKey(propName)) {
			Method m = piMap.get(propName).getWriteMethod();
			if (m != null) {
				return m.getParameterTypes()[0];
			}
		}
		return null;
	}

	public boolean isWritableProp(String propName) {
		if (piMap.containsKey(propName)) {
			return piMap.get(propName).getWriteMethod() != null;
		}
		return false;
	}

	/**
	 * @param method
	 * @return
	 */
	protected boolean isPropReadMethod(Method method) {
		return propReadMethods.contains(method);
	}

	/**
	 * @param method
	 * @return
	 */
	protected boolean isPropWriteMethod(Method method) {
		return propWriteReadMap.containsKey(method);
	}

	protected Method getPropReadMethodByWriteMethod(Method method) {
		return propWriteReadMap.get(method);
	}

	protected boolean isListenerMethod(Method method) {
		return listenerGarMethods.contains(method);
	}

	/**
	 * @param method
	 * @return
	 */
	protected boolean isGetCompMethod(Method method) {
		HierarchyOp hop = hierachyMethodMap.get(method);
		return hop != null && hop == HierarchyOp.GET;
	}

	/**
	 * @param method
	 * @return
	 */
	protected boolean isGetCompArrayMethod(Method method) {
		HierarchyOp hop = hierachyMethodMap.get(method);
		return hop != null && hop == HierarchyOp.GETARRAY;
	}

	/**
	 * @param method
	 * @return
	 */
	protected boolean isAddCompMethod(Method method) {
		HierarchyOp hop = hierachyMethodMap.get(method);
		return hop != null && hop == HierarchyOp.ADD;
	}

	/**
	 * @param method
	 * @return
	 */
	protected boolean isRemoveCompMethod(Method method) {
		HierarchyOp hop = hierachyMethodMap.get(method);
		return hop != null && hop == HierarchyOp.REMOVE;
	}

	/**
	 * @param method
	 * @return
	 */
	protected boolean isJoinElementMethod(Method method) {
		HierarchyOp hop = hierachyMethodMap.get(method);
		return hop != null && hop == HierarchyOp.JOIN;
	}

	/**
	 * @param method
	 * @return
	 */
	protected boolean isRefElementMethod(Method method) {
		HierarchyOp hop = hierachyMethodMap.get(method);
		return hop != null && hop == HierarchyOp.REF;
	}

	/**
	 * @param method
	 * @return
	 */
	protected boolean isRef2ElementMethod(Method method) {
		HierarchyOp hop = hierachyMethodMap.get(method);
		return hop != null && hop == HierarchyOp.REF2;
	}

	protected boolean isAddRef2Method(Method method) {
		HierarchyOp hop = hierachyMethodMap.get(method);
		return hop != null && hop == HierarchyOp.ADD_REF2;
	}

	/**
	 * Extract an array of PropertyDescriptor from the given methods.
	 * 
	 * @return An array of PropertyDescriptors, with the order of getter declared.
	 * @throws IntrospectionException
	 */
	private static Map<String, PropertyInfo> getPropertyInfo(Method[] methods)
			throws IntrospectionException {

		Map<String, Method> pdReaderMap = new LinkedHashMap<String, Method>();
		Map<String, Method> pdWriterMap = new HashMap<String, Method>();

		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method == null) {
				continue;
			}
			// skip static methods.
			int mods = method.getModifiers();
			if (Modifier.isStatic(mods)) {
				continue;
			}
			String name = method.getName();
			// Optimization. Don't bother with invalid propertyNames.
			if (name.length() <= 3 && !name.startsWith(IS_PREFIX)) {
				continue;
			}

			Class<?>[] argTypes = method.getParameterTypes();
			Class<?> resultType = method.getReturnType();
			int argCount = argTypes.length;

			if (argCount == 0) {
				String pname = null;
				if (name.startsWith(GET_PREFIX)) {
					// Simple getter
					pname = decapitalize(name.substring(3));
				} else if (resultType == boolean.class && name.startsWith(IS_PREFIX)) {
					// Boolean getter
					pname = decapitalize(name.substring(2));
				}
				if (pname != null) {
					// replace the overridden getter method
					Method pre = pdReaderMap.get(pname);
					if (pre == null) {
						pdReaderMap.put(pname, method);
					} else if (pre.getReturnType().isAssignableFrom(method.getReturnType())) {
						pdReaderMap.put(pname, method);
					} else if (method.getReturnType().isAssignableFrom(pre.getReturnType())) {
						// the return type of exist getter is more precise
					} else {
						throw new IntrospectionException("getter name conflict: " + name);
					}
				}
			} else if (argCount == 1) {
				if (resultType == void.class && name.startsWith(SET_PREFIX)) {
					// Simple setter
					String pname = decapitalize(name.substring(3));
					if (pdWriterMap.containsKey(pname)) {
						throw new IntrospectionException("setter name conflict: " + name);
					}
					pdWriterMap.put(pname, method);
				}
			}
		}

		Map<String, PropertyInfo> pimap = new HashMap<String, PropertyInfo>();
		for (Map.Entry<String, Method> me : pdReaderMap.entrySet()) {
			String pname = me.getKey();
			Method reader = me.getValue();
			Method writer = pdWriterMap.get(pname);
			PropertyInfo pd = new PropertyInfo(pname, reader, writer);
			pimap.put(pname, pd);
		}

		return pimap;
	}

	/**
	 * Utility method to take a string and convert it to normal Java variable name capitalization.
	 * This normally means converting the first character from upper case to lower case, but in the
	 * (unusual) special case when there is more than one character and both the first and second
	 * characters are upper case, we leave it alone.
	 * <p>
	 * Thus "FooBah" becomes "fooBah" and "X" becomes "x", but "URL" stays as "URL".
	 * 
	 * @param name
	 *            The string to be decapitalized.
	 * @return The decapitalized version of the string.
	 */
	private static String decapitalize(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}
		if (name.length() > 1 && Character.isUpperCase(name.charAt(1))
				&& Character.isUpperCase(name.charAt(0))) {
			return name;
		}
		char chars[] = name.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

}