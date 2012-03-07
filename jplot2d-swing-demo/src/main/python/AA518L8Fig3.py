from org.jplot2d.python import *
from org.jplot2d.swing import JPlot2DFrame

# Create plot
p = plot(preferredContentSize=(300, 200), sizeMode=AutoPackSizeMode(), fontSize = 16)
p.legend.visible = 0
pf = JPlot2DFrame(p)
pf.size = (480, 360)
pf.visible = 1

# Axes
xaxis = axis(transform=LOGARITHMIC, range=(5e13, 2.5e17))
xaxis.title.text = "r (cm)"
xaxis.title.fontScale = 1
xaxisTop = axis(position=AxisPosition.POSITIVE_SIDE, transform=LOGARITHMIC, range=(2.8e-2, 2e2), labelFormat="%.0m")
xaxisTop.title.text = u"r(\u2033)"
xaxisTop.title.fontScale = 1

yaxisLeft = axis(color=RED, transform=LOGARITHMIC, range=(3e-10, 7e-7))
yaxisLeft.title.text = "X"
yaxisLeft.title.fontScale = 1
yaxisRight = axis(color=BLUE, position=AxisPosition.POSITIVE_SIDE, transform=LINEAR, range=(0, 1200), tickInterval=500)
yaxisRight.title.text = "$\\mathrm{T_K}$"
yaxisRight.title.fontScale = 1

p.addXAxis(xaxis)
p.addXAxis(xaxisTop)
p.addYAxis(yaxisLeft)
p.addYAxis(yaxisRight)

# Tk Layer
tkx = 1.1e14, 1e15, 1e16, 1e17
tky = 1200, 360, 80, 0
tkl = layer(tkx, tky, color=BLUE)
tka = symbolannotation(5e14, 600, "T$\\mathrm{_K}$", fontScale=1.2)
tkl.addAnnotation(tka)
p.addLayer(tkl, xaxis, yaxisRight)

# SiC2
scx = 5e13, 1e16, 2e16, 7e16, 1e17, 2.1e17
scy = 2e-7, 2e-7, 5e-7, 2e-7, 5e-8, 3e-10
scl = layer(scx, scy, color=RED)
sca = symbolannotation(2e16, 1e-7, "SiC$\\mathrm{_2}$", fontScale=1.2)
scl.addAnnotation(sca)
p.addLayer(scl, xaxis, yaxisLeft)

# SiC2 LTE
ltex = 5e13, 6e13, 9e13, 1.3e14, 2e14, 2.1e14, 2.7e14
ltey = 5e-8, 3e-7, 1.8e-7, 3e-7, 4e-8, 6e-8, 3e-10
ltel = layer(ltex, ltey, color=GREEN)
ltea = symbolannotation(7e13, 3e-9, "SiC$\\mathrm{_2}$\nLTE", fontScale = 1.2)
ltel.addAnnotation(ltea)
p.addLayer(ltel, xaxis, yaxisLeft)
