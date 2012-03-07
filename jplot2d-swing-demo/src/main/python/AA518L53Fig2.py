from org.jplot2d.python import *
from org.jplot2d.swing import JPlot2DFrame

# Create plot
p = plot(sizeMode=AutoPackSizeMode(), layoutDirector=GridLayoutDirector())
pf = JPlot2DFrame(p)
pf.size = (480, 480)
pf.visible = 1

# create subplots
usp = subplot(preferredContentSize=(380, 260))
lsp = subplot(preferredContentSize=(380, 160))
lsp.margin.extraTop = 10

p.addSubplot(usp, GridConstraint(0, 1))
p.addSubplot(lsp, GridConstraint(0, 0))

# upper subplot Axes
uspx = axes(2, range=(10, 2e6), transform=LOGARITHMIC)
uspx[0].title.text = "wavelength $\\mathrm{\\lambda}$ [$\\mathrm{\\micro}$m]"
uspx[0].title.fontSize = 12
uspx[1].labelVisible = 0

uspy = axes(2, range=(0.05, 1200), transform=LOGARITHMIC, labelFormat="%.0f")
uspy[0].title.text = "flux density [Jy]"
uspy[0].title.fontSize = 12
uspy[1].labelVisible = 0

usp.addXAxes(uspx)
usp.addYAxes(uspy)

# lower subplot Axes
lspx = axes(2, range=(10, 1500), transform=LOGARITHMIC)
lspx[0].title.text = "wavelength $\\mathrm{\\lambda}$ [$\\mathrm{\\micro}$m]"
lspx[0].title.fontSize = 12
lspx[1].labelVisible = 0

lspy = axes(2, range=(-0.7, 0.7), ticks=3)
lspy[0].title.text = "residual [Jy]"
lspy[0].title.fontSize = 12
lspy[1].labelVisible = 0

lsp.addXAxes(lspx)
lsp.addYAxes(lspy)

# Layer
ulayer = layer()
usp.addLayer(ulayer, uspx[0], uspy[0])
llayer = layer()
lsp.addLayer(llayer, lspx[0], lspy[0])

# solid line
solx = 10, 2000000
soly = 0.09, 900
sol = xygraphplotter(solx, soly, color=BLUE)
sol.legendItem.visible = 0
ulayer.addGraphPlotter(sol)

# dashed line
dlx = 10, 2000000
dly = 0.1, 820
dl = xygraphplotter(dlx, dly, color=BLUE, lineStroke=stroke(1,[1, 3]))
dl.legendItem.visible = 0
ulayer.addGraphPlotter(dl)

# ISO
xx = (15,)
xy = (0.1059,)
xye = (0.0212,)
xl = xygraphplotter(xx, xy, None, None, xye, xye, color=GREEN, lineVisible=0, symbolVisible=1, symbolShape=SymbolShape.SQUARE)
xl.legendItem.text = "Xilouris et al. 2004"
ulayer.addGraphPlotter(xl)

# IRAS
gx = 24.9, 59.9, 99.8
gy = 0.187, 0.546, 0.559
gye = 0.0281, 0.0819, 0.0839
gl = xygraphplotter(gx, gy, None, None, gye, gye, color=GREEN, lineVisible=0, symbolVisible=1, symbolShape=SymbolShape.FTRIANGLE)
gl.legendItem.text = "Golombek et al. 1988"
ulayer.addGraphPlotter(gl)

# MIPS
sx = 23.67, 71.3, 156
sy = 0.171, 0.455, 0.582
sye = 0.013, 0.0092, 0.01
sl = xygraphplotter(sx, sy, None, None, sye, sye, color=GREEN, lineVisible=0, symbolVisible=1, symbolShape=SymbolShape.FDIAMOND)
sl.legendItem.text = "Shi et al. 2007"
ulayer.addGraphPlotter(sl)

# SCUBA
hx = 449, 848
hy = 1.32, 2.48
hye = 0.396, 0.496
hl = xygraphplotter(hx, hy, None, None, hye, hye, color=GREEN, lineVisible=0, symbolVisible=1, symbolShape=SymbolShape.TRIANGLE)
hl.legendItem.text = "Haas et al. 2004"
ulayer.addGraphPlotter(hl)

# WMAP
wx = 3180, 4910, 7300, 9070, 13000
wy = 6.2, 9.7, 13.3, 15.5, 19.7
wye = 0.4, 0.2, 0.1, 0.09, 0.06
wl = xygraphplotter(wx, wy, None, None, wye, wye, color=GREEN, lineVisible=0, symbolVisible=1, symbolShape=SymbolShape.STAR)
wl.legendItem.text = "Wright et al. 2009"
ulayer.addGraphPlotter(wl)

# VLA
cx = 20130, 36540, 61730, 180200, 908400
cy = 26.4, 45.8, 70.1, 136.2, 327
cye = 2.643, 3.66, 5.61, 10.89, 16.38
cl = xygraphplotter(cx, cy, None, None, cye, cye, color=GREEN, lineVisible=0, symbolVisible=1, symbolShape=SymbolShape.FOCTAGON)
cl.legendItem.text = "Cotton et al. 2009"
ulayer.addGraphPlotter(cl)

# HERSCHEL
tx = 100, 160, 250, 350, 500
ty = 0.517, 0.673, 0.86, 1.074, 1.426
tye = 0.129, 0.168, 0.215, 0.267, 0.375
tl = xygraphplotter(tx, ty, None, None, tye, tye, color=RED, lineVisible=0, symbolVisible=1, symbolShape=SymbolShape.FOCTAGON)
tl.legendItem.text = "this paper"
ulayer.addGraphPlotter(tl)

# legend
setp(usp.legend, position=None, columns=1, location=(-10, 250), HAlign=HAlign.LEFT, VAlign=VAlign.TOP, borderVisible=0, fontSize=12)

# residual
slrx = 10, 1000
slry = 0, 0
slrl = xygraphplotter(slrx, slry, color=BLUE)
slrl.legendItem.visible=0
llayer.addGraphPlotter(slrl)

xry = (-0.01,)
xrl = xygraphplotter(xx, xry, None, None, xye, xye, color=GREEN, lineVisible=0, symbolVisible=1, symbolShape=SymbolShape.SQUARE)
xrl.legendItem.visible = 0
llayer.addGraphPlotter(xrl)

gry = 0.01, 0.2, 0.06
grl = xygraphplotter(gx, gry, None, None, gye, gye, color=GREEN, lineVisible=0, symbolVisible=1, symbolShape=SymbolShape.FTRIANGLE)
grl.legendItem.visible = 0
llayer.addGraphPlotter(grl)

sry = 0.0, 0.07, -0.11
srl = xygraphplotter(sx, sry, color=GREEN, lineVisible=0, symbolVisible=1, symbolShape=SymbolShape.FDIAMOND)
srl.legendItem.visible = 0
llayer.addGraphPlotter(srl)

hry = -0.23, -0.03
hrl = xygraphplotter(hx, hry, None, None, hye, hye, color=GREEN, lineVisible=0, symbolVisible=1, symbolShape=SymbolShape.TRIANGLE)
hrl.legendItem.visible = 0
llayer.addGraphPlotter(hrl)

trry = 0.01, -0.03, -0.13, -0.21, -0.26
trl = xygraphplotter(tx, trry, None, None, tye, tye, color=RED, lineVisible=0, symbolVisible=1, symbolShape=SymbolShape.FOCTAGON)
trl.legendItem.visible = 0
llayer.addGraphPlotter(trl)
