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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * This class override all methods of its ancestor. It accept a TeX-like string as its text to render a math.
 * The underlayer component is {@link MathLabelComp}.
 * <p/>
 * Only support left to right writing direction, and subscript superscript.
 *
 * @author Jingjing Li
 */
public class MathLabel {

    private final MathElement me;

    /**
     * created even when me == null
     */
    private final MathLabelXLines mlc;

    private final Font font;

    private final HAlign halign;

    private final VAlign valign;

    /**
     * The paper bounds relative to location
     */
    private Rectangle2D bounds;

    public MathLabel(MathElement me, Font font) {
        this(me, font, VAlign.BOTTOM, HAlign.LEFT);
    }

    public MathLabel(MathElement me, Font font, VAlign valign, HAlign halign) {
        if (font == null) {
            throw new IllegalArgumentException("font cannot be null");
        }
        if (Float.isNaN(font.getSize2D())) {
            throw new IllegalArgumentException("font size2D cannot be NaN");
        }
        if (valign == null) {
            throw new IllegalArgumentException("valign cannot be null");
        }
        if (halign == null) {
            throw new IllegalArgumentException("halign cannot be null");
        }
        this.font = font;
        this.valign = valign;
        this.halign = halign;
        this.me = me;
        mlc = new MathLabelXLines(this.me, this.font, this.halign, this.valign);
        Rectangle2D dbnds = mlc.getBounds();
        bounds = new Rectangle2D.Double(dbnds.getX(), -(dbnds.getY() + dbnds.getHeight()), dbnds.getWidth(), dbnds.getHeight());
    }

    public void draw(Graphics2D g) {
        mlc.draw(g);
    }

    public Font getFont() {
        return font;
    }

    public HAlign getHAlign() {
        return halign;
    }

    public VAlign getVAlign() {
        return valign;
    }

    public MathElement getModel() {
        return me;
    }

    /**
     * Calculate the normal paper bounds of this label. The bounds is relative to its location
     * point, original point is left-bottom. Normal scale is 1.
     *
     * @return the normal paper bounds of this label.
     */
    public Rectangle2D getBounds() {
        return bounds;
    }

}
