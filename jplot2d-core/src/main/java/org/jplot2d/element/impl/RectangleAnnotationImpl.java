/**
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

import org.jplot2d.util.DoubleDimension2D;
import org.jplot2d.util.Range;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Jingjing Li
 */
public class RectangleAnnotationImpl extends AnnotationImpl implements RectangleAnnotationEx {

    private static final Paint DEFAULT_PAINT = new Color(192, 192, 192, 128);

    private Range xrange, yrange;

    private Paint paint = DEFAULT_PAINT;

    public String getId() {
        if (getParent() != null) {
            return "RectangleAnnotation" + getParent().indexOf(this);
        } else {
            return "RectangleAnnotation@" + Integer.toHexString(System.identityHashCode(this));
        }
    }

    public Point2D getLocation() {
        if (getParent() == null || getParent().getSize() == null || getParent().getXAxisTransform() == null
                || getParent().getYAxisTransform() == null) {
            return null;
        } else {
            double locX = getXWtoP(xrange.getStart());
            double locY = getYWtoP(yrange.getStart());
            return new Point2D.Double(locX, locY);
        }
    }

    public void setLocation(double x, double y) {
        Point2D loc = getLocation();
        if (loc != null && loc.getX() != x) {
            double endX = getXWtoP(xrange.getEnd()) - loc.getX() + x;
            double valueX = getXPtoW(x);
            double valueEnd = getXPtoW(endX);
            xrange = new Range.Double(valueX, valueEnd);
            redraw(this);
        }
        if (loc != null && loc.getY() != y) {
            double endY = getYWtoP(yrange.getEnd()) - loc.getY() + y;
            double valueY = getYPtoW(y);
            double valueEnd = getYPtoW(endY);
            yrange = new Range.Double(valueY, valueEnd);
            redraw(this);
        }
    }

    public Dimension2D getSize() {
        if (getParent() == null || getParent().getSize() == null || getParent().getXAxisTransform() == null
                || getParent().getYAxisTransform() == null) {
            return null;
        }
        double paperWidth = getXWtoP(xrange.getEnd()) - getXWtoP(xrange.getStart());
        double paperHeight = getYWtoP(yrange.getEnd()) - getYWtoP(yrange.getStart());
        return new DoubleDimension2D(Math.abs(paperWidth), Math.abs(paperHeight));
    }

    public Rectangle2D getBounds() {
        if (getParent() == null || getParent().getSize() == null || getParent().getXAxisTransform() == null
                || getParent().getYAxisTransform() == null) {
            return null;
        }
        double paperWidth = getXWtoP(xrange.getEnd()) - getXWtoP(xrange.getStart());
        double paperHeight = getYWtoP(yrange.getEnd()) - getYWtoP(yrange.getStart());
        return new Rectangle2D.Double(Math.min(paperWidth, 0), Math.min(paperHeight, 0), Math.abs(paperWidth),
                Math.abs(paperHeight));
    }

    public Rectangle2D getSelectableBounds() {
        if (getParent() == null || getParent().getSize() == null || getParent().getXAxisTransform() == null
                || getParent().getYAxisTransform() == null) {
            return null;
        }

        double paperWidth = getXWtoP(xrange.getEnd()) - getXWtoP(xrange.getStart());
        double paperHeight = getYWtoP(yrange.getEnd()) - getYWtoP(yrange.getStart());
        double rx = Math.min(paperWidth, 0);
        double ry = Math.min(paperHeight, 0);
        double rw = Math.abs(paperWidth);
        double rh = Math.abs(paperHeight);

        if (rw < 2) {
            rx = -1;
            rw = 2;
        }
        if (rh < 2) {
            ry = -1;
            rh = 2;
        }

        return new Rectangle2D.Double(rx, ry, rw, rh);
    }

    public Range getXValueRange() {
        return xrange;
    }

    public void setXValueRange(Range value) {
        this.xrange = value;
        if (getParent() != null && getParent().getXAxisTransform() != null) {
            redraw(this);
        }
    }

    public Range getYValueRange() {
        return yrange;
    }

    public void setYValueRange(Range value) {
        this.yrange = value;
        if (getParent() != null && getParent().getYAxisTransform() != null) {
            redraw(this);
        }
    }

    public Paint getFillPaint() {
        return paint;
    }

    public void setFillPaint(Paint paint) {
        this.paint = paint;
    }

    public void draw(Graphics2D g) {
        Point2D loc = getLocation();
        if (loc == null) {
            return;
        }

        AffineTransform oldTransform = g.getTransform();
        Shape oldClip = g.getClip();

        g.transform(getParent().getPaperTransform().getTransform());
        g.setClip(getParent().getBounds());
        g.setPaint(paint);

        double paperWidth = getXWtoP(xrange.getEnd()) - getXWtoP(xrange.getStart());
        double paperHeight = getYWtoP(yrange.getEnd()) - getYWtoP(yrange.getStart());
        double rx = Math.min(paperWidth, 0);
        double ry = Math.min(paperHeight, 0);
        double rw = Math.abs(paperWidth);
        double rh = Math.abs(paperHeight);

        if (paperWidth == 0 || paperHeight == 0) {
            Line2D line = new Line2D.Double(loc.getX() + rx, loc.getY() + ry, loc.getX() + rw, loc.getY() + rh);
            Stroke oldStroke = g.getStroke();
            g.setStroke(ZERO_WIDTH_STROKE);
            g.draw(line);
            g.setStroke(oldStroke);
        } else {
            Rectangle2D strip = new Rectangle2D.Double(loc.getX() + rx, loc.getY() + ry, rw, rh);
            g.fill(strip);
        }

        g.setTransform(oldTransform);
        g.setClip(oldClip);
    }

    @Override
    public void copyFrom(ElementEx src) {
        super.copyFrom(src);

        RectangleAnnotationImpl lm = (RectangleAnnotationImpl) src;
        this.xrange = lm.xrange;
        this.yrange = lm.yrange;
        this.paint = lm.paint;
    }

}
