from jplot2d.python import *

# the sizeMode must be set
p=plot(sizeMode=AutoPackSizeMode())
from org.jplot2d.swing import JPlot2DFrame
pf = JPlot2DFrame(p)
pf.size = (480, 480)
pf.visible = 1

r = [[0,1],[2,3]]
g = [[3,0],[1,2]]
b = [[2,3],[0,1]]

graph = rgbimagegraph(r,g,b)
layer = layer()
layer.addGraph(graph)
yaxis=axis()
xaxis=axis()
# the axes must be added before adding layers
p.addYAxis(yaxis)
p.addXAxis(xaxis)
p.addLayer(layer,xaxis.tickManager.axisTransform, yaxis.tickManager.axisTransform)