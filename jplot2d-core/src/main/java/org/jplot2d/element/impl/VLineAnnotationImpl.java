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

import org.jplot2d.transform.PaperTransform;
import org.jplot2d.util.DoubleDimension2D;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Jingjing Li
 */
public class VLineAnnotationImpl extends AnnotationImpl implements VLineAnnotationEx {

    private double valueX;

    @Nonnull
    private BasicStroke stroke = DEFAULT_STROKE;

    public String getId() {
        if (getParent() != null) {
            return "VLineAnnotation" + getParent().indexOf(this);
        } else {
            return "VLineAnnotation@" + Integer.toHexString(System.identityHashCode(this));
        }
    }

    public Point2D getLocation() {
        if (getParent() == null || getParent().getSize() == null || getParent().getXAxisTransform() == null) {
            return null;
        } else {
            double locX = getXWtoP(valueX);
            return new Point2D.Double(locX, 0);
        }
    }

    public void setLocation(double x, double y) {
        Point2D loc = getLocation();
        if (loc != null && loc.getX() != x) {
            valueX = getXPtoW(x);
            redraw(this);
        }
    }

    public Dimension2D getSize() {
        if (getParent() == null || getParent().getSize() == null) {
            return null;
        }
        return new DoubleDimension2D(getParent().getSize().getHeight(), 0);
    }

    public Rectangle2D getSelectableBounds() {
        if (getParent() == null || getParent().getSize() == null) {
            return null;
        }

        double lineWidth = stroke.getLineWidth();
        if (lineWidth < 2) {
            lineWidth = 2;
        }
        return new Rectangle2D.Double(0, -lineWidth / 2, getParent().getSize().getHeight(), lineWidth);
    }

    public PaperTransform getPaperTransform() {
        PaperTransform pxf = super.getPaperTransform();
        if (pxf == null) {
            return null;
        } else {
            return pxf.rotate(Math.PI / 2);
        }
    }

    public double getValue() {
        return valueX;
    }

    public void setValue(double value) {
        this.valueX = value;
        redraw(this);
    }

    @Nonnull
    public BasicStroke getStroke() {
        return stroke;
    }

    public void setStroke(@Nullable BasicStroke stroke) {
        if (stroke == null) {
            this.stroke = DEFAULT_STROKE;
        } else {
            this.stroke = stroke;
        }
        redraw(this);
    }

    public void draw(Graphics2D g) {
        Point2D loc = getLocation();
        if (loc == null) {
            return;
        }

        Stroke oldStroke = g.getStroke();
        AffineTransform oldTransform = g.getTransform();
        Shape oldClip = g.getClip();

        g.transform(getParent().getPaperTransform().getTransform());
        g.setClip(getParent().getBounds());
        g.setColor(getEffectiveColor());
        g.setStroke(stroke);

        Line2D line = new Line2D.Double(loc.getX(), 0, loc.getX(), getParent().getSize().getHeight());
        g.draw(line);

        g.setTransform(oldTransform);
        g.setClip(oldClip);
        g.setStroke(oldStroke);
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        VLineAnnotationImpl lm = (VLineAnnotationImpl) src;
        this.valueX = lm.valueX;
        this.stroke = lm.stroke;
    }

}
