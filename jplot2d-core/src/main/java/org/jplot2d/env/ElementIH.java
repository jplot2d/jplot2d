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
import java.util.Map;

import org.jplot2d.element.Component;
import org.jplot2d.element.Element;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ElementEx;
import org.jplot2d.element.impl.MultiParentElementEx;

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
	 * <p>
	 * <em>MUST</em> called within environment begin-end block.
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
			synchronized (Environment.getGlobalLock()) {
				return environment;
			}
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
		/* set(Element element) with join meaning */
		if (iinfo.isJoinElementMethod(method)) {
			invokeJoinElementMethod(method, args);
			return null;
		}
		/* set(Element element) with reference meaning */
		if (iinfo.isRefElementMethod(method)) {
			invokeSetRefElementMethod(method, args);
			return null;
		}
		/* set(Element, Element) with reference meaning */
		if (iinfo.isRef2ElementMethod(method)) {
			invokeSetRef2ElementMethod(method, args);
			return null;
		}

		environment.begin();
		try {

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
				environment.elementPropertyChanged((ElementEx) impl);
			}

		} finally {
			environment.endCommand();
		}

		return null;

	}

	private Object invokeGetCompMethod(Method method, Object[] args)
			throws Throwable {
		synchronized (Environment.getGlobalLock()) {
			environment.beginCommand(method.getName());
		}
		try {
			ElementEx compImpl = (ElementEx) method.invoke(impl, args);
			return environment.getProxy(compImpl);
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
				Array.set(result, i, environment.getProxy((ElementEx) Array
						.get(compImpls, i)));
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
		ComponentEx cimpl;

		Environment penv;
		Environment cenv;
		synchronized (Environment.getGlobalLock()) {
			// local safe copy
			penv = environment;
			cenv = ((Element) args[0]).getEnvironment();
			cenv.beginCommand("");
			cimpl = (ComponentEx) cproxy.getImpl();
			if (cimpl.getParent() != null) {
				cenv.end();
				throw new IllegalArgumentException("");
			}
			penv.beginCommand("");
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

		Throwable throwable = null;

		Environment penv;
		Environment cenv;
		synchronized (Environment.getGlobalLock()) {
			penv = environment;
			penv.beginCommand("");
			ComponentEx cimpl = (ComponentEx) cproxy.getImpl();

			// check removable
			Map<Element, Element> mooringMap = cimpl.getMooringMap();
			if (mooringMap.size() > 0) {
				String msg = "The removing is not allowed, because some element is required.\n";
				for (Map.Entry<Element, Element> me : mooringMap.entrySet()) {
					msg += "\t" + me.getKey() + " is required by "
							+ me.getValue() + "\n";
				}
				throwable = new IllegalStateException(msg);

			} else {

				// notify environment a component is going to be removed
				penv.componentRemoving(cimpl);

				// remove the component
				Object[] cargs = args.clone();
				cargs[0] = cimpl;
				try {
					method.invoke(impl, cargs);
				} catch (InvocationTargetException e) {
					throwable = e.getCause();
				}
			}

			// finish the removing
			if (throwable == null) {
				cenv = penv.componentRemoved((ComponentEx) impl, cimpl);
				// update environment for the removing component
				for (Element proxy : cenv.proxyMap.values()) {
					((ElementAddition) proxy).setEnvironment(cenv);
				}
			}
		}

		penv.endCommand();

		if (throwable != null) {
			throw throwable;
		}

	}

	/**
	 * @param proxy
	 * @param method
	 * @param object
	 *            the component to added
	 * @throws Throwable
	 */
	private void invokeJoinElementMethod(Method method, Object[] args)
			throws Throwable {
		ElementAddition cproxy = (ElementAddition) args[0];

		Throwable throwable = null;

		Environment penv;
		synchronized (Environment.getGlobalLock()) {
			penv = environment;
			Environment denv = ((Element) args[0]).getEnvironment();
			if (penv != denv) {
				throw new IllegalArgumentException(
						"Must belongd to the same environment");
			}

			penv.beginCommand("");
			ElementEx cimpl = (ElementEx) cproxy.getImpl();

			// join the new child
			MultiParentElementEx oldChildImpl = null;
			Object[] cargs = args.clone();
			cargs[0] = cimpl;
			try {
				oldChildImpl = (MultiParentElementEx) method
						.invoke(impl, cargs);
			} catch (InvocationTargetException e) {
				throwable = e.getCause();
			}

			// remove the old child if it's orphan
			if (throwable == null) {
				if (oldChildImpl.getParents().length == 0) {
					Environment cenv = penv.removeOrphan(oldChildImpl);
					// update environment for the removing component
					for (Element proxy : cenv.proxyMap.values()) {
						((ElementAddition) proxy).setEnvironment(cenv);
					}
				}
			}
		}

		penv.endCommand();

		if (throwable != null) {
			throw throwable;
		}

	}

	private void invokeSetRefElementMethod(Method method, Object[] args)
			throws Throwable {
		ElementAddition cproxy = (ElementAddition) args[0];
		Element cimpl;

		Environment env;
		synchronized (Environment.getGlobalLock()) {
			// local safe copy
			env = environment;
			Environment cenv = ((Element) args[0]).getEnvironment();
			if (env != cenv) {
				throw new IllegalArgumentException(
						"Must belongd to the same environment");
			}
			env.beginCommand("");
			cimpl = cproxy.getImpl();
		}
		try {
			Object[] cargs = args.clone();
			cargs[0] = cimpl;
			method.invoke(impl, cargs);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} finally {
			env.endCommand();
		}
	}

	private void invokeSetRef2ElementMethod(Method method, Object[] args)
			throws Throwable {
		Element cimpl0, cimpl1;

		Environment env;
		synchronized (Environment.getGlobalLock()) {
			// local safe copy
			env = environment;

			if (args[0] != null && ((Element) args[0]).getEnvironment() != env) {
				throw new IllegalArgumentException(
						"Must belongd to the same environment");
			}
			if (args[1] != null && ((Element) args[1]).getEnvironment() != env) {
				throw new IllegalArgumentException(
						"Must belongd to the same environment");
			}

			env.beginCommand("");
			cimpl0 = (args[0] == null) ? null : ((ElementAddition) args[0])
					.getImpl();
			cimpl1 = (args[1] == null) ? null : ((ElementAddition) args[1])
					.getImpl();
		}
		try {
			Object[] cargs = args.clone();
			cargs[0] = cimpl0;
			cargs[1] = cimpl1;
			method.invoke(impl, cargs);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} finally {
			env.endCommand();
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
