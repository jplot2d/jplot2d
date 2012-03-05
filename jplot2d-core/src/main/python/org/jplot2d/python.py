#
from org.jplot2d.env import InterfaceInfo
from java.awt.geom import Dimension2D
from java.awt.geom import Point2D
from org.python.core import PyArray
from jarray import array

from org.jplot2d.data import *
from org.jplot2d.element import *
from org.jplot2d.layout import *
from org.jplot2d.sizing import *
from org.jplot2d.transform import *
from org.jplot2d.util import *


jplot2d_default_element_factory = ElementFactory.getInstance()

def jplot2d_set_prop(iinfo, obj, name, v):
#   print name, '=', v
    if isinstance(v, tuple):
        argType = iinfo.getPropWriteMethodType(name)
        if argType == Dimension2D:
            v = DoubleDimension2D(v[0], v[1])
        elif argType == Point2D:
            v = Point2D.Double(v[0], v[1])
    setattr(obj, name, v)

def plot(*args, **kwargs):
    p = jplot2d_default_element_factory.createPlot()
    
    plotinfo = InterfaceInfo.loadInterfaceInfo(Plot)
    for key in kwargs:
        if plotinfo.isWritableProp(key):
            jplot2d_set_prop(plotinfo, p, key, kwargs[key])
        else:
            raise AttributeError, "Plot has no attribute " + key
    
    return p


def axis(*args, **kwargs):
    return axes(1, *args, **kwargs)[0]

def axes(n, *args, **kwargs):
    axes = jplot2d_default_element_factory.createAxes(n)

    axisinfo = InterfaceInfo.loadInterfaceInfo(Axis)
    tminfo = InterfaceInfo.loadInterfaceInfo(AxisTickManager)
    txfinfo = InterfaceInfo.loadInterfaceInfo(AxisTransform)
    for key in kwargs:
        if axisinfo.isWritableProp(key):
            for axis in axes:
                jplot2d_set_prop(axisinfo, axis, key, kwargs[key])
        elif tminfo.isWritableProp(key):
            jplot2d_set_prop(tminfo, axes[0].tickManager, key, kwargs[key])
        elif txfinfo.isWritableProp(key):
            jplot2d_set_prop(tminfo, axes[0].tickManager.axisTransform, key, kwargs[key])
        else:
            raise AttributeError, "Axis has no attribute " + key

    return axes

def layer():
    return jplot2d_default_element_factory.createLayer()

def xygraphplotter(*args, **kwargs):
    gp = None
    
    if len(args) == 1:
        if isinstance(args[0], XYGraph) :
            gp = jplot2d_default_element_factory.createXYGraphPlotter(args[0]);
        elif isinstance(args[0], ArrayPair) :
            gp = jplot2d_default_element_factory.createXYGraphPlotter(args[0]);

    elif len(args) == 2:
        if isinstance(args[0], PyArray) and isinstance(args[1], PyArray) :
            gp = jplot2d_default_element_factory.createXYGraphPlotter(args[0], args[1]);
        elif isinstance(args[0], list) and isinstance(args[1], list) :
            return xygraphplotter(args[0], args[1], 'd', kwargs);
        elif isinstance(args[0], tuple) and isinstance(args[1], tuple) :
            return xygraphplotter(args[0], args[1], 'd', kwargs);

    elif len(args) == 3:
        if isinstance(args[0], ArrayPair) and isinstance(args[1], ArrayPair) and isinstance(args[2], ArrayPair) :
            gp = jplot2d_default_element_factory.createXYGraphPlotter(args[0], args[1], args[2]);
        elif (isinstance(args[0], list) or isinstance(args[0], tuple)) \
            and (isinstance(args[1], list) or isinstance(args[1], tuple)) and isinstance(args[2], str) :
                t = args[2]
                gp = jplot2d_default_element_factory.createXYGraphPlotter(array(args[0], t), array(args[1], t));

    elif len(args) == 6:
        atype = None
        for arg in args:
            if atype == None:
                if isinstance(arg, PyArray):
                    atype = 1
                elif isinstance(arg, list) or isinstance(arg, tuple):
                    atype = 2
                else:
                    atype = 0
                    break;
            elif atype == 1:
                if not isinstance(arg, PyArray):
                    atype = 0
                    break
            elif atype == 2:
                if not (isinstance(arg, list) or isinstance(arg, tuple)):
                    atype = 0
                    break
        
        if atype == 1:
            gp = jplot2d_default_element_factory.createXYGraphPlotter(*args);
        elif atype == 2:
            return xygraphplotter(*(args + ('d',)), **kwargs)
        
    elif len(args) == 7 and isinstance(args[6], str):
        atype = None
        for arg in args[0:6]:
            if atype == None:
                if isinstance(arg, list) or isinstance(arg, tuple):
                    atype = 2
                else:
                    atype = 0
                    break;
            elif atype == 2:
                if not (isinstance(arg, list) or isinstance(arg, tuple)):
                    atype = 0
                    break
        
        if atype == 2:
            t = args[6]
            gp = jplot2d_default_element_factory.createXYGraphPlotter \
                (array(args[0], t), array(args[1], t), array(args[2], t), array(args[3], t), array(args[4], t), array(args[5], t));

            
    if gp == None:
        raise TypeError, "illegal args"
    
    gpinfo = InterfaceInfo.loadInterfaceInfo(XYGraphPlotter)
    for key in kwargs:
        if gpinfo.isWritableProp(key):
            jplot2d_set_prop(gpinfo, gp, key, kwargs[key])
        else:
            raise AttributeError, "XYGraphPlotter has no attribute " + key

    return gp


def hlineannotation(y, *args, **kwargs):
    ann = jplot2d_default_element_factory.createHLineAnnotation(y)
        
    anninfo = InterfaceInfo.loadInterfaceInfo(HLineAnnotation)
    for key in kwargs:
        if anninfo.isWritableProp(key):
            jplot2d_set_prop(anninfo, ann, key, kwargs[key])
        else:
            raise AttributeError, "HLineAnnotation has no attribute " + key

    return ann
 
def vlineannotation(x, *args, **kwargs):
    ann = jplot2d_default_element_factory.createVLineAnnotation(x)
        
    anninfo = InterfaceInfo.loadInterfaceInfo(VLineAnnotation)
    for key in kwargs:
        if anninfo.isWritableProp(key):
            jplot2d_set_prop(anninfo, ann, key, kwargs[key])
        else:
            raise AttributeError, "VLineAnnotation has no attribute " + key

    return ann
 
def hstripannotation(start, end, *args, **kwargs):
    ann = jplot2d_default_element_factory.createHStripAnnotation(start, end)
        
    anninfo = InterfaceInfo.loadInterfaceInfo(HStripAnnotation)
    for key in kwargs:
        if anninfo.isWritableProp(key):
            jplot2d_set_prop(anninfo, ann, key, kwargs[key])
        else:
            raise AttributeError, "HStripAnnotation has no attribute " + key

    return ann
 
def vstripannotation(start, end, *args, **kwargs):
    ann = jplot2d_default_element_factory.createVStripAnnotation(start, end)
        
    anninfo = InterfaceInfo.loadInterfaceInfo(VStripAnnotation)
    for key in kwargs:
        if anninfo.isWritableProp(key):
            jplot2d_set_prop(anninfo, ann, key, kwargs[key])
        else:
            raise AttributeError, "VStripAnnotation has no attribute " + key

    return ann
 
 
 
def stroke(width, dash=None):
    return jplot2d_default_element_factory.createStroke(width, dash)



#
def setp(obj, *args, **kwargs):
    if isinstance(obj, Legend):
        iinfo = InterfaceInfo.loadInterfaceInfo(Legend)
        for key in kwargs:
            if iinfo.isWritableProp(key):
                jplot2d_set_prop(iinfo, obj, key, kwargs[key])
            else:
                raise AttributeError, "Legend has no attribute " + key
        

