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
package org.jplot2d.tex;

import org.jplot2d.element.HAlign;
import org.jplot2d.element.VAlign;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Composed of MathLabelComp, vertically group them. The base point of MathLabelComp is string base point,
 * and the location is relative to this MathLabelXLines.
 * Even if there is only one MathLabelComp, XLines still need to locate base point for the MathLabelComp.
 */
public class MathLabelXLines {

    /**
     * sub components list
     */
    private final List<MathLabelComp> subEL = new ArrayList<>();

    private double width, height;
    private double leftx, topy;
    private HAlign halign;
    private VAlign valign;

    /**
     * @param me     can be <code>null</code>
     * @param font   the font
     * @param halign the horizontal align
     * @param valign the vertical align
     */
    protected MathLabelXLines(@Nullable MathElement me, Font font, HAlign halign, VAlign valign) {
        this.halign = halign;
        this.valign = valign;

        if (me == null || me == MathElement.EMPTY) {
            width = 0;
            height = 0;
            return;
        }

        if (me instanceof MathElement.XLines) {
            MathElement[] es = ((MathElement.XLines) me).getElements();
            width = 0;
            double locy = 0;
            for (int i = 0; i < es.length; i++) {
                MathLabelComp ml = MathLabelComp.getBoxInstance(es[i], 0, 0, font, false);
                if (i > 0) {
                    locy += ml.getLeading();
                }
                locy += ml.getAscent();
                ml.pan(0, locy);
                subEL.add(ml);
                locy += ml.getDescent();

                if (width < ml.getWidth()) {
                    width = ml.getWidth();
                }
            }
            height = locy;
        } else {
            MathLabelComp mlc = MathLabelComp.getBoxInstance(me, 0, 0, font, false);
            mlc.pan(0, mlc.getAscent());
            subEL.add(mlc);
            width = mlc.getWidth();
            height = mlc.getAscent() + mlc.getDescent();
        }

        relocate();
    }

    /**
     * Re-layout the math label with the given font size.
     *
     * @param fontSize the font size
     */
    @SuppressWarnings("unused")
    protected void relayout(float fontSize) {
        width = 0;

        double locy = 0;
        for (int i = 0; i < subEL.size(); i++) {
            MathLabelComp ml = subEL.get(i);
            ml.relayout(fontSize, 0, 0);
            if (i > 0) {
                locy += ml.getLeading();
            }
            locy += ml.getAscent();
            ml.pan(0, locy);
            locy += ml.getDescent();

            if (width < ml.getWidth()) {
                width = ml.getWidth();
            }
        }
        height = locy;

        topy = 0;
        relocate();
    }

    protected void draw(Graphics2D g2) {
        for (MathLabelComp ml : subEL) {
            ml.draw(g2);
        }
    }

    /**
     * Returns the bounds of this XLines. The x,y of the bounds is relative to its base point.
     *
     * @return a Rectangle2D that is the bounds of this XLines
     */
    protected Rectangle2D getBounds() {
        return new Rectangle2D.Double(leftx, topy, width, height);
    }

    protected void setHAlign(HAlign halign) {
        this.halign = halign;
        relocate();
    }

    protected void setVAlign(VAlign valign) {
        this.valign = valign;
        relocate();
    }

    private void relocate() {

        double xoff = 0, yoff = 0;
        for (MathLabelComp ml : subEL) {
            switch (halign) {
                case LEFT:
                    xoff = -ml.x;
                    break;
                case CENTER:
                    xoff = -ml.getWidth() / 2 - ml.x;
                    break;
                case RIGHT:
                    xoff = -ml.getWidth() - ml.x;
                    break;
            }
            ml.pan(xoff, 0);
        }
        switch (halign) {
            case LEFT:
                leftx = 0;
                break;
            case CENTER:
                leftx = -width / 2;
                break;
            case RIGHT:
                leftx = -width;
                break;
        }

        switch (valign) {
            case TOP:
                yoff = -topy;
                topy = 0;
                break;
            case MIDDLE:
                yoff = -height / 2 - topy;
                topy = -height / 2;
                break;
            case BOTTOM:
                yoff = -height - topy;
                topy = -height;
                break;
        }
        for (MathLabelComp ml : subEL) {
            ml.pan(0, yoff);
        }
    }

}