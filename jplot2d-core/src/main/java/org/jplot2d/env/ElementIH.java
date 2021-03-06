/*
 * Copyright 2010-2015 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with jplot2d.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.env;

import org.jplot2d.element.Element;
import org.jplot2d.element.impl.ComponentEx;
import org.jplot2d.element.impl.ElementEx;
import org.jplot2d.element.impl.Joinable;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * This InvocationHandler intercept calls on proxy objects, and wrap the calls with environment lock.
 *
 * @author Jingjing Li
 */
public class ElementIH implements InvocationHandler {

    private final InterfaceInfo iinfo;
    /**
     * This field must be read within Environment Global LOCK
     */
    protected volatile Environment environment;
    /**
     * Guarded by environment
     */
    private ElementEx impl;

    /**
     * Construct a ElementIH
     *
     * @param impl  the implementation object
     * @param clazz the interface class
     */
    public ElementIH(ElementEx impl, Class<?> clazz) {
        this.impl = impl;
        iinfo = InterfaceInfo.loadInterfaceInfo(clazz);
    }

    /**
     * This method replace the implementation of a proxy with the given implementation, when undoing or redoing.
     * <p/>
     * This method must be called within a environment begin-end block.
     *
     * @param impl the new implementation
     */
    void replaceImpl(ElementEx impl) {
        this.impl = impl;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        switch (method.getName()) {
            case "equals":
                return proxy == args[0];
            case "hashCode":
                Environment env;
                env = environment;
                env.begin();
                try {
                    return impl.hashCode();
                } finally {
                    env.end();
                }
            case "toString":
                env = environment;
                env.begin();
                try {
                    return "Proxy@" + System.identityHashCode(proxy) + "(" + impl.toString() + ")";
                } finally {
                    env.end();
                }
            case "getEnvironment":
                return environment;
            case "setEnvironment":
                // assert environment.lock == null || environment.lock.isHeldByCurrentThread();
                environment = (Environment) args[0];
                return null;
            case "getImpl":
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
        /* set(Element element) as a reference. For example, layer reference an AxisTransform */
        if (iinfo.isRefElementMethod(method)) {
            invokeSetRefElementMethod(method, args);
            return null;
        }
        /* set(Element, Element) as references. For example, layer reference 2 AxisTransforms */
        if (iinfo.isRef2ElementMethod(method)) {
            invokeSetRef2ElementMethod(method, args);
            return null;
        }
        /* addComponent(Component comp, Element e1, Element2) */
        if (iinfo.isAddRef2Method(method)) {
            invokeAddRefMethod(method, args, 2);
            return null;
        }

        /* property getter */
        if (iinfo.isPropReadMethod(method)) {
            Environment env = environment;
            env.begin();
            try {
                return invokeGetter(method);
            } finally {
                env.end();
            }
        }

        Environment env = environment;
        env.beginCommand(method.getName());
        try {

            if (iinfo.isPropWriteMethod(method)) {
                /* property setter */
                env.logCommand(method, impl, args);
                boolean propChanged = invokeSetter(method, args);
                if (propChanged) {
                    env.elementPropertyChanged(impl);
                }
                return null;
            } else {
                /* other method */
                env.logCommand(method, impl, args);
                Object result = invokeOther(method, args);
                env.elementPropertyChanged(impl);
                return result;
            }
        } finally {
            env.endCommand();
        }

    }

    private Object invokeGetCompMethod(Method method, Object[] args) throws Throwable {
        Environment env = environment;
        env.begin();
        try {
            ElementEx compImpl = (ElementEx) method.invoke(impl, args);
            return env.getProxy(compImpl);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } finally {
            env.end();
        }
    }

    private Object invokeGetCompArrayMethod(Method method, Object[] args) throws Throwable {
        Environment env = environment;
        env.begin();
        try {
            Object compImpls = method.invoke(impl, args);
            int length = Array.getLength(compImpls);
            Object result = Array.newInstance(method.getReturnType().getComponentType(), length);
            for (int i = 0; i < length; i++) {
                Array.set(result, i, env.getProxy((ElementEx) Array.get(compImpls, i)));
            }
            return result;
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } finally {
            env.end();
        }
    }

    /**
     * Invoke a method to add an array of child components. The array of child components is the args[0].
     *
     * @param method the method to be invoked
     * @param args   the arguments
     * @throws Throwable
     */
    private void invokeAddCompArrayMethod(Method method, Object[] args) throws Throwable {

        if (((Object[]) args[0]).length == 0) {
            return;
        }

        Environment penv = environment;
        Object[] arg0 = (Object[]) args[0];
        Environment cenv = ((Element) arg0[0]).getEnvironment();
        for (int i = 1; i < arg0.length; i++) {
            if (cenv != ((Element) arg0[i]).getEnvironment()) {
                throw new IllegalArgumentException("The components to be added must belong to the same environment.");
            }
        }

        Class<?> arg0LcType = method.getParameterTypes()[0].getComponentType();
        Object cimpls = Array.newInstance(arg0LcType, arg0.length);
        cenv.beginCommand(method.getName());
        for (int i = 0; i < arg0.length; i++) {
            ComponentEx cimpl = (ComponentEx) ((ElementAddition) arg0[i]).getImpl();
            if (cimpl.getParent() != null) {
                cenv.endCommand();
                throw new IllegalArgumentException(
                        "At lease one of the components to be added already has a parent.");
            }
            Array.set(cimpls, i, cimpl);
        }

        penv.beginCommand(method.getName());
        penv.logCommand(method, impl, args);

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

        penv.endCommand();
        cenv.endCommand();
    }

    /**
     * Invoke a method to add a child component. The child component is the args[0].
     *
     * @param method the method to be invoked
     * @param args   the arguments
     * @throws Throwable
     */
    private void invokeAddCompMethod(Method method, Object[] args) throws Throwable {
        ElementAddition cproxy = (ElementAddition) args[0];
        ComponentEx cimpl;

        Environment penv = environment;
        Environment cenv = ((Element) args[0]).getEnvironment();
        cenv.beginCommand(method.getName());
        cimpl = (ComponentEx) cproxy.getImpl();
        if (cimpl.getParent() != null) {
            cenv.endCommand();
            throw new IllegalArgumentException("The component to be added already has a parent.");
        }
        penv.beginCommand(method.getName());
        penv.logCommand(method, impl, args);

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

        penv.endCommand();
        cenv.endCommand();
    }

    /**
     * Invoke a method to remove a element.
     *
     * @param method the method to be invoked
     * @param args   the component to removed
     * @throws Throwable
     */
    private void invokeRemoveCompMethod(Method method, Object[] args) throws Throwable {
        ElementAddition cproxy = (ElementAddition) args[0];

        Throwable throwable = null;

        Environment penv = environment;
        penv.beginCommand(method.getName());
        ComponentEx cimpl = (ComponentEx) cproxy.getImpl();

        if (cimpl.getParent() != impl) {
            throwable = new IllegalArgumentException("The component to be removed doesn't belong to this container.");

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
                penv.logCommand(method, impl, args);

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
            Environment nenv = penv.componentRemoved(cimpl);
            // update environment for the removing component
            nenv.begin();
            for (Element proxy : nenv.proxyMap.values()) {
                ((ElementAddition) proxy).setEnvironment(nenv);
            }
            nenv.end();
        }

        penv.endCommand();

        if (throwable != null) {
            throw throwable;
        }
    }

    /**
     * Invoke a method to set a join reference. Such as axis join a axis tick manager,
     * or axis tick manager join an AxisTransform, or an AxisTransform join an AxisRangeLockGroup.
     *
     * @param method the method to be invoked
     * @param args   the component to join
     * @throws Throwable
     */
    private void invokeJoinElementMethod(Method method, Object[] args) throws Throwable {
        if (args[0] == null) {
            throw new IllegalArgumentException("Null is not a valid argument.");
        }

        // the join target belongs to a dummy env, bring it to this env.
        boolean add = false;

        Environment env = environment;
        Environment cenv = ((Element) args[0]).getEnvironment();

        cenv.beginCommand(method.getName());
        ElementEx cimpl = ((ElementAddition) args[0]).getImpl();
        if (env != cenv) {
            if (cimpl instanceof Joinable) {
                if (((Joinable) cimpl).getPrim() != null) {
                    cenv.endCommand();
                    throw new IllegalArgumentException(
                            "The element to be referenced has been referenced by element from another environment.");
                }
            } else {
                cenv.endCommand();
                throw new IllegalArgumentException("The argument is not referenceable.");
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

        env.logCommand(method, impl, args);

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
            nenv.begin();
            for (Element proxy : nenv.proxyMap.values()) {
                ((ElementAddition) proxy).setEnvironment(nenv);
            }
            nenv.end();
        }

        env.endCommand();
        if (add) {
            cenv.endCommand();
        }

    }

    /**
     * Called when setting a reference to the element args[0]. The referred component must belong to the same
     * environment. For example, layer refer to x AxisTransform.
     *
     * @param method the method to be invoked
     * @param args   the arguments
     * @throws Throwable
     */
    private void invokeSetRefElementMethod(Method method, Object[] args) throws Throwable {
        if (args[0] == null) {
            throw new IllegalArgumentException("Null is not a valid argument.");
        }
        Element cimpl;

        Environment env = environment;
        Environment cenv = ((Element) args[0]).getEnvironment();
        if (env != cenv) {
            throw new IllegalArgumentException("Must belongs to the same environment");
        }
        env.beginCommand(method.getName());
        cimpl = ((ElementAddition) args[0]).getImpl();

        env.logCommand(method, impl, args);

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
     * Called when setting a reference to the element args[0] and args[1].
     * The referred component must belong to the same environment.
     * For example, layer refer to x AxisTransform and y AxisTransform.
     *
     * @param method the method to be invoked
     * @param args   the arguments
     * @throws Throwable
     */
    private void invokeSetRef2ElementMethod(Method method, Object[] args) throws Throwable {
        if (args[0] == null || args[1] == null) {
            throw new IllegalArgumentException("Null is not a valid argument.");
        }
        Element cimpl0, cimpl1;

        Environment env = environment;

        if (((Element) args[0]).getEnvironment() != env) {
            throw new IllegalArgumentException("Must belongs to the same environment");
        }
        if (((Element) args[1]).getEnvironment() != env) {
            throw new IllegalArgumentException("Must belongs to the same environment");
        }

        env.beginCommand(method.getName());
        cimpl0 = ((ElementAddition) args[0]).getImpl();
        cimpl1 = ((ElementAddition) args[1]).getImpl();

        env.logCommand(method, impl, args);

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
     * For example, plot can add a layer and set the x/y AxisTransform for the layer.
     *
     * @param method the method to be invoked
     * @param args   the arguments
     * @param nref   the number of references
     * @throws Throwable
     */
    private void invokeAddRefMethod(Method method, Object[] args, int nref) throws Throwable {
        ElementAddition cproxy = (ElementAddition) args[0];
        ComponentEx cimpl;
        Object[] cargs = args.clone();

        Environment penv = environment;
        Environment cenv = ((Element) args[0]).getEnvironment();
        cenv.beginCommand(method.getName());
        cimpl = (ComponentEx) cproxy.getImpl();
        if (cimpl.getParent() != null) {
            cenv.endCommand();
            throw new IllegalArgumentException("The component to be added already has a parent.");
        }
        penv.beginCommand(method.getName());
        penv.logCommand(method, impl, args);

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
     * @param method the setter method to be invoked
     * @param args   the arguments
     * @return <code>true</code> if the setter method is called.
     * @throws Throwable
     */
    protected boolean invokeSetter(Method method, Object[] args) throws Throwable {
        if (iinfo.isPropWriteDisabled(method)) {
            throw new UnsupportedOperationException("The property setter " + method.getName() + " is unsupported.");
        }

        // the old value is retrieved from concrete engine
        Method reader = iinfo.getPropReadMethodByWriteMethod(method);
        Object oldValue = invokeGetter(reader);

        if (args[0] == oldValue) {
            return false;
        }
        if (args[0] != null && args[0].equals(oldValue)) {
            return false;
        }

        try {
            method.invoke(impl, args[0]);
            return true;
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    protected Object invokeOther(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(impl, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

}
