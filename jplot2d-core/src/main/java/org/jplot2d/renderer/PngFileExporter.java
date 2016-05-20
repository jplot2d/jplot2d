/*
 * Copyright 2010-2012 Jingjing Li.
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
package org.jplot2d.renderer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Export plot image to a png file.
 *
 * @author Jingjing Li
 */
public class PngFileExporter extends ImageExporter {

    private static final ImageFactory INT_RGB_IMAGE_FACTORY = new BufferedImageFactory(BufferedImage.TYPE_INT_RGB,
            Color.WHITE);

    private final File file;

    public PngFileExporter(String pathname) {
        this(new File(pathname));
    }

    public PngFileExporter(File file) {
        this(INT_RGB_IMAGE_FACTORY, file);
    }

    public PngFileExporter(ImageFactory imageFactory, File file) {
        super(imageFactory);
        this.file = file;
    }

    @Override
    protected void fireRenderingFinished(long sn, BufferedImage img) {
        super.fireRenderingFinished(sn, img);
        try {
            ImageIO.write(img, "PNG", file);
        } catch (IOException e) {
            throw new RuntimeException("Png file I/O exception", e);
        }
    }

}
