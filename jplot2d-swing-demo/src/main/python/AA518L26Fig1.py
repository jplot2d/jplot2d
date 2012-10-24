from jplot2d.python import *
from org.jplot2d.swing import JPlot2DFrame

# Create plot
p = plot()
p.sizeMode = AutoPackSizeMode()
pf = JPlot2DFrame(p)
pf.size = (480, 480)
pf.visible = 1

# create subplots
lsp = subplot(preferredContentSize=(400, 100))
msp = subplot(preferredContentSize=(400, 100))
hsp = subplot(preferredContentSize=(400, 100))
p.layoutDirector = GridLayoutDirector();
p.addSubplot(lsp, GridConstraint(0, 0))
p.addSubplot(msp, GridConstraint(0, 1))
p.addSubplot(hsp, GridConstraint(0, 2))

# x-axes
lxaxes = axes(2, transform=LOGARITHMIC, range=(0.1, 6))
lxaxes[0].title.text = "Redshift"
lxaxes[0].title.fontScale = 1
lxaxes[0].tickVisible = 0
lxaxes[1].tickVisible = 0
lxaxes[1].labelVisible = 0
lsp.addXAxes(lxaxes)

hxaxes = axes(2)
hxaxes[0].tickVisible = 0
hxaxes[0].labelVisible = 0
hxaxes[1].tickVisible = 0
hxaxes[1].labelVisible = 0
hsp.addXAxes(hxaxes)
hxaxes[0].tickManager = lxaxes[0].tickManager

# y-axes
lyaxes = axes(2, range=(0, 34))
lyaxes[0].title.text = "Number"
lyaxes[0].title.fontScale = 1
lyaxes[1].labelVisible = 0
lsp.addYAxes(lyaxes)

myaxes = axes(2, range=(0, 34))
myaxes[0].title.text = "Number"
myaxes[0].title.fontScale = 1
myaxes[1].labelVisible = 0
msp.addYAxes(myaxes)
myaxes[0].tickManager = lyaxes[0].tickManager

hyaxes = axes(2, range=(0, 34))
hyaxes[0].title.text = "Number"
hyaxes[0].title.fontScale = 1
hyaxes[1].labelVisible = 0
hsp.addYAxes(hyaxes)
hyaxes[0].tickManager = lyaxes[0].tickManager

# Layer
hlayer = layer()
hsp.addLayer(hlayer, hxaxes[0], hyaxes[0])
mlayer = layer()
msp.addLayer(mlayer, hxaxes[0], myaxes[0])
llayer = layer()
lsp.addLayer(llayer, lxaxes[0], lyaxes[0])

z_grid = 0.00000, 0.100000, 0.200000, 0.330000, 0.480000, 0.630000, 0.800000, 1.00000, 1.20000, 1.40000, 1.65000, 1.90000, 2.20000, 2.50000, 2.85000, 3.20000, 3.60000, 4.05000, 4.55000, 5.10000, 5.70000

p1_all = 0, 0, 3, 7, 12, 12, 28, 19, 9, 8, 2, 5, 5, 6, 5, 4, 1, 1, 0, 1, 0
p1_both = 0, 0, 1, 1, 3, 3, 6, 3, 3, 3, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0
p1_100 = 0, 0, 1, 1, 5, 3, 7, 4, 4, 3, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0
p1_160 = 0, 0, 1, 1, 5, 5, 10, 6, 4, 3, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0

hlayer.addGraph(xygraph(z_grid, p1_all, chartType=HISTOGRAM_EDGE, name="GOODS-N AGNs"))
hlayer.addGraph(xygraph(z_grid, p1_100, chartType=HISTOGRAM_EDGE, fillEnabled=1, fillPaint=LineHatchPaint(0, 45, 4), name="100$\\micro$m detecte only"))
hlayer.addGraph(xygraph(z_grid, p1_160, chartType=HISTOGRAM_EDGE, fillEnabled=1, fillPaint=LineHatchPaint(0, -45, 4), name="100$\\micro$m detecte only"))
hlayer.addGraph(xygraph(z_grid, p1_both, chartType=HISTOGRAM_EDGE, fillEnabled=1, fillPaint=GRAY, name="Detected in both bamds"))

setp(hsp.legend, position=None, location=(390, 90), columns=1, HAlign=HAlign.RIGHT, VAlign=VAlign.TOP, fontScale=0.9, borderVisible=0)

p2_all = 0, 0, 0, 1, 0, 0, 3, 7, 17, 17, 17, 15, 7, 4, 4, 3, 1, 0, 0, 0, 0
p2_both = 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0
p2_100 = 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0
p2_160 = 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 1, 2, 0, 0, 1, 0, 0, 0, 0, 0, 0

mlayer.addGraph(xygraph(z_grid, p2_all, chartType=HISTOGRAM_EDGE))
mlayer.addGraph(xygraph(z_grid, p2_100, chartType=HISTOGRAM_EDGE, fillEnabled=1, fillPaint=LineHatchPaint(0, 45, 4)))
mlayer.addGraph(xygraph(z_grid, p2_160, chartType=HISTOGRAM_EDGE, fillEnabled=1, fillPaint=LineHatchPaint(0, -45, 4)))
mlayer.addGraph(xygraph(z_grid, p2_both, chartType=HISTOGRAM_EDGE, fillEnabled=1, fillPaint=GRAY))

p3_all = 0, 0, 3, 8, 12, 12, 31, 26, 26, 25, 19, 20, 12, 10, 9, 7, 2, 1, 0, 1, 0
p3_both = 0, 0, 1, 1, 3, 3, 6, 3, 3, 4, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0
p3_100 = 0, 0, 1, 1, 5, 3, 7, 4, 5, 4, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0
p3_160 = 0, 0, 1, 1, 5, 5, 10, 6, 6, 5, 2, 3, 0, 1, 1, 0, 0, 0, 0, 0, 0

llayer.addGraph(xygraph(z_grid, p2_all, chartType=HISTOGRAM_EDGE))
llayer.addGraph(xygraph(z_grid, p2_100, chartType=HISTOGRAM_EDGE, fillEnabled=1, fillPaint=LineHatchPaint(0, 45, 4)))
llayer.addGraph(xygraph(z_grid, p2_160, chartType=HISTOGRAM_EDGE, fillEnabled=1, fillPaint=LineHatchPaint(0, -45, 4)))
llayer.addGraph(xygraph(z_grid, p2_both, chartType=HISTOGRAM_EDGE, fillEnabled=1, fillPaint=GRAY))


