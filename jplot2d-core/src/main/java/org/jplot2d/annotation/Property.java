/**
 * 
 */
package org.jplot2d.annotation;

import java.lang.annotation.*;

/**
 * 
 * 
 * @author Jingjing Li
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Property {
    String displayName() default "";
    String description() default "";
}
