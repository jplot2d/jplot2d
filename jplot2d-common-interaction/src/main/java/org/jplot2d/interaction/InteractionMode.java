/**
 * Copyright 2010-2012 Jingjing Li.
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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * An operation mode contains a list of available commands and configuration of how to trigger them.
 *
 * @author Jingjing Li
 */
@NotThreadSafe
public class InteractionMode {

    private final String name;

    private final Set<MouseBehavior> availableBehaviors = new LinkedHashSet<>();

    private final Set<ValueChangeBehavior> vcBehaviors = new HashSet<>();

    final Map<MouseBehavior, Integer> modifiersKeyMap = new LinkedHashMap<>();

    final Map<MouseBehavior, MouseButtonCombination> clickMap = new LinkedHashMap<>();

    final Map<MouseBehavior, MouseButtonCombination> pressMap = new LinkedHashMap<>();

    final Map<MouseBehavior, MouseButtonCombination> releaseMap = new LinkedHashMap<>();

    final Map<MouseBehavior, MouseButtonCombination> dragMap = new LinkedHashMap<>();

    final Map<MouseBehavior, MouseButtonCombination> moveMap = new LinkedHashMap<>();

    final Map<MouseWheelBehavior, MouseButtonCombination> wheelMap = new LinkedHashMap<>();

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
        return availableBehaviors.toArray(new MouseBehavior[availableBehaviors.size()]);
    }

    public void setAvailableMouseBehaviors(MouseBehavior... behaviors) {
        Collections.addAll(availableBehaviors, behaviors);
    }

    public void setValueChangeBehaviors(ValueChangeBehavior... feedbacks) {
        Collections.addAll(vcBehaviors, feedbacks);
    }

    public ValueChangeBehavior[] getValueChangeBehaviors() {
        return vcBehaviors.toArray(new ValueChangeBehavior[vcBehaviors.size()]);
    }

    /**
     * Binds the given action with the MouseButtonAction.
     */
    public void bindMoveBehavior(MouseMoveBehavior behavior, MouseButtonCombination mbc) {
        if (mbc == null) {
            moveMap.remove(behavior);
            modifiersKeyMap.remove(behavior);
        } else {
            moveMap.put(behavior, mbc);
            if (mbc.getModifiers() != 0) {
                modifiersKeyMap.put(behavior, mbc.getModifiers());
            }
        }
    }

    /**
     * Binds the given behavior with the MouseButtonCombination.
     */
    public void bindClickBehavior(MouseClickBehavior behavior, MouseButtonCombination mbc) {
        if (mbc == null) {
            clickMap.remove(behavior);
        } else {
            clickMap.put(behavior, mbc);
        }
    }

    /**
     * Binds the given behavior with the MouseButtonCombination.
     */
    public void bindDragBehavior(MouseDragBehavior behavior, MouseButtonCombination mbc) {
        if (mbc == null) {
            pressMap.remove(behavior);
            dragMap.remove(behavior);
            releaseMap.remove(behavior);
        } else {
            MouseButtonCombination pressmbc = new MouseButtonCombination(mbc.getModifiers(),
                    mbc.getClickCount(), mbc.getButton());
            pressMap.put(behavior, pressmbc);
            MouseButtonCombination dragmbc = new MouseButtonCombination(mbc.getModifiers(),
                    MouseButtonCombination.ANY_CLICK_COUNT, MouseButtonCombination.ANY_BUTTON);
            dragMap.put(behavior, dragmbc);
            MouseButtonCombination releasembc = new MouseButtonCombination(0, 0,
                    MouseButtonCombination.ANY_CLICK_COUNT, MouseButtonCombination.ANY_BUTTON);
            releaseMap.put(behavior, releasembc);
        }
    }

    /**
     * Binds the given behavior with the MouseButtonCombination.
     */
    public void bindWheelBehavior(MouseWheelBehavior behavior, MouseButtonCombination mbc) {
        if (mbc == null) {
            wheelMap.remove(behavior);
        } else {
            MouseButtonCombination wheelmbc = new MouseButtonCombination(mbc.getModifiers(),
                    MouseButtonCombination.ANY_CLICK_COUNT, MouseButtonCombination.ANY_BUTTON);
            wheelMap.put(behavior, wheelmbc);
        }
    }

    public String toString() {
        return name;
    }

}
