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
package org.jplot2d.gui.interaction;


import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import org.jplot2d.interaction.InteractionMode;
import org.jplot2d.interaction.MouseBehavior;
import org.jplot2d.interaction.MouseButtonCombination;
import org.jplot2d.interaction.MouseButtonCombinationEnablity;
import org.jplot2d.interaction.MousePreference;

/**
 * provide the default mouse preference.
 * 
 * @author Jingjing Li
 * 
 */
public class PlotDefaultMousePreference implements MousePreference {

    private static PlotDefaultMousePreference _instance = new PlotDefaultMousePreference();

    private static final Integer DEFAULT_CLICK_THRESHOLD = 3;

    private final Map<InteractionMode, Map<MouseBehavior, MouseButtonCombinationEnablity>> _bindingMap = new HashMap<InteractionMode, Map<MouseBehavior, MouseButtonCombinationEnablity>>();

    public static PlotDefaultMousePreference getInstance() {
        return _instance;
    }

    private PlotDefaultMousePreference() {
        initDefault();
    }

    private void initDefault() {
        PlotInteractionManager manager = PlotInteractionManager.getInstance();
        InteractionMode modes[] = PlotInteractionManager.getInstance()
                .getModes();
        for (InteractionMode mode : modes) {
            _bindingMap
                    .put(
                            mode,
                            new HashMap<MouseBehavior, MouseButtonCombinationEnablity>());
        }

        MouseButtonCombinationEnablity moveBA = new MouseButtonCombinationEnablity(
                0, 0, MouseEvent.NOBUTTON);
        MouseButtonCombinationEnablity clickBA = new MouseButtonCombinationEnablity(
                0, 1, MouseEvent.BUTTON1);
        MouseButtonCombinationEnablity ctlClickBA = new MouseButtonCombinationEnablity(
                MouseEvent.CTRL_DOWN_MASK, 1, MouseEvent.BUTTON1);
        MouseButtonCombinationEnablity metaClickBA = new MouseButtonCombinationEnablity(
                MouseEvent.META_DOWN_MASK, 1, MouseEvent.BUTTON1);
        MouseButtonCombinationEnablity popupBA = new MouseButtonCombinationEnablity(
                0, 1, MouseButtonCombination.ANY_BUTTON);
        MouseButtonCombinationEnablity dragBA = new MouseButtonCombinationEnablity(
                MouseEvent.BUTTON1_DOWN_MASK, 1, MouseEvent.BUTTON1);
        MouseButtonCombinationEnablity middleDragBA = new MouseButtonCombinationEnablity(
                MouseEvent.BUTTON2_DOWN_MASK, 1, MouseEvent.BUTTON2);
        MouseButtonCombinationEnablity wheelBA = new MouseButtonCombinationEnablity(
                0, 0, MouseEvent.NOBUTTON);
        MouseButtonCombinationEnablity ctlWheelBA = new MouseButtonCombinationEnablity(
                MouseEvent.CTRL_DOWN_MASK, 0, MouseEvent.NOBUTTON);
        MouseButtonCombinationEnablity metaWheelBA = new MouseButtonCombinationEnablity(
                MouseEvent.META_DOWN_MASK, 0, MouseEvent.NOBUTTON);

        addBinding(manager._defaultMode, manager._activeComponentBehavior,
                moveBA);
        addBinding(manager._defaultMode, manager._popupBehavior, popupBA);
        addBinding(manager._defaultMode, manager._moveComponentBehavior, dragBA);
        addBinding(manager._defaultMode, manager._marqueeZoomBehavior, dragBA);
        addBinding(manager._defaultMode, manager._wheelZoomBehavior, wheelBA);
        addBinding(manager._defaultMode, manager._panBehavior, middleDragBA);
        addBinding(manager._defaultMode, manager._axisRangeZoomBehavior, dragBA);
        addBinding(manager._defaultMode, manager._axisWheelZoomBehavior,
                wheelBA);
        addBinding(manager._defaultMode, manager._axisPanBehavior, middleDragBA);
        if (isMacOSX()) {
            addBinding(manager._defaultMode, manager._adaptiveZoomBehavior,
                    metaClickBA);
            addBinding(manager._defaultMode, manager._wheelFinerZoomBehavior,
                    metaWheelBA);
            addBinding(manager._defaultMode, manager._axisAdaptiveZoomBehavior,
                    metaClickBA);
            addBinding(manager._defaultMode,
                    manager._axisWheelFinerZoomBehavior, metaWheelBA);
        } else {
            addBinding(manager._defaultMode, manager._adaptiveZoomBehavior,
                    ctlClickBA);
            addBinding(manager._defaultMode, manager._wheelFinerZoomBehavior,
                    ctlWheelBA);
            addBinding(manager._defaultMode, manager._axisAdaptiveZoomBehavior,
                    ctlClickBA);
            addBinding(manager._defaultMode,
                    manager._axisWheelFinerZoomBehavior, ctlWheelBA);
        }

    }

    private void addBinding(InteractionMode mode, MouseBehavior behavior,
            MouseButtonCombinationEnablity mba) {
        Map<MouseBehavior, MouseButtonCombinationEnablity> mapinmode = _bindingMap
                .get(mode);
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
        return _bindingMap.keySet().toArray(new InteractionMode[0]);
    }

    public MouseBehavior[] getBehaviorsInMode(InteractionMode mode) {
        Map<MouseBehavior, MouseButtonCombinationEnablity> mapinmode = _bindingMap
                .get(mode);
        return mapinmode.keySet().toArray(new MouseBehavior[0]);
    }

    public MouseButtonCombinationEnablity getMouseButtonCombinationEnablity(
            InteractionMode mode, MouseBehavior behavior) {
        Map<MouseBehavior, MouseButtonCombinationEnablity> mapinmode = _bindingMap
                .get(mode);
        return mapinmode.get(behavior);
    }

}
