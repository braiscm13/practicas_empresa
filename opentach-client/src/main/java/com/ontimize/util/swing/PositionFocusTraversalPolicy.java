package com.ontimize.util.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.LayoutFocusTraversalPolicy;

public class PositionFocusTraversalPolicy extends LayoutFocusTraversalPolicy {

	static class PositionComparator implements Comparator, java.io.Serializable {

		private static final int ROW_TOLERANCE = 10;

		public boolean DEBUG = false;

		private boolean horizontal = true;

		private boolean leftToRight = true;

		void setComponentOrientation(ComponentOrientation orientation) {
			this.horizontal = orientation.isHorizontal();
			this.leftToRight = orientation.isLeftToRight();
		}

		@Override
		public int compare(Object o1, Object o2) {
			Component a = (Component) o1;
			Component b = (Component) o2;

			if (a == b) {
				return 0;
			}

			Container commonParent = null;
			ArrayList aParents = new ArrayList();
			Container parent = a.getParent();
			while (parent != null) {
				aParents.add(parent);
				parent = parent.getParent();
			}
			ArrayList bParents = new ArrayList();
			parent = b.getParent();
			while (parent != null) {
				bParents.add(parent);
				parent = parent.getParent();
			}
			for (int i = 0; i < aParents.size(); i++) {
				for (int j = 0; j < bParents.size(); j++) {
					if ((aParents.get(i) == bParents.get(j)) && ((Container) aParents.get(i)).isFocusCycleRoot()) {
						commonParent = (Container) aParents.get(i);
						break;
					}
				}
				if (commonParent != null)
					break;
			}

			int ax = this.getX(commonParent, a), ay = this.getY(commonParent, a), bx = this.getX(commonParent, b), by = this.getY(commonParent, b);
			if ((a instanceof com.ontimize.gui.field.IdentifiedElement) && (b instanceof com.ontimize.gui.field.IdentifiedElement)) {
				if (this.DEBUG) {
					System.out.println("a: " + ((com.ontimize.gui.field.IdentifiedElement) a).getAttribute() + " , b: " + ((com.ontimize.gui.field.IdentifiedElement) b).getAttribute());
				}
			}
			if (this.horizontal) {
				if (this.leftToRight) {

					// LT - Western Europe (optional for Japanese, Chinese, Korean)

					if (Math.abs(ay - by) < ROW_TOLERANCE) {
						if (ax == bx) {
							if (ay == by) {
								return 0;
							}
							return (ay < by) ? -1 : 1;
						} else {
							return (ax < bx) ? -1 : 1;
						}
					} else {
						return (ay < by) ? -1 : 1;
					}
				} else { // !leftToRight

					// RT - Middle East (Arabic, Hebrew)

					if (Math.abs(ay - by) < ROW_TOLERANCE) {
						if (ax == bx) {
							if (ay == by) {
								return 0;
							}
							return (ay > by) ? -1 : 1;
						} else {
							return (ax > bx) ? -1 : 1;
						}
					} else {
						return (ay < by) ? -1 : 1;
					}
				}
			} else { // !horizontal
				if (this.leftToRight) {

					// TL - Mongolian

					if (Math.abs(ax - bx) < ROW_TOLERANCE) {
						if (ay == by) {
							if (ax == bx) {
								return 0;
							}
							return (ax < bx) ? -1 : 1;
						} else {
							return (ay < by) ? -1 : 1;
						}
					} else {
						return (ax < bx) ? -1 : 1;
					}
				} else { // !leftToRight

					// TR - Japanese, Chinese, Korean

					if (Math.abs(ax - bx) < ROW_TOLERANCE) {
						if (ay == by) {
							if (ax == bx) {
								return 0;
							}
							return (ax < bx) ? -1 : 1;
						} else {
							return (ay < by) ? -1 : 1;
						}
					} else {
						return (ax > bx) ? -1 : 1;
					}
				}
			}
		}

		public int getX(Container commonParent, Component c) {
			if (commonParent == null)
				return c.getX();
			else {
				int x = c.getX();
				while ((c.getParent() != commonParent) && (c.getParent() != null)) {
					c = c.getParent();
					x = x + c.getX();
				}
				return x;
			}
		}

		public int getY(Container commonParent, Component c) {
			if (commonParent == null)
				return c.getX();
			else {
				int y = c.getY();
				while ((c.getParent() != commonParent) && (c.getParent() != null)) {
					c = c.getParent();
					y = y + c.getY();
				}
				return y;
			}
		}

	}

	public PositionFocusTraversalPolicy() {
		super();
		this.setComparator(new PositionComparator());
	}

}
