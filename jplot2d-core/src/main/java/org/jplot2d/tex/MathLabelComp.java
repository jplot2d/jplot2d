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

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represent a component of {@link MathLabel}. It may be a simple string or composite of other components.
 *
 * @author Jingjing Li
 */
public abstract class MathLabelComp {

    private static final float SUBSUPERSCRIPT_SIZE = 0.583f;

    private static final double SUBSCRIPT_POS = 0.333;

    private static final double SUPERSCRIPT_POS = 0.333;

    protected final MathElement me;
    protected Font font;

    protected double width;
    /**
     * The ascent and descent of the base font.
     */
    protected float ascent, descent, leading;
    /**
     * The ascent and descent of this component box.
     */
    protected double boxAscent, boxDescent;
    /**
     * base point of the 1st string location
     */
    protected double x, y;

    /**
     * Create underlayer components, width and height are calculated. Subclass should create its sub-component recursively.
     *
     * @param me   the MathElement object. can be null if the parsed text is empty string.
     * @param font the font
     * @param x    the x of relative location
     * @param y    the y of relative location
     */
    protected MathLabelComp(MathElement me, Font font, double x, double y) {
        this.me = me;
        this.font = font;
        this.x = x;
        this.y = y;
        /* ensure _height is a proper value */
        computeFontHeight();
    }

    /**
     * Static factory method. Create a proper subclass according to the given MathElement argument.
     *
     * @param me     the math element
     * @param x      the x of relative location
     * @param y      the y of relative location
     * @param font   the font
     * @param wstyle with math style
     */
    protected static MathLabelComp getBoxInstance(MathElement me, double x, double y, Font font, boolean wstyle) {
        if (me instanceof MathElement.PToken) {
            return new Ptoken(((MathElement.PToken) me), x, y, font, wstyle);
        } else if (me instanceof MathElement.Msub) {
            return new Msub(((MathElement.Msub) me), x, y, font, wstyle);
        } else if (me instanceof MathElement.Msup) {
            return new Msup(((MathElement.Msup) me), x, y, font, wstyle);
        } else if (me instanceof MathElement.Msubsup) {
            return new Msubsup(((MathElement.Msubsup) me), x, y, font, wstyle);
        } else if (me instanceof MathElement.PContainer) {
            if (me instanceof MathElement.Mstyle) {
                wstyle = true;
                font = deriveMathVariantFont(font, ((MathElement.Mstyle) me).getMathVariant());
            }
            return new PContainer(((MathElement.PContainer) me), x, y, font, wstyle);
        } else {
            return null;
        }
    }

    private static Font deriveMathVariantFont(Font font, MathElement.MathVariant mathVariant) {
        switch (mathVariant) {
            case NORMAL:
                return font.deriveFont(Font.PLAIN);
            case ITALIC:
                return font.deriveFont(font.getStyle() | Font.ITALIC);
            case BOLD:
                return font.deriveFont(font.getStyle() | Font.BOLD);
        }
        return font;
    }

    /**
     * Compute the font height
     */
    private void computeFontHeight() {
        FontRenderContext frc = new FontRenderContext(null, false, true);
        LineMetrics lm = font.getLineMetrics("Can be any string", frc);
        ascent = lm.getAscent();
        descent = lm.getDescent();
        leading = lm.getLeading();
        boxAscent = ascent;
        boxDescent = descent;
    }

    /**
     * re-layout sub elements, after font changed. The height and width are re-calculated.
     */
    protected void relayout(float fontSize, double x, double y) {
        this.font = font.deriveFont(fontSize);
        this.x = x;
        this.y = y;
        computeFontHeight();
    }

    /**
     * Pan the location of this component an offset.
     *
     * @param xoff the x offset
     * @param yoff the y offset
     */
    protected void pan(double xoff, double yoff) {
        if (xoff != 0 || yoff != 0) {
            x += xoff;
            y += yoff;
        }
    }

    protected abstract void draw(Graphics2D g2);

    /**
     * Returns the width of this MLC
     *
     * @return the width of this MLC
     */
    protected double getWidth() {
        return width;
    }

    /**
     * The returned ascent, not include leading.
     *
     * @return the ascent of this MLC
     */
    protected double getAscent() {
        return boxAscent;
    }

    protected double getDescent() {
        return boxDescent;
    }

    protected double getLeading() {
        return leading;
    }

    private static class Ptoken extends MathLabelComp {

        /**
         * The string of simple label
         */
        private final String str;

        private Ptoken(MathElement.PToken me, double x, double y, Font font, boolean wstyle) {
            super(me, font, x, y);
            str = me.getToken();

            // set default font style for Mi
            if (!wstyle && this.me instanceof MathElement.Mi) {
                this.font = this.font.deriveFont(Font.ITALIC);
            }

            double lw = getStrLogicalWidth();
            if (this.font.getStyle() == Font.ITALIC) {
                /* italic correction */
                lw += (ascent + descent) * this.font.getItalicAngle();
            }
            width = lw;
        }

        @Override
        protected void relayout(float fontSize, double x, double y) {
            super.relayout(fontSize, x, y);

            double lw = getStrLogicalWidth();
            if (font.getStyle() == Font.ITALIC) {
                /* italic correction */
                lw += (ascent + descent) * font.getItalicAngle();
            }
            width = lw;
        }

        @Override
        protected void draw(Graphics2D g2) {
            g2.setFont(font);
            g2.drawString(str, (float) (x), (float) (y));
        }

        /**
         * Returns the logical width of str.
         *
         * @return the logical width of str
         */
        private double getStrLogicalWidth() {
            FontRenderContext frc = new FontRenderContext(null, false, true);
            return font.getStringBounds(str, frc).getWidth();
        }

    }

    private static class Msub extends MathLabelComp {
        final MathLabelComp base;
        final MathLabelComp sub;

        private Msub(MathElement.Msub me, double x, double y, Font font,
                     boolean wstyle) {
            super(me, font, x, y);
            base = MathLabelComp.getBoxInstance(me.base, this.x, this.y, this.font, wstyle);

            Font subfont = this.font.deriveFont(this.font.getSize2D()
                    * SUBSUPERSCRIPT_SIZE);
            double sublocx = this.x + base.width;
            double sublocy = this.y + SUBSCRIPT_POS * (ascent + descent) - (1 - SUBSUPERSCRIPT_SIZE) * (ascent - descent) / 2;
            sub = MathLabelComp.getBoxInstance(((MathElement.Msub) this.me).subscript, sublocx, sublocy, subfont, wstyle);
            width = base.width + sub.width;
            boxDescent = sublocy - y + sub.getDescent();
        }

        @Override
        protected void relayout(float fontSize, double x, double y) {
            super.relayout(fontSize, x, y);
            base.relayout(fontSize, x, y);

            float subfont = font.getSize2D() * SUBSUPERSCRIPT_SIZE;
            double sublocx = this.x + base.width;
            double sublocy = this.y + SUBSCRIPT_POS * (ascent + descent) - (1 - SUBSUPERSCRIPT_SIZE) * (ascent - descent) / 2;
            sub.relayout(subfont, sublocx, sublocy);
            width = base.width + sub.width;
            boxDescent = sublocy - y + sub.getDescent();
        }

        @Override
        protected void pan(double xoff, double yoff) {
            super.pan(xoff, yoff);
            base.pan(xoff, yoff);
            sub.pan(xoff, yoff);
        }

        protected void draw(Graphics2D g2) {
            base.draw(g2);
            sub.draw(g2);
        }

    }

    private static class Msup extends MathLabelComp {
        final MathLabelComp base;
        final MathLabelComp sup;

        private Msup(MathElement.Msup me, double x, double y, Font font, boolean wstyle) {
            super(me, font, x, y);
            base = MathLabelComp.getBoxInstance(me.base, this.x, this.y, this.font, wstyle);

            Font supfont = this.font.deriveFont(this.font.getSize2D() * SUBSUPERSCRIPT_SIZE);
            double suplocx = this.x + base.width;
            double suplocy = this.y - SUPERSCRIPT_POS * (ascent + descent) - (1 - SUBSUPERSCRIPT_SIZE) * (ascent - descent) / 2;
            sup = MathLabelComp.getBoxInstance(((MathElement.Msup) this.me).superscript, suplocx, suplocy, supfont, wstyle);
            width = base.width + sup.width;
            boxAscent = y - suplocy + sup.getAscent();
        }

        @Override
        protected void relayout(float fontSize, double x, double y) {
            super.relayout(fontSize, x, y);
            base.relayout(fontSize, x, y);

            float supfont = font.getSize2D() * SUBSUPERSCRIPT_SIZE;
            double suplocx = this.x + base.width;
            double suplocy = this.y - SUPERSCRIPT_POS * (ascent + descent) - (1 - SUBSUPERSCRIPT_SIZE) * (ascent - descent) / 2;
            sup.relayout(supfont, suplocx, suplocy);
            width = base.width + sup.width;
            boxAscent = y - suplocy + sup.getAscent();
        }

        @Override
        protected void pan(double xoff, double yoff) {
            super.pan(xoff, yoff);
            base.pan(xoff, yoff);
            sup.pan(xoff, yoff);
        }

        protected void draw(Graphics2D g2) {
            base.draw(g2);
            sup.draw(g2);
        }

    }

    private static class Msubsup extends MathLabelComp {
        final MathLabelComp base;
        final MathLabelComp sub;
        final MathLabelComp sup;

        private Msubsup(MathElement.Msubsup me, double x, double y, Font font,
                        boolean wstyle) {
            super(me, font, x, y);
            base = MathLabelComp.getBoxInstance(me.base, this.x, this.y, this.font, wstyle);

            Font subsupfont = this.font.deriveFont(this.font.getSize2D() * SUBSUPERSCRIPT_SIZE);
            double subsuplocx = this.x + base.width;
            double sublocy = this.y + SUBSCRIPT_POS * (ascent + descent) - (1 - SUBSUPERSCRIPT_SIZE) * (ascent - descent) / 2;
            double suplocy = this.y - SUPERSCRIPT_POS * (ascent + descent) - (1 - SUBSUPERSCRIPT_SIZE) * (ascent - descent) / 2;
            sub = MathLabelComp.getBoxInstance(
                    ((MathElement.Msubsup) this.me).subscript, subsuplocx, sublocy, subsupfont, wstyle);
            sup = MathLabelComp.getBoxInstance(
                    ((MathElement.Msubsup) this.me).superscript, subsuplocx, suplocy, subsupfont, wstyle);
            double ssw = (sub.width > sup.width) ? sub.width : sup.width;
            width = base.width + ssw;
            boxAscent = y - suplocy + sup.getAscent();
            boxDescent = sublocy - y + sub.getDescent();
        }

        @Override
        protected void relayout(float fontSize, double x, double y) {
            super.relayout(fontSize, x, y);
            base.relayout(fontSize, x, y);

            float subsupfont = font.getSize2D() * SUBSUPERSCRIPT_SIZE;
            double subsuplocx = this.x + base.width;
            double sublocy = this.y + SUBSCRIPT_POS * (ascent + descent) - (1 - SUBSUPERSCRIPT_SIZE) * (ascent - descent) / 2;
            double suplocy = this.y - SUPERSCRIPT_POS * (ascent + descent) - (1 - SUBSUPERSCRIPT_SIZE) * (ascent - descent) / 2;
            sub.relayout(subsupfont, subsuplocx, sublocy);
            sup.relayout(subsupfont, subsuplocx, suplocy);
            double ssw = (sub.width > sup.width) ? sub.width : sup.width;
            width = base.width + ssw;
            boxAscent = y - suplocy + sup.getAscent();
            boxDescent = sublocy - y + sub.getDescent();
        }

        @Override
        protected void pan(double xoff, double yoff) {
            super.pan(xoff, yoff);
            base.pan(xoff, yoff);
            sub.pan(xoff, yoff);
            sup.pan(xoff, yoff);
        }

        protected void draw(Graphics2D g2) {
            base.draw(g2);
            sub.draw(g2);
            sup.draw(g2);
        }

    }

    /**
     * Presentation Expression, composed of Presentations. The most common PresExpression is Mrow, Horizontally Group Sub-Expressions.
     */
    private static class PContainer extends MathLabelComp {

        /**
         * sub components list
         */
        private final List<MathLabelComp> subEL = new ArrayList<>();

        private PContainer(MathElement.PContainer me, double x, double y,
                           Font font, boolean wstyle) {
            super(me, font, x, y);

            double nextLocx = this.x;
            for (MathElement.PElement pe : me.getElementList()) {
                MathLabelComp ml = MathLabelComp.getBoxInstance(pe, nextLocx, this.y, this.font, wstyle);
                subEL.add(ml);
                nextLocx += ml.width;
                if (boxAscent < ml.boxAscent) {
                    boxAscent = ml.boxAscent;
                }
                if (boxDescent < ml.boxDescent) {
                    boxDescent = ml.boxDescent;
                }
            }
            width = nextLocx - this.x;
        }

        @Override
        protected void relayout(float fontSize, double x, double y) {
            super.relayout(fontSize, x, y);

            if (subEL.size() == 0) {
                width = 0;
            } else {
                double w = 0;
                for (MathLabelComp ml : subEL) {
                    ml.relayout(fontSize, x + w, y);
                    w += ml.width;
                    if (boxAscent < ml.boxAscent) {
                        boxAscent = ml.boxAscent;
                    }
                    if (boxDescent < ml.boxDescent) {
                        boxDescent = ml.boxDescent;
                    }
                }
                width = w;
            }
        }

        @Override
        protected void pan(double xoff, double yoff) {
            super.pan(xoff, yoff);
            if (xoff != 0 || yoff != 0) {
                for (MathLabelComp ml : subEL) {
                    ml.pan(xoff, yoff);
                }
            }
        }

        protected void draw(Graphics2D g2) {
            for (MathLabelComp ml : subEL) {
                ml.draw(g2);
            }
        }

    }

}
