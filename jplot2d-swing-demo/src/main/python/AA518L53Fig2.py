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
sp0 = ef.createSubplot()
sp1 = ef.createSubplot()
sp0.setPreferredContentSize(300, 200)
sp1.setPreferredContentSize(300, 120)
p.addSubplot(sp0, GridConstraint(0,0))
p.addSubplot(sp1, GridConstraint(1,1))

# sp0 Axes
sp0x = ef.createAxes(2)
sp0x[0].title.text = "wavelength $\\mathrm{\\lambda}$ [$\\mathrm{\\micro}$m]"
sp0x[0].tickManager.range = Range.Double(10, 2e6)
sp0x[0].tickManager.axisTransform.type = TransformType.LOGARITHMIC
sp0x[1].labelVisible = 0

sp0y = ef.createAxes(2)
sp0y[0].title.text = "flux density [Jy]"
sp0y[0].tickManager.range = Range.Double(0.05, 1200)
sp0y[0].tickManager.axisTransform.type = TransformType.LOGARITHMIC
sp0y[1].labelVisible = 0
sp0.addXAxes(sp0x)
sp0.addYAxes(sp0y)

# sp0 Axes
sp1x = ef.createAxes(2)
sp1x[0].title.text = "wavelength $\\mathrm{\\lambda}$ [$\\mathrm{\\micro}$m]"
sp1x[0].tickManager.range = Range.Double(10, 1500)
sp1x[0].tickManager.axisTransform.type = TransformType.LOGARITHMIC
sp1x[1].labelVisible = 0

sp1y = ef.createAxes(2)
sp1y[0].title.text = "residual [Jy]"
sp1y[0].tickManager.range = Range.Double(-0.7, 0.7)
sp1y[1].labelVisible = 0
sp1.addXAxes(sp1x)
sp1.addYAxes(sp1y)

# Layer
