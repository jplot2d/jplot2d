from org.jplot2d.python import *
# the sizeMode must be set
p=plot(sizeMode=AutoPackSizeMode())
from org.jplot2d.swing import JPlot2DFrame
pf = JPlot2DFrame(p)
pf.size = (480, 480)
pf.visible = 1

x = 0,1,2,3
y = 0,2,1,4
graph = xygraph(x, y)
layer = layer()
layer.addGraph(graph)
yaxis=axis()
xaxis=axis()
# the axes must be added before adding layers
p.addYAxis(yaxis)
p.addXAxis(xaxis)
p.addLayer(layer,xaxis.tickManager.axisTransform, yaxis.tickManager.axisTransform)