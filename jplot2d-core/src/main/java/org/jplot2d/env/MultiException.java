/*
 * $Id: MultiException.java,v 1.6 2009/07/16 15:20:39 jli Exp $
 */
package org.jplot2d.env;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Wraps multiple WarningException.
 * 
 * Allows multiple exceptions to be thrown as a single exception.
 * 
 * @ThreadNote Not thread safe
 * @author Jingjing Li
 */
public class MultiException extends WarningException {

    private static final long serialVersionUID = -2430472360898569623L;

    private List<WarningException> nested = new ArrayList<WarningException>();

    public MultiException() {
        super("Multiple exceptions");
    }

    public void add(WarningException e) {
        if (e instanceof MultiException) {
            MultiException me = (MultiException) e;
            for (int i = 0; i < me.nested.size(); i++)
                nested.add(me.nested.get(i));
        } else
            nested.add(e);
    }

    public int size() {
        return nested.size();
    }

    public List<WarningException> getExceptions() {
        return nested;
    }

    public WarningException getException(int i) {
        return nested.get(i);
    }

    /**
     * If this multi exception is empty then return null. If it contains a
     * single exception that is returned, otherwise the this multi exception is
     * returned.
     */
    public WarningException getSingleOrMultiException() {
        switch (nested.size()) {
        case 0:
            return null;
        case 1:
            return nested.get(0);
        default:
            return this;
        }
    }

    /**
     * Throw a multi-exception. If this multi exception is empty then no action
     * is taken. If it contains a single exception that is thrown, otherwise the
     * this multi exception is thrown.
     * 
     * @exception WarningException
     */
    public void ifExceptionThrow() throws WarningException {
        switch (nested.size()) {
        case 0:
            break;
        case 1:
            throw nested.get(0);
        default:
            throw this;
        }
    }

    /**
     * Throw a multi-exception. If this multi exception is empty then no action
     * is taken. If it contains a any exceptions then this multi exception is
     * thrown.
     */
    public void ifExceptionThrowMulti() throws MultiException {
        if (nested.size() > 0)
            throw this;
    }

    public String getMessage() {
        if (nested.size() > 0)
            return "Multiple exceptions " + nested.toString();
        return "Multiple exceptions[]";
    }

    public void printStackTrace() {
        super.printStackTrace();
        for (int i = 0; i < nested.size(); i++)
            nested.get(i).printStackTrace();
    }

    public void printStackTrace(PrintStream out) {
        super.printStackTrace(out);
        for (int i = 0; i < nested.size(); i++)
            nested.get(i).printStackTrace(out);
    }

    public void printStackTrace(PrintWriter out) {
        super.printStackTrace(out);
        for (int i = 0; i < nested.size(); i++)
            nested.get(i).printStackTrace(out);
    }

    public static WarningException addEx(WarningException ex, WarningException e) {
        if (ex == null) {
            return e;
        }
        if (ex instanceof MultiException) {
            ((MultiException) ex).add(e);
            return ex;
        }
        MultiException me = new MultiException();
        me.add(ex);
        me.add(e);
        return me;
    }

}
