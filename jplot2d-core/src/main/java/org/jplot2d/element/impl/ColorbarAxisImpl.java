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
package org.jplot2d.element.impl;

import org.jplot2d.element.*;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 */
public class ColorbarAxisImpl extends AxisImpl implements ColorbarAxisEx {

    private final int id;

    public ColorbarAxisImpl(int id) {
        this(id, new AxisTitleImpl());
        setSelectable(true);
    }

    public ColorbarAxisImpl(int id, AxisTitleEx axisTitle) {
        super(axisTitle);
        this.id = id;
    }

    @Override
    public String getId() {
        switch (id) {
            case 0:
                return "InnerAxis";
            case 1:
                return "OuterAxis";
        }
        return "ColorbarAxis@" + Integer.toHexString(System.identityHashCode(this));
    }

    @Override
    public String getShortId() {
        String pid = getParent().getShortId();
        if (pid == null) {
            return getId();
        } else {
            return getId() + "." + pid;
        }
    }

    @Override
    public InvokeStep getInvokeStepFormParent() {
        if (parent == null) {
            return null;
        }

        try {
            switch (id) {
                case 0:
                    Method method0 = Colorbar.class.getMethod("getInnerAxis");
                    return new InvokeStep(method0);
                case 1:
                    Method method1 = Colorbar.class.getMethod("getOuterAxis");
                    return new InvokeStep(method1);
                default:
                    return null;
            }
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
    }

    @Nonnull
    @Override
    public ColorbarEx getParent() {
        return (ColorbarEx) super.getParent();
    }

    @Nonnull
    @Override
    public AxisTickManagerEx getTickManager() {
        // An axis of colorbar always has a tick manager.
        assert tickManager != null;
        return tickManager;
    }

    @Override
    public AxisOrientation getOrientation() {
        switch (getParent().getPosition()) {
            case LEFT:
            case RIGHT:
                return AxisOrientation.VERTICAL;
            case TOP:
            case BOTTOM:
                return AxisOrientation.HORIZONTAL;
            default:
                return null;
        }
    }

    @Override
    public ColorbarAxisEx copyStructure(@Nonnull Map<ElementEx, ElementEx> orig2copyMap) {
        ColorbarAxisImpl result = new ColorbarAxisImpl(id, (AxisTitleEx) title.copyStructure(orig2copyMap));

        orig2copyMap.put(this, result);

        // copy or link axis tick manager
        if (tickManager != null) {
            AxisTickManagerEx atmCopy = (AxisTickManagerEx) orig2copyMap.get(tickManager);
            if (atmCopy == null) {
                atmCopy = (AxisTickManagerEx) tickManager.copyStructure(orig2copyMap);
            }
            result.tickManager = atmCopy;
            atmCopy.addAxis(result);
        }

        return result;

    }

    protected boolean isTickAscSide() {
        switch (getParent().getPosition()) {
            case TOP:
                return (id == 0) == (getTickSide() == AxisTickSide.INWARD);
            case BOTTOM:
                return (id == 0) == (getTickSide() == AxisTickSide.OUTWARD);
            case LEFT:
                return (id == 0) == (getTickSide() == AxisTickSide.INWARD);
            case RIGHT:
                return (id == 0) == (getTickSide() == AxisTickSide.OUTWARD);
            default:
                throw new IllegalStateException(getParent().getPosition().toString());
        }
    }

    protected boolean isLabelAscSide() {
        switch (getParent().getPosition()) {
            case TOP:
                return (id == 0) == (getLabelSide() == AxisLabelSide.INWARD);
            case BOTTOM:
                return (id == 0) == (getLabelSide() == AxisLabelSide.OUTWARD);
            case LEFT:
                return (id == 0) == (getLabelSide() == AxisLabelSide.INWARD);
            case RIGHT:
                return (id == 0) == (getLabelSide() == AxisLabelSide.OUTWARD);
            default:
                throw new IllegalStateException(getParent().getPosition().toString());
        }
    }

    @Nonnull
    protected HAlign getLabelHAlign() {
        switch (getParent().getPosition()) {
            case TOP:
                return (id == 0) == (getLabelSide() == AxisLabelSide.INWARD) ? HAlign.LEFT : HAlign.RIGHT;
            case BOTTOM:
                return (id == 0) == (getLabelSide() == AxisLabelSide.OUTWARD) ? HAlign.LEFT : HAlign.RIGHT;
            case LEFT:
                return (id == 0) == (getLabelSide() == AxisLabelSide.INWARD) ? HAlign.RIGHT : HAlign.LEFT;
            case RIGHT:
                return (id == 0) == (getLabelSide() == AxisLabelSide.OUTWARD) ? HAlign.RIGHT : HAlign.LEFT;
            default:
                throw new IllegalStateException(getParent().getPosition().toString());
        }
    }

    protected boolean isTitleAscSide() {
        switch (getParent().getPosition()) {
            case TOP:
                return id != 0;
            case BOTTOM:
                return id == 0;
            case LEFT:
                return id != 0;
            case RIGHT:
                return id == 0;
            default:
                throw new IllegalStateException(getParent().getPosition().toString());
        }
    }

}
