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
package org.jplot2d.interaction;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * An operation mode contains a list of available commands and configuration of
 * how to trigger them.
 * 
 * @author Jingjing Li
 * 
 */
public class InteractionMode {

    private String name;

    private Set<MouseBehavior> availableBehaviors = new LinkedHashSet<MouseBehavior>();

    private Set<ValueChangeBehavior> vcBehaviors = new HashSet<ValueChangeBehavior>();

    final Map<MouseBehavior, MouseButtonCombination> clickMap = new LinkedHashMap<MouseBehavior, MouseButtonCombination>();

    final Map<MouseBehavior, MouseButtonCombination> pressMap = new LinkedHashMap<MouseBehavior, MouseButtonCombination>();

    final Map<MouseBehavior, MouseButtonCombination> releaseMap = new LinkedHashMap<MouseBehavior, MouseButtonCombination>();

    final Map<MouseBehavior, MouseButtonCombination> dragMap = new LinkedHashMap<MouseBehavior, MouseButtonCombination>();

    final Map<MouseBehavior, MouseButtonCombination> moveMap = new LinkedHashMap<MouseBehavior, MouseButtonCombination>();

    final Map<MouseWheelBehavior, MouseButtonCombination> wheelMap = new LinkedHashMap<MouseWheelBehavior, MouseButtonCombination>();

    public InteractionMode(String name) {
        this.name = name;
    }

    /**
     * Returns the mode name.
     * 
     * @return the mode name
     */
    public String getName() {
        return this.name;
    }

    public MouseBehavior[] getAvailableMouseBehaviors() {
        return availableBehaviors.toArray(new MouseBehavior[0]);
    }

    public void setAvailableMouseBehaviors(MouseBehavior... behaviors) {
        for (MouseBehavior behavior : behaviors) {
            availableBehaviors.add(behavior);
        }
    }

    public void setValueChangeBehaviors(ValueChangeBehavior... feedbacks) {
        for (ValueChangeBehavior feedback : feedbacks) {
            vcBehaviors.add(feedback);
        }
    }

    public ValueChangeBehavior[] getValueChangeBehaviors() {
        return vcBehaviors.toArray(new ValueChangeBehavior[0]);
    }

    /**
     * Binds the given action with the MouseButtonAction.
     */
    public void bindMoveBehavior(MouseMoveBehavior behavior,
            MouseButtonCombination mbc) {
        if (mbc == null) {
            moveMap.remove(behavior);
        } else {
            moveMap.put(behavior, mbc);
        }
    }

    /**
     * Binds the given behavior with the MouseButtonCombination.
     */
    public void bindClickBehavior(MouseClickBehavior behavior,
            MouseButtonCombination mbc) {
        if (mbc == null) {
            clickMap.remove(behavior);
        } else {
            clickMap.put(behavior, mbc);
        }
    }

    /**
     * Binds the given behavior with the MouseButtonCombination.
     */
    public void bindDragBehavior(MouseDragBehavior behavior,
            MouseButtonCombination mbc) {
        if (mbc == null) {
            pressMap.remove(behavior);
            dragMap.remove(behavior);
            releaseMap.remove(behavior);
        } else {
            MouseButtonCombination pressmbc = new MouseButtonCombination(mbc
                    .getModifiers(), mbc.getClickCount(), mbc.getButton());
            pressMap.put(behavior, pressmbc);
            MouseButtonCombination dragmbc = new MouseButtonCombination(mbc
                    .getModifiers(), MouseButtonCombination.ANY_CLICK_COUNT,
                    MouseButtonCombination.ANY_BUTTON);
            dragMap.put(behavior, dragmbc);
            MouseButtonCombination releasembc = new MouseButtonCombination(0,
                    0, MouseButtonCombination.ANY_CLICK_COUNT,
                    MouseButtonCombination.ANY_BUTTON);
            releaseMap.put(behavior, releasembc);
        }
    }

    /**
     * Binds the given behavior with the MouseButtonCombination.
     */
    public void bindWheelBehavior(MouseWheelBehavior behavior,
            MouseButtonCombination mbc) {
        if (mbc == null) {
            wheelMap.remove(behavior);
        } else {
            MouseButtonCombination wheelmbc = new MouseButtonCombination(mbc
                    .getModifiers(), MouseButtonCombination.ANY_CLICK_COUNT,
                    MouseButtonCombination.ANY_BUTTON);
            wheelMap.put(behavior, wheelmbc);
        }
    }

    public String toString() {
        return name;
    }

}
