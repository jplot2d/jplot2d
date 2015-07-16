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

import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

/**
 * An operation mode contains a list of available behavior and configuration of how to trigger them.
 *
 * @author Jingjing Li
 */
@NotThreadSafe
public class InteractionMode {

    final Map<MouseBehavior, List<Integer>> modifiersKeyMap = new LinkedHashMap<>();
    final Map<MouseBehavior, List<MouseButtonCombination>> moveMap = new LinkedHashMap<>();
    final Map<MouseBehavior, List<MouseButtonCombination>> clickMap = new LinkedHashMap<>();
    final Map<MouseBehavior, List<MouseButtonCombination>> pressMap = new LinkedHashMap<>();
    final Map<MouseBehavior, List<MouseButtonCombination>> releaseMap = new LinkedHashMap<>();
    final Map<MouseBehavior, List<MouseButtonCombination>> dragMap = new LinkedHashMap<>();
    final Map<MouseBehavior, List<MouseButtonCombination>> wheelMap = new LinkedHashMap<>();
    private final String name;
    private final Set<MouseBehavior> availableBehaviors = new LinkedHashSet<>();
    private final Set<ValueChangeBehavior> vcBehaviors = new HashSet<>();

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

    public ValueChangeBehavior[] getValueChangeBehaviors() {
        return vcBehaviors.toArray(new ValueChangeBehavior[vcBehaviors.size()]);
    }

    public void setValueChangeBehaviors(ValueChangeBehavior... feedbacks) {
        Collections.addAll(vcBehaviors, feedbacks);
    }

    public void clearBinding() {
        modifiersKeyMap.clear();
        moveMap.clear();
        clickMap.clear();
        pressMap.clear();
        releaseMap.clear();
        dragMap.clear();
        wheelMap.clear();
    }

    private <M> void put(Map<MouseBehavior, List<M>> map, MouseBehavior b, M mbc) {
        List<M> mbcList = map.get(b);
        if (mbcList == null) {
            mbcList = new ArrayList<>(1);
            map.put(b, mbcList);
        }
        mbcList.add(mbc);
    }

    /**
     * Binds the given action with the MouseButtonAction.
     */
    public void bindMoveBehavior(MouseMoveBehavior behavior, MouseButtonCombination mbc) {
        if (mbc == null) {
            moveMap.remove(behavior);
            modifiersKeyMap.remove(behavior);
        } else {
            put(moveMap, behavior, mbc);
            if (mbc.getModifiers() != 0) {
                put(modifiersKeyMap, behavior, mbc.getModifiers());
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
            put(clickMap, behavior, mbc);
        }
    }

    /**
     * Binds the drag behavior with the MouseButtonCombination. A drag behavior also receive mouse move event.
     */
    public void bindDragBehavior(MouseDragBehavior behavior, MouseButtonCombination mbc) {
        if (mbc == null) {
            moveMap.remove(behavior);
            modifiersKeyMap.remove(behavior);
            pressMap.remove(behavior);
            dragMap.remove(behavior);
            releaseMap.remove(behavior);
        } else {
            if (behavior.isMoveAware()) {
                int modifierWithoutButton = mbc.getModifiers() & ~mbc.getButtonMask();
                put(moveMap, behavior, new MouseButtonCombination(modifierWithoutButton, 0, GenericMouseEvent.NOBUTTON));
                if (mbc.getModifiers() != 0) {
                    put(modifiersKeyMap, behavior, mbc.getModifiers());
                }
            }
            MouseButtonCombination pressmbc = new MouseButtonCombination(mbc.getModifiers(),
                    mbc.getClickCount(), mbc.getButton());
            put(pressMap, behavior, pressmbc);
            MouseButtonCombination dragmbc = new MouseButtonCombination(mbc.getModifiers(),
                    MouseButtonCombination.ANY_CLICK_COUNT, MouseButtonCombination.ANY_BUTTON);
            put(dragMap, behavior, dragmbc);
            MouseButtonCombination releasembc = new MouseButtonCombination(0, 0,
                    MouseButtonCombination.ANY_CLICK_COUNT, MouseButtonCombination.ANY_BUTTON);
            put(releaseMap, behavior, releasembc);
        }
    }

    /**
     * Binds the wheel behavior with the MouseButtonCombination.
     */
    public void bindWheelBehavior(MouseWheelBehavior behavior, MouseButtonCombination mbc) {
        if (mbc == null) {
            wheelMap.remove(behavior);
        } else {
            MouseButtonCombination wheelmbc = new MouseButtonCombination(mbc.getModifiers(),
                    MouseButtonCombination.ANY_CLICK_COUNT, MouseButtonCombination.ANY_BUTTON);
            put(wheelMap, behavior, wheelmbc);
        }
    }

    public String toString() {
        return name;
    }

}
