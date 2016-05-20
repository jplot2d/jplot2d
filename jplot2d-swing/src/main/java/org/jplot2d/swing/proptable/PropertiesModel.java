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
package org.jplot2d.swing.proptable;


import org.jplot2d.env.PropertyInfo;
import org.jplot2d.swing.proptable.property.MainProperty;
import org.jplot2d.swing.proptable.property.PropertyFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This model contains a list of PropertyGroup.
 *
 * @author Jingjing Li
 */
public class PropertiesModel implements Iterable<PropertyGroup> {

    protected final ArrayList<PropertyGroup> groups = new ArrayList<>();

    protected final Map<String, MainProperty<?>> propMap = new HashMap<>();

    public PropertiesModel(Map<String, PropertyInfo[]> pisGroupMap) {
        for (Map.Entry<String, PropertyInfo[]> me : pisGroupMap.entrySet()) {
            PropertyGroup group = new PropertyGroup(me.getKey());
            groups.add(group);
            for (PropertyInfo p : me.getValue()) {
                MainProperty<?> prop = PropertyFactory.createProperty(p);
                propMap.put(p.getName(), prop);
                group.add(prop);
            }
        }
    }

    protected MainProperty<?> p(String pname) {
        return propMap.get(pname);
    }

    public Iterator<PropertyGroup> iterator() {
        return groups.iterator();
    }

    public void checkEditable(String pname) {
        // do nothing
    }

}
