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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * This class handle the mouse actions that can respond to user's mouse events like left-click, double-click, right-click.
 * The mouse event will translate to application level behavior, such as select an object, select data points in a layer, zoom in/out, etc.
 * The translation can be configured by register and unregister the binding from mouse events to application behaviors.
 *
 * @author Jingjing Li
 */
@NotThreadSafe
public abstract class InteractionManager {

    private int clickThreshold;

    private final Map<String, InteractionMode> modeMap = new LinkedHashMap<>();

    private InteractionMode defaultMode;

    public int getClickThreshold() {
        return clickThreshold;
    }

    /**
     * Register modes to this InteractionManager. All available behaviors of modes are registered together.
     *
     * @param mode the interaction mode
     */
    protected void registerMode(InteractionMode mode) {
        modeMap.put(mode.getName(), mode);
    }

    public InteractionMode getMode(String name) {
        return modeMap.get(name);
    }

    public InteractionMode[] getModes() {
        return modeMap.values().toArray(new InteractionMode[modeMap.size()]);
    }

    public InteractionMode getDefaultMode() {
        return defaultMode;
    }

    protected void setDefaultMode(InteractionMode defaultMode) {
        this.defaultMode = defaultMode;
    }

    /**
     * Set the mouse preference. The pereference store the binding from mouse events to application behaviors.
     *
     * @param prefs the mouse preference
     */
    public void setMousePreference(MousePreference prefs) {

        clickThreshold = prefs.getClickThreshold();

        for (InteractionMode mode : modeMap.values()) {
            MouseBehavior[] behaviors = mode.getAvailableMouseBehaviors();
            for (MouseBehavior behavior : behaviors) {
                MouseButtonCombinationEnablity[] mbces = prefs.getMouseButtonCombinationEnablity(mode, behavior);
                for (MouseButtonCombinationEnablity mbce : mbces) {
                    MouseButtonCombination mba = ((mbce == null) || !mbce.isEnabled()) ? null : mbce.getMouseButtonCombination();
                    if (behavior instanceof MouseClickBehavior) {
                        mode.bindClickBehavior((MouseClickBehavior) behavior, mba);
                    } else if (behavior instanceof MouseMoveBehavior) {
                        mode.bindMoveBehavior((MouseMoveBehavior) behavior, mba);
                    } else if (behavior instanceof MouseDragBehavior) {
                        mode.bindDragBehavior((MouseDragBehavior) behavior, mba);
                    } else if (behavior instanceof MouseWheelBehavior) {
                        mode.bindWheelBehavior((MouseWheelBehavior) behavior, mba);
                    }
                }
            }
        }
    }

}
