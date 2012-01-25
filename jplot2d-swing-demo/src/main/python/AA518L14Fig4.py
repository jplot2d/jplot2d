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
p.setPreferredContentSize(300, 300)
p.sizeMode = AutoPackSizeMode()
pf = JPlot2DFrame(p)
pf.size = (480, 480)
pf.visible = 1

# Axes
xaxes = ef.createAxes(2)
xaxes[0].title.text = "S$_100$/S$_24$"
xaxes[0].tickManager.range = Range.Double(0.8, 2.2)
xaxes[0].tickSide = AxisTickSide.OUTWARD
xaxes[1].tickSide = AxisTickSide.OUTWARD
xaxes[1].labelVisible = 0

yaxes = ef.createAxes(2)
yaxes[0].title.text = "SFR$_\\mathrm{FIR}$/SFR$_\\mathrm{24\\microm}$"
yaxes[0].tickManager.range = Range.Double(0, 5.2)
yaxes[0].tickManager.interval = 1
yaxes[0].tickManager.autoMinorNumber = 0
yaxes[0].tickSide = AxisTickSide.OUTWARD
yaxes[1].tickSide = AxisTickSide.OUTWARD
yaxes[1].labelVisible = 0
p.addXAxes(xaxes)
p.addYAxes(yaxes)

# Layer
layer = ef.createLayer()
p.addLayer(layer, xaxes[0], yaxes[1])

# horizontal dash line
hdl = ef.createHLineMarker(1)
hdl.color = GRAY
hdl.stroke = ef.createStroke(1, [6, 6])
layer.addMarker(hdl);

# horizontal strip
strip = ef.createHStripMarker(0.5, 1.5)
strip.fillPaint = Color(251, 232, 189)
strip.ZOrder = -1
layer.addMarker(strip);

#
b1_1 = array([1.419899, 1.619893, 1.419907, 1.319923, 1.810285, 1.841473, 1.608309], 'd')
b1_2 = array([0.017994, 0.017103, 0.015887, 0.017436, 0.037015, 0.036288, 0.023305], 'd')
b1_3 = array([1.194547, 2.334788, 0.975215, 1.046990, 4.026402, 4.353637, 1.914610], 'd')
b1_4 = array([0.106746, 0.670998, 0.114467, 0.120429, 0.592627, 0.566388, 0.199998], 'd')
b1 = ef.createXYGraphPlotter(b1_1, b1_3, b1_2, b1_2, b1_4, b1_4)
b1.color = GRAY
b1.linesVisible = 0
b1.symbolsVisible = 1
b1.symbolShape = SymbolShape.FTRIANGLE
b1.symbolColor = RED
b1.legendItem.text = "Bullet Cluster in PACS field"
layer.addGraphPlotter(b1)

#
bg1_1 = array([1.391505, 1.442690, 1.467886, 1.139075, 0.959461, 1.393919, 1.505945, 1.506306, 1.671487, 1.336313, 1.272283, 0.941866, 1.857896], 'd')
bg1_2 = array([0.014170, 0.018310, 0.015610, 0.054709, 0.046394, 0.033695, 0.038559, 0.023530, 0.016676, 0.022353, 0.035198, 0.027152, 0.011652], 'd')
bg1_3 = array([1.005709, 1.128696, 1.483923, 0.824007, 0.430120, 1.229956, 2.137710, 1.136676, 2.438485, 0.910690, 0.965880, 0.409521, 3.091505], 'd')
bg1_4 = array([0.091131, 0.102801, 0.118804, 0.139412, 0.058699, 0.147821, 0.326636, 0.105967, 0.280889, 0.211824, 0.112502, 0.052559, 0.246040], 'd')
bg1 = ef.createXYGraphPlotter(bg1_1, bg1_3, bg1_2, bg1_2, bg1_4, bg1_4)
bg1.color = GRAY
bg1.linesVisible = 0
bg1.symbolsVisible = 1
bg1.symbolShape = SymbolShape.FCIRCLE
bg1.symbolSize = 6
bg1.symbolColor = RED
bg1.legendItem.text = "BG system in PACS field"
layer.addGraphPlotter(bg1)

#
b0_1 = array([1.674486, 1.069313, 1.584537, 1.654299, 1.365276, 1.578115, 1.196919, 1.942886, 1.144155, 1.609685, 0.374725, 0.304246, 1.245910, 1.659333, 1.379772], 'd')
b0_2 = array([0.046158, 0.045599, 0.044985, 0.046158, 0.044663, 0.045469, 0.044663, 0.049357, 0.045100, 0.045599, 0.045342, 0.045342, 0.044663, 0.045219, 0.046013], 'd')
b0_3 = array([1.931079, 0.439483, 1.661472, 1.946264, 1.213043, 1.480344, 1.051577, 4.586347, 0.561341, 1.994514, 0.245011, 0.367738, 1.145222, 2.320756, 1.071991], 'd')
b0_4 = array([0.618866, 0.085960, 0.291826, 0.681112, 0.230290, 0.260769, 0.264749, 0.856806, 0.111786, 0.390115, 0.114122, 0.144550, 0.593335, 0.443113, 0.816351], 'd')
b0 = ef.createXYGraphPlotter(b0_1, b0_3, b0_2, b0_2, b0_4, b0_4)
b0.color = GRAY
b0.linesVisible = 0
b0.symbolsVisible = 1
b0.symbolShape = SymbolShape.TRIANGLE
b0.symbolColor = DARK_GRAY
b0.legendItem.text = "Bullet Cluster OutSide PACS (100$\\micro$m estimated)"
layer.addGraphPlotter(b0)

#
bg0_1 = array([1.548598, 2.066779, 1.143054, 1.648204, 1.721049, 1.417007, 1.807384, 1.491405], 'd')
bg0_2 = array([0.045469, 0.045219, 0.044985, 0.044873, 0.045219, 0.045733, 0.049990, 0.045342], 'd')
bg0_3 = array([1.552600, 3.422128, 0.459055, 1.807932, 2.435197, 0.955189, 3.364770, 1.068040], 'd')
bg0_4 = array([0.312489, 0.465107, 0.080527, 0.324565, 0.495231, 0.165624, 0.684698, 0.183726], 'd')
bg0 = ef.createXYGraphPlotter(bg0_1, bg0_3, bg0_2, bg0_2, bg0_4, bg0_4)
bg0.color = GRAY
bg0.linesVisible = 0
bg0.symbolsVisible = 1
bg0.symbolShape = SymbolShape.CIRCLE
bg0.symbolSize = 6
bg0.symbolColor = DARK_GRAY
bg0.legendItem.text = "BG system OutSide PACS (100$\\micro$m estimated)"
layer.addGraphPlotter(bg0)


# legend
p.legend.position = None
p.legend.columns = 1
p.legend.setLocation(-20, 300)
p.legend.HAlign = HAlign.LEFT
p.legend.VAlign = VAlign.TOP
p.legend.borderVisible = 0
p.legend.fontSize = 9
