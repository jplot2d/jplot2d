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

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;


/**
 * provide the default mouse preference.
 *
 * @author Jingjing Li
 */
@NotThreadSafe
public class PlotDefaultMousePreference implements MousePreference {

    private static final PlotDefaultMousePreference instance = new PlotDefaultMousePreference();

    private static final Integer DEFAULT_CLICK_THRESHOLD = 3;

    private final Map<InteractionMode, Map<MouseBehavior, MouseButtonCombinationEnablity>> _bindingMap = new HashMap<>();

    public static PlotDefaultMousePreference getInstance() {
        return instance;
    }

    private PlotDefaultMousePreference() {
        initDefault();
    }

    private void initDefault() {
        PlotInteractionManager manager = PlotInteractionManager.getInstance();
        InteractionMode modes[] = PlotInteractionManager.getInstance().getModes();
        for (InteractionMode mode : modes) {
            _bindingMap.put(mode, new HashMap<MouseBehavior, MouseButtonCombinationEnablity>());
        }

        MouseButtonCombinationEnablity moveBA = new MouseButtonCombinationEnablity(0, 0,
                MouseEvent.NOBUTTON);
        MouseButtonCombinationEnablity moveCoordinatesBA = new MouseButtonCombinationEnablity(
                MouseEvent.SHIFT_DOWN_MASK, 0, MouseEvent.NOBUTTON);
        //noinspection UnusedAssignment
        MouseButtonCombinationEnablity clickBA = new MouseButtonCombinationEnablity(0, 1,
                MouseEvent.BUTTON1);
        MouseButtonCombinationEnablity ctlClickBA = new MouseButtonCombinationEnablity(
                MouseEvent.CTRL_DOWN_MASK, 1, MouseEvent.BUTTON1);
        MouseButtonCombinationEnablity metaClickBA = new MouseButtonCombinationEnablity(
                MouseEvent.META_DOWN_MASK, 1, MouseEvent.BUTTON1);
        MouseButtonCombinationEnablity dragBA = new MouseButtonCombinationEnablity(
                MouseEvent.BUTTON1_DOWN_MASK, 1, MouseEvent.BUTTON1);
        MouseButtonCombinationEnablity middleDragBA = new MouseButtonCombinationEnablity(
                MouseEvent.BUTTON2_DOWN_MASK, 1, MouseEvent.BUTTON2);
        MouseButtonCombinationEnablity wheelBA = new MouseButtonCombinationEnablity(0, 0,
                MouseEvent.NOBUTTON);
        MouseButtonCombinationEnablity ctlWheelBA = new MouseButtonCombinationEnablity(
                MouseEvent.CTRL_DOWN_MASK, 0, MouseEvent.NOBUTTON);
        MouseButtonCombinationEnablity metaWheelBA = new MouseButtonCombinationEnablity(
                MouseEvent.META_DOWN_MASK, 0, MouseEvent.NOBUTTON);

        addBinding(manager.defaultMode, manager.activeComponentBehavior, moveBA);
        addBinding(manager.defaultMode, manager.showCoordinatesTooltipBehavior, moveCoordinatesBA);
        addBinding(manager.defaultMode, manager.moveComponentBehavior, dragBA);
        addBinding(manager.defaultMode, manager.marqueeZoomBehavior, dragBA);
        addBinding(manager.defaultMode, manager.wheelZoomBehavior, wheelBA);
        addBinding(manager.defaultMode, manager.panBehavior, middleDragBA);
        addBinding(manager.defaultMode, manager.axisRangeZoomBehavior, dragBA);
        addBinding(manager.defaultMode, manager.axisWheelZoomBehavior, wheelBA);
        addBinding(manager.defaultMode, manager.axisPanBehavior, middleDragBA);
        if (isMacOSX()) {
            addBinding(manager.defaultMode, manager.adaptiveZoomBehavior, metaClickBA);
            addBinding(manager.defaultMode, manager.wheelFinerZoomBehavior, metaWheelBA);
            addBinding(manager.defaultMode, manager.axisAdaptiveZoomBehavior, metaClickBA);
            addBinding(manager.defaultMode, manager.axisWheelFinerZoomBehavior, metaWheelBA);
        } else {
            addBinding(manager.defaultMode, manager.adaptiveZoomBehavior, ctlClickBA);
            addBinding(manager.defaultMode, manager.wheelFinerZoomBehavior, ctlWheelBA);
            addBinding(manager.defaultMode, manager.axisAdaptiveZoomBehavior, ctlClickBA);
            addBinding(manager.defaultMode, manager.axisWheelFinerZoomBehavior, ctlWheelBA);
        }

    }

    private void addBinding(InteractionMode mode, MouseBehavior behavior,
                            MouseButtonCombinationEnablity mba) {
        Map<MouseBehavior, MouseButtonCombinationEnablity> mapinmode = _bindingMap.get(mode);
        mapinmode.put(behavior, mba);
    }

    private boolean isMacOSX() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.startsWith("mac os x");
    }

    public Integer getClickThreshold() {
        return DEFAULT_CLICK_THRESHOLD;
    }

    public InteractionMode[] getModes() {
        return _bindingMap.keySet().toArray(new InteractionMode[_bindingMap.size()]);
    }

    public MouseBehavior[] getBehaviorsInMode(InteractionMode mode) {
        Map<MouseBehavior, MouseButtonCombinationEnablity> mapinmode = _bindingMap.get(mode);
        return mapinmode.keySet().toArray(new MouseBehavior[mapinmode.size()]);
    }

    public MouseButtonCombinationEnablity getMouseButtonCombinationEnablity(InteractionMode mode,
                                                                            MouseBehavior behavior) {
        Map<MouseBehavior, MouseButtonCombinationEnablity> mapinmode = _bindingMap.get(mode);
        return mapinmode.get(behavior);
    }

}
