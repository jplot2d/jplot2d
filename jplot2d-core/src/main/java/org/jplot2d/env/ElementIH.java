/**
 * Copyright 2010-2014 Jingjing Li.
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
import java.util.Map;

import org.jplot2d.element.Element;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ElementEx;
import org.jplot2d.element.impl.InvokeStep;
import org.jplot2d.element.impl.Joinable;

/**
 * This InvocationHandler intercept calls on proxy objects, and wrap the calls with environment lock.
 * 
 * @author Jingjing Li
 */
public class ElementIH implements InvocationHandler {

	private final InterfaceInfo iinfo;

	/**
	 * Guarded by EnvLock
	 */
	private ElementEx impl;

	/**
	 * This field must be read within Environment Global LOCK
	 */
	private volatile Environment environment;

	/**
	 * @param impl
	 * @param clazz
	 */
	public ElementIH(ElementEx impl, Class<?> clazz) {
		this.impl = impl;
		iinfo = InterfaceInfo.loadInterfaceInfo(clazz);
	}

	/**
	 * This method replace the impl with the given impl, when undoing or redoing.
	 * <p>
	 * <em>MUST</em> called within environment begin-end block.
	 * 
	 * @param impl
	 */
	void replaceImpl(ElementEx impl) {
		this.impl = impl;
	}

	public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

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
		if (iinfo.isListenerMethod(method)) {
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
			environment = (Environment) args[0];
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
			if (args[0] instanceof Object[]) {
				invokeAddCompArrayMethod(method, args);
			} else {
				invokeAddCompMethod(method, args);
			}
			return null;
		}
		/* removeComponent(Component comp) */
		if (iinfo.isRemoveCompMethod(method)) {
			invokeRemoveCompMethod(method, args);
			return null;
		}
		/* set(Element) as a join reference. For example, axis join an axis tick manager */
		if (iinfo.isJoinElementMethod(method)) {
			invokeJoinElementMethod(method, args);
			return null;
		}
		/* set(Element element) as a reference. For example, layer reference an axis range manager */
		if (iinfo.isRefElementMethod(method)) {
			invokeSetRefElementMethod(method, args);
			return null;
		}
		/* set(Element, Element) as references. For example, layer reference 2 axis range managers */
		if (iinfo.isRef2ElementMethod(method)) {
			invokeSetRef2ElementMethod(method, args);
			return null;
		}
		/* addComponent(Component comp, Element e1, Elemente2) */
		if (iinfo.isAddRef2Method(method)) {
			invokeAddRefMethod(method, args, 2);
			return null;
		}

		environment.begin();
		try {

			if (method.getName().equals("equals")) {
				return proxy == args[0];
			}
			if (method.getName().equals("hashCode")) {
				return impl.hashCode();
			}
			if (method.getName().equals("toString")) {
				return "Proxy@" + System.identityHashCode(proxy) + "(" + impl.toString() + ")";
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
			if (iinfo.isPropWriteMethod(method)) {
				/* property setter method */
				boolean propChanged = invokeSetter(method, args);
				if (propChanged) {
					// fire PropertyChanged
					environment.elementPropertyChanged((ElementEx) impl);
				}
			} else {
				/* other method */
				invokeOther(method, args);
				// fire PropertyChanged
				environment.elementPropertyChanged((ElementEx) impl);
			}

		} finally {
			environment.endCommand();
		}

		return null;

	}

	private Object invokeGetCompMethod(Method method, Object[] args) throws Throwable {
		synchronized (Environment.getGlobalLock()) {
			environment.begin();
		}
		try {
			ElementEx compImpl = (ElementEx) method.invoke(impl, args);
			return environment.getProxy(compImpl);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} finally {
			environment.end();
		}
	}

	private Object invokeGetCompArrayMethod(Method method, Object[] args) throws Throwable {
		synchronized (Environment.getGlobalLock()) {
			environment.begin();
		}
		try {
			Object compImpls = method.invoke(impl, args);
			int length = Array.getLength(compImpls);
			Object result = Array.newInstance(method.getReturnType().getComponentType(), length);
			for (int i = 0; i < length; i++) {
				Array.set(result, i, environment.getProxy((ElementEx) Array.get(compImpls, i)));
			}
			return result;
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} finally {
			environment.end();
		}
	}

	/**
	 * Called when adding a array of child components. The array of child components is the 1st argument of the calling
	 * method.
	 * 
	 * @param method
	 * @param args
	 *            the arguments
	 * @throws Throwable
	 */
	private void invokeAddCompArrayMethod(Method method, Object[] args) throws Throwable {

		if (((Object[]) args[0]).length == 0) {
			return;
		}

		Environment penv;
		Environment cenv;
		synchronized (Environment.getGlobalLock()) {
			// local safe copy
			penv = environment;
			Object[] arg0 = (Object[]) args[0];
			cenv = ((Element) arg0[0]).getEnvironment();
			for (int i = 1; i < arg0.length; i++) {
				if (cenv != ((Element) arg0[i]).getEnvironment()) {
					throw new IllegalArgumentException("The components to be added must belong to the same envoriment.");
				}
			}

			Class<?> arg0LcType = method.getParameterTypes()[0].getComponentType();
			Object cimpls = Array.newInstance(arg0LcType, arg0.length);
			cenv.beginCommand(method.getName());
			for (int i = 0; i < arg0.length; i++) {
				ComponentEx cimpl = (ComponentEx) ((ElementAddition) arg0[i]).getImpl();
				if (cimpl.getParent() != null) {
					cenv.end();
					throw new IllegalArgumentException(
							"At lease one of the components to be added already has a parent.");
				}
				Array.set(cimpls, i, cimpl);
			}

			penv.beginCommand(method.getName());

			logCommand(method, args);

			Object[] cargs = args.clone();
			cargs[0] = cimpls;
			try {
				method.invoke(impl, cargs);
			} catch (InvocationTargetException e) {
				penv.endCommand();
				cenv.endCommand();
				throw e.getCause();
			}

			// update environment for all adding components
			for (Element proxy : cenv.proxyMap.values()) {
				((ElementAddition) proxy).setEnvironment(penv);
			}
			for (int i = 0; i < Array.getLength(cimpls); i++) {
				penv.componentAdded((ComponentEx) Array.get(cimpls, i), cenv);
			}
		}

		penv.endCommand();
		cenv.endCommand();
	}

	/**
	 * Called when adding a child component. The child component is the 1st argument of the calling method.
	 * 
	 * @param method
	 * @param args
	 *            the arguments
	 * @throws Throwable
	 */
	private void invokeAddCompMethod(Method method, Object[] args) throws Throwable {
		ElementAddition cproxy = (ElementAddition) args[0];
		ComponentEx cimpl;

		Environment penv;
		Environment cenv;
		synchronized (Environment.getGlobalLock()) {
			// local safe copy
			penv = environment;
			cenv = ((Element) args[0]).getEnvironment();
			cenv.beginCommand(method.getName());
			cimpl = (ComponentEx) cproxy.getImpl();
			if (cimpl.getParent() != null) {
				cenv.end();
				throw new IllegalArgumentException("The component to be added already has a parent.");
			}
			penv.beginCommand(method.getName());

			logCommand(method, args);

			Object[] cargs = args.clone();
			cargs[0] = cimpl;
			try {
				method.invoke(impl, cargs);
			} catch (InvocationTargetException e) {
				penv.endCommand();
				cenv.endCommand();
				throw e.getCause();
			}

			// update environment for all adding components
			for (Element proxy : cenv.proxyMap.values()) {
				((ElementAddition) proxy).setEnvironment(penv);
			}
			penv.componentAdded(cimpl, cenv);
		}

		penv.endCommand();
		cenv.endCommand();
	}

	/**
	 * @param method
	 * @param object
	 *            the component to added
	 * @throws Throwable
	 */
	private void invokeRemoveCompMethod(Method method, Object[] args) throws Throwable {
		ElementAddition cproxy = (ElementAddition) args[0];

		Throwable throwable = null;

		Environment penv;
		Environment cenv;
		synchronized (Environment.getGlobalLock()) {
			penv = environment;
			penv.beginCommand(method.getName());
			ComponentEx cimpl = (ComponentEx) cproxy.getImpl();

			if (cimpl.getParent() != impl) {
				throwable = new IllegalArgumentException("The component to be removed dosn't belong to this container.");

			} else {
				// check removable
				Map<Element, Element> mooringMap = cimpl.getMooringMap();
				if (mooringMap.size() > 0) {
					String msg = "The removing is not allowed, because some element is required.\n";
					for (Map.Entry<Element, Element> me : mooringMap.entrySet()) {
						msg += "\t" + me.getKey() + " is required by " + me.getValue() + "\n";
					}
					throwable = new IllegalStateException(msg);

				} else {

					logCommand(method, args);

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
			}

			// finish the removing
			if (throwable == null) {
				cenv = penv.componentRemoved(cimpl);
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
	 * Sets args[0] as a join reference. Such as axis join a axis tick manager, or axis tick manager join a axis range
	 * manager, or axis range manager join a axis lock group.
	 * 
	 * @param method
	 * @param object
	 *            the component to added
	 * @throws Throwable
	 */
	private void invokeJoinElementMethod(Method method, Object[] args) throws Throwable {
		if (args[0] == null) {
			throw new IllegalArgumentException("Null is not a valid argument.");
		}

		// the join target belongs to a dummy env, bring it to this env.
		boolean add = false;

		ElementEx cimpl;
		Environment env;
		Environment cenv;
		synchronized (Environment.getGlobalLock()) {
			env = environment;
			cenv = ((Element) args[0]).getEnvironment();

			cenv.beginCommand(method.getName());
			cimpl = ((ElementAddition) args[0]).getImpl();
			if (env != cenv) {
				if (cimpl instanceof Joinable) {
					if (((Joinable) cimpl).getPrim() != null) {
						cenv.end();
						throw new IllegalArgumentException(
								"The element to be referenced has been referenced by element from another environment.");
					}
				} else {
					cenv.end();
					throw new IllegalArgumentException("The argument is not referencable.");
				}
				add = true;
				env.beginCommand(method.getName());
			}

			Method reader = iinfo.getPropReadMethodByWriteMethod(method);
			Object oldRef = invokeGetter(reader);
			if (cimpl == oldRef) {
				if (add) {
					cenv.endCommand();
				}
				env.endCommand();
				return;
			}

			logCommand(method, args);

			// join the new child
			Object[] cargs = args.clone();
			cargs[0] = cimpl;
			try {
				method.invoke(impl, cargs);
			} catch (InvocationTargetException e) {
				env.endCommand();
				if (add) {
					cenv.endCommand();
				}
				throw e.getCause();
			}

			// update environment for all adding components
			for (Element proxy : cenv.proxyMap.values()) {
				((ElementAddition) proxy).setEnvironment(env);
			}
			if (add) {
				env.elementAdded(cimpl, cenv);
			}
			// remove the old child if it's orphan
			if (oldRef != null && ((Joinable) oldRef).getPrim() == null) {
				Environment nenv = env.componentRemoved((ElementEx) oldRef);
				// update environment for the removing component
				for (Element proxy : nenv.proxyMap.values()) {
					((ElementAddition) proxy).setEnvironment(nenv);
				}
			}
		}

		env.endCommand();
		if (add) {
			cenv.endCommand();
		}

	}

	/**
	 * Called when setting a reference to the element args[0]. The referred component must belong to the same
	 * environment. For example, layer refer to x axis range manager.
	 * 
	 * @param method
	 * @param args
	 *            the arguments
	 * @throws Throwable
	 */
	private void invokeSetRefElementMethod(Method method, Object[] args) throws Throwable {
		if (args[0] == null) {
			throw new IllegalArgumentException("Null is not a valid argument.");
		}
		Element cimpl;

		Environment env;
		synchronized (Environment.getGlobalLock()) {
			// local safe copy
			env = environment;
			Environment cenv = ((Element) args[0]).getEnvironment();
			if (env != cenv) {
				throw new IllegalArgumentException("Must belongd to the same environment");
			}
			env.beginCommand(method.getName());
			cimpl = ((ElementAddition) args[0]).getImpl();
		}

		logCommand(method, args);

		Object[] cargs = args.clone();
		cargs[0] = cimpl;
		try {
			method.invoke(impl, cargs);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} finally {
			env.endCommand();
		}
	}

	/**
	 * Called when setting a reference to the element args[0] and args[1]. The referred component must belong to the
	 * same environment. For example, layer refer to x axis range manager and y axis range manager.
	 * 
	 * @param method
	 * @param args
	 *            the arguments
	 * @throws Throwable
	 */
	private void invokeSetRef2ElementMethod(Method method, Object[] args) throws Throwable {
		if (args[0] == null || args[1] == null) {
			throw new IllegalArgumentException("Null is not a valid argument.");
		}
		Element cimpl0, cimpl1;

		Environment env;
		synchronized (Environment.getGlobalLock()) {
			// local safe copy
			env = environment;

			if (((Element) args[0]).getEnvironment() != env) {
				throw new IllegalArgumentException("Must belongd to the same environment");
			}
			if (((Element) args[1]).getEnvironment() != env) {
				throw new IllegalArgumentException("Must belongd to the same environment");
			}

			env.beginCommand(method.getName());
			cimpl0 = ((ElementAddition) args[0]).getImpl();
			cimpl1 = ((ElementAddition) args[1]).getImpl();
		}

		logCommand(method, args);

		Object[] cargs = args.clone();
		cargs[0] = cimpl0;
		cargs[1] = cimpl1;
		try {
			method.invoke(impl, cargs);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} finally {
			env.endCommand();
		}
	}

	/**
	 * Called when adding a component and set n references at the same time.
	 * 
	 * @param method
	 * @param args
	 * @param nref
	 * @throws Throwable
	 */
	private void invokeAddRefMethod(Method method, Object[] args, int nref) throws Throwable {
		ElementAddition cproxy = (ElementAddition) args[0];
		ComponentEx cimpl;
		Object[] cargs = args.clone();

		Environment penv;
		Environment cenv;
		synchronized (Environment.getGlobalLock()) {
			// local safe copy
			penv = environment;
			cenv = ((Element) args[0]).getEnvironment();
			cenv.beginCommand(method.getName());
			cimpl = (ComponentEx) cproxy.getImpl();
			if (cimpl.getParent() != null) {
				cenv.end();
				throw new IllegalArgumentException("The component to be added already has a parent.");
			}
			penv.beginCommand(method.getName());

			logCommand(method, args);

			// update environment for all adding components
			for (Element proxy : cenv.proxyMap.values()) {
				((ElementAddition) proxy).setEnvironment(penv);
			}
			for (int i = 1; i <= nref; i++) {
				if (((Element) args[i]).getEnvironment() != penv) {
					throw new IllegalArgumentException("The argument " + args[i]
							+ " must belong to the environment of the container.");
				}
				cargs[i] = (args[i] == null) ? null : ((ElementAddition) args[i]).getImpl();
			}
		}

		cargs[0] = cimpl;
		try {
			method.invoke(impl, cargs);
			penv.componentAdded(cimpl, cenv);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} finally {
			penv.endCommand();
			cenv.endCommand();
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
	 * This method invoke reader 1st, only when the setting value is different, the setter method is called.
	 * 
	 * @param method
	 *            the setter method
	 * @param args
	 *            arguments
	 * @return <code>true</code> if the setter method is called.
	 * @throws Throwable
	 */
	protected boolean invokeSetter(Method method, Object[] args) throws Throwable {
		if (iinfo.isPropWriteDisabled(method)) {
			throw new UnsupportedOperationException("The property setter " + method.getName() + " is unsupported.");
		}

		// the old value is retrieved from concrete engine
		Object oldValue = null;
		Method reader = iinfo.getPropReadMethodByWriteMethod(method);
		oldValue = invokeGetter(reader);

		if (args[0] == oldValue) {
			return false;
		}
		if (args[0] != null && args[0].equals(oldValue)) {
			return false;
		}

		logCommand(method, args);

		try {
			method.invoke(impl, args[0]);
			return true;
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} finally {

		}
	}

	protected Object invokeOther(Method method, Object[] args) throws Throwable {

		logCommand(method, args);

		try {
			return method.invoke(impl, args);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	/**
	 * Log a command invocation. The logging is protected by environment.
	 * 
	 * @param method
	 *            the command method
	 * @param args
	 *            plain java object or Element proxy object
	 */
	private void logCommand(Method method, Object[] args) {
		StringBuilder sb = new StringBuilder();

		fillElementExpString(sb, environment, impl);
		sb.append(".");
		sb.append(method.getName());
		sb.append("(");
		if (args != null && args.length > 0) {
			fillArgString(sb, args[0]);
			for (int i = 1; i < args.length; i++) {
				sb.append(", ");
				fillArgString(sb, args[i]);
			}
		}
		sb.append(")");

		if (environment.cmdLogger != null) {
			environment.cmdLogger.log(sb.toString());
		}
	}

	/**
	 * Fills the argument object into the given stringBuilder
	 * 
	 * @param sb
	 *            the string builder to fill in
	 * @param arg
	 *            the argument object
	 */
	private static void fillArgString(StringBuilder sb, Object arg) {
		if (arg instanceof Element) {
			fillElementExpString(sb, ((Element) arg).getEnvironment(), ((ElementAddition) arg).getImpl());
		} else if (arg instanceof Element[]) {
			sb.append("[");
			if (((Element[]) arg).length > 0) {
				fillArgString(sb, ((Element[]) arg)[0]);
				for (int i = 1; i < ((Element[]) arg).length; i++) {
					sb.append(", ");
					fillArgString(sb, ((Element[]) arg)[i]);
				}
			}
			sb.append("]");
		} else if (arg instanceof String) {
			sb.append("\"");
			sb.append(arg);
			sb.append("\"");
		} else {
			sb.append(arg);
		}
	}

	/**
	 * Fills into a string builder to represent how to get the object from it's ancestor proxy.
	 * 
	 * @param sb
	 *            the string builder to fill in
	 * @param env
	 *            the environment that contains the giving obj
	 * @param obj
	 *            the object unpacked from proxy
	 */
	private static void fillElementExpString(StringBuilder sb, Environment env, Object obj) {

		if (obj instanceof ElementEx) {
			ElementEx parent;
			if (obj instanceof Joinable) {
				parent = ((Joinable) obj).getPrim();
			} else {
				parent = ((ElementEx) obj).getParent();
			}

			InvokeStep ivs = null;
			if (parent != null) {
				ivs = ((ElementEx) obj).getInvokeStepFormParent();
			}

			if (ivs != null) {
				fillElementExpString(sb, env, parent);
				sb.append(".");
				sb.append(ivs.getMethod().getName());
				sb.append("(");
				if (ivs.getIndex() >= 0) {
					sb.append(ivs.getIndex());
				}
				sb.append(")");
				return;
			} else {
				sb.append(String.valueOf(env.getProxy((ElementEx) obj)));
				return;
			}
		} else {
			sb.append(obj.toString());
			return;
		}
	}
}
