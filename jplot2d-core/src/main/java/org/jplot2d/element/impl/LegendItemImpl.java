/*
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
package org.jplot2d.element.impl;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;

/**
 * @author Jingjing Li
 */
public abstract class LegendItemImpl extends ElementImpl implements LegendItemEx {

    private boolean visible = true;

    private double locX, locY;

    private LegendEx legend;

    public LegendItemImpl() {

    }

    public String getId() {
        return "LegendItem";
    }

    public InvokeStep getInvokeStepFormParent() {
        if (parent == null) {
            return null;
        }

        Method method;
        try {
            method = XYGraphEx.class.getMethod("getLegendItem");
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
        return new InvokeStep(method);
    }

    public XYGraphEx getParent() {
        return (XYGraphEx) super.getParent();
    }

    public LegendEx getLegend() {
        return legend;
    }

    public void setLegend(LegendEx legend) {
        this.legend = legend;
    }

    public Point2D getLocation() {
        return new Point2D.Double(locX, locY);
    }

    public void setLocation(double locX, double locY) {
        this.locX = locX;
        this.locY = locY;
    }

    public Rectangle2D getBounds() {
        Dimension2D size = getSize();
        return new Rectangle2D.Double(locX, locY, size.getWidth(), size.getHeight());
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (getLegend() != null) {
            getLegend().itemVisibilityChanged(this);
        }
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        LegendItemImpl item = (LegendItemImpl) src;
        visible = item.visible;
        locX = item.locX;
        locY = item.locY;
    }

}
