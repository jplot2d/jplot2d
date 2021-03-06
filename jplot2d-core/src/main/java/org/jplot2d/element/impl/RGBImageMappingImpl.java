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
package org.jplot2d.element.impl;

import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.data.MultiBandImageData;
import org.jplot2d.notice.Notice;

import javax.annotation.Nonnull;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Jingjing Li
 */
public class RGBImageMappingImpl extends ElementImpl implements RGBImageMappingEx {

    private final List<RGBImageGraphEx> graphs = new ArrayList<>();

    private final ImageBandTransformEx redTransform, greenTransform, blueTransform;

    public RGBImageMappingImpl() {
        redTransform = new ImageBandTransformImpl();
        greenTransform = new ImageBandTransformImpl();
        blueTransform = new ImageBandTransformImpl();
        redTransform.setParent(this);
        greenTransform.setParent(this);
        blueTransform.setParent(this);
    }

    public RGBImageMappingImpl(ImageBandTransformEx redTransform, ImageBandTransformEx greenTransform, ImageBandTransformEx blueTransform) {
        this.redTransform = redTransform;
        this.greenTransform = greenTransform;
        this.blueTransform = blueTransform;
        redTransform.setParent(this);
        greenTransform.setParent(this);
        blueTransform.setParent(this);
    }

    public RGBImageGraphEx getParent() {
        return (RGBImageGraphEx) parent;
    }

    public ElementEx getPrim() {
        if (graphs.size() == 0) {
            return null;
        } else {
            return graphs.get(0);
        }
    }

    public void notify(Notice msg) {
        if (getPrim() != null) {
            getPrim().notify(msg);
        }
    }

    public String getId() {
        StringBuilder sb = new StringBuilder();
        sb.append("RGBImageMapping(");
        for (RGBImageGraphEx graph : graphs) {
            sb.append(graph.getId()).append(',');
        }
        sb.replace(sb.length() - 1, sb.length(), ")");
        return sb.toString();
    }

    public String getFullId() {
        return "RGBImageMapping@" + Integer.toHexString(System.identityHashCode(this));
    }

    public InvokeStep getInvokeStepFormParent() {
        if (graphs.size() == 0) {
            return null;
        }

        Method method;
        try {
            method = ImageGraphEx.class.getMethod("getMapping");
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
        return new InvokeStep(method);
    }

    public void addImageGraph(RGBImageGraphEx graph) {
        graphs.add(graph);
        invalidateLimits();
        if (graphs.size() == 1) {
            parent = graphs.get(0);
        } else {
            parent = null;
        }
    }

    public void removeImageGraph(RGBImageGraphEx graph) {
        if (graphs.remove(graph)) {
            invalidateLimits();
        }
        if (graphs.size() == 1) {
            parent = graphs.get(0);
        } else {
            parent = null;
        }
    }

    public RGBImageGraphEx[] getGraphs() {
        return graphs.toArray(new RGBImageGraphEx[graphs.size()]);
    }

    @Nonnull
    public ImageBandTransformEx getRedTransform() {
        return redTransform;
    }

    @Nonnull
    public ImageBandTransformEx getGreenTransform() {
        return greenTransform;
    }

    @Nonnull
    public ImageBandTransformEx getBlueTransform() {
        return blueTransform;
    }

    @Override
    public ElementEx copyStructure(@Nonnull Map<ElementEx, ElementEx> orig2copyMap) {
        ImageBandTransformEx redCopy = (ImageBandTransformEx) redTransform.copyStructure(orig2copyMap);
        ImageBandTransformEx greenCopy = (ImageBandTransformEx) greenTransform.copyStructure(orig2copyMap);
        ImageBandTransformEx blueCopy = (ImageBandTransformEx) blueTransform.copyStructure(orig2copyMap);
        RGBImageMappingImpl result = new RGBImageMappingImpl(redCopy, greenCopy, blueCopy);

        orig2copyMap.put(this, result);

        return result;
    }

    public void invalidateLimits() {
        redTransform.invalidateLimits();
        greenTransform.invalidateLimits();
        blueTransform.invalidateLimits();
    }

    public void calcLimits() {
        ImageDataBuffer[] redBuffers = new ImageDataBuffer[graphs.size()];
        ImageDataBuffer[] greenBuffers = new ImageDataBuffer[graphs.size()];
        ImageDataBuffer[] blueBuffers = new ImageDataBuffer[graphs.size()];
        Dimension[] sizeArray = new Dimension[graphs.size()];

        int n = 0;
        for (RGBImageGraphEx graph : graphs) {
            MultiBandImageData data = graph.getData();
            if (data != null) {
                ImageDataBuffer[] dbBands = data.getDataBuffer();
                redBuffers[n] = dbBands[0];
                greenBuffers[n] = dbBands[1];
                blueBuffers[n] = dbBands[2];
                sizeArray[n] = new Dimension(data.getWidth(), data.getHeight());
                n++;
            }
        }
        if (n != graphs.size()) {
            redBuffers = Arrays.copyOf(redBuffers, n);
            greenBuffers = Arrays.copyOf(greenBuffers, n);
            blueBuffers = Arrays.copyOf(blueBuffers, n);
            sizeArray = Arrays.copyOf(sizeArray, n);
        }

        redTransform.calcLimits(redBuffers, sizeArray);
        greenTransform.calcLimits(greenBuffers, sizeArray);
        blueTransform.calcLimits(blueBuffers, sizeArray);
    }
}
