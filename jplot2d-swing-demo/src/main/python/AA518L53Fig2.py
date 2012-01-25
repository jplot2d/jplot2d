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
p.sizeMode = AutoPackSizeMode()
pf = JPlot2DFrame(p)
pf.size = (480, 480)
pf.visible = 1

# create subplots
usp = ef.createSubplot()
lsp = ef.createSubplot()
usp.setPreferredContentSize(380, 260)
lsp.setPreferredContentSize(380, 160)
lsp.margin.extraTop = 10
p.layoutDirector = GridLayoutDirector();
p.addSubplot(usp, GridConstraint(0, 1))
p.addSubplot(lsp, GridConstraint(0, 0))

# upper subplot Axes
uspx = ef.createAxes(2)
uspx[0].title.text = "wavelength $\\mathrm{\\lambda}$ [$\\mathrm{\\micro}$m]"
uspx[0].title.fontSize = 10
uspx[0].tickManager.range = Range.Double(10, 2e6)
uspx[0].tickManager.axisTransform.type = TransformType.LOGARITHMIC
uspx[1].labelVisible = 0

uspy = ef.createAxes(2)
uspy[0].title.text = "flux density [Jy]"
uspy[0].title.fontSize = 12
uspy[0].tickManager.range = Range.Double(0.05, 1200)
uspy[0].tickManager.axisTransform.type = TransformType.LOGARITHMIC
uspy[0].tickManager.labelFormat = "%.0f"
uspy[1].labelVisible = 0
usp.addXAxes(uspx)
usp.addYAxes(uspy)

# lower subplot Axes
lspx = ef.createAxes(2)
lspx[0].title.text = "wavelength $\\mathrm{\\lambda}$ [$\\mathrm{\\micro}$m]"
lspx[0].title.fontSize = 10
lspx[0].tickManager.range = Range.Double(10, 1500)
lspx[0].tickManager.axisTransform.type = TransformType.LOGARITHMIC
lspx[1].labelVisible = 0

lspy = ef.createAxes(2)
lspy[0].title.text = "residual [Jy]"
lspy[0].title.fontSize = 10
lspy[0].tickManager.range = Range.Double(-0.7, 0.7)
lspy[0].tickManager.number = 3
lspy[1].labelVisible = 0
lsp.addXAxes(lspx)
lsp.addYAxes(lspy)

# Layer
ulayer = ef.createLayer()
usp.addLayer(ulayer, uspx[0], uspy[0])
llayer = ef.createLayer()
lsp.addLayer(llayer, lspx[0], lspy[0])

# solid line
solx = array([10, 2000000], 'd')
soly = array([0.09, 900], 'd')
sol = ef.createXYGraphPlotter(solx, soly)
sol.color = BLUE
sol.legendItem.visible = 0
ulayer.addGraphPlotter(sol)

# dashed line
dlx = array([10, 2000000], 'd')
dly = array([0.1, 820], 'd')
dl = ef.createXYGraphPlotter(dlx, dly)
dl.color = BLUE
dl.lineStroke = ef.createStroke(1, [1, 3])
dl.legendItem.visible = 0
ulayer.addGraphPlotter(dl)

# ISO
xx = array([15], 'd')
xy = array([0.1059], 'd')
xye = array([0.0212], 'd')
xl = ef.createXYGraphPlotter(xx, xy, None, None, xye, xye)
xl.color = GREEN
xl.linesVisible = 0
xl.symbolsVisible = 1
xl.symbolShape = SymbolShape.SQUARE
xl.legendItem.text = "Xilouris et al. 2004"
ulayer.addGraphPlotter(xl)

# IRAS
gx = array([24.9, 59.9, 99.8], 'd')
gy = array([0.187, 0.546, 0.559], 'd')
gye = array([0.0281, 0.0819, 0.0839], 'd')
gl = ef.createXYGraphPlotter(gx, gy, None, None, gye, gye)
gl.color = GREEN
gl.linesVisible = 0
gl.symbolsVisible = 1
gl.symbolShape = SymbolShape.FTRIANGLE
gl.legendItem.text = "Golombek et al. 1988"
ulayer.addGraphPlotter(gl)

# MIPS
sx = array([23.67, 71.3, 156], 'd')
sy = array([0.171, 0.455, 0.582], 'd')
sye = array([0.013, 0.0092, 0.01], 'd')
sl = ef.createXYGraphPlotter(sx, sy, None, None, sye, sye)
sl.color = GREEN
sl.linesVisible = 0
sl.symbolsVisible = 1
sl.symbolShape = SymbolShape.FDIAMOND
sl.legendItem.text = "Shi et al. 2007"
ulayer.addGraphPlotter(sl)

# SCUBA
hx = array([449, 848], 'd')
hy = array([1.32, 2.48], 'd')
hye = array([0.396, 0.496], 'd')
hl = ef.createXYGraphPlotter(hx, hy, None, None, hye, hye)
hl.color = GREEN
hl.linesVisible = 0
hl.symbolsVisible = 1
hl.symbolShape = SymbolShape.TRIANGLE
hl.legendItem.text = "Haas et al. 2004"
ulayer.addGraphPlotter(hl)

# WMAP
wx = array([3180, 4910, 7300, 9070, 13000], 'd')
wy = array([6.2, 9.7, 13.3, 15.5, 19.7], 'd')
wye = array([0.4, 0.2, 0.1, 0.09, 0.06], 'd')
wl = ef.createXYGraphPlotter(wx, wy, None, None, wye, wye)
wl.color = GREEN
wl.linesVisible = 0
wl.symbolsVisible = 1
wl.symbolShape = SymbolShape.STAR
wl.legendItem.text = "Wright et al. 2009"
ulayer.addGraphPlotter(wl)

# VLA
cx = array([20130, 36540, 61730, 180200, 908400], 'd')
cy = array([26.4, 45.8, 70.1, 136.2, 327], 'd')
cye = array([2.643, 3.66, 5.61, 10.89, 16.38], 'd')
cl = ef.createXYGraphPlotter(cx, cy, None, None, cye, cye)
cl.color = GREEN
cl.linesVisible = 0
cl.symbolsVisible = 1
cl.symbolShape = SymbolShape.FOCTAGON
cl.legendItem.text = "Cotton et al. 2009"
ulayer.addGraphPlotter(cl)

# HERSCHEL
tx = array([100, 160, 250, 350, 500], 'd')
ty = array([0.517, 0.673, 0.86, 1.074, 1.426], 'd')
tye = array([0.129, 0.168, 0.215, 0.267, 0.375], 'd')
tl = ef.createXYGraphPlotter(tx, ty, None, None, tye, tye)
tl.color = RED
tl.linesVisible = 0
tl.symbolsVisible = 1
tl.symbolShape = SymbolShape.FOCTAGON
tl.legendItem.text = "this paper"
ulayer.addGraphPlotter(tl)

# legend
usp.legend.position = None
usp.legend.columns = 1
usp.legend.setLocation(-10, 250)
usp.legend.HAlign = HAlign.LEFT
usp.legend.VAlign = VAlign.TOP
usp.legend.borderVisible = 0
usp.legend.fontSize = 12

# residual
slrx = array([10, 1000], 'd')
slry = array([0, 0], 'd')
slrl = ef.createXYGraphPlotter(slrx, slry)
slrl.color = BLUE
slrl.legendItem.visible = 0
llayer.addGraphPlotter(slrl)

xry = array([-0.01], 'd')
xrl = ef.createXYGraphPlotter(xx, xry, None, None, xye, xye)
xrl.color = GREEN
xrl.linesVisible = 0
xrl.symbolsVisible = 1
xrl.symbolShape = SymbolShape.SQUARE
xrl.legendItem.visible = 0
llayer.addGraphPlotter(xrl)

gry = array([0.01, 0.2, 0.06], 'd')
grl = ef.createXYGraphPlotter(gx, gry, None, None, gye, gye)
grl.color = GREEN
grl.linesVisible = 0
grl.symbolsVisible = 1
grl.symbolShape = SymbolShape.FTRIANGLE
grl.legendItem.visible = 0
llayer.addGraphPlotter(grl)

sry = array([0.0, 0.07, -0.11], 'd')
srl = ef.createXYGraphPlotter(sx, sry)
srl.color = GREEN
srl.linesVisible = 0
srl.symbolsVisible = 1
srl.symbolShape = SymbolShape.FDIAMOND
srl.legendItem.visible = 0
llayer.addGraphPlotter(srl)

hry = array([-0.23, -0.03], 'd')
hrl = ef.createXYGraphPlotter(hx, hry, None, None, hye, hye)
hrl.color = GREEN
hrl.linesVisible = 0
hrl.symbolsVisible = 1
hrl.symbolShape = SymbolShape.TRIANGLE
hrl.legendItem.visible = 0
llayer.addGraphPlotter(hrl)

trry = array([0.01, -0.03, -0.13, -0.21, -0.26], 'd')
trl = ef.createXYGraphPlotter(tx, trry, None, None, tye, tye)
trl.color = RED
trl.linesVisible = 0
trl.symbolsVisible = 1
trl.symbolShape = SymbolShape.FOCTAGON
trl.legendItem.visible = 0
llayer.addGraphPlotter(trl)
