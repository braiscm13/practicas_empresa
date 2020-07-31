package com.opentach.downclient.component;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.Hashtable;

import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.utilmize.client.gui.UCardPanel;
import com.utilmize.client.gui.animatedcardlayout.AnimatingCardLayout;
import com.utilmize.client.gui.animatedcardlayout.Animation;
import com.utilmize.client.gui.animatedcardlayout.CubeAnimation;
import com.utilmize.client.gui.animatedcardlayout.DashboardAnimation;
import com.utilmize.client.gui.animatedcardlayout.FadeAnimation;
import com.utilmize.client.gui.animatedcardlayout.IrisAnimation;
import com.utilmize.client.gui.animatedcardlayout.RadialAnimation;
import com.utilmize.client.gui.animatedcardlayout.SlideAnimation;

public class SizedCardPanel extends UCardPanel {

	public SizedCardPanel(Hashtable parameters) {
		super(parameters);
	}

	@Override
	public void init(Hashtable parameters) {
		String animValue = (String) parameters.remove("animation");
		super.init(parameters);

		String animation = ParseUtilsExtended.getString(animValue, null);
		if (animation != null) {
			if (animation.equalsIgnoreCase("cube")) {
				this.setLayout(new SizedAnimatingCardLayout(new CubeAnimation()));
			} else if (animation.equalsIgnoreCase("slide")) {
				this.setLayout(new SizedAnimatingCardLayout(new SlideAnimation()));
			} else if (animation.equalsIgnoreCase("radial")) {
				this.setLayout(new SizedAnimatingCardLayout(new RadialAnimation()));
			} else if (animation.equalsIgnoreCase("fade")) {
				this.setLayout(new SizedAnimatingCardLayout(new FadeAnimation()));
			} else if (animation.equalsIgnoreCase("iris")) {
				this.setLayout(new SizedAnimatingCardLayout(new IrisAnimation()));
			} else {
				this.setLayout(new SizedAnimatingCardLayout(new DashboardAnimation()));
			}
			Integer animationDuration = ParseUtilsExtended.getInteger((String) parameters.get("animationduration"), 500);
			if (this.getLayout() instanceof AnimatingCardLayout) {
				((AnimatingCardLayout) this.getLayout()).setAnimationDuration(animationDuration);
			}
		} else {
			this.setLayout(new SizedCardLayout());
		}
	}

	private static class SizedAnimatingCardLayout extends AnimatingCardLayout {
		public SizedAnimatingCardLayout(Animation animation) {
			super(animation);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) {
				Insets insets = parent.getInsets();
				int ncomponents = parent.getComponentCount();
				int w = Integer.MAX_VALUE;
				int h = Integer.MAX_VALUE;

				for (int i = 0; i < ncomponents; i++) {
					Component comp = parent.getComponent(i);
					Dimension d = comp.getPreferredSize();
					if (d.width < w) {
						w = d.width;
					}
					if (d.height < h) {
						h = d.height;
					}
				}
				return new Dimension(insets.left + insets.right + w + (this.getHgap() * 2), insets.top + insets.bottom + h + (this.getVgap() * 2));
			}
		}
	}

	private static class SizedCardLayout extends CardLayout {
		public SizedCardLayout() {
			super();
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) {
				Insets insets = parent.getInsets();
				int ncomponents = parent.getComponentCount();

				Component comp = parent.getComponent((Integer) ReflectionTools.getFieldValue(this, "currentCard"));
				Dimension d = comp.getPreferredSize();
				int w = d.width;
				int h = d.height;
				return new Dimension(insets.left + insets.right + w + (this.getHgap() * 2), insets.top + insets.bottom + h + (this.getVgap() * 2));
			}
		}
	}

}
