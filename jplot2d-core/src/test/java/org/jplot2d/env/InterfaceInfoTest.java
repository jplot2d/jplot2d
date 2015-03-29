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

import org.jplot2d.element.Title;
import org.jplot2d.util.TestUtils;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.lang.reflect.Method;
import java.util.HashSet;

import static org.junit.Assert.assertTrue;

/**
 * @author Jingjing Li
 */
public class InterfaceInfoTest {

    /**
     * Test loadInterfaceInfo for all interfaces will throw no exception.
     *
     * @throws ClassNotFoundException
     */
    @Test
    public void testloadAllInterface() throws ClassNotFoundException {
        Class<?>[] classes = TestUtils.getClassesInPackage("org.jplot2d.element");
        for (Class<?> cls : classes) {
            InterfaceInfo.loadInterfaceInfo(cls);
        }

    }

    @Test
    public void testProfileParser() throws ClassNotFoundException {
        HashSet<Class<?>> props = new HashSet<>();
        Class<?>[] classes = TestUtils.getClassesInPackage("org.jplot2d.element");
        for (Class<?> cls : classes) {
            InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(cls);
            for (PropertyInfo[] pinfos : iinfo.getPropertyInfoGroupMap().values()) {
                for (PropertyInfo pinfo : pinfos) {
                    if (pinfo.getWriteMethod() != null) {
                        Class<?> ptype = pinfo.getPropertyType();
                        props.add(ptype);
                        // System.out.println(cls.getSimpleName() + "\t" + pinfo.getName() + "\t" + ptype);
                    }
                }
            }
        }

        System.out.println("All return types:");
        for (Class<?> cls : props) {
            System.out.println(cls);
        }
    }

    /**
     * Test the Title.setLocation() can match Component.getLocation()
     *
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Test
    public void testTitle() throws SecurityException, NoSuchMethodException {
        InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(Title.class);
        Method getLocationMethod = Title.class.getMethod("getLocation");
        Method setLocationMethod = Title.class.getMethod("setLocation", Point2D.class);
        assertTrue(iinfo.isPropReadMethod(getLocationMethod));
        assertTrue(iinfo.isPropWriteMethod(setLocationMethod));
    }
}
