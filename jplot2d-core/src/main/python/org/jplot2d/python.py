#
from org.jplot2d.env import InterfaceInfo
from org.jplot2d.axtype import *
from org.jplot2d.data import *
from org.jplot2d.element import *
from org.jplot2d.element.AxisPosition import *
from org.jplot2d.element.XYGraph.ChartType import *
from org.jplot2d.layout import *
from org.jplot2d.sizing import *
from org.jplot2d.transform.TransformType import *
from org.jplot2d.util import *

from java.awt import Color
from java.awt.Color import *
from java.awt import Paint
from java.awt.geom import Dimension2D
from java.awt.geom import Point2D
from org.python.core import PyArray
from jarray import array


jplot2d_default_element_factory = ElementFactory.getInstance()

def jplot2d_set_prop(iinfo, obj, name, v):
#   print name, '=', v
    if isinstance(v, tuple):
        argType = iinfo.getPropWriteMethodType(name)
        if argType == Dimension2D:
            v = DoubleDimension2D(v[0], v[1])
        elif argType == Point2D:
            v = Point2D.Double(v[0], v[1])
        elif argType == Range:
            v = Range.Double(v[0], v[1])
        elif argType == Paint and len(v) == 3:
            v = Color(*v)
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

def subplot(*args, **kwargs):
    p = jplot2d_default_element_factory.createSubplot()
    
    plotinfo = InterfaceInfo.loadInterfaceInfo(Plot)
    for key in kwargs:
        if plotinfo.isWritableProp(key):
            jplot2d_set_prop(plotinfo, p, key, kwargs[key])
        else:
            raise AttributeError, "Plot has no attribute " + key
    
    return p

def title(text, *args, **kwargs):
    title = jplot2d_default_element_factory.createTitle(text)
    
    iinfo = InterfaceInfo.loadInterfaceInfo(Title)
    for key in kwargs:
        if iinfo.isWritableProp(key):
            jplot2d_set_prop(iinfo, title, key, kwargs[key])
        else:
            raise AttributeError, "Title has no attribute " + key
        
    return title


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

def layer(*args, **kwargs):
    layer = jplot2d_default_element_factory.createLayer()
    
    iinfo = InterfaceInfo.loadInterfaceInfo(Layer)
    for key in kwargs:
        if iinfo.isWritableProp(key):
            jplot2d_set_prop(iinfo, layer, key, kwargs[key])
        else:
            raise AttributeError, "Layer has no attribute " + key

    for arg in args:
        if isinstance(arg, Graph):
            layer.addGraph(arg)
        else:
            raise TypeError, "Cannot add " + str(type(arg)) + " to layer."

    return layer


def xygraph(*args, **kwargs):
    gp = None
    
    if len(args) == 1:
        if isinstance(args[0], XYGraph) :
            gp = jplot2d_default_element_factory.createXYGraph(args[0]);
        elif isinstance(args[0], ArrayPair) :
            gp = jplot2d_default_element_factory.createXYGraph(args[0]);

    elif len(args) == 2:
        if isinstance(args[0], PyArray) and isinstance(args[1], PyArray) :
            gp = jplot2d_default_element_factory.createXYGraph(args[0], args[1]);
        elif (isinstance(args[0], list) or isinstance(args[0], tuple)) \
            and (isinstance(args[1], list) or isinstance(args[1], tuple)):
            return xygraph(args[0], args[1], 'd', **kwargs);

    elif len(args) == 3:
        if isinstance(args[0], ArrayPair) and isinstance(args[1], ArrayPair) and isinstance(args[2], ArrayPair) :
            gp = jplot2d_default_element_factory.createXYGraph(args[0], args[1], args[2]);
        elif (isinstance(args[0], list) or isinstance(args[0], tuple)) \
            and (isinstance(args[1], list) or isinstance(args[1], tuple)) and isinstance(args[2], str) :
                t = args[2]
                gp = jplot2d_default_element_factory.createXYGraph(array(args[0], t), array(args[1], t));

    elif len(args) == 6:
        return xygraph(*(args + ('d',)), **kwargs)
        
    elif len(args) == 7 and isinstance(args[6], str):
        argserror = 0
        ali = [None] * 6 
        for i in range(0, 6):
            if isinstance(args[i], PyArray):
                ali[i] = args[i]
            elif isinstance(args[i], list) or isinstance(args[i], tuple):
                ali[i] = array(args[i], args[6])
            elif not args[i] == None:
                argserror = 1
                break;
        
        if (argserror == 0):
            gp = jplot2d_default_element_factory.createXYGraph(*ali);

            
    if gp == None:
        amsg = ''
        for arg in args:
            amsg += str(type(arg))
        raise TypeError, "illegal args " + amsg
    
    gpinfo = InterfaceInfo.loadInterfaceInfo(XYGraph)
    for key in kwargs:
        if gpinfo.isWritableProp(key):
            jplot2d_set_prop(gpinfo, gp, key, kwargs[key])
        else:
            raise AttributeError, "XYGraph has no attribute " + key

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
 
def symbolannotation(*args, **kwargs):
    ann = jplot2d_default_element_factory.createSymbolAnnotation(*args)
        
    anninfo = InterfaceInfo.loadInterfaceInfo(SymbolAnnotation)
    for key in kwargs:
        if anninfo.isWritableProp(key):
            jplot2d_set_prop(anninfo, ann, key, kwargs[key])
        else:
            raise AttributeError, "SymbolAnnotation has no attribute " + key

    return ann

def stroke(width, dash=None):
    return jplot2d_default_element_factory.createStroke(width, dash)



# set property for the given obj
def setp(obj, *args, **kwargs):
    if isinstance(obj, Legend):
        iinfo = InterfaceInfo.loadInterfaceInfo(Legend)
        for key in kwargs:
            if iinfo.isWritableProp(key):
                jplot2d_set_prop(iinfo, obj, key, kwargs[key])
            else:
                raise AttributeError, obj + " has no attribute " + key
        

