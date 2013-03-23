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

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.jplot2d.element.Element;

/**
 * Profile can persist in java Preferences.
 * 
 * @author Jingjing Li
 * 
 */
public class PrefsProfile implements Profile {

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
		Class<?> eif = null;
		Class<?>[] interfaces = element.getClass().getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			eif = getElementInterface(interfaces[i]);
		}

		if (eif == null) {
			return;
		}

		String nodeName = eif.getSimpleName();

		boolean nodeExist = false;
		try {
			nodeExist = pref.nodeExists(nodeName);
		} catch (BackingStoreException e) {
		}

		Preferences epref = pref.node(nodeName);
		if (!nodeExist) {
			initElementPrefs(eif, epref, element);
		} else {
			applyElementPrefs(eif, epref, element);
		}

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
	 * Initiate the given pref node with the element instance
	 * 
	 * @param node
	 * @param element
	 */
	private static void initElementPrefs(Class<?> eif, Preferences node, Element element) {
		// TODO Auto-generated method stub

	}

	/**
	 * Apply the given pref node with the element instance
	 * 
	 * @param node
	 * @param element
	 */
	private static void applyElementPrefs(Class<?> eif, Preferences node, Element element) {
		InterfaceInfo iinfo = InterfaceInfo.loadInterfaceInfo(eif);
		for (PropertyInfo pinfo : iinfo.getPropertyInfos()) {
			if (pinfo.getWriteMethod() != null) {
				
			}
		}
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
