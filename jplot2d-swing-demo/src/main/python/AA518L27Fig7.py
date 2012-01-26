from org.jplot2d.element import *
from org.jplot2d.layout import *
from org.jplot2d.sizing import *
from org.jplot2d.transform import *
from org.jplot2d.util import *
from org.jplot2d.swing import JPlot2DFrame

from java.awt import Color
from java.lang import Double

from jarray import array
from jarray import zeros

# ElementFactory
ef = ElementFactory.getInstance()

# Create plot
p = ef.createPlot()
p.setPreferredContentSize(400, 230)
p.sizeMode = AutoPackSizeMode()
p.legend.visible = 0
pf = JPlot2DFrame(p)
pf.size = (480, 360)
pf.visible = 1

# Axes
xaxes = ef.createAxes(2)
xaxes[0].title.text = "z"
xaxes[0].tickManager.range = Range.Double(0, 4)
xaxes[0].tickManager.interval = 1
xaxes[0].tickManager.minorNumber = 9
xaxes[1].labelVisible = 0

yaxes = ef.createAxes(2)
yaxes[0].title.text = "$\\mathrm{S_160/S_100}$"
yaxes[0].tickManager.axisTransform.type = TransformType.LOGARITHMIC
yaxes[0].tickManager.range = Range.Double(0.4, 7)
yaxes[0].tickManager.interval = 1
yaxes[1].labelVisible = 0
p.addXAxes(xaxes)
p.addYAxes(yaxes)

# Layer
layer = ef.createLayer()
p.addLayer(layer, xaxes[0], yaxes[0])

x = array([0.00000, 0.100000, 0.200000, 0.300000, 0.400000, 0.500000, 0.600000, 0.700000, 0.800000, 0.900000, 1.00000, 1.10000, 1.20000, 1.30000, 1.40000, 1.50000, 1.60000, 1.70000, 1.80000, 1.90000, 2.00000, 2.10000, 2.20000, 2.30000, 2.40000, 2.50000, 2.60000, 2.70000, 2.80000, 2.90000, 3.00000, 3.10000, 3.20000, 3.30000, 3.40000, 3.50000, 3.60000, 3.70000, 3.80000, 3.90000, 4.00000, 4.10000, 4.20000, 4.30000, 4.40000, 4.50000, 4.60000, 4.70000, 4.80000, 4.90000], 'd')

# GAL
galy = array([1.03222, 1.25094, 1.49081, 1.74462, 2.01059, 2.28914, 2.57141, 2.84729, 3.10365, 3.33465, 3.53097, 3.68335, 3.78715, 3.83992, 3.84221, 3.79946, 3.71873, 3.61091, 3.48488, 3.34745, 3.20485, 3.06281, 2.92494, 2.79436, 2.67321, 2.56291, 2.46347, 2.37459, 2.29566, 2.22629, 2.16592, 2.11373, 2.06849, 2.02944, 1.99586, 1.96725, 1.94287, 1.92241, 1.90535, 1.89116, 1.87969, 1.87064, 1.86369, 1.85858, 1.85505, 1.85300, 1.85217, 1.85245, 1.85373, 1.85584], 'd')
gal = ef.createXYGraphPlotter(x, galy)
gal.color = Color(95, 207, 125)
gal.lineStroke = ef.createStroke(2, [6, 6])
layer.addGraphPlotter(gal)

# STARB
starby = array([0.912616, 1.06472, 1.21351, 1.35387, 1.48253, 1.59905, 1.70259, 1.79352, 1.86953, 1.93058, 1.97602, 2.00531, 2.02072, 2.02515, 2.02118, 2.01191, 2.00092, 1.99176, 1.98598, 1.98427, 1.98757, 1.99676, 2.01133, 2.02998, 2.05105, 2.07340, 2.09615, 2.11933, 2.14282, 2.16651, 2.19010, 2.21347, 2.23635, 2.25868, 2.28044, 2.30127, 2.32095, 2.33888, 2.35458, 2.36749, 2.37679, 2.38218, 2.38364, 2.38127, 2.37430, 2.36275, 2.34753, 2.33303, 2.31973, 2.30573], 'd')
starb = ef.createXYGraphPlotter(x, starby)
starb.color = Color(131, 213, 227)
starb.lineStroke = ef.createStroke(2, [1, 3, 6, 3])
layer.addGraphPlotter(starb)

# COMP
compy = array([0.501706, 0.562159, 0.620910, 0.686251, 0.746177, 0.796718, 0.848579, 0.907463, 0.973662, 1.04519, 1.11828, 1.19289, 1.26902, 1.34396, 1.41677, 1.48817, 1.55816, 1.62519, 1.68898, 1.74984, 1.80912, 1.86740, 1.92393, 1.97873, 2.03165, 2.08255, 2.13060, 2.17504, 2.21671, 2.25848, 2.30272, 2.35204, 2.40766, 2.46871, 2.53245, 2.59683, 2.66079, 2.72436, 2.78559, 2.84441, 2.89868, 2.94720, 2.98878, 3.02225, 3.04582, 3.05892, 3.06194, 3.05405, 3.03615, 3.00939], 'd')
comp = ef.createXYGraphPlotter(x, compy)
comp.color = Color(200, 99, 184)
comp.lineStroke = ef.createStroke(2, [1, 3])
layer.addGraphPlotter(comp)

# AGN2
agn2y = array([0.760343, 0.857363, 0.938720, 1.00954, 1.09040, 1.19802, 1.33700, 1.50992, 1.71970, 1.96892, 2.25033, 2.53001, 2.79015, 3.02753, 3.24030, 3.42664, 3.58853, 3.72476, 3.83835, 3.92658, 3.99002, 4.03012, 4.05141, 4.06144, 4.06581, 4.06756, 4.06678, 4.06253, 4.05450, 4.04236, 4.02581, 4.00460, 3.97868, 3.94816, 3.91312, 3.87413, 3.83128, 3.78502, 3.73568, 3.68375, 3.62957, 3.57363, 3.51634, 3.45818, 3.39824, 3.33743, 3.27520, 3.21520, 3.15798, 3.10293], 'd')
agn2 = ef.createXYGraphPlotter(x, agn2y)
agn2.color = Color(229, 57, 42)
agn2.lineStroke = ef.createStroke(2)
layer.addGraphPlotter(agn2)

# AGN1
agn1y = array([0.857343, 0.951230, 1.01367, 1.04761, 1.06091, 1.06239, 1.05883, 1.05450, 1.05286, 1.05524, 1.06168, 1.07179, 1.08492, 1.10048, 1.11786, 1.13652, 1.15561, 1.17397, 1.19142, 1.20795, 1.22359, 1.23840, 1.25238, 1.26561, 1.27814, 1.29005, 1.30133, 1.31218, 1.32266, 1.33314, 1.34376, 1.35463, 1.36581, 1.37737, 1.38929, 1.40153, 1.41405, 1.42675, 1.43960, 1.45255, 1.46554, 1.47853, 1.49147, 1.50432, 1.51704, 1.52957, 1.54183, 1.55370, 1.56498, 1.57554], 'd')
agn1 = ef.createXYGraphPlotter(x, agn1y)
agn1.color = Color(71, 82, 166)
agn1.lineStroke = ef.createStroke(2, [12, 6])
layer.addGraphPlotter(agn1)

# GAL Dots
galdx = array([0.458000, 0.253000, 0.210000, 0.189000, 0.278000, 0.120000, 0.437000, 0.200000, 0.0500000, 0.233000, 0.0790000, 0.519000, 0.136000, 0.299000, 0.254000, 0.211000, 0.438000, 0.556000, 0.136000, 0.337000, 0.562000, 0.348000, 0.114000, 0.224000, 0.377000, 0.0870000, 0.456000, 0.0700000, 0.299000, 0.286000, 0.520000, 0.139000, 0.278000, 0.954000, 0.207000, 0.561000, 0.638000, 0.114000, 0.845000, 0.202000, 0.517000, 0.478000, 0.105000, 1.14600, 0.377000, 0.560000, 0.642000, 0.253000, 0.639000, 0.560000, 0.557000, 0.562000, 0.377000, 0.206000, 0.561000, 0.457000, 0.476000, 0.848000, 0.423000, 0.534000, 0.354000, 0.410000, 0.559000], 'd')
galdy = array([1.19983, 1.40916, 3.97851, 1.54511, 1.64740, 1.62750, 2.01484, 1.39483, 1.31159, 2.37443, 1.08603, 1.71949, 1.33610, 1.17804, 1.71285, 1.15963, 1.43642, 5.52177, 2.52000, 1.94232, 3.49881, 3.03669, 1.54654, 4.35791, 1.95578, 1.08363, 1.63426, 0.619898, 1.24222, 2.34942, 2.12971, 0.751623, 2.06348, 2.41457, 1.54825, 1.93624, 2.67497, 0.776319, 2.51536, 0.894211, 1.86535, 2.19948, 1.85952, 2.46762, 2.38978, 1.88730, 2.33775, 2.24698, 2.19522, 2.18212, 1.67340, 1.58139, 2.13363, 1.55927, 1.11779, 1.21026, 1.96498, 1.92929, 1.42285, 1.84178, 1.46110, 1.74453, 1.62121], 'd')
galdyel = zeros(63, 'd')
galdyeh = array([0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Double.POSITIVE_INFINITY, 0, 0, 0, 0, 0, Double.POSITIVE_INFINITY, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Double.POSITIVE_INFINITY, 0, 0, 0, 0, 0, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 0, Double.POSITIVE_INFINITY, 0, 0, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY], 'd')
gald = ef.createXYGraphPlotter(galdx, galdy, None, None, galdyel, galdyeh)
gald.lineVisible = 0
gald.symbolVisible = 1
gald.symbolShape = SymbolShape.FCIRCLE
gald.symbolSize = 6
gald.color = Color(95, 207, 125)
layer.addGraphPlotter(gald)

# STARB Stars
starbdx = array([1.22400, 0.276000, 0.792000, 0.971000, 0.638000, 2.07800, 0.276000, 0.837000, 2.00000, 1.14800, 0.678000, 1.44900, 1.76000, 1.27000, 0.965000, 1.01300, 1.52300, 4.42800, 0.556000, 0.817000, 1.24800, 1.54800, 0.590000, 0.534000, 0.634000, 1.01600, 0.835000, 0.937000, 0.839000, 2.23500, 1.46500, 0.846000, 0.472000, 1.54800, 1.36300, 1.01200, 1.67800, 1.15200, 2.49000, 1.79000, 0.761000, 1.57400, 0.821000, 1.42400, 0.835000, 0.845000, 1.73200, 0.914000, 0.678000, 1.22600, 1.91700, 1.15200, 0.486000, 1.52500, 0.935000, 0.711000, 1.70500, 2.20300, 1.22300, 1.54800, 3.15700, 0.784000, 1.60400, 1.47300, 1.01300, 1.02100, 0.855000, 1.44900, 0.850000, 1.02900, 1.40000, 0.940000, 0.959000, 1.22400, 1.01700, 1.57400, 1.03100, 1.02200, 1.01600, 2.68200, 2.53800, 0.796000], 'd')
starbdy = array([1.86449, 1.67325, 1.41945, 1.93981, 1.19289, 3.11257, 1.60635, 1.40437, 4.23058, 1.89618, 0.761027, 1.39534, 3.40534, 1.41488, 2.83191, 2.78433, 2.56756, 3.24429, 3.78861, 2.80002, 3.17892, 1.63983, 1.90686, 1.91320, 1.66525, 1.32813, 4.64642, 1.18533, 1.55998, 3.95584, 3.19948, 1.39701, 3.73524, 3.40942, 3.72253, 0.927855, 3.51171, 3.48882, 3.41758, 1.86501, 1.20016, 3.23265, 1.88261, 3.19712, 1.23492, 2.02955, 2.80465, 0.860281, 1.12129, 2.11778, 2.68806, 2.67497, 1.13344, 2.55834, 2.55175, 2.50633, 2.49022, 2.45448, 1.72822, 2.37990, 2.30865, 1.28932, 2.28265, 2.23398, 2.21146, 2.17891, 1.14953, 1.52855, 1.40540, 2.03310, 2.01353, 0.874178, 1.99085, 1.98116, 1.89042, 1.52757, 1.87413, 1.86122, 1.83856, 1.83197, 1.71521, 1.68282], 'd')
starbdyel = zeros(82, 'd')
starbdyeh = array([0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Double.POSITIVE_INFINITY, 0, 0, Double.POSITIVE_INFINITY, 0, 0, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 0, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, 0, 0, Double.POSITIVE_INFINITY, 0, 0, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 0, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY], 'd')
starbd = ef.createXYGraphPlotter(starbdx, starbdy, None, None, starbdyel, starbdyeh)
starbd.lineVisible = 0
starbd.symbolVisible = 1
starbd.symbolShape = SymbolShape.STAR
starbd.symbolSize = 8
starbd.color = Color(131, 213, 227)
layer.addGraphPlotter(starbd)

# COMP F-Squares
compdx = array([2.00200, 2.42000, 2.00500, 2.79400, 3.49300, 2.66000, 3.72200, 1.84300, 3.86500, 2.43400, 2.75600, 1.61000, 0.764000], 'd')
compdy = array([2.11174, 7.80762, 1.83755, 2.77715, 4.02086, 3.82929, 3.75145, 1.64104, 1.02414, 2.04871, 1.82877, 0.580657, 1.71854], 'd')
compdyel = zeros(13, 'd')
compdyeh = array([0, Double.POSITIVE_INFINITY, 0, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 0, 0, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY], 'd')
compd = ef.createXYGraphPlotter(compdx, compdy, None, None, compdyel, compdyeh)
compd.lineVisible = 0
compd.symbolVisible = 1
compd.symbolShape = SymbolShape.FSQUARE
compd.symbolSize = 6
compd.color = Color(200, 99, 184)
layer.addGraphPlotter(compd)

# AGN2 F-TRIANGLE
agn2dx = array([0.279000, 0.473000, 0.410000, 0.640000, 0.423000, 0.489000, 0.433000, 0.638000, 0.639000, 0.507000, 0.858000, 0.475000, 0.306000, 0.555000, 0.458000, 0.438000, 0.934000, 0.903000, 0.946000, 0.817000, 0.694000, 0.975000, 1.21500, 0.799000, 0.460000, 0.557000, 0.529000, 0.489000, 1.19500, 0.849000, 0.202000, 0.566000, 0.840000, 0.851000, 1.01400, 0.475000, 0.559000, 1.02100, 1.33600, 0.841000, 0.935000, 0.271000, 0.557000, 0.839000, 1.00700, 0.679000, 0.840000, 0.489000, 0.847000, 0.763000, 0.508000, 1.92000, 1.70500, 0.746000, 0.683000, 1.26400, 0.975000, 0.936000, 1.01400, 0.940000, 0.836000, 1.01600, 3.02700, 0.417000, 0.502000, 0.556000, 1.14500, 1.14400, 1.67800, 0.517000, 1.75900, 0.612000, 1.30700, 1.01800, 0.454000, 0.484000], 'd')
agn2dy = array([1.30293, 1.32434, 1.16939, 1.83265, 1.32471, 1.26253, 1.17360, 1.41605, 1.20282, 2.31398, 1.78550, 1.56257, 1.21428, 3.12925, 1.06307, 1.44278, 1.76653, 1.59544, 5.95989, 5.93360, 3.30248, 2.34596, 2.13678, 1.75307, 0.878780, 1.12357, 1.12512, 1.08773, 4.48744, 2.08833, 0.688018, 4.21850, 4.14075, 1.61897, 1.98866, 1.71554, 1.25268, 3.74834, 2.78586, 2.09353, 3.37877, 1.42903, 2.39244, 2.39662, 1.60694, 0.881049, 1.53038, 1.33242, 1.46893, 1.79275, 2.96701, 1.91567, 2.85022, 2.58988, 1.58679, 1.33070, 2.65534, 1.54625, 2.72044, 2.66857, 1.38688, 2.60687, 2.59083, 0.953587, 1.11573, 0.937821, 2.24057, 2.07835, 2.07835, 2.05569, 2.00705, 1.99746, 1.93249, 1.89042, 1.06601, 0.988098], 'd')
agn2dyel = zeros(76, 'd')
agn2dyeh = array([0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 0, 0, 0, 0, 0, 0, 0, Double.POSITIVE_INFINITY, 0, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 0, 0, 0, Double.POSITIVE_INFINITY, 0, 0, Double.POSITIVE_INFINITY, 0, 0, 0, 0, 0, 0, 0, 0, 0, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, 0, 0, 0, 0, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 0, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 0], 'd')
agn2d = ef.createXYGraphPlotter(agn2dx, agn2dy, None, None, agn2dyel, agn2dyeh)
agn2d.lineVisible = 0
agn2d.symbolVisible = 1
agn2d.symbolShape = SymbolShape.FTRIANGLE
agn2d.symbolSize = 8
agn2d.color = Color(229, 57, 42)
layer.addGraphPlotter(agn2d)

# AGN1 U-TRIANGLE
agn1dx = array([4.16400, 3.23300, 3.58300], 'd')
agn1dy = array([0.608247, 2.42214, 1.41423], 'd')
agn1dyel = zeros(3, 'd')
agn1dyeh = array([0, Double.POSITIVE_INFINITY, 0], 'd')
agn1d = ef.createXYGraphPlotter(agn1dx, agn1dy, None, None, agn1dyel, agn1dyeh)
agn1d.lineVisible = 0
agn1d.symbolVisible = 1
agn1d.symbolShape = SymbolShape.UTRIANGLE
agn1d.symbolSize = 8
agn1d.color = Color(71, 82, 166)
layer.addGraphPlotter(agn1d)


