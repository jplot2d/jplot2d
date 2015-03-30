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
package org.jplot2d.swing.proptable.property;

import org.jplot2d.env.PropertyInfo;

import java.awt.BasicStroke;

/**
 * @author Jingjing Li
 */
public class BasicStrokeProperty extends PropertyDescriptorAdapter<BasicStroke> {

    private Property<Float> spLineWidth;

    private Property<float[]> spDashArray;

    private Float lineWidth;

    private float[] dashArray;

    public BasicStrokeProperty(PropertyInfo descriptor) {
        super(descriptor);

        initSubProperties();
    }

    public Property<?>[] getSubProperties() {
        return new Property<?>[]{spLineWidth, spDashArray};
    }

    public void readFromObject(Object object) {
        super.readFromObject(object);

        if (getValue() != null) {
            lineWidth = getValue().getLineWidth();
            dashArray = getValue().getDashArray();
        }
    }

    private void updateValue() {
        BasicStroke v = getValue();
        if (lineWidth == null) {
            setValue(null);
        } else if (dashArray == null) {
            if (v == null) {
                setValue(new BasicStroke(lineWidth));
            } else {
                setValue(new BasicStroke(lineWidth, v.getEndCap(), v.getLineJoin(), v.getMiterLimit()));
            }
        } else {
            if (v == null) {
                setValue(new BasicStroke(lineWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f));
            } else {
                setValue(new BasicStroke(lineWidth, v.getEndCap(), v.getLineJoin(), v.getMiterLimit(), dashArray,
                        v.getDashPhase()));
            }
        }
    }

    private void initSubProperties() {
        // initial sub-properties
        spLineWidth = new SubProperty<Float>(this) {

            public String getName() {
                return "LineWidth";
            }

            public Class<Float> getType() {
                return Float.class;
            }

            public Float getValue() {
                return lineWidth;
            }

            public void setValue(Float x) {
                lineWidth = x;
                updateValue();
            }

        };

        spDashArray = new SubProperty<float[]>(this) {

            public String getName() {
                return "DashArray";
            }

            public Class<float[]> getType() {
                return float[].class;
            }

            public float[] getValue() {
                return dashArray;
            }

            public void setValue(float[] y) {
                dashArray = y;
                updateValue();
            }

        };

    }
}
