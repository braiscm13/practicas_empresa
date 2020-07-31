import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.plaf.nimbus.AbstractRegionPainter;

public final class IndeterminateStyleTest {
	private final BoundedRangeModel	model	= new DefaultBoundedRangeModel();

	public JComponent makeUI() {
		JProgressBar progressBar0 = new JProgressBar(this.model);

		UIDefaults d = new UIDefaults();
		d.put("ProgressBar[Enabled+Indeterminate].foregroundPainter", new IndeterminateRegionPainter());

		JProgressBar progressBar1 = new JProgressBar(this.model);
		progressBar1.putClientProperty("Nimbus.Overrides", d);

		progressBar0.setIndeterminate(true);
		progressBar1.setIndeterminate(true);

		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEmptyBorder(32, 5, 32, 5));
		p.add(progressBar0);
		p.add(progressBar1);
		return p;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				IndeterminateStyleTest.createAndShowGUI();
			}
		});
	}

	public static void createAndShowGUI() {
		try {
			for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(laf.getName())) {
					UIManager.setLookAndFeel(laf.getClassName());
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.getContentPane().add(new IndeterminateStyleTest().makeUI());
		f.setSize(320, 240);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
}

class IndeterminateRegionPainter extends AbstractRegionPainter {
	// Copied from javax.swing.plaf.nimbus.ProgressBarPainter.java
	private final Color			color17	= this.decodeColor("nimbusOrange", .0f, .0f, .0f, -156);
	private final Color			color18	= this.decodeColor("nimbusOrange", -.015796512f, .02094239f, -.15294117f, 0);
	private final Color			color19	= this.decodeColor("nimbusOrange", -.004321605f, .02094239f, -.0745098f, 0);
	private final Color			color20	= this.decodeColor("nimbusOrange", -.008021399f, .02094239f, -.10196078f, 0);
	private final Color			color21	= this.decodeColor("nimbusOrange", -.011706904f, -.1790576f, -.02352941f, 0);
	private final Color			color22	= this.decodeColor("nimbusOrange", -.048691254f, .02094239f, -.3019608f, 0);
	private final Color			color23	= this.decodeColor("nimbusOrange", .003940329f, -.7375322f, .17647058f, 0);
	private final Color			color24	= this.decodeColor("nimbusOrange", .005506739f, -.46764207f, .109803915f, 0);
	private final Color			color25	= this.decodeColor("nimbusOrange", .0042127445f, -.18595415f, .04705882f, 0);
	private final Color			color26	= this.decodeColor("nimbusOrange", .0047626942f, .02094239f, .0039215684f, 0);
	private final Color			color27	= this.decodeColor("nimbusOrange", .0047626942f, -.15147138f, .1607843f, 0);
	private final Color			color28	= this.decodeColor("nimbusOrange", .010665476f, -.27317524f, .25098038f, 0);
	private Rectangle2D			rect	= new Rectangle2D.Float(0, 0, 0, 0);
	private final Path2D		path	= new Path2D.Float();
	private final PaintContext	ctx		= new PaintContext(new Insets(5, 5, 5, 5), new Dimension(29, 19), false);

	@Override
	public void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
		// this.path = this.decodePath1();
		// g.setPaint(this.color17);
		// g.fill(this.path);

		this.rect = this.decodeRect3();
		g.setPaint(this.decodeGradient5(this.rect));
		g.fill(this.rect);
		//
		// this.rect = this.decodeRect4();
		// g.setPaint(this.decodeGradient6(this.rect));
		// g.fill(this.rect);
	}

	@Override
	public PaintContext getPaintContext() {
		return this.ctx;
	}

	private Path2D decodePath1() {
		this.path.reset();
		this.path.moveTo(this.decodeX(0.6f), this.decodeY(0.12666667f));
		this.path.curveTo(this.decodeAnchorX(0.6000000238418579f, -2.0f), this.decodeAnchorY(0.12666666507720947f, 0.0f), this.decodeAnchorX(0.12666666507720947f, 0.0f),
				this.decodeAnchorY(0.6000000238418579f, -2.0f), this.decodeX(0.12666667f), this.decodeY(0.6f));
		this.path.curveTo(this.decodeAnchorX(0.12666666507720947f, 0.0f), this.decodeAnchorY(0.6000000238418579f, 2.0f), this.decodeAnchorX(0.12666666507720947f, 0.0f),
				this.decodeAnchorY(2.4000000953674316f, -2.0f), this.decodeX(0.12666667f), this.decodeY(2.4f));
		this.path.curveTo(this.decodeAnchorX(0.12666666507720947f, 0.0f), this.decodeAnchorY(2.4000000953674316f, 2.0f), this.decodeAnchorX(0.6000000238418579f, -2.0f),
				this.decodeAnchorY(2.8933334350585938f, 0.0f), this.decodeX(0.6f), this.decodeY(2.8933334f));
		this.path.curveTo(this.decodeAnchorX(0.6000000238418579f, 2.0f), this.decodeAnchorY(2.8933334350585938f, 0.0f), this.decodeAnchorX(3.0f, 0.0f),
				this.decodeAnchorY(2.8933334350585938f, 0.0f), this.decodeX(3.0f), this.decodeY(2.8933334f));
		this.path.lineTo(this.decodeX(3.0f), this.decodeY(2.6f));
		this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.6f));
		this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.4f));
		this.path.lineTo(this.decodeX(3.0f), this.decodeY(0.4f));
		this.path.lineTo(this.decodeX(3.0f), this.decodeY(0.120000005f));
		this.path.curveTo(this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(0.12000000476837158f, 0.0f), this.decodeAnchorX(0.6000000238418579f, 2.0f),
				this.decodeAnchorY(0.12666666507720947f, 0.0f), this.decodeX(0.6f), this.decodeY(0.12666667f));
		this.path.closePath();
		return this.path;
	}

	private Rectangle2D decodeRect3() {
		this.rect.setRect(this.decodeX(0.4f), // x
				this.decodeY(0.4f), // y
				this.decodeX(3.0f) - this.decodeX(0.4f), // width
				this.decodeY(2.6f) - this.decodeY(0.4f)); // height
		return this.rect;
	}

	private Rectangle2D decodeRect4() {
		this.rect.setRect(this.decodeX(0.6f), // x
				this.decodeY(0.6f), // y
				this.decodeX(2.8f) - this.decodeX(0.6f), // width
				this.decodeY(2.4f) - this.decodeY(0.6f)); // height
		return this.rect;
	}

	private Paint decodeGradient5(Shape s) {
		Rectangle2D bounds = s.getBounds2D();
		float x = (float) bounds.getX();
		float y = (float) bounds.getY();
		float w = (float) bounds.getWidth();
		float h = (float) bounds.getHeight();
		return this
				.decodeGradient(
						(0.5f * w) + x,
						(0.0f * h) + y,
						(0.5f * w) + x,
						(1.0f * h) + y,
						new float[] { 0.038709678f, 0.05483871f, 0.07096774f, 0.28064516f, 0.4903226f, 0.6967742f, 0.9032258f, 0.9241935f, 0.9451613f },
						new Color[] { this.color18, this.decodeColor(this.color18, this.color19, 0.5f), this.color19, this.decodeColor(this.color19, this.color20, 0.5f), this.color20, this
								.decodeColor(this.color20, this.color21, 0.5f), this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22 });
	}

	private Paint decodeGradient6(Shape s) {
		Rectangle2D bounds = s.getBounds2D();
		float x = (float) bounds.getX();
		float y = (float) bounds.getY();
		float w = (float) bounds.getWidth();
		float h = (float) bounds.getHeight();
		return this
				.decodeGradient(
						(0.5f * w) + x,
						(0.0f * h) + y,
						(0.5f * w) + x,
						(1.0f * h) + y,
						new float[] { 0.038709678f, 0.061290324f, 0.08387097f, 0.27258065f, 0.46129033f, 0.4903226f, 0.5193548f, 0.71774197f, 0.91612905f, 0.92419356f, 0.93225807f },
						new Color[] { this.color23, this.decodeColor(this.color23, this.color24, 0.5f), this.color24, this.decodeColor(this.color24, this.color25, 0.5f), this.color25, this
								.decodeColor(this.color25, this.color26, 0.5f), this.color26, this.decodeColor(this.color26, this.color27, 0.5f), this.color27, this.decodeColor(
										this.color27, this.color28, 0.5f), this.color28 });
	}
}