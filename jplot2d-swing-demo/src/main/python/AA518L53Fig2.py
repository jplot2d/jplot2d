from org.jplot2d.element import *
from org.jplot2d.layout import *
from org.jplot2d.sizing import *
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
usp.setPreferredContentSize(300, 200)
lsp.setPreferredContentSize(300, 120)
p.layoutDirector = GridLayoutDirector();
p.addSubplot(usp, GridConstraint(0, 1))
p.addSubplot(lsp, GridConstraint(0, 0))

# upper subplot Axes
uspx = ef.createAxes(2)
uspx[0].title.text = "wavelength $\\mathrm{\\lambda}$ [$\\mathrm{\\micro}$m]"
uspx[0].tickManager.range = Range.Double(10, 2e6)
uspx[0].tickManager.axisTransform.type = TransformType.LOGARITHMIC
uspx[1].labelVisible = 0

uspy = ef.createAxes(2)
uspy[0].title.text = "flux density [Jy]"
uspy[0].tickManager.range = Range.Double(0.05, 1200)
uspy[0].tickManager.axisTransform.type = TransformType.LOGARITHMIC
uspy[1].labelVisible = 0
usp.addXAxes(uspx)
usp.addYAxes(uspy)

# lower subplot Axes
lspx = ef.createAxes(2)
lspx[0].title.text = "wavelength $\\mathrm{\\lambda}$ [$\\mathrm{\\micro}$m]"
lspx[0].tickManager.range = Range.Double(10, 1500)
lspx[0].tickManager.axisTransform.type = TransformType.LOGARITHMIC
lspx[1].labelVisible = 0

lspy = ef.createAxes(2)
lspy[0].title.text = "residual [Jy]"
lspy[0].tickManager.range = Range.Double(-0.7, 0.7)
lspy[1].labelVisible = 0
lsp.addXAxes(lspx)
lsp.addYAxes(lspy)

# Layer
