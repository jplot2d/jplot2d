/*
 * $Id: Log.java,v 1.8 2009/02/14 08:06:00 jli Exp $
 */
package org.jplot2d.env;

import java.util.logging.Logger;

/** package private Log facillity */
public class Log {
    static Logger book = Logger.getLogger("herschel.ia.gui.plot");

    static Logger drawer = Logger.getLogger("herschel.ia.gui.plot.drawer");

    static Logger renderer = Logger.getLogger("herschel.ia.gui.plot.renderer");

    static Logger rendererFSN = Logger.getLogger("herschel.ia.gui.plot.renderer.fsn");

    static Logger layout = Logger.getLogger("herschel.ia.gui.plot.layout");

    static Logger axis = Logger.getLogger("herschel.ia.gui.plot.axis");

    static Logger getTickLogger(String axisId) {
        return Logger.getLogger("herschel.ia.gui.plot." + axisId + ".tick");
    }
}
