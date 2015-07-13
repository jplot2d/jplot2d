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
package org.jplot2d.swing.interaction;

import org.jplot2d.interaction.InteractiveComp;
import org.jplot2d.swing.JPlot2DComponent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jingjing Li
 */
public class SwingInteractiveComp implements InteractiveComp {

    private final JPlot2DComponent comp;

    private CursorStyle cursorStyle;

    public SwingInteractiveComp(JPlot2DComponent comp) {
        this.comp = comp;
    }

    public Point getCursorLocation() {
        Point p = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(p, comp);
        p.x -= comp.getImageOffsetX();
        p.y -= comp.getImageOffsetY();
        return p;
    }

    public void repaint() {
        comp.repaint();
    }

    public CursorStyle getCursor() {
        return cursorStyle;
    }

    public void setCursor(CursorStyle cursorStyle) {
        this.cursorStyle = cursorStyle;

        switch (cursorStyle) {
            case DEFAULT_CURSOR:
                comp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                break;
            case MOVE_CURSOR:
                comp.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                break;
            case CROSSHAIR_CURSOR:
                comp.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                break;
        }
    }

    public void drawLine(Object g, int rgb, int x1, int y1, int x2, int y2) {
        Graphics2D g2 = (Graphics2D) g;
        Color c = new Color(rgb);
        g2.setColor(c);
        g2.setXORMode(comp.getPlotBackground());
        g2.drawLine(x1 + comp.getImageOffsetX(), y1 + comp.getImageOffsetY(),
                x2 + comp.getImageOffsetX(), y2 + comp.getImageOffsetY());
        g2.setPaintMode();
    }

    public void drawRectangle(Object g, int rgb, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        Color c = new Color(rgb);
        g2.setColor(c);
        g2.setXORMode(comp.getPlotBackground());
        g2.drawRect(x + comp.getImageOffsetX(), y + comp.getImageOffsetY(), width, height);
        g2.setPaintMode();
    }

    public void drawShape(Object g, int rgb, Shape shape) {
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(comp.getImageOffsetX(), comp.getImageOffsetY());
        Color c = new Color(rgb);
        g2.setColor(c);
        g2.setXORMode(comp.getPlotBackground());
        g2.draw(shape);
        g2.setPaintMode();
        g2.translate(-comp.getImageOffsetX(), -comp.getImageOffsetY());
    }

    public void drawTooltip(Object g, String s, int x, int y) {
        x += comp.getImageOffsetX() + 4;
        y += comp.getImageOffsetY() + 4;

        Graphics2D g2 = (Graphics2D) g;
        FontMetrics fm = g2.getFontMetrics();


        int cridx = s.indexOf('\n');
        if (cridx == -1) {
            int width = fm.stringWidth(s);
            int height = fm.getHeight();
            g2.setColor(new Color((SystemColor.info.getRGB() & 0x00ffffff) | 0x80000000, true));
            g2.fillRect(x, y, width, height);

            y += fm.getAscent();
            g2.setColor(SystemColor.infoText);
            g2.drawString(s, x, y);
        } else {
            String sa = s.substring(0, cridx);
            String sb = s.substring(cridx + 1);

            int width = Math.max(fm.stringWidth(sa), fm.stringWidth(sb));
            int height = fm.getHeight() * 2 - fm.getLeading();
            g2.setColor(new Color((SystemColor.info.getRGB() & 0x00ffffff) | 0x80000000, true));
            g2.fillRect(x, y, width, height);

            y += fm.getAscent();
            g2.setColor(SystemColor.infoText);
            g2.drawString(sa, x, y);
            y += fm.getHeight();
            g2.drawString(sb, x, y);
        }
    }
}
