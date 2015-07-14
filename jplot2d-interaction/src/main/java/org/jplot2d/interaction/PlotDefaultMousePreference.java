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
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;


/**
 * provide the default mouse preference.
 *
 * @author Jingjing Li
 */
@NotThreadSafe
public class PlotDefaultMousePreference implements MousePreference {

    private static final PlotDefaultMousePreference instance = new PlotDefaultMousePreference();

    private static final Integer DEFAULT_CLICK_THRESHOLD = 3;

    private final Map<InteractionMode, Map<MouseBehavior, MouseButtonCombinationEnablity[]>> _bindingMap = new HashMap<>();

    private PlotDefaultMousePreference() {
        initDefault();
    }

    public static PlotDefaultMousePreference getInstance() {
        return instance;
    }

    private void initDefault() {
        PlotInteractionManager manager = PlotInteractionManager.getInstance();
        InteractionMode modes[] = PlotInteractionManager.getInstance().getModes();
        for (InteractionMode mode : modes) {
            _bindingMap.put(mode, new HashMap<MouseBehavior, MouseButtonCombinationEnablity[]>());
        }

        MouseButtonCombinationEnablity move = new MouseButtonCombinationEnablity(0, 0, MouseEvent.NOBUTTON);
        MouseButtonCombinationEnablity shiftDown = new MouseButtonCombinationEnablity(MouseEvent.SHIFT_DOWN_MASK, 0, MouseEvent.NOBUTTON);

//      MouseButtonCombinationEnablity click = new MouseButtonCombinationEnablity(0, 1, MouseEvent.BUTTON1);
        MouseButtonCombinationEnablity ctlClick = new MouseButtonCombinationEnablity(MouseEvent.CTRL_DOWN_MASK, 1, MouseEvent.BUTTON1);
        MouseButtonCombinationEnablity metaClickBA = new MouseButtonCombinationEnablity(MouseEvent.META_DOWN_MASK, 1, MouseEvent.BUTTON1);
        MouseButtonCombinationEnablity drag = new MouseButtonCombinationEnablity(MouseEvent.BUTTON1_DOWN_MASK, 1, MouseEvent.BUTTON1);
        MouseButtonCombinationEnablity altDrag = new MouseButtonCombinationEnablity(MouseEvent.ALT_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK, 1, MouseEvent.BUTTON1);
        MouseButtonCombinationEnablity middleDrag = new MouseButtonCombinationEnablity(MouseEvent.BUTTON2_DOWN_MASK, 1, MouseEvent.BUTTON2);
        MouseButtonCombinationEnablity wheel = new MouseButtonCombinationEnablity(0, 0, MouseEvent.NOBUTTON);
        MouseButtonCombinationEnablity ctlWheel = new MouseButtonCombinationEnablity(MouseEvent.CTRL_DOWN_MASK, 0, MouseEvent.NOBUTTON);

        addBinding(manager.defaultMode, manager.activeComponentBehavior, move);
        addBinding(manager.defaultMode, manager.showCoordinatesTooltipBehavior, shiftDown);
        addBinding(manager.defaultMode, manager.moveComponentBehavior, drag);
        addBinding(manager.defaultMode, manager.marqueeZoomBehavior, drag);
        addBinding(manager.defaultMode, manager.wheelZoomBehavior, wheel);
        addBinding(manager.defaultMode, manager.wheelFinerZoomBehavior, ctlWheel);
        addBinding(manager.defaultMode, manager.panBehavior, middleDrag, altDrag);
        addBinding(manager.defaultMode, manager.axisRangeZoomBehavior, drag);
        addBinding(manager.defaultMode, manager.axisWheelZoomBehavior, wheel);
        addBinding(manager.defaultMode, manager.axisWheelFinerZoomBehavior, ctlWheel);
        addBinding(manager.defaultMode, manager.axisPanBehavior, middleDrag, altDrag);
        addBinding(manager.defaultMode, manager.colorbarBehavior, drag);
        if (isMacOSX()) {
            addBinding(manager.defaultMode, manager.adaptiveZoomBehavior, metaClickBA);
            addBinding(manager.defaultMode, manager.axisAdaptiveZoomBehavior, metaClickBA);
        } else {
            addBinding(manager.defaultMode, manager.adaptiveZoomBehavior, ctlClick);
            addBinding(manager.defaultMode, manager.axisAdaptiveZoomBehavior, ctlClick);
        }

    }

    private void addBinding(InteractionMode mode, MouseBehavior behavior, MouseButtonCombinationEnablity... mba) {
        Map<MouseBehavior, MouseButtonCombinationEnablity[]> mapinmode = _bindingMap.get(mode);
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
        Map<MouseBehavior, MouseButtonCombinationEnablity[]> mapinmode = _bindingMap.get(mode);
        return mapinmode.keySet().toArray(new MouseBehavior[mapinmode.size()]);
    }

    public MouseButtonCombinationEnablity[] getMouseButtonCombinationEnablity(InteractionMode mode, MouseBehavior behavior) {
        Map<MouseBehavior, MouseButtonCombinationEnablity[]> mapinmode = _bindingMap.get(mode);
        return mapinmode.get(behavior);
    }

}
