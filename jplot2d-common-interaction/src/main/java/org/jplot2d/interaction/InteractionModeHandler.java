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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * An operation mode contains a list of available commands and configuration of how to trigger them.
 * 
 * @author Jingjing Li
 * 
 */
public class InteractionModeHandler {

	public static final String MODE_ENTERED_KEY = "MODE_ENTERED";

	private final InteractionHandler ihandler;

	private final InteractionMode imode;

	final Map<String, Object> valueMap = new HashMap<String, Object>();

	private final Map<MouseBehavior, MouseBehaviorHandler<?>> handlerMap = new HashMap<MouseBehavior, MouseBehaviorHandler<?>>();

	private final Map<ValueChangeBehavior, ValueChangeHandler<?>> vcHandlerMap = new HashMap<ValueChangeBehavior, ValueChangeHandler<?>>();

	public InteractionModeHandler(InteractionHandler ihandler, InteractionMode imode) {
		this.imode = imode;
		this.ihandler = ihandler;
	}

	public final InteractiveComp getInteractiveComp() {
		return ihandler.getInteractiveComp();
	}

	/**
	 * Returns the mode name.
	 * 
	 * @return the mode name
	 */
	public final InteractionMode getInteractionMode() {
		return imode;
	}

	void addMouseBehaviorHandler(MouseBehaviorHandler<?> handler) {
		handlerMap.put(handler.behavior, handler);
	}

	void addValueChangeHandler(ValueChangeHandler<?> handler) {
		vcHandlerMap.put(handler.behavior, handler);
	}

	public void mouseEntered(GenericMouseEvent e) {
		// do nothing
	}

	public void mouseExited(GenericMouseEvent e) {
		// do nothing
	}

	public void mouseClicked(GenericMouseEvent e) {
		handleMouseEvent(e, imode.clickMap);
	}

	public void mousePressed(GenericMouseEvent e) {
		handleMouseEvent(e, imode.pressMap);
	}

	public void mouseReleased(GenericMouseEvent e) {
		handleMouseEvent(e, imode.releaseMap);
	}

	public void mouseMoved(GenericMouseEvent e) {
		handleMouseEvent(e, imode.moveMap);
	}

	public void mouseDragged(GenericMouseEvent e) {
		handleMouseEvent(e, imode.dragMap);
	}

	public void mouseWheelMoved(GenericMouseEvent e) {
		for (Map.Entry<MouseWheelBehavior, MouseButtonCombination> me : imode.wheelMap.entrySet()) {
			MouseWheelBehavior behavior = me.getKey();
			MouseButtonCombination mbc = me.getValue();
			if (mbc.match(e)) {
				MouseBehaviorHandler<?> handler = handlerMap.get(behavior);
				if (handler.processMouseEvent(e)) {
					break;
				}
			}
		}
	}

	private void handleMouseEvent(GenericMouseEvent e,
			Map<MouseBehavior, MouseButtonCombination> behaviorMap) {
		for (Map.Entry<MouseBehavior, MouseButtonCombination> me : behaviorMap.entrySet()) {
			MouseBehavior behavior = me.getKey();
			MouseButtonCombination mbc = me.getValue();
			if (mbc.match(e)) {
				MouseBehaviorHandler<?> handler = handlerMap.get(behavior);
				if (handler.processMouseEvent(e)) {
					break;
				}
			}
		}
	}

	public void draw(Object graphics) {
		for (MouseBehaviorHandler<?> handler : handlerMap.values()) {
			if (handler instanceof VisualFeedbackDrawer) {
				((VisualFeedbackDrawer) handler).draw(graphics);
			}
		}
		for (ValueChangeHandler<?> handler : vcHandlerMap.values()) {
			if (handler instanceof VisualFeedbackDrawer) {
				((VisualFeedbackDrawer) handler).draw(graphics);
			}
		}
	}

	/**
	 * Gets the <code>Object</code> associated with the specified key.
	 * 
	 * @param key
	 *            a string containing the specified <code>key</code>
	 * @return the binding <code>Object</code> stored with this key; if there are no keys, it will
	 *         return <code>null</code>
	 */
	public Object getValue(String key) {
		Object result = valueMap.get(key);
		if (result == null) {
			result = ihandler.getValue(key);
		}
		return result;
	}

	/**
	 * Sets the <code>Value</code> associated with the specified key.
	 * 
	 * @param key
	 *            the <code>String</code> that identifies the stored object
	 * @param newValue
	 *            the <code>Object</code> to store using this key
	 */
	public void putValue(String key, Object newValue) {
		Object oldValue;
		if (valueMap.containsKey(key) && (newValue == null)) {
			// Remove the entry for key if newValue is null
			// else put in the newValue for key.
			oldValue = valueMap.remove(key);
		} else {
			oldValue = valueMap.put(key, newValue);
		}
		firePropertyChange(key, oldValue, newValue);
	}

	private void firePropertyChange(String key, Object oldValue, Object newValue) {
		if (oldValue == newValue) {
			return;
		}
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}
		PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this, key, oldValue,
				newValue);
		for (MouseBehaviorHandler<?> handler : handlerMap.values()) {
			if (handler instanceof PropertyChangeListener)
				((PropertyChangeListener) handler).propertyChange(propertyChangeEvent);
		}
		for (ValueChangeHandler<?> handler : vcHandlerMap.values()) {
			handler.propertyChange(propertyChangeEvent);
		}
	}

	public void modeEntered() {
		Object oldValue = valueMap.get(MODE_ENTERED_KEY);
		firePropertyChange(MODE_ENTERED_KEY, oldValue, Boolean.TRUE);
	}

	public void modeExited() {
		Object oldValue = valueMap.get(MODE_ENTERED_KEY);
		firePropertyChange(MODE_ENTERED_KEY, oldValue, Boolean.FALSE);
	}

	public String toString() {
		return imode.getName() + " handler";
	}

}
