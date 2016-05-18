/**
 * Copyright 2010-2016 Jingjing Li.
 * <p>
 * This file is part of jplot2d.
 * <p>
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.data;

import org.jplot2d.util.TestUtils;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * @author Jingjing Li
 */
public class SingleBandImageDataTest {

    @Test
    public void testCreateImageDataBuffer() {
        ImageDataBuffer idb = mock(ImageDataBuffer.class);
        SingleBandImageData data = new SingleBandImageData(idb, 300, 200);
        TestUtils.checkRange(data.getXRange(), -0.5, 299.5);
        TestUtils.checkRange(data.getYRange(), -0.5, 199.5);
    }

    @Test
    public void testCreateImageDataBufferCR() {
        ImageDataBuffer idb = mock(ImageDataBuffer.class);
        ImageCoordinateReference cr = new ImageCoordinateReference();
        cr.setXRefPixel(100);
        cr.setYRefPixel(10);
        SingleBandImageData data = new SingleBandImageData(idb, 300, 200, cr);
        TestUtils.checkRange(data.getXRange(), -100.5, 199.5);
        TestUtils.checkRange(data.getYRange(), -10.5, 189.5);

        cr = new ImageCoordinateReference();
        cr.setXRefVal(100);
        cr.setYRefVal(10);
        data = new SingleBandImageData(idb, 300, 200, cr);
        TestUtils.checkRange(data.getXRange(), 99.5, 399.5);
        TestUtils.checkRange(data.getYRange(), 9.5, 209.5);

        cr = new ImageCoordinateReference();
        cr.setXPixelSize(2);
        cr.setYPixelSize(3);
        data = new SingleBandImageData(idb, 300, 200, cr);
        TestUtils.checkRange(data.getXRange(), -1, 599);
        TestUtils.checkRange(data.getYRange(), -1.5, 598.5);
    }

}
