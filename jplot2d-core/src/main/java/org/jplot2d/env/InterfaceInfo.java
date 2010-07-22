/**
 * 
 */
package org.jplot2d.env;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.Redraw;

public class InterfaceInfo {

	private static Map<Class<?>, InterfaceInfo> interfaceInfoCache = new HashMap<Class<?>, InterfaceInfo>();

	private final Map<Method, Method> propWriteReadMap = new HashMap<Method, Method>();

	private final Collection<Method> propReadMethods = new HashSet<Method>();

	private final Map<Method, HierarchyOp> hierachyMethodMap = new HashMap<Method, HierarchyOp>();

	private final Collection<Method> redrawMethods = new HashSet<Method>();

	/** listener get add remove Methods */
	private final Collection<Method> listenerGarMethods = new HashSet<Method>();

	private final Map<String, PropertyDescriptor> properties = new HashMap<String, PropertyDescriptor>();

	public static InterfaceInfo loadInterfaceInfo(Class<?> interfaceClass) {
		InterfaceInfo iinfo = interfaceInfoCache.get(interfaceClass);
		if (iinfo == null) {
			iinfo = new InterfaceInfo();
			iinfo.loadBeanInfo(interfaceClass);
			for (Class<?> superClass : interfaceClass.getInterfaces()) {
				InterfaceInfo sii = loadInterfaceInfo(superClass);
				iinfo.merge(sii);
			}
		}
		return iinfo;
	}

	/**
	 * The subclass may hide the info of its superclass. For example, if a
	 * sub-interface only redeclare a getter, the info will report the property
	 * is read-only.
	 * 
	 * @param sii
	 */
	private void merge(InterfaceInfo sii) {
		propWriteReadMap.putAll(sii.propWriteReadMap);
		propReadMethods.addAll(sii.propReadMethods);
		listenerGarMethods.addAll(sii.listenerGarMethods);
		properties.putAll(sii.properties);
	}

	private void loadBeanInfo(Class<?> beanClass) {
		BeanInfo bi = null;
		try {
			bi = Introspector.getBeanInfo(beanClass);
		} catch (IntrospectionException e) {
			throw new Error(e);
		}
		PropertyDescriptor[] pds = bi.getPropertyDescriptors();
		for (PropertyDescriptor p : pds) {
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
					}
					properties.put(p.getName(), p);

					propReadMethods.add(readMethod);
					if (writeMethod != null) {
						propWriteReadMap.put(writeMethod, readMethod);
					}
				}
			}
		}

		MethodDescriptor[] mds = bi.getMethodDescriptors();
		for (MethodDescriptor md : mds) {
			Method method = md.getMethod();
			Hierarchy hierAnn = method.getAnnotation(Hierarchy.class);
			if (hierAnn != null) {
				hierachyMethodMap.put(method, hierAnn.value());
			}
			Redraw redrawAnn = method.getAnnotation(Redraw.class);
			if (redrawAnn != null) {
				redrawMethods.add(method);
			}
		}

		EventSetDescriptor[] events = bi.getEventSetDescriptors();
		for (EventSetDescriptor esd : events) {
			Method getListener = esd.getGetListenerMethod();
			if (getListener != null) {
				listenerGarMethods.add(getListener);
			}
			Method addListener = esd.getAddListenerMethod();
			if (addListener != null) {
				listenerGarMethods.add(addListener);
			}
			Method removeListener = esd.getRemoveListenerMethod();
			if (removeListener != null) {
				listenerGarMethods.add(removeListener);
			}
		}
	}

	/**
	 * @param method
	 * @return
	 */
	public boolean isPropReadMethod(Method method) {
		return propReadMethods.contains(method);
	}

	/**
	 * @param method
	 * @return
	 */
	public boolean isPropWriteMethod(Method method) {
		return propWriteReadMap.containsKey(method);
	}

	/**
	 * @param method
	 * @return
	 */
	public boolean isRedrawMethod(Method method) {
		return redrawMethods.contains(method);
	}

	public Method getPropReadMethodByWriteMethod(Method method) {
		return propWriteReadMap.get(method);
	}

	public boolean isListenerMethod(Method method) {
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

}