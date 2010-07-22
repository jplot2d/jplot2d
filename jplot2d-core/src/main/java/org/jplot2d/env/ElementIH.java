/**
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.jplot2d.element.Component;
import org.jplot2d.element.Element;

/**
 * @author Jingjing Li
 * 
 * @param <T>
 *            the element type
 */
public class ElementIH<T extends Element> implements InvocationHandler {

	private final InterfaceInfo iinfo;

	/**
	 * Guarded by EnvLock
	 */
	private T impl;

	/**
	 * Guarded by Env Global LOCK
	 */
	private Environment environment;

	/**
	 * @param impl
	 * @param class1
	 */
	public ElementIH(T impl, Class<T> clazz) {
		this.impl = impl;
		iinfo = InterfaceInfo.loadInterfaceInfo(clazz);
	}

	/**
	 * This method replace the impl with the given impl, when undoing or
	 * redoing.
	 * 
	 * @param impl
	 */
	void replaceImpl(T impl) {
		this.impl = impl;
	}

	public final Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		/* quick equals */
		if (method.getName().equals("equals")) {
			if (proxy == args[0]) {
				return Boolean.TRUE;
			}
			if (args[0] == null) {
				return Boolean.FALSE;
			}
		}

		/* listener methods */
		if (isListenerMethod(method)) {
			try {
				return method.invoke(impl, args);
			} catch (InvocationTargetException ex) {
				throw ex.getCause();
			}
		}

		/* ElementEx */
		if (method.getName().equals("getEnvironment")) {
			assert Thread.holdsLock(Environment.getGlobalLock());
			return environment;
		}
		if (method.getName().equals("setEnvironment")) {
			assert Thread.holdsLock(Environment.getGlobalLock());
			setEnvironment((Environment) args[0]);
			return null;
		}
		if (method.getName().equals("getImpl")) {
			return impl;
		}

		/* getElement(int n) */
		if (iinfo.isGetCompMethod(method)) {
			return invokeGetCompMethod(method, args);
		}
		/* addElement(Object comp) */
		if (iinfo.isAddCompMethod(method)) {
			invokeAddCompMethod(method, args);
			return null;
		}
		/* removeXxxx(Object comp) */
		if (iinfo.isRemoveCompMethod(method)) {
			invokeRemoveCompMethod(method, args);
			return null;
		}

		WarningException ex = null;
		synchronized (Environment.getGlobalLock()) {
			environment.beginCommand(method.getName());
		}
		try {

			/* element */
			if (method.getName().equals("getParent")) {
				return environment.getProxy(impl.getParent());
			}

			if (method.getName().equals("equals")) {
				ElementIH<?> h = (ElementIH<?>) Proxy
						.getInvocationHandler(args[0]);
				if (ElementIH.this.getClass() == h.getClass()) {
					return impl.equals(h.impl);
				} else {
					return Boolean.FALSE;
				}
			}
			if (method.getName().equals("hashCode")) {
				return impl.hashCode();
			}
			if (method.getName().equals("toString")) {
				return "Proxy(" + impl.toString() + ")";
			}

			/* property getter */
			if (iinfo.isPropReadMethod(method)) {
				return invokeGetter(method);
			}

			boolean setterValueChanged = false;
			try {
				if (iinfo.isPropWriteMethod(method)) {
					/* property setter method */
					setterValueChanged = invokeSetter(method, args[0]);
					if (!setterValueChanged) {
						return null;
					}
				} else {
					/* other method */
					invokeOther(method, args);
				}
			} catch (WarningException e) {
				ex = MultiException.addEx(ex, (WarningException) e.getCause());
			}

			if (iinfo.isRedrawMethod(method)) {
				environment.requireRedraw(impl);
			}
			if (setterValueChanged) {
				environment.elementPropertyChanged(impl);
			}

		} finally {
			environment.endCommand();
		}

		if (ex != null) {
			throw ex;
		}
		return null;

	}

	private Component invokeGetCompMethod(Method method, Object[] args)
			throws WarningException {
		synchronized (Environment.getGlobalLock()) {
			environment.beginCommand(method.getName());
		}
		try {
			Component compImpl = (Component) method.invoke(impl, args);
			return environment.getProxy(compImpl);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			} else {
				throw new RuntimeException(e.getCause());
			}
		} catch (IllegalArgumentException e) {
			throw new Error(e);
		} catch (IllegalAccessException e) {
			throw new Error(e);
		} finally {
			environment.endCommand();
		}

	}

	/**
	 * Called when addXxxx
	 * 
	 * @param proxy
	 * @param method
	 * @param object
	 *            the component to added
	 * @throws WarningException
	 */
	private void invokeAddCompMethod(Method method, Object[] args)
			throws WarningException {
		ElementEx cproxy = (ElementEx) args[0];
		Component cimpl = (Component) cproxy.getImpl();

		Environment penv;
		Environment cenv;
		synchronized (Environment.getGlobalLock()) {
			penv = environment;
			cenv = ((ElementEx) args[0]).getEnvironment();
			penv.beginCommand("");
			cenv.beginCommand("");
			// update environment for the adding component
			cproxy.setEnvironment(penv);
		}
		try {
			Object[] cargs = args.clone();
			cargs[0] = cimpl;
			method.invoke(impl, cargs);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof WarningException) {
				throw (WarningException) e.getCause();
			} else if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			} else {
				throw new RuntimeException(e.getCause());
			}
		} catch (IllegalArgumentException e) {
			throw new Error(e);
		} catch (IllegalAccessException e) {
			throw new Error(e);
		} finally {
			environment.componentAdded(cimpl, cenv.proxyMap);

			cenv.endCommand();
			penv.endCommand();
		}
	}

	/**
	 * @param proxy
	 * @param method
	 * @param object
	 *            the component to added
	 * @throws WarningException
	 */
	private void invokeRemoveCompMethod(Method method, Object[] args)
			throws WarningException {
		ElementEx cproxy = (ElementEx) args[0];
		Component cimpl = (Component) cproxy.getImpl();

		Environment penv;
		Environment cenv;
		synchronized (Environment.getGlobalLock()) {
			penv = environment;
			cenv = environment.createDummyEnvironment();
			penv.beginCommand("");
			cenv.beginCommand("");
			// update environment for the removing component
			cproxy.setEnvironment(cenv);
		}

		penv.componentRemoving(cimpl);
		try {
			Object[] cargs = args.clone();
			cargs[0] = cimpl;
			method.invoke(impl, cargs);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof WarningException) {
				throw (WarningException) e.getCause();
			} else if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			} else {
				throw new RuntimeException(e.getCause());
			}
		} catch (IllegalArgumentException e) {
			throw new Error(e);
		} catch (IllegalAccessException e) {
			throw new Error(e);
		} finally {
			Map<Element, Element> removedProxyMap = environment
					.componentRemoved(cimpl);
			cenv.proxyMap.clear();
			cenv.proxyMap.putAll(removedProxyMap);

			cenv.endCommand();
			penv.endCommand();
		}
	}

	protected Object invokeGetter(Method method) throws Throwable {
		try {
			return method.invoke(impl);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof WarningException) {
				throw e.getCause();
			} else if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			} else {
				throw new RuntimeException(e.getCause());
			}
		} catch (IllegalArgumentException e) {
			throw new Error(e);
		} catch (IllegalAccessException e) {
			throw new Error(e);
		}
	}

	/**
	 * This method invoke reader 1st, only when the setting value is different,
	 * the setter method is called.
	 * 
	 * @param method
	 *            the setter method
	 * @param arg
	 *            arguments
	 * @return <code>true</code> if the setter method is called.
	 * @throws Throwable
	 */
	protected boolean invokeSetter(Method method, Object arg) throws Throwable {
		// the old value is retrieved from concrete engine
		Object oldValue = null;
		Method reader = iinfo.getPropReadMethodByWriteMethod(method);
		oldValue = invokeGetter(reader);

		if (arg == oldValue) {
			return false;
		}
		if (arg != null && arg.equals(oldValue)) {
			return false;
		}

		try {
			method.invoke(impl, arg);
			return true;
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof WarningException) {
				throw (WarningException) e.getCause();
			} else if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			} else {
				throw new RuntimeException(e.getCause());
			}
		} catch (IllegalArgumentException e) {
			throw new Error(e);
		} catch (IllegalAccessException e) {
			throw new Error(e);
		} finally {
			if (impl instanceof Component
					&& method.getName().equals("setZOrder")) {
				environment.componentZOrderChanged((Component) impl);
			}
		}
	}

	protected Object invokeOther(Method method, Object... args) {
		try {
			return method.invoke(impl, args);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof WarningException) {
				return (WarningException) e.getCause();
			} else if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			} else {
				throw new RuntimeException(e.getCause());
			}
		} catch (IllegalArgumentException e) {
			throw new Error(e);
		} catch (IllegalAccessException e) {
			throw new Error(e);
		}
	}

	protected boolean isListenerMethod(Method method) {
		return iinfo.isListenerMethod(method);
	}

	/**
	 * Set the Environment to this element and all of its sub-elements.
	 */
	protected void setEnvironment(Environment env) {
		environment = env;
		// TODO: sub elements
	}

}
