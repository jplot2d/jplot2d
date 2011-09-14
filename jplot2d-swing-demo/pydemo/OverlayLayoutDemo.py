import sys
print sys.path

from org.jplot2d.element import ElementFactory
from org.jplot2d.layout import *
from org.jplot2d.sizing import *
from org.jplot2d.swing import JPlot2DFrame
 
ef=ElementFactory.getInstance()
p=ef.createPlot()
p.sizeMode=FillContainerSizeMode(1)
p.layoutDirector=OverlayLayoutDirector()

pf=JPlot2DFrame(p)
pf.size=(640,480)
pf.visible=1

xaxes=ef.createAxes(2)
yaxes=ef.createAxes(2)
p.addXAxes(xaxes)
p.addYAxes(yaxes)

ly=ef.createLayer([0,0.1,0.2],[0,0.1,0.4],"Line A")
p.addLayer(ly, xaxes[0], yaxes[0])
