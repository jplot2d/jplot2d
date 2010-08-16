/*
 * $Id: WarningException.java,v 1.1 2006/08/10 08:29:42 jli Exp $
 */
package org.jplot2d.env;

/**
 * This exception is thrown when some warning should be notified. The object
 * that throw this exception is in good status.
 * 
 * @author Jingjing Li
 */
public class WarningException extends RuntimeException {

    private static final long serialVersionUID = -8856559325014058901L;

    public WarningException(String message) {
        super(message);
    }

    public WarningException(String message, Throwable cause) {
        super(message, cause);
    }

}
