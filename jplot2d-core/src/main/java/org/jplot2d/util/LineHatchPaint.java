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
package org.jplot2d.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static org.jplot2d.util.NumberUtils.approximate;

/**
 * The LineHatchPaint class provides a way to fill a Shape with line hatch pattern. Because Java
 * require instances of classes implementing Paint must be read-only, this class is more like a
 * factory to create paint instance for renderer threads.
 *
 * @author Jingjing Li
 */
public class LineHatchPaint implements Paint {

    private static final double EPSILON = Math.pow(10, -14);

    /**
     * ascend X
     */
    public static final class PointComparatorX implements Comparator<Point2D> {

        private static final Comparator<Point2D> instance = new PointComparatorX();

        public static Comparator<Point2D> getInstance() {
            return instance;
        }

        public int compare(Point2D p1, Point2D p2) {
            double d = p1.getX() - p2.getX();
            if (d > 0) {
                return 1;
            } else if (d < 0) {
                return -1;
            } else {
                return 0;
            }
        }

    }

    /**
     * descend X
     */
    public static final class PointComparatorNX implements Comparator<Point2D> {

        private static final Comparator<Point2D> instance = new PointComparatorNX();

        public static Comparator<Point2D> getInstance() {
            return instance;
        }

        public int compare(Point2D p1, Point2D p2) {
            double d = p1.getX() - p2.getX();
            if (d > 0) {
                return -1;
            } else if (d < 0) {
                return 1;
            } else {
                return 0;
            }
        }

    }

    /**
     * ascend Y
     */
    public static final class PointComparatorY implements Comparator<Point2D> {

        private static final Comparator<Point2D> instance = new PointComparatorY();

        public static Comparator<Point2D> getInstance() {
            return instance;
        }

        public int compare(Point2D p1, Point2D p2) {
            double d = p1.getY() - p2.getY();
            if (d > 0) {
                return 1;
            } else if (d < 0) {
                return -1;
            } else {
                return 0;
            }
        }

    }

    /**
     * descend Y
     */
    public static final class PointComparatorNY implements Comparator<Point2D> {

        private static final Comparator<Point2D> instance = new PointComparatorNY();

        public static Comparator<Point2D> getInstance() {
            return instance;
        }

        public int compare(Point2D p1, Point2D p2) {
            double d = p1.getY() - p2.getY();
            if (d > 0) {
                return -1;
            } else if (d < 0) {
                return 1;
            } else {
                return 0;
            }
        }

    }

    private static final Color DEFAULT_COLOR = Color.BLACK;

    private final Color color;

    private final BasicStroke stroke;

    private final double angle;

    private final double spacing;

    private final Comparator<Point2D> pointComparator;

    /**
     * Construct a LineHatchPaint with the given angle. The default values:
     * <ul>
     * <li>color: BLACK</li>
     * <li>line width: 0.0</li>
     * <li>line style: solid</li>
     * <li>line spacing: 6pt</li>
     * </ul>
     *
     * @param angle the counterclockwise angle in degrees from horizontal
     */
    public LineHatchPaint(double angle) {
        this(0, angle, 6);
    }

    /**
     * Construct a LineHatchPaint with the given angle. The default values:
     * <ul>
     * <li>color: BLACK</li>
     * <li>line style: solid</li>
     * </ul>
     *
     * @param width   the line width
     * @param angle   the counterclockwise angle in degrees from horizontal
     * @param spacing the spacing, in point(1/72 inch), between the parallel lines
     */
    public LineHatchPaint(float width, double angle, double spacing) {
        this(DEFAULT_COLOR, new BasicStroke(width), angle, spacing);
    }

    /**
     * Construct a LineHatchPaint with given arguments.
     *
     * @param color   the line color
     * @param stroke  the line stroke
     * @param angle   the counterclockwise angle in degrees from horizontal
     * @param spacing the spacing, in point(1/72 inch), between the parallel lines
     */
    public LineHatchPaint(Color color, BasicStroke stroke, double angle, double spacing) {
        this.color = color;
        this.stroke = stroke;
        this.angle = angle;
        this.spacing = spacing;

        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        if (90 - 45 < angle && angle < 90 + 45) {
            pointComparator = PointComparatorNY.getInstance();
        } else if (270 - 45 < angle && angle < 270 + 45) {
            pointComparator = PointComparatorY.getInstance();
        } else if (180 - 45 <= angle && angle <= 180 + 45) {
            pointComparator = PointComparatorNX.getInstance();
        } else {
            pointComparator = PointComparatorX.getInstance();
        }
    }

    public Color getColor() {
        return color;
    }

    public BasicStroke getStroke() {
        return stroke;
    }

    public double getAngle() {
        return angle;
    }

    public double getSpacing() {
        return spacing;
    }

    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds,
                                      Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        throw new UnsupportedOperationException();
    }

    public int getTransparency() {
        throw new UnsupportedOperationException();
    }

    /**
     * Calculate hatch shapes to fill the given shape.
     *
     * @param shape the shape to hatch
     * @param clip  the clip
     * @param scale the scale
     * @return hatch shapes
     */
    public Shape[] calcHatchShapes(Shape shape, Rectangle2D clip, double scale) {

        Line2D[] clippedLines = calcHatchLinesClipped(this, clip, scale);

        ArrayList<Line2D> result = new ArrayList<>();
        for (Line2D line : clippedLines) {
            Line2D[] ilines = calcLineSegInside(line, shape);
            result.addAll(Arrays.asList(ilines));
        }

        return result.toArray(new Shape[result.size()]);
    }

    public static Line2D[] calcHatchLinesClipped(LineHatchPaint lhp, Rectangle2D clip, double scale) {

        // the angle to positive x axis or negative x axis
        double anglex = lhp.getAngle() % 180.0;
        if (anglex > 90) {
            anglex = 180 - anglex;
        } else if (anglex < -90) {
            anglex += 180;
        } else if (anglex < -0) {
            anglex = -anglex;
        }
        anglex *= Math.PI / 180;

        double diagonalLength = Math.hypot(clip.getHeight(), clip.getWidth());
        double diagonalAngle = Math.atan(clip.getHeight() / clip.getWidth());

        double halfLength = diagonalLength / 2 * Math.cos(anglex - diagonalAngle);
        double halfRange = diagonalLength / 2 * Math.sin(anglex + diagonalAngle);
        double spacing = lhp.getSpacing() * scale;
        int halfn = (int) (halfRange / spacing);

        Line2D[] result = new Line2D[2 * halfn + 1];

        // angle is positive x toward positive y (downward in graphic coordinate system)
        double angle = -lhp.getAngle() * Math.PI / 180;

        double a0x = clip.getCenterX() - halfLength * Math.cos(angle);
        double a0y = clip.getCenterY() - halfLength * Math.sin(angle);
        double b0x = clip.getCenterX() + halfLength * Math.cos(angle);
        double b0y = clip.getCenterY() + halfLength * Math.sin(angle);
        for (int i = 0; i < result.length; i++) {
            double aix = a0x - (i - halfn) * spacing * Math.sin(angle);
            double aiy = a0y + (i - halfn) * spacing * Math.cos(angle);
            double bix = b0x - (i - halfn) * spacing * Math.sin(angle);
            double biy = b0y + (i - halfn) * spacing * Math.cos(angle);
            result[i] = new Line2D.Double(aix, aiy, bix, biy);
        }

        return result;
    }

    private Line2D[] calcLineSegInside(Line2D line, Shape shape) {

        // all intersect points
        HashSet<Point2D> inersects = new HashSet<>();
        inersects.add(line.getP1());
        inersects.add(line.getP2());

        double segCoords[] = new double[6];
        double inersectCoords[] = new double[2];
        double lastMoveX = 0.0, prex = 0.0;
        double lastMoveY = 0.0, prey = 0.0;

        for (PathIterator pi = shape.getPathIterator(null); !pi.isDone(); pi.next()) {
            int segType = pi.currentSegment(segCoords);

            switch (segType) {
                case PathIterator.SEG_MOVETO:
                    lastMoveX = prex = segCoords[0];
                    lastMoveY = prey = segCoords[1];
                    break;
                case PathIterator.SEG_LINETO:
                    if ((segCoords[0] != lastMoveX) || (segCoords[1] != lastMoveY)) {
                        boolean its = intersectLines(line, prex, prey, segCoords[0], segCoords[1],
                                inersectCoords);
                        if (its) {
                            inersects.add(new Point2D.Double(inersectCoords[0], inersectCoords[1]));
                        }
                        prex = segCoords[0];
                        prey = segCoords[1];
                    }
                    break;
                case PathIterator.SEG_QUADTO:
                    boolean itqs = intersectLines(line, prex, prey, segCoords[2], segCoords[3],
                            inersectCoords);
                    if (itqs) {
                        inersects.add(new Point2D.Double(inersectCoords[0], inersectCoords[1]));
                    }
                    prex = segCoords[2];
                    prey = segCoords[3];
                    break;
                case PathIterator.SEG_CUBICTO:
                    boolean itcs = intersectLines(line, prex, prey, segCoords[4], segCoords[5],
                            inersectCoords);
                    if (itcs) {
                        inersects.add(new Point2D.Double(inersectCoords[0], inersectCoords[1]));
                    }
                    prex = segCoords[4];
                    prey = segCoords[5];
                    break;
                case PathIterator.SEG_CLOSE:
                    if ((prex != lastMoveX) || (prey != lastMoveY)) {
                        boolean its = intersectLines(line, prex, prey, lastMoveX, lastMoveY,
                                inersectCoords);
                        if (its) {
                            inersects.add(new Point2D.Double(inersectCoords[0], inersectCoords[1]));
                        }
                    }
                    break;
            }

        }

        Point2D[] ips = inersects.toArray(new Point2D[inersects.size()]);
        Arrays.sort(ips, pointComparator);

        List<Line2D> result = new ArrayList<>();
        for (int i = 0, j = 1; j < ips.length; i++, j++) {
            double x = (ips[i].getX() + ips[j].getX()) / 2;
            double y = (ips[i].getY() + ips[j].getY()) / 2;
            if (shape.contains(x, y)) {
                result.add(new Line2D.Double(ips[i], ips[j]));
            }
        }

        return result.toArray(new Line2D[result.size()]);
    }

    /**
     * The method checks up if line and line (x3, y3) - (x4, y4) intersect. If lines intersect then
     * the result parameters are saved to point array. The size of array point must be at least 2.
     * Note the algorithm is not symmetric. The given line is the primary line.
     *
     * @param line  the primary line
     * @param x3    x of the 3rd point
     * @param y3    y of the 3rd point
     * @param x4    x of the 4th point
     * @param y4    y of the 4th point
     * @param point receive the intersection point
     * @return true if two lines intersect
     */
    public static boolean intersectLines(Line2D line, double x3, double y3, double x4, double y4, double[] point) {

        double dxa = line.getX1() - line.getX2();
        double dya = line.getY1() - line.getY2();
        double dxb = x3 - x4;
        double dyb = y3 - y4;
        double ca = line.getX1() * line.getY2() - line.getY1() * line.getX2();
        double cb = x3 * y4 - y3 * x4;
        double coefParallel = dxa * dyb - dya * dxb;

        // point on primary line
        if (approximate(x3, x4, 4) && approximate(y3, y4, 4)) {
            double x = (x3 + x4) / 2;
            double y = (y3 + y4) / 2;
            if (approximate(dxa * y - dya * x, ca, 4)) {
                point[0] = x;
                point[1] = y;
                return true;
            }
        }

        // parallel
        if (Math.abs(coefParallel) < EPSILON) {
            return false;
        }

        // vertical
        if (approximate(line.getX1(), line.getX2(), 4)) {
            point[0] = (line.getX1() + line.getX2()) / 2;
            point[1] = (point[0] * dyb + cb) / dxb;
            return approxBetween(line.getY1(), line.getY2(), point[1])
                    && approxBetween(x3, x4, point[0]);
        }

        // horizontal
        if (approximate(line.getY1(), line.getY2(), 4)) {
            point[1] = (line.getY1() + line.getY2()) / 2;
            point[0] = (point[1] * dxb - cb) / dyb;
            return approxBetween(line.getX1(), line.getX2(), point[0])
                    && approxBetween(y3, y4, point[1]);
        }

        point[0] = (ca * dxb - dxa * cb) / coefParallel;
        point[1] = (ca * dyb - dya * cb) / coefParallel;

        boolean inlineA, inlineB;
        if (Math.abs(dxa) > Math.abs(dya)) {
            inlineA = approxBetween(line.getX1(), line.getX2(), point[0]);
        } else {
            inlineA = approxBetween(line.getY1(), line.getY2(), point[1]);
        }
        if (Math.abs(dxb) > Math.abs(dyb)) {
            inlineB = approxBetween(x3, x4, point[0]);
        } else {
            inlineB = approxBetween(y3, y4, point[1]);
        }
        return inlineA && inlineB;
    }

    public static boolean approxBetween(double a, double b, double v) {
        if (a > b) {
            if (a >= v && v >= b) {
                return true;
            }
        } else {
            if (a <= v && v <= b) {
                return true;
            }
        }
        return approximate(a, v, 4) || approximate(b, v, 4);
    }
}
