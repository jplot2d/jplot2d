/**
 * 
 */
package org.jplot2d.annotation;

import java.lang.annotation.*;

/**
 * The property annotation indicate that the property should appear in properties table. This annotation mark on the
 * getter method.
 * 
 * @author Jingjing Li
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Property {

	/**
	 * The name displayed in properties table. The default value "" means displaying the property name.
	 */
	String displayName() default "";

	/**
	 * The string displayed in tooltip.
	 */
	String description() default "";

	/**
	 * The order in its group
	 */
	int order();

	/**
	 * Determine if this property is a styleable property. A styleable property must be writable.
	 */
	boolean styleable() default true;
}
