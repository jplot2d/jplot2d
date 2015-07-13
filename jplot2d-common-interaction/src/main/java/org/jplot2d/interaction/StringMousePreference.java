/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.interaction;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

/**
 * A mouse preference can load from / save to a properties string.
 *
 * @author Jingjing Li
 */
public class StringMousePreference implements MousePreference {

    protected final InteractionManager _imanager;
    private final Map<InteractionMode, Map<MouseBehavior, MouseButtonCombinationEnablity[]>> _bindingMap;
    private Integer _clickThreshold;

    public StringMousePreference(InteractionManager imanager) {
        _imanager = imanager;

        _bindingMap = new LinkedHashMap<>();

        // initialize the binding map
        InteractionMode modes[] = imanager.getModes();
        for (InteractionMode mode : modes) {
            Map<MouseBehavior, MouseButtonCombinationEnablity[]> behaviorMap = new LinkedHashMap<>();
            _bindingMap.put(mode, behaviorMap);
            for (MouseBehavior behavior : mode.getAvailableMouseBehaviors()) {
                behaviorMap.put(behavior, null);
            }
        }
    }

    /**
     * Encode the BindingPreference into a string.
     *
     * @return a string
     */
    public static String encodeBindingPreference(MousePreference prefs) {

        Properties prop = new Properties();
        for (InteractionMode mode : prefs.getModes()) {
            for (MouseBehavior behavior : prefs.getBehaviorsInMode(mode)) {
                MouseButtonCombinationEnablity[] mbces = prefs.getMouseButtonCombinationEnablity(mode, behavior);
                String key = mode.getName() + "." + behavior.getName();
                StringBuilder sb = new StringBuilder();
                for (MouseButtonCombinationEnablity mbce : mbces) {
                    sb.append(mbce.toString()).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                prop.put(key, sb.toString());
            }
        }

        StringWriter writer = new StringWriter();
        try {
            prop.store(writer, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }

    /**
     * Loads the mouse binding preference from the given binding String.
     */
    public void loadBindingPreference(String bindingStr) {
        if (bindingStr == null) {
            return;
        }
        // build binding map
        Properties prop = new Properties();
        try {
            prop.load(new StringReader(bindingStr));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // initialize the binding map
        InteractionMode modes[] = _imanager.getModes();
        for (InteractionMode mode : modes) {
            Map<MouseBehavior, MouseButtonCombinationEnablity[]> behaviorMap = _bindingMap.get(mode);
            for (MouseBehavior behavior : mode.getAvailableMouseBehaviors()) {
                String bkey = mode.getName() + "." + behavior.getName();
                String bstr = (String) prop.get(bkey);
                if (bstr == null || bstr.length() == 0) {
                    MouseButtonCombinationEnablity mbce = new MouseButtonCombinationEnablity(null, false);
                    behaviorMap.put(behavior, new MouseButtonCombinationEnablity[]{mbce});
                } else {
                    String[] mbceStrs = bstr.split(",");
                    List<MouseButtonCombinationEnablity> mbceList = new ArrayList<>(mbceStrs.length);
                    for (String mbceStr : mbceStrs) {
                        try {
                            MouseButtonCombinationEnablity mbce = MouseButtonCombinationEnablity.valueOf(mbceStr);
                            mbceList.add(mbce);
                        } catch (Exception ignored) {

                        }
                    }
                    if (mbceList.size() == 0) {
                        mbceList.add(new MouseButtonCombinationEnablity(null, false));
                    }
                    behaviorMap.put(behavior, mbceList.toArray(new MouseButtonCombinationEnablity[mbceList.size()]));
                }
            }
        }
    }

    public Integer getClickThreshold() {
        return _clickThreshold;
    }

    public void setClickThreshold(Integer threshold) {
        _clickThreshold = threshold;
    }

    public InteractionMode[] getModes() {
        return _bindingMap.keySet().toArray(new InteractionMode[_bindingMap.size()]);
    }

    public MouseBehavior[] getBehaviorsInMode(InteractionMode mode) {
        Map<MouseBehavior, MouseButtonCombinationEnablity[]> mapinmode = _bindingMap.get(mode);
        return mapinmode.keySet().toArray(new MouseBehavior[mapinmode.size()]);
    }

    public MouseButtonCombinationEnablity[] getMouseButtonCombinationEnablity(InteractionMode mode, MouseBehavior behavior) {
        Map<MouseBehavior, MouseButtonCombinationEnablity[]> mapinmode = _bindingMap.get(mode);
        return mapinmode.get(behavior);
    }

}
