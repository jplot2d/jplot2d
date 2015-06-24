/**
 * Copyright 2010-2013 Jingjing Li.
 * <p/>
 * This file is part of jplot2d.
 * <p/>
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.element.impl;

import org.jplot2d.element.Plot;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Jingjing Li
 */
public class PlotMarginImpl extends ElementImpl implements PlotMarginEx, Cloneable {

    private boolean autoMarginTop = true, autoMarginLeft = true, autoMarginBottom = true, autoMarginRight = true;

    private double marginTop, marginLeft, marginBottom, marginRight;

    private double extraTop, extraLeft, extraBottom, extraRight;

    @Override
    public PlotEx getParent() {
        return (PlotEx) parent;
    }

    @Override
    public String getId() {
        if (getParent() != null) {
            return "Margin";
        } else {
            return "Margin@" + Integer.toHexString(System.identityHashCode(this));
        }
    }

    @Override
    public InvokeStep getInvokeStepFormParent() {
        if (parent == null) {
            return null;
        }

        Method method;
        try {
            method = Plot.class.getMethod("getMargin");
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
        return new InvokeStep(method);
    }

    @Override
    public boolean isAutoTop() {
        return autoMarginTop;
    }

    @Override
    public void setAutoTop(boolean auto) {
        autoMarginTop = auto;
        getParent().invalidate();
    }

    @Override
    public boolean isAutoLeft() {
        return autoMarginLeft;
    }

    @Override
    public void setAutoLeft(boolean auto) {
        autoMarginLeft = auto;
        getParent().invalidate();
    }

    @Override
    public boolean isAutoBottom() {
        return autoMarginBottom;
    }

    @Override
    public void setAutoBottom(boolean auto) {
        autoMarginBottom = auto;
        getParent().invalidate();
    }

    @Override
    public boolean isAutoRight() {
        return autoMarginRight;
    }

    @Override
    public void setAutoRight(boolean auto) {
        autoMarginRight = auto;
        getParent().invalidate();
    }

    @Override
    public double getTop() {
        return marginTop;
    }

    @Override
    public void setTop(double marginTop) {
        this.marginTop = marginTop;
        this.autoMarginTop = false;
        getParent().invalidate();
    }

    @Override
    public double getLeft() {
        return marginLeft;
    }

    @Override
    public void setLeft(double marginLeft) {
        this.marginLeft = marginLeft;
        this.autoMarginLeft = false;
        getParent().invalidate();
    }

    @Override
    public double getBottom() {
        return marginBottom;
    }

    @Override
    public void setBottom(double marginBottom) {
        this.marginBottom = marginBottom;
        this.autoMarginBottom = false;
        getParent().invalidate();
    }

    @Override
    public double getRight() {
        return marginRight;
    }

    @Override
    public void setRight(double marginRight) {
        this.marginRight = marginRight;
        this.autoMarginRight = false;
        getParent().invalidate();
    }

    @Override
    public void directTop(double marginTop) {
        this.marginTop = marginTop;
    }

    @Override
    public void directLeft(double marginLeft) {
        this.marginLeft = marginLeft;
    }

    @Override
    public void directBottom(double marginBottom) {
        this.marginBottom = marginBottom;
    }

    @Override
    public void directRight(double marginRight) {
        this.marginRight = marginRight;
    }

    @Override
    public double getExtraTop() {
        return extraTop;
    }

    @Override
    public void setExtraTop(double marginTop) {
        this.extraTop = marginTop;
        getParent().invalidate();
    }

    @Override
    public double getExtraLeft() {
        return extraLeft;
    }

    @Override
    public void setExtraLeft(double marginLeft) {
        this.extraLeft = marginLeft;
        getParent().invalidate();
    }

    @Override
    public double getExtraBottom() {
        return extraBottom;
    }

    @Override
    public void setExtraBottom(double marginBottom) {
        this.extraBottom = marginBottom;
        getParent().invalidate();
    }

    @Override
    public double getExtraRight() {
        return extraRight;
    }

    @Override
    public void setExtraRight(double marginRight) {
        this.extraRight = marginRight;
        getParent().invalidate();
    }

    @Override
    public PlotMarginImpl copyStructure(@Nonnull Map<ElementEx, ElementEx> orig2copyMap) {
        PlotMarginImpl result = new PlotMarginImpl();
        orig2copyMap.put(this, result);
        return result;
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        PlotMarginImpl s = (PlotMarginImpl) src;

        autoMarginTop = s.autoMarginTop;
        autoMarginBottom = s.autoMarginBottom;
        autoMarginLeft = s.autoMarginLeft;
        autoMarginRight = s.autoMarginRight;
        marginTop = s.marginTop;
        marginBottom = s.marginBottom;
        marginLeft = s.marginLeft;
        marginRight = s.marginRight;
        extraTop = s.extraTop;
        extraBottom = s.extraBottom;
        extraLeft = s.extraLeft;
        extraRight = s.extraRight;
    }

}
