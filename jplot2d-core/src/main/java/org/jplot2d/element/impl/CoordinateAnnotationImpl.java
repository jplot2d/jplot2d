/*
 * Copyright 2010-2014 Jingjing Li.
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

import org.jplot2d.element.AxisTransform;
import org.jplot2d.element.Layer;
import org.jplot2d.element.Plot;
import org.jplot2d.element.VAlign;
import org.jplot2d.tex.MathElement;
import org.jplot2d.util.NumberUtils;

import java.awt.geom.Point2D;
import java.util.Locale;

public class CoordinateAnnotationImpl extends SymbolAnnotationImpl implements CoordinateAnnotationEx {

    public CoordinateAnnotationImpl() {
        setVAlign(VAlign.TOP);
        setTextOffsetFactorX(0.8f);
        setTextOffsetFactorY(-0.8f);
    }

    public String getId() {
        if (getParent() != null) {
            return "ValuePicker" + getParent().indexOf(this);
        } else {
            return "ValuePicker@" + Integer.toHexString(System.identityHashCode(this));
        }
    }

    public MathElement getTextModel() {
        if (textModel != null) {
            return textModel;
        }

        Layer layer = getParent();
        Plot plot = (layer == null) ? null : layer.getParent();
        if (plot == null) {
            return null;
        }
        Point2D loc = getLocation();
        if (loc == null) {
            return null;
        }

        Point2D.Double p = (Point2D.Double) plot.getPaperTransform().getPtoD(loc);

        AxisTransform xat = layer.getXAxisTransform();
        double nx = plot.getPaperTransform().getXDtoP(p.x) / plot.getContentSize().getWidth();
        double nxL = plot.getPaperTransform().getXDtoP(p.x - 1) / plot.getContentSize().getWidth();
        double nxH = plot.getPaperTransform().getXDtoP(p.x + 1) / plot.getContentSize().getWidth();
        double vx = xat.getNormalTransform().convFromNR(nx);
        double deltaXL = Math.abs(xat.getNormalTransform().convFromNR(nxL) - vx);
        double deltaXH = Math.abs(xat.getNormalTransform().convFromNR(nxH) - vx);
        String xformat = NumberUtils.calcDeltaFormatStr(vx, Math.min(deltaXL, deltaXH) / 2);

        AxisTransform yat = layer.getYAxisTransform();
        double ny = plot.getPaperTransform().getYDtoP(p.y) / plot.getContentSize().getHeight();
        double nyL = plot.getPaperTransform().getYDtoP(p.y - 1) / plot.getContentSize().getHeight();
        double nyH = plot.getPaperTransform().getYDtoP(p.y + 1) / plot.getContentSize().getHeight();
        double vy = yat.getNormalTransform().convFromNR(ny);
        double deltaYL = Math.abs(yat.getNormalTransform().convFromNR(nyL) - vy);
        double deltaYH = Math.abs(yat.getNormalTransform().convFromNR(nyH) - vy);
        String yformat = NumberUtils.calcDeltaFormatStr(vy, Math.min(deltaYL, deltaYH) / 2);

        setText(String.format((Locale) null, xformat, vx) + ", " + String.format((Locale) null, yformat, vy));

        return textModel;
    }

    public void setValuePoint(double x, double y) {
        super.setValuePoint(x, y);
        setTextModel(null);
    }

}
