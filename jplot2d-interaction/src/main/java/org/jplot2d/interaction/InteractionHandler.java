/**
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
package org.jplot2d.interaction;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class handle the mouse actions that can respond to user's mouse events like left-click,
 * double-click, right-click. The mouse event will translate to application level behavior, such as
 * select an object, select data points in a layer, zoom in/out, etc. The translation can be
 * configured by register and unregister the binding between application behavior to mouse events.
 * <p>
 * Notice: most methods in this class is not thread-safe. They should be called within EDT.
 * 
 * @author Jingjing Li
 * 
 */
public class InteractionHandler implements PlotPaintListener {

	public static final String PLOT_ENV_KEY = "PLOT_ENV";

	public static final String ACTIVE_COMPONENT_KEY = "ACTIVE_COMPONENT";

	public static final String ACTIVE_COMPONENT_MOVABLE_KEY = "ACTIVE_COMPONENT_MOVABLE";

	private final InteractionManager imanager;

	private final InteractiveComp icomp;

	private InteractionModeHandler modeHandler;

	private final Map<InteractionMode, InteractionModeHandler> modeHandlerMap = new LinkedHashMap<InteractionMode, InteractionModeHandler>();

	private final Map<String, Object> valueMap = new HashMap<String, Object>();

	/**
	 * Keeps all buttons which were pressed at the time of the last mouse drag beyond threshold,
	 * until all buttons will be released.
	 */
	private int mouseSigDragState = 0;

	/**
	 * Keep all buttons which were pressed at the time of the last mouse drag within threshold,
	 * until all buttons will be released.
	 */
	private int mouseTrivialDragState = 0;

	/**
	 * The point where the last mouse button pressed.
	 */
	private Point lastPoint;

	public InteractionHandler(InteractionManager imanager, InteractiveComp icomp) {
		this.imanager = imanager;
		this.icomp = icomp;
	}

	protected final InteractionManager getInteractionManager() {
		return imanager;
	}

	protected final InteractiveComp getInteractiveComp() {
		return icomp;
	}

	/**
	 * create mode handler and fill them with behavior handlers
	 * 
	 * @param ihandler
	 */
	public void init() {
		for (InteractionMode mode : imanager.getModes()) {
			InteractionModeHandler mhandler = new InteractionModeHandler(this, mode);
			mhandler.valueMap.putAll(valueMap);
			modeHandlerMap.put(mode, mhandler);

			for (MouseBehavior behavior : mode.getAvailableMouseBehaviors()) {
				MouseBehaviorHandler<?> mbHandler = behavior.createMouseBehaviorHandler(mhandler);
				mhandler.addMouseBehaviorHandler(mbHandler);
			}

			for (ValueChangeBehavior feedback : mode.getValueChangeBehaviors()) {
				ValueChangeHandler<?> fbHandler = feedback.createValueChangeHandler(mhandler);
				mhandler.addValueChangeHandler(fbHandler);
			}
		}

		setMode(imanager.getDefaultMode());
	}

	/**
	 * SWT send menu event independent from mouse event.
	 * 
	 * @param p
	 */
	public void menuDetected(int x, int y) {
		modeHandler.menuDetected(x, y);
	}

	public void mouseEntered(GenericMouseEvent e) {
		modeHandler.mouseEntered(e);
	}

	public void mouseExited(GenericMouseEvent e) {
		modeHandler.mouseExited(e);
	}

	public void mousePressed(GenericMouseEvent e) {
		modeHandler.mousePressed(e);

		lastPoint = new Point(e.getX(), e.getY());
		mouseTrivialDragState |= e.getButton();
	}

	public void mouseReleased(GenericMouseEvent e) {
		modeHandler.mouseReleased(e);

		if ((mouseTrivialDragState & ~mouseSigDragState & e.getButton()) != 0) {
			e = new GenericMouseEvent(MouseEvent.MOUSE_CLICKED, e.getModifiers(), e.getX(),
					e.getY(), 1, e.getButton());
			mouseClicked(e);
		}

		mouseSigDragState &= ~e.getButton();
		mouseTrivialDragState &= ~e.getButton();
	}

	private void mouseClicked(GenericMouseEvent e) {
		modeHandler.mouseClicked(e);
	}

	public void mouseDragged(GenericMouseEvent e) {
		int mouseKeyState = e.getModifiers()
				& (InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK);
		int threshold = imanager.getClickThreshold();
		if (lastPoint == null || Math.abs(lastPoint.x - e.getX()) > threshold
				|| Math.abs(lastPoint.y - e.getY()) > threshold) {
			lastPoint = null;
			mouseSigDragState = mouseKeyState;
			modeHandler.mouseDragged(e);
		} else {
			// dragging within the Threshold
			mouseTrivialDragState = mouseKeyState;
		}
	}

	public void mouseMoved(GenericMouseEvent e) {
		modeHandler.mouseMoved(e);
	}

	/**
	 * @param e
	 *            the negative count values if the mouse wheel was rotated up/away from the user,
	 *            and positive count values if the mouse wheel was rotated down/ towards the user
	 */
	public void mouseWheelMoved(GenericMouseEvent e) {
		modeHandler.mouseWheelMoved(e);
	}

	public void plotPainted(PlotPaintEvent evt) {
		modeHandler.plotPainted(evt);
	}

	public InteractionMode getMode() {
		return modeHandler.getInteractionMode();
	}

	public void setMode(InteractionMode mode) {
		InteractionModeHandler newhandler = modeHandlerMap.get(mode);
		if (newhandler == null) {
			throw new IllegalArgumentException();
		}

		if (modeHandler != null) {
			modeHandler.modeExited();
		}
		modeHandler = newhandler;
		modeHandler.modeEntered();
	}

	public InteractionModeHandler getInteractionModeHandler(String modeName) {
		InteractionMode mode = imanager.getMode(modeName);
		if (mode == null) {
			return null;
		} else {
			return modeHandlerMap.get(mode);
		}
	}

	/**
	 * Gets the <code>Object</code> associated with the specified key.
	 * 
	 * @param key
	 *            a string containing the specified <code>key</code>
	 * @return the binding <code>Object</code> stored with this key; if there are no keys, it will
	 *         return <code>null</code>
	 * @see Action#getValue
	 */
	public Object getValue(String key) {
		return valueMap.get(key);
	}

	/**
	 * Sets the <code>Value</code> associated with the specified key.
	 * 
	 * @param key
	 *            the <code>String</code> that identifies the stored object
	 * @param newValue
	 *            the <code>Object</code> to store using this key
	 * @see Action#putValue
	 */
	public void putValue(String key, Object newValue) {
		if (valueMap.containsKey(key) && (newValue == null)) {
			// Remove the entry for key if newValue is null
			// else put in the newValue for key.
			valueMap.remove(key);
		} else {
			valueMap.put(key, newValue);
		}
	}

}
