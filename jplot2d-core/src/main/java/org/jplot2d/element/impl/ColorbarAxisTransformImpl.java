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

import org.jplot2d.element.AxisRangeLockGroup;
import org.jplot2d.util.Range;

import javax.annotation.Nullable;

/**
 * A colorbar tick manager always has a fixed AxisTransform, which always a NUMBER LINEAR axis type.
 * The range will be set by PlotEx when commit changes.
 */
public class ColorbarAxisTransformImpl extends AxisTransformImpl implements AxisTransformEx {

    public ColorbarAxisTransformImpl() {
        super();

        super.setAutoMargin(false);
    }

    public String getId() {
        StringBuilder sb = new StringBuilder();
        sb.append("AxisTransform");
        for (AxisTickManagerEx tick : tickManagers) {
            sb.append("(");
            for (AxisEx axis : tick.getAxes()) {
                sb.append(axis.getShortId()).append(',');
            }
            sb.replace(sb.length() - 1, sb.length(), ")");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    public void setAutoMargin(boolean autoMargin) {
        throw new UnsupportedOperationException("Cannot be set. Always false.");
    }

    @Nullable
    public AxisRangeLockGroupEx getLockGroup() {
        // always returns null since user should not change anything in lock group of colorbar.
        return null;
    }

    public void setLockGroup(@Nullable AxisRangeLockGroup group) {
        throw new UnsupportedOperationException();
    }

    public void addLayer(LayerEx layer) {
        throw new UnsupportedOperationException();
    }

    public void removeLayer(LayerEx layer) {
        throw new UnsupportedOperationException();
    }

    public void linkLayer(LayerEx layer) {
        throw new UnsupportedOperationException();
    }

    public void setCoreRange(@Nullable Range crange) {
        throw new UnsupportedOperationException("core range is always null in colorbar axis.");
    }

    public Range expandRangeToTick(Range ur) {
        throw new UnsupportedOperationException();
    }

}
