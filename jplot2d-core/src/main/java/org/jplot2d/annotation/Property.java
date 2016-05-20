/*
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
package org.jplot2d.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The property annotation indicate that the property should appear in properties table. This annotation mark on the
 * getter method.
 *
 * @author Jingjing Li
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Property {

    /**
     * The name displayed in properties table. The default value "" means displaying the property name.
     */
    String displayName() default "";

    /**
     * How many significant digits should display for the value. Only useful for float and double property. The default
     * value 0 means no limit.
     */
    int displayDigits() default 0;

    /**
     * The string displayed in tooltip.
     */
    String description() default "";

    /**
     * The property is read-only, even if there is a writer method. A sub-interface can use it to disable a writer.
     */
    boolean readOnly() default false;

    /**
     * The order in its group
     */
    int order();

    /**
     * Determine if this property is a styleable property. A styleable property must be writable.
     */
    boolean styleable() default true;
}
