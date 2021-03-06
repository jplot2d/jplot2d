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
package org.jplot2d.renderer;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.env.CacheableBlock;

import java.awt.*;
import java.io.*;
import java.util.List;

/**
 * Export plot image to a png file.
 *
 * @author Jingjing Li
 */
public class PdfExporter extends Renderer {

    private final OutputStream os;

    private String title;

    public PdfExporter(String pathname) throws FileNotFoundException {
        this(new File(pathname));
    }

    public PdfExporter(File file) throws FileNotFoundException {
        this(new FileOutputStream(file));
    }

    public PdfExporter(OutputStream out) {
        super();
        os = new BufferedOutputStream(out);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void render(ComponentEx comp, List<CacheableBlock> cacheBlockList) {

        Dimension size = getDeviceBounds(comp).getSize();

        Document document = new Document(new Rectangle(size.width, size.height), 0, 0, 0, 0);
        PdfWriter writer;
        try {
            writer = PdfWriter.getInstance(document, os);
        } catch (DocumentException e) {
            /*
             * should not happen but if it happens it should be notified to the integration instead of leaving it
			 * half-done and tell nothing.
			 */
            throw new RuntimeException("Error creating PDF document", e);
        }

        document.open();
        if (title != null) {
            document.addTitle(title);
        }
        PdfContentByte cb = writer.getDirectContent();
        Graphics2D g = cb.createGraphics(size.width, size.height);

        for (CacheableBlock cblock : cacheBlockList) {
            List<ComponentEx> sublist = cblock.getSubcomps();
            for (ComponentEx subcomp : sublist) {
                subcomp.draw(g);
            }
        }

        g.dispose();
        document.close();
    }

}
