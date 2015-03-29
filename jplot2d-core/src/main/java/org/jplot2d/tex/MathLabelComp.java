/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.tex;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represent a component of {@link MathLabel}. It may be a simple string or composite of other components.
 * The laying out operate at device (screen) coordinate system.
 *
 * @author Jingjing Li
 */
public abstract class MathLabelComp {

    private static final float SUBSUPERSCRIPT_SIZE = 0.583f;

    private static final double SUBSCRIPT_POS = 0.333;

    private static final double SUPERSCRIPT_POS = 0.333;

    private static class Ptoken extends MathLabelComp {

        /**
         * The string of simple label
         */
        private final String _str;

        private Ptoken(MathElement.PToken me, double x, double y, Font font,
                       boolean wstyle) {
            super(me, font, x, y);
            _str = me.getToken();

            // set default font style for Mi
            if (!wstyle && _me instanceof MathElement.Mi) {
                _font = _font.deriveFont(Font.ITALIC);
            }

            double lw = getStrLogicalWidth();
            if (_font.getStyle() == Font.ITALIC) {
                /* italic correction */
                lw += (_ascent + _descent) * _font.getItalicAngle();
            }
            _width = lw;
        }

        @Override
        protected void relayout(float fontSize, double x, double y) {
            super.relayout(fontSize, x, y);

            double lw = getStrLogicalWidth();
            if (_font.getStyle() == Font.ITALIC) {
                /* italic correction */
                lw += (_ascent + _descent) * _font.getItalicAngle();
            }
            _width = lw;
        }

        @Override
        protected void draw(Graphics2D g2) {
            g2.setFont(_font);
            g2.drawString(_str, (float) (_x), (float) (_y));
        }

        /**
         * Returns the logical width of _str.
         *
         * @return the logical width of _str
         */
        private double getStrLogicalWidth() {
            FontRenderContext frc = new FontRenderContext(null, false, true);
            return _font.getStringBounds(_str, frc).getWidth();
        }

    }

    private static class Msub extends MathLabelComp {
        final MathLabelComp _base;
        final MathLabelComp _sub;

        private Msub(MathElement.Msub me, double x, double y, Font font,
                     boolean wstyle) {
            super(me, font, x, y);
            _base = MathLabelComp
                    .getBoxInstance(me.base, _x, _y, _font, wstyle);

            Font subfont = _font.deriveFont(_font.getSize2D()
                    * SUBSUPERSCRIPT_SIZE);
            double sublocx = _x + _base._width;
            double sublocy = _y + SUBSCRIPT_POS * (_ascent + _descent)
                    - (1 - SUBSUPERSCRIPT_SIZE) * (_ascent - _descent) / 2;
            _sub = MathLabelComp.getBoxInstance(
                    ((MathElement.Msub) _me).subscript, sublocx, sublocy,
                    subfont, wstyle);
            _width = _base._width + _sub._width;
            boxDescent = sublocy - y + _sub.getDescent();
        }

        @Override
        protected void relayout(float fontSize, double x, double y) {
            super.relayout(fontSize, x, y);
            _base.relayout(fontSize, x, y);

            float subfont = _font.getSize2D() * SUBSUPERSCRIPT_SIZE;
            double sublocx = _x + _base._width;
            double sublocy = _y + SUBSCRIPT_POS * (_ascent + _descent)
                    - (1 - SUBSUPERSCRIPT_SIZE) * (_ascent - _descent) / 2;
            _sub.relayout(subfont, sublocx, sublocy);
            _width = _base._width + _sub._width;
            boxDescent = sublocy - y + _sub.getDescent();
        }

        @Override
        protected void pan(double xoff, double yoff) {
            super.pan(xoff, yoff);
            _base.pan(xoff, yoff);
            _sub.pan(xoff, yoff);
        }

        protected void draw(Graphics2D g2) {
            _base.draw(g2);
            _sub.draw(g2);
        }

    }

    private static class Msup extends MathLabelComp {
        final MathLabelComp _base;
        final MathLabelComp _sup;

        private Msup(MathElement.Msup me, double x, double y, Font font, boolean wstyle) {
            super(me, font, x, y);
            _base = MathLabelComp.getBoxInstance(me.base, _x, _y, _font, wstyle);

            Font supfont = _font.deriveFont(_font.getSize2D()
                    * SUBSUPERSCRIPT_SIZE);
            double suplocx = _x + _base._width;
            double suplocy = _y - SUPERSCRIPT_POS * (_ascent + _descent)
                    - (1 - SUBSUPERSCRIPT_SIZE) * (_ascent - _descent) / 2;
            _sup = MathLabelComp.getBoxInstance(
                    ((MathElement.Msup) _me).superscript, suplocx, suplocy, supfont, wstyle);
            _width = _base._width + _sup._width;
            boxAscent = y - suplocy + _sup.getAscent();
        }

        @Override
        protected void relayout(float fontSize, double x, double y) {
            super.relayout(fontSize, x, y);
            _base.relayout(fontSize, x, y);

            float supfont = _font.getSize2D() * SUBSUPERSCRIPT_SIZE;
            double suplocx = _x + _base._width;
            double suplocy = _y - SUPERSCRIPT_POS * (_ascent + _descent)
                    - (1 - SUBSUPERSCRIPT_SIZE) * (_ascent - _descent) / 2;
            _sup.relayout(supfont, suplocx, suplocy);
            _width = _base._width + _sup._width;
            boxAscent = y - suplocy + _sup.getAscent();
        }

        @Override
        protected void pan(double xoff, double yoff) {
            super.pan(xoff, yoff);
            _base.pan(xoff, yoff);
            _sup.pan(xoff, yoff);
        }

        protected void draw(Graphics2D g2) {
            _base.draw(g2);
            _sup.draw(g2);
        }

    }

    private static class Msubsup extends MathLabelComp {
        final MathLabelComp _base;
        final MathLabelComp _sub;
        final MathLabelComp _sup;

        private Msubsup(MathElement.Msubsup me, double x, double y, Font font,
                        boolean wstyle) {
            super(me, font, x, y);
            _base = MathLabelComp
                    .getBoxInstance(me.base, _x, _y, _font, wstyle);

            Font subsupfont = _font.deriveFont(_font.getSize2D()
                    * SUBSUPERSCRIPT_SIZE);
            double subsuplocx = _x + _base._width;
            double sublocy = _y + SUBSCRIPT_POS * (_ascent + _descent)
                    - (1 - SUBSUPERSCRIPT_SIZE) * (_ascent - _descent) / 2;
            double suplocy = _y - SUPERSCRIPT_POS * (_ascent + _descent)
                    - (1 - SUBSUPERSCRIPT_SIZE) * (_ascent - _descent) / 2;
            _sub = MathLabelComp.getBoxInstance(
                    ((MathElement.Msubsup) _me).subscript, subsuplocx, sublocy,
                    subsupfont, wstyle);
            _sup = MathLabelComp.getBoxInstance(
                    ((MathElement.Msubsup) _me).superscript, subsuplocx,
                    suplocy, subsupfont, wstyle);
            double ssw = (_sub._width > _sup._width) ? _sub._width
                    : _sup._width;
            _width = _base._width + ssw;
            boxAscent = y - suplocy + _sup.getAscent();
            boxDescent = sublocy - y + _sub.getDescent();
        }

        @Override
        protected void relayout(float fontSize, double x, double y) {
            super.relayout(fontSize, x, y);
            _base.relayout(fontSize, x, y);

            float subsupfont = _font.getSize2D() * SUBSUPERSCRIPT_SIZE;
            double subsuplocx = _x + _base._width;
            double sublocy = _y + SUBSCRIPT_POS * (_ascent + _descent)
                    - (1 - SUBSUPERSCRIPT_SIZE) * (_ascent - _descent) / 2;
            double suplocy = _y - SUPERSCRIPT_POS * (_ascent + _descent)
                    - (1 - SUBSUPERSCRIPT_SIZE) * (_ascent - _descent) / 2;
            _sub.relayout(subsupfont, subsuplocx, sublocy);
            _sup.relayout(subsupfont, subsuplocx, suplocy);
            double ssw = (_sub._width > _sup._width) ? _sub._width
                    : _sup._width;
            _width = _base._width + ssw;
            boxAscent = y - suplocy + _sup.getAscent();
            boxDescent = sublocy - y + _sub.getDescent();
        }

        @Override
        protected void pan(double xoff, double yoff) {
            super.pan(xoff, yoff);
            _base.pan(xoff, yoff);
            _sub.pan(xoff, yoff);
            _sup.pan(xoff, yoff);
        }

        protected void draw(Graphics2D g2) {
            _base.draw(g2);
            _sub.draw(g2);
            _sup.draw(g2);
        }

    }

    /**
     * Presentation Expression, composed of Presentations. The most common
     * PresExpression is Mrow, Horizontally Group Sub-Expressions.
     */
    private static class PContainer extends MathLabelComp {

        /**
         * sub components list
         */
        private final List<MathLabelComp> _subEL = new ArrayList<>();

        private PContainer(MathElement.PContainer me, double x, double y,
                           Font font, boolean wstyle) {
            super(me, font, x, y);

            double nextLocx = _x;
            for (MathElement.PElement pe : me.getElementList()) {
                MathLabelComp ml = MathLabelComp.getBoxInstance(pe, nextLocx,
                        _y, _font, wstyle);
                _subEL.add(ml);
                nextLocx += ml._width;
                if (boxAscent < ml.boxAscent) {
                    boxAscent = ml.boxAscent;
                }
                if (boxDescent < ml.boxDescent) {
                    boxDescent = ml.boxDescent;
                }
            }
            _width = nextLocx - _x;
        }

        @Override
        protected void relayout(float fontSize, double x, double y) {
            super.relayout(fontSize, x, y);

            if (_subEL.size() == 0) {
                _width = 0;
            } else {
                double w = 0;
                for (int i = 0; i < _subEL.size(); i++) {
                    MathLabelComp ml = _subEL.get(i);
                    ml.relayout(fontSize, x + w, y);
                    w += _subEL.get(i)._width;
                    if (boxAscent < ml.boxAscent) {
                        boxAscent = ml.boxAscent;
                    }
                    if (boxDescent < ml.boxDescent) {
                        boxDescent = ml.boxDescent;
                    }
                }
                _width = w;
            }
        }

        @Override
        protected void pan(double xoff, double yoff) {
            super.pan(xoff, yoff);
            if (xoff != 0 || yoff != 0) {
                for (MathLabelComp ml : _subEL) {
                    ml.pan(xoff, yoff);
                }
            }
        }

        protected void draw(Graphics2D g2) {
            for (MathLabelComp ml : _subEL) {
                ml.draw(g2);
            }
        }

    }

    protected final MathElement _me;

    /* device width of this component */
    protected double _width;

    protected Font _font;

    /**
     * The ascent and descent of the base font.
     */
    protected float _ascent, _descent, _leading;

    /**
     * The ascent and descent of this component box.
     */
    protected double boxAscent, boxDescent;

    /**
     * base point of the 1st string location
     */
    protected double _x, _y;

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
     * Create underlayer components, width and height are calculated. Subclass
     * should create its sub-component recursively.
     *
     * @param me   the MathElement object. can be null if the parsed text is empty string.
     * @param font the font
     * @param x    the x of relative location
     * @param y    the y of relative location
     */
    protected MathLabelComp(MathElement me, Font font, double x, double y) {
        _me = me;
        _font = font;
        _x = x;
        _y = y;
        /* ensure _height is a proper value */
        computeFontHeight();
    }

    /**
     * Compute the font height in device (screen)
     */
    private void computeFontHeight() {
        FontRenderContext frc = new FontRenderContext(null, false, true);
        LineMetrics lm = _font.getLineMetrics("Can be any string", frc);
        _ascent = lm.getAscent();
        _descent = lm.getDescent();
        _leading = lm.getLeading();
        boxAscent = _ascent;
        boxDescent = _descent;
    }

    /**
     * re-layout sub elements, after font changed. The height and width are
     * re-calculated.
     */
    protected void relayout(float fontSize, double x, double y) {
        _font = _font.deriveFont(fontSize);
        _x = x;
        _y = y;
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
            _x += xoff;
            _y += yoff;
        }
    }

    protected abstract void draw(Graphics2D g2);

    /**
     * Returns the pixel width of this MLC
     *
     * @return the pixel width of this MLC
     */
    protected double getWidth() {
        return _width;
    }

    /**
     * The returned ascent, not include leading.
     *
     * @return the pixel ascent of this MLC
     */
    protected double getAscent() {
        return boxAscent;
    }

    protected double getDescent() {
        return boxDescent;
    }

    protected double getLeading() {
        return _leading;
    }

}
