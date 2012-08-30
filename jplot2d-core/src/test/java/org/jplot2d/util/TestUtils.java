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

import static org.jplot2d.util.NumberUtils.approximate;
import static junit.framework.TestCase.*;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.lang.reflect.Array;
import java.net.URL;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jplot2d.element.Axis;
import org.jplot2d.env.PropertyInfo;

/**
 * @author Jingjing Li
 * 
 */
public class TestUtils {

	private TestUtils() {

	}

	public static void checkDouble(double a, double ref) {
		assertEquals(a, ref, Math.abs(ref) * 1e-12);
	}

	public static void checkAxisTickRange(Axis axis, double b, double e) {
		Range r = axis.getTickManager().getRange();
		assertEquals(r.getStart(), b, Math.abs(b) * 1e-12);
		assertEquals(r.getEnd(), e, Math.abs(e) * 1e-12);
	}

	public static void checkAxisTickRange(Axis axis1, Axis axis2) {
		Range xr1 = axis1.getTickManager().getRange();
		Range xr2 = axis2.getTickManager().getRange();
		assertEquals(xr1.getStart(), xr2.getStart(), Math.abs(xr2.getStart()) * 1e-12);
		assertEquals(xr1.getStart(), xr2.getStart(), Math.abs(xr2.getStart()) * 1e-12);
	}

	public static void checkLine(Line2D a, Line2D b) {
		assertTrue(approximate(a.getX1(), b.getX1(), 4));
		assertTrue(approximate(a.getY1(), b.getY1(), 4));
		assertTrue(approximate(a.getX2(), b.getX2(), 4));
		assertTrue(approximate(a.getY2(), b.getY2(), 4));
	}

	public static void checkDimension(Dimension d, int width, int height) {
		assertEquals(d.width, width);
		assertEquals(d.height, height);
	}

	public static void checkDimension2D(Dimension2D d, Dimension2D v) {
		checkDimension2D(d, v.getWidth(), v.getHeight());
	}

	public static void checkDimension2D(Dimension2D d, double width, double height) {
		assertEquals(d.getWidth(), width, Math.abs(width) * 1e-12);
		assertEquals(d.getHeight(), height, Math.abs(height) * 1e-12);
	}

	public static void checkRectangle2D(Rectangle2D d, double x, double y, double width,
			double height) {
		assertEquals(d.getX(), x, Math.abs(x) * 1e-12);
		assertEquals(d.getY(), y, Math.abs(y) * 1e-12);
		assertEquals(d.getWidth(), width, Math.abs(width) * 1e-12);
		assertEquals(d.getHeight(), height, Math.abs(height) * 1e-12);
	}

	public static void checkRectangle2DSize(Rectangle2D d, double width, double height) {
		assertEquals(d.getWidth(), width, Math.abs(width) * 1e-12);
		assertEquals(d.getHeight(), height, Math.abs(height) * 1e-12);
	}

	public static void checkRange(Range r, double start, double end) {
		assertEquals(r.getStart(), start, Math.abs(start) * 1e-12);
		assertEquals(r.getEnd(), end, Math.abs(end) * 1e-12);
	}

	public static void checkPoint2D(Point2D p, double x, double y) {
		assertEquals(p.getX(), x, Math.abs(x) * 1e-12);
		assertEquals(p.getY(), y, Math.abs(y) * 1e-12);
	}

	public static void checkDoubleArray(double[] da, double... v) {
		assertEquals("length error", da.length, v.length);
		int length = v.length;
		for (int i = 0; i < length; i++) {
			assertEquals(da[i], v[i], Math.abs(v[i]) * 1e-12);
		}
	}

	public static void checkDoubleArray(Object array, double... v) {
		if (array instanceof double[]) {
			checkDoubleArray((double[]) array, v);
			return;
		}

		assertEquals("length error", Array.getLength(array), v.length);
		int length = v.length;
		for (int i = 0; i < length; i++) {
			assertEquals(Array.getDouble(array, i), v[i], Math.abs(v[i]) * 1e-12);
		}
	}

	public static void checkLongArray(long[] da, long... v) {
		assertEquals("length error", da.length, v.length);
		int length = v.length;
		for (int i = 0; i < length; i++) {
			assertEquals(da[i], v[i], Math.abs(v[i]) * 1e-12);
		}
	}

	public static void checkFormat(Format format, double[] values, String... strings) {
		assertEquals("length error", values.length, strings.length);
		int length = values.length;
		for (int i = 0; i < length; i++) {
			assertEquals(format.format(values[i]), strings[i]);
		}
	}

	public static void checkAxisTicks(Axis axis, double... v) {
		checkDoubleArray(axis.getTickManager().getTickValues(), v);
	}

	public static void checkAxisMinorTicks(Axis axis, double... v) {
		checkDoubleArray(axis.getTickManager().getMinorTickValues(), v);
	}

	public static void checkAxisLabels(Axis axis, String... v) {
		String[] labels = axis.getTickManager().getLabelStrings();
		assertEquals("length error", labels.length, v.length);
		for (int i = 0; i < v.length; i++) {
			assertEquals(labels[i], v[i]);
		}
	}

	public static <T> void checkHashSet(Set<T> set, T... strs) {
		HashSet<T> extraSet = new HashSet<T>(set);
		HashSet<T> omitSet = new HashSet<T>(Arrays.asList(strs));
		extraSet.removeAll(omitSet);
		omitSet.removeAll(set);

		if (extraSet.size() > 0 || omitSet.size() > 0) {
			StringBuilder msg = new StringBuilder("The set");
			if (omitSet.size() != 0) {
				msg.append(" dosn't contains ");
				msg.append(omitSet);
			}
			if (extraSet.size() != 0) {
				if (omitSet.size() != 0) {
					msg.append(" and have extra items ");
				} else {
					msg.append(" have extra items ");
				}
				msg.append(extraSet);
			}
			fail(msg.toString());
		}
	}

	public static <T> void checkCollecionOrder(Collection<T> set, T... strs) {
		Object[] clt = set.toArray();
		if (clt.length != strs.length) {
			fail("length error [" + ArrayUtils.toString(clt) + "] [" + ArrayUtils.toString(strs));
		}
		int length = clt.length;
		for (int i = 0; i < length; i++) {
			assertEquals(clt[i], strs[i]);
		}
	}

	public static void checkPropertyInfoNames(PropertyInfo[] pis, String... names) {
		if (pis.length != names.length) {
			fail("length error [" + ArrayUtils.toString(pis) + "] [" + ArrayUtils.toString(names));
		}
		int length = pis.length;
		for (int i = 0; i < length; i++) {
			assertEquals(pis[i].getName(), names[i]);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public static Class[] getClassesInPackage(String pckgname) throws ClassNotFoundException {
		ArrayList<Class> classes = new ArrayList<Class>();
		// Get a File object for the package
		File directory = null;
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			String path = pckgname.replace('.', '/');
			URL resource = cld.getResource(path);
			if (resource == null) {
				throw new ClassNotFoundException("No resource for " + path);
			}
			directory = new File(resource.getFile());
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(pckgname + " (" + directory
					+ ") does not appear to be a valid package");
		}
		if (directory.exists()) {
			// Get the list of the files contained in the package
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					classes.add(Class.forName(pckgname + '.'
							+ files[i].substring(0, files[i].length() - 6)));
				}
			}
		} else {
			throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
		}

		return classes.toArray(new Class[classes.size()]);
	}

}
