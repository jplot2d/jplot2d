/**
 * 
 */
package org.jplot2d.annotation;

import java.lang.annotation.*;

/**
 * The property annotation are apply to the getter method
 * 
 * @author Jingjing Li
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Property {

	String displayName() default "";

	String description() default "";

	/**
	 * @return the order in its group
	 */
	int order() default 0;
}
