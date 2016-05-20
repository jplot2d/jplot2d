/*
 * Copyright 2010, 2011 Jingjing Li.
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
package org.jplot2d.swing.proptable.editor;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.UIManager;

/**
 * A button with a fixed size to workaround bugs in OSX. Submitted by Hani Suleiman.
 * Hani uses an icon for the ellipsis, I've decided to hardcode the dimension to 16x30 but only on Mac OS X.
 */
public final class FixedButton extends JButton {

    private static final long serialVersionUID = 1L;

    public FixedButton() {
        super("...");

        if (isMacOSX() && UIManager.getLookAndFeel().isNativeLookAndFeel()) {
            setPreferredSize(new Dimension(16, 30));
        }

        setMargin(new Insets(0, 0, 0, 0));
    }

    private boolean isMacOSX() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.startsWith("mac os x");
    }

}
