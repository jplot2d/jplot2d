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
package org.jplot2d.env;

import org.jplot2d.annotation.Hierarchy;
import org.jplot2d.annotation.HierarchyOp;
import org.jplot2d.annotation.Property;
import org.jplot2d.annotation.PropertyGroup;

import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class extract properties from method declaration, rather than using java introspector.
 * There is no add/remove listener on element interfaces.
 * The property annotation is defined on getter.
 * If a sub-interface override a getter, the overriding method will take effect.
 * The setter method type must match the getter's return type.
 * A setter methods with same name but different argument type are not allowed.
 * The indexed getter and setter are not considered.
 *
 * @author Jingjing Li
 */
public class InterfaceInfo {

    private static final String IS_PREFIX = "is";
    private static final String GET_PREFIX = "get";
    private static final String SET_PREFIX = "set";

    private static final Map<Class<?>, InterfaceInfo> interfaceInfoCache = new HashMap<>();

    /**
     * property name to PropertyInfo map. It contains all properties no matter if it has @property annotation.
     */
    private final Map<String, PropertyInfo> piMap;

    private final Map<Method, PropertyInfo> propReadMap = new HashMap<>();

    private final Map<Method, PropertyInfo> propWriteMap = new HashMap<>();

    /**
     * Hierarchy annotated Method to HierarchyOp map
     */
    private final Map<Method, HierarchyOp> hierarchyMethodMap = new HashMap<>();

    private final Map<String, PropertyInfo[]> pisGroupMap = new LinkedHashMap<>();

    /**
     * Load interface info for the given interface and its parents. The subclass may hide the info of its superclass.
     * For example, if a sub-interface only redeclare a getter, the info will report the property is read-only.
     *
     * @param interfaceClass the class to be analyzed
     */
    public static synchronized InterfaceInfo loadInterfaceInfo(Class<?> interfaceClass) {
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
                hierarchyMethodMap.put(method, hierAnn.value());
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

    @SuppressWarnings("UnusedParameters")
    private void load(Class<?> beanClass) {

        Map<String, List<PropertyInfo>> pilGroupMap = new HashMap<>();

        for (PropertyInfo p : piMap.values()) {
            Method readMethod = p.getReadMethod();
            Method writeMethod = p.getWriteMethod();
            if (readMethod != null) {
                Property pann = readMethod.getAnnotation(Property.class);
                if (pann != null) {
                    p.setDisplayName(pann.displayName());
                    p.setDisplayDigits(pann.displayDigits());
                    p.setShortDescription(pann.description());
                    p.setReadOnly(pann.readOnly());
                    p.setOrder(pann.order());
                    // only writable property can be in profile
                    p.setStyleable(pann.styleable() && writeMethod != null);
                    PropertyGroup pg = readMethod.getDeclaringClass().getAnnotation(PropertyGroup.class);
                    if (pg != null) {
                        List<PropertyInfo> pis = pilGroupMap.get(pg.value());
                        if (pis == null) {
                            pis = new ArrayList<>();
                            pilGroupMap.put(pg.value(), pis);
                        }
                        pis.add(p);
                    }
                }

                propReadMap.put(readMethod, p);
                if (writeMethod != null) {
                    propWriteMap.put(writeMethod, p);
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

    public PropertyInfo getPropertyInfo(String pname) {
        return piMap.get(pname);
    }

    public Map<String, PropertyInfo[]> getPropertyInfoGroupMap() {
        return pisGroupMap;
    }

    public Map<String, PropertyInfo[]> getProfilePropertyInfoGroupMap() {
        Map<String, PropertyInfo[]> result = new LinkedHashMap<>();

        for (Map.Entry<String, PropertyInfo[]> me : pisGroupMap.entrySet()) {
            String group = me.getKey();
            List<PropertyInfo> ppl = new ArrayList<>();
            for (PropertyInfo pi : me.getValue()) {
                if (pi.isStyleable()) {
                    ppl.add(pi);
                }
            }
            if (ppl.size() > 0) {
                PropertyInfo[] pps = ppl.toArray(new PropertyInfo[ppl.size()]);
                result.put(group, pps);
            }
        }

        return result;
    }

    /**
     * Returns <code>true</true> if the given property is writable.
     *
     * @param pname The property name
     * @return <code>true</true> if the given property is writable
     */
    public boolean isWritableProp(String pname) {
        return piMap.containsKey(pname) && piMap.get(pname).getWriteMethod() != null;
    }

    /**
     * Returns <code>true</true> if the given method is a property getter.
     *
     * @param method the method to be check
     * @return <code>true</true> if the given method is a property getter
     */
    protected boolean isPropReadMethod(Method method) {
        return propReadMap.containsKey(method);
    }

    /**
     * Returns <code>true</true> if the given method is a property setter.
     *
     * @param method the method to be check
     * @return <code>true</true> if the given method is a property setter
     */
    protected boolean isPropWriteMethod(Method method) {
        return propWriteMap.containsKey(method);
    }

    /**
     * Returns <code>true</true> if the given setter is disabled by property annotation.
     *
     * @param method the writer method
     * @return <code>true</true> if the given setter is disabled
     */
    protected boolean isPropWriteDisabled(Method method) {
        PropertyInfo pinfo = propWriteMap.get(method);
        if (pinfo == null) {
            throw new IllegalArgumentException("The method " + method.getName() + " is not a property writter.");
        }
        return pinfo.isReadOnly();
    }

    /**
     * Returns the getter of the given setter.
     *
     * @param method the writer method
     * @return the reader method
     */
    protected Method getPropReadMethodByWriteMethod(Method method) {
        PropertyInfo pinfo = propWriteMap.get(method);
        if (pinfo == null) {
            throw new IllegalArgumentException("The method " + method.getName() + " is not a property writter.");
        }
        return pinfo.getReadMethod();
    }

    protected boolean isGetCompMethod(Method method) {
        HierarchyOp hop = hierarchyMethodMap.get(method);
        return hop != null && hop == HierarchyOp.GET;
    }

    protected boolean isGetCompArrayMethod(Method method) {
        HierarchyOp hop = hierarchyMethodMap.get(method);
        return hop != null && hop == HierarchyOp.GETARRAY;
    }

    protected boolean isAddCompMethod(Method method) {
        HierarchyOp hop = hierarchyMethodMap.get(method);
        return hop != null && hop == HierarchyOp.ADD;
    }

    protected boolean isRemoveCompMethod(Method method) {
        HierarchyOp hop = hierarchyMethodMap.get(method);
        return hop != null && hop == HierarchyOp.REMOVE;
    }

    protected boolean isJoinElementMethod(Method method) {
        HierarchyOp hop = hierarchyMethodMap.get(method);
        return hop != null && hop == HierarchyOp.JOIN;
    }

    protected boolean isRefElementMethod(Method method) {
        HierarchyOp hop = hierarchyMethodMap.get(method);
        return hop != null && hop == HierarchyOp.REF;
    }

    protected boolean isRef2ElementMethod(Method method) {
        HierarchyOp hop = hierarchyMethodMap.get(method);
        return hop != null && hop == HierarchyOp.REF2;
    }

    protected boolean isAddRef2Method(Method method) {
        HierarchyOp hop = hierarchyMethodMap.get(method);
        return hop != null && hop == HierarchyOp.ADD_REF2;
    }

    /**
     * Extract an array of PropertyDescriptor from the given methods.
     *
     * @return An array of PropertyDescriptors, with the order of getter declared.
     * @throws IntrospectionException
     */
    private static Map<String, PropertyInfo> getPropertyInfo(Method[] methods) throws IntrospectionException {

        Map<String, Method> pdReaderMap = new LinkedHashMap<>();
        Map<String, Method> pdWriterMap = new HashMap<>();

        for (Method method : methods) {
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
                    } else //noinspection StatementWithEmptyBody
                        if (method.getReturnType().isAssignableFrom(pre.getReturnType())) {
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

        Map<String, PropertyInfo> pimap = new HashMap<>();
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
     * Utility method to take a string and convert it to normal Java variable name capitalization. This normally means
     * converting the first character from upper case to lower case, but in the (unusual) special case when there is
     * more than one character and both the first and second characters are upper case, we leave it alone.
     * <p/>
     * Thus "FooBah" becomes "fooBah" and "X" becomes "x", but "URL" stays as "URL".
     *
     * @param name The string to be de-capitalized.
     * @return The de-capitalized version of the string.
     */
    private static String decapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) {
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

}