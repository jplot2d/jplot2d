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
package org.jplot2d.swing.print;

import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.renderer.CacheableBlock;
import org.jplot2d.renderer.Renderer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

/**
 * @author JingJing Li
 */
public class PrintRenderer extends Renderer implements Printable {

    public enum PageFitMode {
        /**
         * Fit onto printer page. Magnify or shrink to fit onto printer page.
         */
        TO_FIT,
        /**
         * Default scale for printing. A value of 1.0 paper units = 72 pts.
         */
        DEFAULT_SCALE,
        /**
         * Shrink to fit onto printer page. Will not magnify if graphic will already fit.
         */
        SHRINK_TO_FIT
    }

    /**
     * Align to top of printer page.
     */
    public static final int TOP = 0;

    /**
     * Align to middle of printer page.
     */
    public static final int MIDDLE = 1;

    /**
     * Align to bottom of printer page.
     */
    public static final int BOTTOM = 2;

    /**
     * Align to left of printer page.
     */
    public static final int LEFT = 0;

    /**
     * Align to center of printer page.
     */
    public static final int CENTER = 1;

    /**
     * Align to right of printer page.
     */
    public static final int RIGHT = 2;

    private static final PrinterJob printerJob = PrinterJob.getPrinterJob();

    /**
     * The global pageFormat used in {@link #pageDialog()} and {@link #printDialog(RenderEnvironment)}
     */
    private static PageFormat pageFormat;

    private final RenderEnvironment env;

    private Graphics2D g2;

    /**
     * The PageFormat passed in by {@link #print(Graphics, PageFormat, int)}
     */
    private PageFormat pf;

    private final PageFitMode fitMode;

    public PrintRenderer(RenderEnvironment env) {
        this.env = env;
        fitMode = PageFitMode.TO_FIT;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        } else {
            this.g2 = (Graphics2D) graphics;
            this.pf = pageFormat;
            env.exportPlot(this);
            return PAGE_EXISTS;
        }
    }

    @Override
    public void render(ComponentEx comp, List<CacheableBlock> cacheBlockList) {

        Dimension2D size = comp.getSize();
        double dscale = comp.getPaperTransform().getScale();
        double dw = size.getWidth() * dscale;
        double dh = size.getHeight() * dscale;

        double pintScale = 1 / dscale; // printing size / display pixel size
        double dx = pf.getImageableX();
        double dy = pf.getImageableY();
        if (fitMode == PageFitMode.TO_FIT || fitMode == PageFitMode.SHRINK_TO_FIT) {
            double fitScale;
            double xf = pf.getImageableWidth() / dw;
            double yf = pf.getImageableHeight() / dh;
            if (xf < yf) {
                fitScale = xf;
            } else {
                fitScale = yf;
            }
            // fit or shrink
            if (fitMode == PageFitMode.TO_FIT || fitScale > 1) {
                pintScale = fitScale;
            }
        }

        switch (getPageHAlign()) {
            default:
            case CENTER:
                dx += (pf.getImageableWidth() - pintScale * dw) / 2.0;
                break;
            case RIGHT:
                dx += pf.getImageableWidth() - pintScale * dw;
                break;
            case LEFT:
                // do nothing
                break;
        }

        switch (getPageVAlign()) {
            default:
            case TOP:
                // do nothing
                break;
            case BOTTOM:
                dy += pf.getImageableHeight() - pintScale * dw;
                break;
            case MIDDLE:
                dy += (pf.getImageableHeight() - pintScale * dh) / 2.0;
                break;
        }
        g2.translate(dx, dy);
        g2.scale(pintScale, pintScale);

        for (CacheableBlock cb : cacheBlockList) {
            List<ComponentEx> sublist = cb.getSubcomps();
            for (ComponentEx subcomp : sublist) {
                subcomp.draw(g2);
            }
        }

    }

    private int getPageHAlign() {
        return CENTER;
    }

    private int getPageVAlign() {
        return MIDDLE;
    }

    public static void pageDialog() {
        if (pageFormat == null) {
            pageFormat = printerJob.defaultPage();
        }
        pageFormat = printerJob.pageDialog(pageFormat);
    }

    public static void printDialog(RenderEnvironment env) throws PrinterException {
        if (pageFormat == null) {
            pageFormat = printerJob.defaultPage();
            pageFormat = printerJob.pageDialog(pageFormat);
        }
        printerJob.setPrintable(new PrintRenderer(env), pageFormat);
        if (printerJob.printDialog()) {
            printerJob.print();
        }
    }
}
