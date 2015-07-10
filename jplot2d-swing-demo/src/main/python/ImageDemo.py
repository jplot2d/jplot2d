from jplot2d.python import *
from array import *

# the sizeMode must be set
p=plot(sizeMode=AutoPackSizeMode())
from org.jplot2d.swing import JPlot2DFrame
pf = JPlot2DFrame(p)
pf.size = (480, 480)
pf.visible = 1

z = []
for i in range(4000):
    row = array('f')
    for j in range(8000):
        v = 1000.0 * (j % (i + 1)) / (i + 1)
        row.append(v)
    z.append(row);

graph = imagegraph(z)
layer = layer()
layer.addGraph(graph)
yaxis=axis()
xaxis=axis()
# the axes must be added before adding layers
p.addYAxis(yaxis)
p.addXAxis(xaxis)
p.addLayer(layer,xaxis.tickManager.axisTransform, yaxis.tickManager.axisTransform)

colorbar = colorbar(barWidth=10, tickHeight=3)
p.addColorbar(colorbar)
colorbar.imageMapping = graph.mapping