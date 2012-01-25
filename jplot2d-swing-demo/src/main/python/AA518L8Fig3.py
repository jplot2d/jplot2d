from org.jplot2d.element import *
from org.jplot2d.layout import *
from org.jplot2d.sizing import *
from org.jplot2d.transform import *
from org.jplot2d.util import *
from org.jplot2d.swing import JPlot2DFrame

from java.awt import Stroke
from java.awt import Color
from java.awt.Color import *

from jarray import array

# ElementFactory
ef = ElementFactory.getInstance()

# Create plot
p = ef.createPlot()
p.setPreferredContentSize(300, 200)
p.sizeMode = AutoPackSizeMode()
p.legend.visible = 0
p.fontSize = 16

pf = JPlot2DFrame(p)
pf.size = (480, 360)
pf.visible = 1

# Axes
xaxis = ef.createAxis()
xaxis.title.text = "r (cm)"
xaxis.title.fontScale = 1
xaxis.tickManager.axisTransform.type = TransformType.LOGARITHMIC
xaxis.tickManager.axisTransform.range = Range.Double(5e13, 2.5e17)
xaxisTop = ef.createAxis()
xaxisTop.position = AxisPosition.POSITIVE_SIDE
xaxisTop.title.text = u"r(\u2033)"
xaxisTop.title.fontScale = 1
xaxisTop.tickManager.axisTransform.type = TransformType.LOGARITHMIC
xaxisTop.tickManager.axisTransform.range = Range.Double(2.8e-2, 2e2)
xaxisTop.tickManager.labelFormat = "%.0m"

yaxisLeft = ef.createAxis()
yaxisLeft.color = RED
yaxisLeft.title.text = "X"
yaxisLeft.title.fontScale = 1
yaxisLeft.tickManager.axisTransform.type = TransformType.LOGARITHMIC
yaxisLeft.tickManager.axisTransform.range = Range.Double(3e-10, 7e-7)
yaxisRight = ef.createAxis()
yaxisRight.color = BLUE
yaxisRight.position = AxisPosition.POSITIVE_SIDE
yaxisRight.title.text = "$\\mathrm{T_K}$"
yaxisRight.title.fontScale = 1
yaxisRight.tickManager.axisTransform.type = TransformType.LINEAR
yaxisRight.tickManager.axisTransform.range = Range.Double(0, 1200)
yaxisRight.tickManager.interval = 500

p.addXAxis(xaxis)
p.addXAxis(xaxisTop)
p.addYAxis(yaxisLeft)
p.addYAxis(yaxisRight)

# Tk Layer
tkx = array([1.1e14, 1e15, 1e16, 1e17], 'd')
tky = array([1200, 360, 80, 0], 'd')
tkl = ef.createLayer(tkx, tky)
tkl.color = BLUE
tka = ef.createSymbolAnnotation(5e14, 600, "T$\\mathrm{_K}$")
tka.color = BLUE
tka.fontScale = 1.2
tkl.addAnnotation(tka)
p.addLayer(tkl, xaxis, yaxisRight)

# SiC2
scx = array([5e13, 1e16, 2e16, 7e16, 1e17, 2.1e17], 'd')
scy = array([2e-7, 2e-7, 5e-7, 2e-7, 5e-8, 3e-10], 'd')
scl = ef.createLayer(scx, scy)
scl.color = RED
sca = ef.createSymbolAnnotation(2e16, 1e-7, "SiC$\\mathrm{_2}$")
sca.color = RED
sca.fontScale = 1.2
scl.addAnnotation(sca)
p.addLayer(scl, xaxis, yaxisLeft)

# SiC2 LTE
ltex = array([5e13, 6e13, 9e13, 1.3e14, 2e14, 2.1e14, 2.7e14], 'd')
ltey = array([5e-8, 3e-7, 1.8e-7, 3e-7, 4e-8, 6e-8, 3e-10], 'd')
ltel = ef.createLayer(ltex, ltey)
ltel.color=GREEN
ltea = ef.createSymbolAnnotation(7e13, 3e-9, "SiC$\\mathrm{_2}$\nLTE")
ltea.color = GREEN
ltea.fontScale = 1.2
ltel.addAnnotation(ltea)
p.addLayer(ltel, xaxis, yaxisLeft)
