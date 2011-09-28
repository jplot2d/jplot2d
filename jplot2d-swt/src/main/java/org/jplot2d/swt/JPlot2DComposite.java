package org.jplot2d.swt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.jplot2d.element.Plot;
import org.jplot2d.env.RenderEnvironment;
import org.jplot2d.interaction.InteractionManager;
import org.jplot2d.interaction.MousePreference;
import org.jplot2d.interaction.PlotDefaultMousePreference;
import org.jplot2d.interaction.PlotInteractionManager;
import org.jplot2d.renderer.AsyncImageRenderer;
import org.jplot2d.renderer.BufferedImageFactory;
import org.jplot2d.renderer.ImageRenderer;
import org.jplot2d.renderer.RenderingFinishedEvent;
import org.jplot2d.renderer.RenderingFinishedListener;
import org.jplot2d.swt.interaction.InteractionListener;
import org.jplot2d.swt.interaction.MenuHandler;
import org.jplot2d.warning.DefaultWarningManager;

/**
 * @author Jingjing Li
 * 
 */
public class JPlot2DComposite extends Composite implements ControlListener, DisposeListener,
		PaintListener, RenderingFinishedListener {

	static Logger logger = Logger.getLogger("org.jplot2d.swt");

	private final RenderEnvironment env;

	private ImageRenderer r;

	private long ifsn;

	private volatile BufferedImage bi;

	private Image image;

	private InteractionManager imanager;

	private final InteractionListener ial;

	private Runnable redrawRunner = new Runnable() {
		public void run() {
			redraw();
		}
	};

	/**
	 * Construct a Composite to display the given plot in its center. The plot properties can be
	 * safely modified by multiple threads.
	 * 
	 * @param plot
	 *            the plot to be display
	 */
	public JPlot2DComposite(Composite parent, Plot plot) {
		this(parent, plot, true);
	}

	/**
	 * Construct a Composite to display the given plot in its center.
	 * 
	 * @param plot
	 *            the plot to be display
	 * @param threadSafe
	 *            if <code>false</code>, all plot properties can only be changed within the Event
	 *            Dispatcher Thread. if <code>true</code>, all plot properties can be safely changed
	 *            by multiple threads.
	 */
	public JPlot2DComposite(Composite parent, Plot plot, boolean threadSafe) {
		this(parent, createRenderEnvironment(plot, threadSafe));
		env.setWarningManager(new DefaultWarningManager(this));
	}

	/**
	 * create a RenderEnvironment with the given plot.
	 * 
	 * @param plot
	 * @param threadSafe
	 * @return
	 */
	private static RenderEnvironment createRenderEnvironment(Plot plot, boolean threadSafe) {
		RenderEnvironment env = new RenderEnvironment(threadSafe);
		env.setPlot(plot);
		return env;
	}

	/**
	 * Construct a Composite to display a plot in its center. The plot has been assigned to the
	 * given RenderEnvironment.
	 * 
	 * @param env
	 *            the RenderEnvironment
	 */
	public JPlot2DComposite(Composite parent, RenderEnvironment env) {
		super(parent, SWT.NO_SCROLL);
		this.env = env;

		setBackground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
		addControlListener(this);
		addDisposeListener(this);
		addPaintListener(this);

		setMenu(buildMenu());

		ial = buildInteractionListener();
		addMouseListener(ial);
		addMouseMoveListener(ial);
		addMouseTrackListener(ial);
		addMouseWheelListener(ial);

		r = createImageRenderer();
		r.addRenderingFinishedListener(this);
		env.addRenderer(r);

	}

	public void renderingFinished(RenderingFinishedEvent event) {
		long fsn = event.getFsn();
		if (fsn < ifsn) {
			logger.info("[R] Rendering finished in wrong order, drop F." + fsn
					+ " Current frame is " + ifsn);
			return;
		}
		ifsn = fsn;

		bi = (BufferedImage) event.getResult();

		getDisplay().syncExec(redrawRunner);
	}

	/**
	 * @return
	 */
	public Color getPlotBackground() {
		return Color.WHITE;
	}

	protected ImageRenderer createImageRenderer() {
		return new AsyncImageRenderer(new BufferedImageFactory(BufferedImage.TYPE_INT_RGB,
				getPlotBackground()));
	}

	public void controlMoved(ControlEvent e) {
		// do nothing
	}

	public void controlResized(ControlEvent e) {
		env.getPlot().setContainerSize(new Dimension(getSize().x, getSize().y));
	}

	public void paintControl(PaintEvent e) {

		if (bi != null) {
			// dispose old image
			if (image != null) {
				image.dispose();
			}
			// create new image
			image = new Image(getDisplay(), convertToSWT(bi));
			bi = null;
		}

		if (image != null) {
			int width = image.getImageData().width;
			int height = image.getImageData().height;
			int x = (getSize().x - width) / 2;
			int y = (getSize().y - height) / 2;

			e.gc.drawImage(image, x, y);
		}

		ial.draw(e.gc);
	}

	public void widgetDisposed(DisposeEvent e) {
		// nothing to dispose
	}

	protected static ImageData convertToSWT(BufferedImage bufferedImage) {
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(),
					colorModel.getGreenMask(), colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int rgb = bufferedImage.getRGB(x, y);
					int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF,
							rgb & 0xFF));
					data.setPixel(x, y, pixel);
					if (colorModel.hasAlpha()) {
						data.setAlpha(x, y, (rgb >> 24) & 0xFF);
					}
				}
			}
			return data;
		} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
			IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		}
		return null;
	}

	/**
	 * Subclass can return it's own menu
	 * 
	 * @return an InteractionManager
	 */
	protected Menu buildMenu() {
		return new MenuHandler(this, env).getMenu();
	}

	/**
	 * Subclass can return it's own InteractionManager, initiated with a MousePreference
	 * 
	 * @return an InteractionListener
	 */
	protected InteractionListener buildInteractionListener() {
		imanager = PlotInteractionManager.getInstance();
		imanager.setMousePreference(PlotDefaultMousePreference.getInstance());
		return new InteractionListener(this, imanager, env);
	}

	public void setMousePreference(MousePreference prefs) {
		imanager.setMousePreference(prefs);
	}
}
