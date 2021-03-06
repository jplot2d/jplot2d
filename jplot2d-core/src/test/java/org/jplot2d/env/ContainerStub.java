/*
 * Copyright 2010 Jingjing Li.
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
package org.jplot2d.env;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ContainerImpl;
import org.jplot2d.element.impl.InvokeStep;
import org.jplot2d.transform.PaperTransform;

/**
 * Stub for ContainerImpl
 * 
 * @author Jingjing Li
 * 
 */
public class ContainerStub extends ContainerImpl {

	public String getId() {
		return null;
	}

	public PaperTransform getPaperTransform() {
		return null;
	}

	public Point2D getLocation() {
		return null;
	}

	public Dimension2D getSize() {
		return null;
	}

	public Rectangle2D getBounds() {
		return null;
	}

	public ComponentEx[] getComponents() {
		return null;
	}

	public InvokeStep getInvokeStepFormParent() {
		return null;
	}

}
