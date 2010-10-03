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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.jplot2d.element.Component;
import org.jplot2d.element.Element;
import org.jplot2d.element.impl.ComponentEx;

/**
 * This InvocationHandler intercept calls on proxy objects, and wrap the calls
 * with environment lock.
 * 
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
	private volatile Environment environment;

	/**
	 * @param impl
	 * @param clazz
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

		/* getComponent(int n) */
		if (iinfo.isGetCompMethod(method)) {
			return invokeGetCompMethod(method, args);
		}
		/* getComponents() */
		if (iinfo.isGetCompArrayMethod(method)) {
			return invokeGetCompArrayMethod(method, args);
		}
		/* addComponent(Component comp) */
		if (iinfo.isAddCompMethod(method)) {
			invokeAddCompMethod(method, args);
			return null;
		}
		/* removeComponent(Component comp) */
		if (iinfo.isRemoveCompMethod(method)) {
			invokeRemoveCompMethod(method, args);
			return null;
		}

		environment.begin();
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
		} finally {
			environment.end();
		}

		environment.beginCommand(method.getName());
		try {
			boolean setterValueChanged = false;
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

			if (setterValueChanged) {
				environment.elementPropertyChanged(impl);
			}

		} finally {
			environment.endCommand();
		}

		return null;

	}

	private Component invokeGetCompMethod(Method method, Object[] args)
			throws Throwable {
		synchronized (Environment.getGlobalLock()) {
			environment.beginCommand(method.getName());
		}
		try {
			Component compImpl = (Component) method.invoke(impl, args);
			return (Component) environment.getProxy(compImpl);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} finally {
			environment.endCommand();
		}
	}

	private Object invokeGetCompArrayMethod(Method method, Object[] args)
			throws Throwable {
		synchronized (Environment.getGlobalLock()) {
			environment.beginCommand(method.getName());
		}
		try {
			Object compImpls = method.invoke(impl, args);
			int length = Array.getLength(compImpls);
			Object result = Array.newInstance(method.getReturnType()
					.getComponentType(), length);
			for (int i = 0; i < length; i++) {
				Array.set(result, i, environment.getProxy((Element) Array.get(
						compImpls, i)));
			}
			return result;
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} finally {
			environment.endCommand();
		}
	}

	/**
	 * Called when adding a child component. The child component is the 1st
	 * argument of the calling method.
	 * 
	 * @param method
	 * @param args
	 *            the arguments
	 * @throws Throwable
	 */
	private void invokeAddCompMethod(Method method, Object[] args)
			throws Throwable {
		ElementAddition cproxy = (ElementAddition) args[0];
		ComponentEx cimpl = (ComponentEx) cproxy.getImpl();

		Environment penv;
		Environment cenv;
		synchronized (Environment.getGlobalLock()) {
			// local safe copy
			penv = environment;
			cenv = ((ElementAddition) args[0]).getEnvironment();
			penv.beginCommand("");
			cenv.beginCommand("");
			// update environment for all adding components
			for (Element proxy : cenv.proxyMap.values()) {
				((ElementAddition) proxy).setEnvironment(penv);
			}
		}
		try {
			Object[] cargs = args.clone();
			cargs[0] = cimpl;
			method.invoke(impl, cargs);
			penv.componentAdded(cimpl, cenv);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} finally {
			cenv.endCommand();
			penv.endCommand();
		}
	}

	/**
	 * @param proxy
	 * @param method
	 * @param object
	 *            the component to added
	 * @throws Throwable
	 */
	private void invokeRemoveCompMethod(Method method, Object[] args)
			throws Throwable {
		ElementAddition cproxy = (ElementAddition) args[0];
		ComponentEx cimpl = (ComponentEx) cproxy.getImpl();

		Environment penv;
		Environment cenv;
		synchronized (Environment.getGlobalLock()) {
			penv = environment;
			penv.beginCommand("");
			cenv = penv.componentRemoving(cimpl);
			cenv.beginCommand("");
			// update environment for the removing component
			for (Element proxy : cenv.proxyMap.values()) {
				((ElementAddition) proxy).setEnvironment(cenv);
			}
		}

		try {
			Object[] cargs = args.clone();
			cargs[0] = cimpl;
			method.invoke(impl, cargs);
			penv.componentRemoved(cimpl);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} finally {
			cenv.endCommand();
			penv.endCommand();
		}
	}

	protected Object invokeGetter(Method method) throws Throwable {
		try {
			return method.invoke(impl);
		} catch (InvocationTargetException e) {
			throw e.getCause();
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
			throw e.getCause();
		} finally {
			if (impl instanceof Component
					&& method.getName().equals("setZOrder")) {
				environment.componentZOrderChanged((ComponentEx) impl);
			}
		}
	}

	protected Object invokeOther(Method method, Object... args)
			throws Throwable {
		try {
			return method.invoke(impl, args);
		} catch (InvocationTargetException e) {
			throw e.getCause();
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
	}

}
