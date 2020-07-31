package com.opentach.client.comp;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 * The Class JScrollablePanel.
 */
public class JScrollablePanel extends JPanel implements Scrollable {

	private static final int	UNIT_INCREMENt		= 10;
	private static final int	BLOCK_INCREMENT		= 20;
	/** The Constant serialVersionUID. */
	private static final long	serialVersionUID	= 1L;
	private final boolean		horizontal;
	private final boolean		vertical;

	public JScrollablePanel(boolean vertical, boolean horizontal) {
		super();
		this.horizontal = horizontal;
		this.vertical = vertical;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
	 */
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return this.getPreferredSize();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
	 */
	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return JScrollablePanel.UNIT_INCREMENt;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
	 */
	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return JScrollablePanel.BLOCK_INCREMENT;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
	 */
	@Override
	public boolean getScrollableTracksViewportWidth() {
		return !this.vertical;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
	 */
	@Override
	public boolean getScrollableTracksViewportHeight() {
		return !this.horizontal;
	}

}
