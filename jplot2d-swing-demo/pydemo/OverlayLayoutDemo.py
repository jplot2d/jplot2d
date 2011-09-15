import sys
print sys.path

from org.jplot2d.element import ElementFactory
from org.jplot2d.layout import *
from org.jplot2d.sizing import *
from org.jplot2d.swing import JPlot2DFrame
from org.jplot2d.util import Insets2D;

 
ef = ElementFactory.getInstance()
p = ef.createPlot()
p.sizeMode = FillContainerSizeMode(1)
p.layoutDirector = OverlayLayoutDirector()

pf = JPlot2DFrame(p)
pf.size = (640,480)
pf.visible = 1

xaxes = ef.createAxes(2)
yaxes = ef.createAxes(2)
p.addXAxes(xaxes)
p.addYAxes(yaxes)

ly = ef.createLayer([0,0.1,0.2], [0,0.1,0.4], "Line A")
p.addLayer(ly, xaxes[0], yaxes[0])

# add subplot
sp1 = ef.createSubplot();
p.addSubplot(sp1, BoundsConstraint(Insets2D(0, 0, 0, 0), Insets2D(0.05,	0.05, 0.45, 0.45)));
sp1.setLocation(80, 250);
sp1.setSize(300, 200);

p1x = ef.createAxes(2);
p1y = ef.createAxes(2);
p1x[0].getTitle().setText("x axis");
p1y[0].getTitle().setText("y axis");
p1x[1].tickVisible = 0;
p1x[1].labelVisible = 0;
p1y[1].tickVisible = 0;
p1y[1].labelVisible = 0;
sp1.addXAxes(p1x);
sp1.addYAxes(p1y);

nestLayer = ef.createLayer([0, 0.1, 0.2],[0, 0.1, 0.4], "line B");
sp1.addLayer(nestLayer, p1x[0], p1y[0]);

