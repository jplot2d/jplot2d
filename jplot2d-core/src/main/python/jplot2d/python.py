#
# Copyright 2010-2015 Jingjing Li.
#
# This file is part of jplot2d.
#
# jplot2d is free software:
# you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
# as published by the Free Software Foundation, either version 3 of the License, or any later version.
#
# jplot2d is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU General Lesser Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License along with jplot2d.
# If not, see <http://www.gnu.org/licenses/>.
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

import jarray


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
    arglist = []
    for arg in args:
        if type(arg) == list or type(arg) == tuple:
            arglist.append(jarray.array(arg, 'd'))
        else:
            arglist.append(arg)

    graph = jplot2d_default_element_factory.createXYGraph(*arglist);

    ginfo = InterfaceInfo.loadInterfaceInfo(XYGraph)
    for key in kwargs:
        if ginfo.isWritableProp(key):
            jplot2d_set_prop(ginfo, graph, key, kwargs[key])
        else:
            raise AttributeError, "XYGraph has no attribute " + key

    return graph


def imagegraph(*args, **kwargs):
    graph = jplot2d_default_element_factory.createImageGraph(*args)

    ginfo = InterfaceInfo.loadInterfaceInfo(ImageGraph)
    for key in kwargs:
        if ginfo.isWritableProp(key):
            jplot2d_set_prop(ginfo, graph, key, kwargs[key])
        else:
            raise AttributeError, "ImageGraph has no attribute " + key

    return graph


def rgbimagegraph(*args, **kwargs):
    graph = jplot2d_default_element_factory.createRGBImageGraph(*args);

    ginfo = InterfaceInfo.loadInterfaceInfo(RGBImageGraph)
    for key in kwargs:
        if ginfo.isWritableProp(key):
            jplot2d_set_prop(ginfo, graph, key, kwargs[key])
        else:
            raise AttributeError, "RGBImageGraph has no attribute " + key

    return graph

def colorbar(*args, **kwargs):
    colorbar = jplot2d_default_element_factory.createColorbar(*args);

    colorbarinfo = InterfaceInfo.loadInterfaceInfo(Colorbar)
    cbainfo = InterfaceInfo.loadInterfaceInfo(ColorbarAxis)
    tminfo = InterfaceInfo.loadInterfaceInfo(AxisTickManager)
    txfinfo = InterfaceInfo.loadInterfaceInfo(AxisTransform)
    for key in kwargs:
        if colorbarinfo.isWritableProp(key):
            jplot2d_set_prop(colorbarinfo, colorbar, key, kwargs[key])
        elif cbainfo.isWritableProp(key):
            jplot2d_set_prop(cbainfo, colorbar.innerAxis, key, kwargs[key])
            jplot2d_set_prop(cbainfo, colorbar.outerAxis, key, kwargs[key])
        elif tminfo.isWritableProp(key):
            jplot2d_set_prop(tminfo, colorbar.innerAxis.tickManager, key, kwargs[key])
        elif txfinfo.isWritableProp(key):
            jplot2d_set_prop(tminfo, colorbar.axisTransform, key, kwargs[key])
        else:
            raise AttributeError, "Colorbar has no attribute " + key

    return colorbar


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

def rectangleannotation(x1, x2, y1, y2, *args, **kwargs):
    ann = jplot2d_default_element_factory.createRectangleAnnotation(x1, x2, y1, y2)

    anninfo = InterfaceInfo.loadInterfaceInfo(RectangleAnnotation)
    for key in kwargs:
        if anninfo.isWritableProp(key):
            jplot2d_set_prop(anninfo, ann, key, kwargs[key])
        else:
            raise AttributeError, "RectangleAnnotation has no attribute " + key

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
    if isinstance(obj, Element):
        elementclass = ElementFactory.getElementInterface(obj.getClass())
        iinfo = InterfaceInfo.loadInterfaceInfo(elementclass)
        for key in kwargs:
            if iinfo.isWritableProp(key):
                jplot2d_set_prop(iinfo, obj, key, kwargs[key])
            else:
                raise AttributeError, obj + " has no attribute " + key
    else:
        for key in kwargs:
            setattr(obj, key, kwargs[key])
